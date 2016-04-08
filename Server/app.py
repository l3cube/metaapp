from flask import Flask
from flask import render_template
from flask import request
import json
from crypto import *
from aes import AESCipher
import zmq
import time
import requests
from flask_sqlalchemy import SQLAlchemy
import uuid

server_ip = '10.42.0.1'
server_port = '5000'
proxy_ip = '10.42.0.1'
proxy_port = '5001'

# configuration
DATABASE = 'sqlite:////tmp/MetaAppServer.db'
DEBUG = True
privateKeyString = ''
zmq_port = '5556'
zmq_ip = server_ip

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = DATABASE
db = SQLAlchemy(app)
context = zmq.Context()
pubsocket = context.socket(zmq.PUB)


class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.Text, unique=True)
    public_key = db.Column(db.Text)             #Actually the Aes seed

    def __init__(self, uuid, public_key):
        self.uuid = uuid
        self.public_key = public_key

    def __repr__(self):
        return '<User %r>' % self.uuid


class UserReq(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.Text)
    requestid = db.Column(db.String(80))
    topic = db.Column(db.String(80))
    message = db.Column(db.Text)

    def __init__(self, uuid, requestid, topic, message):
        self.uuid = uuid
        self.requestid = requestid
        self.topic = topic
        self.message = message

    def __repr__(self):
        return '<User %r>' % self.uuid


def mogrify(topic, msg):
    """ json encode the message and prepend the topic """
    return topic + ' ' + json.dumps(msg)


def demogrify(topicmsg):
    """ Inverse of mogrify() """
    json0 = topicmsg.find('{')
    topic = topicmsg[0:json0].strip()
    msg = json.loads(topicmsg[json0:])
    return topic, msg


@app.route('/', methods=['POST', 'GET'])
def start():
    if request.method == 'POST':
        return reg_user(request.form['Message'])
    else:
        curr = User.query.all()
        entries = [dict(id=row.id, uuid=row.uuid, public_key=row.public_key) for row in curr]
        return render_template('display.html', entries=entries)


def reg_user(message):
    with open('private.pem', 'rb') as f:
        privateKeyString = f.read()
    privateKey = load_private_key_string(privateKeyString)
    message = decrypt_message(privateKey, message)
    message = message.replace('-', '+');
    message = message.replace('_', '/');
    print message
    d = json.loads(message)
    aes_seed = d['AesSeed']
    Uuid = uuid.uuid1().hex
    print Uuid
    print aes_seed
    user = User(Uuid, aes_seed)
    print 'ok'
    db.session.add(user)
    db.session.commit()
    print 'ok'
    d1 = {}
    d1['Uuid'] = Uuid
    print d1
    result = requests.post('http://'+proxy_ip+':'+proxy_port+'/adduser/', data=json.dumps(d1))
    print result
    return json.dumps(d1)


@app.route('/receive/', methods=['POST'])
def receive_msg():
    with open('private.pem', 'rb') as f:
        privateKeyString = f.read()
    privateKey = load_private_key_string(privateKeyString)
    emessage = request.form['Message']
    seed = request.form['Seed']
    seed = decrypt_message(privateKey, seed)
    emessage = emessage.replace('-', '+')
    emessage = emessage.replace('_', '/')
    print seed
    print emessage
    obj = AESCipher(seed)
    finalmessage = obj.decrypt(emessage)
    print finalmessage

    d = json.loads(finalmessage)
    uuid = d['Uuid']
    requestId = d['RequestId']
    topic = d['Topic']
    message = d['Message']

    d1 = json.loads(message)
    intent_description = d1['Intent_Description']
    deadline = d1['Deadline']

    print uuid
    print requestId
    print topic
    print message
    userreq = UserReq(uuid, requestId, topic, message)
    db.session.add(userreq)
    db.session.commit()
    d2 = {}
    d2['Uuid'] = uuid
    d2['RequestId'] = requestId
    d2['Topic'] = topic
    d2['Deadline'] = deadline
    print "ok"
    result = requests.post('http://'+proxy_ip+":"+proxy_port+'/receive/request/', data=json.dumps(d2))
    print result
    publish_msg(mogrify(topic, d))
    return "Intent Published"


def publish_msg(message):
    url = 'tcp://'+zmq_ip+':'+zmq_port
    print url
    try:
        pubsocket.bind(url)
        time.sleep(1)
        print "Sending message : "+message
        pubsocket.send_string(message)
    except Exception as e:
        print "Error : "+str(e)
    finally:
        pubsocket.unbind(url)


@app.route('/session/update', methods=['POST'])
def session_update():
    if request.method == 'POST':
        message = request.form['Message']
        with open('private.pem', 'rb') as f:
            privateKeyString = f.read()
        privateKey = load_private_key_string(privateKeyString)
        message = decrypt_message(privateKey, message)
        message = message.replace('-', '+')
        message = message.replace('_', '/')
        print message
        d = json.loads(message)
        Uuid = d['Uuid']
        aes_seed = d['AesSeed']
        user = User(Uuid, aes_seed)
        db.session.add(user)
        db.session.commit()
        return "Session updated"


@app.route('/display/', methods=['GET'])
def display():
    if request.method == 'GET':
        curr = User.query.all()
        entries = [dict(id=row.id, uuid=row.uuid, public_key=row.public_key) for row in curr]
        return render_template('display.html', entries=entries)


if __name__ == '__main__':
    app.run(host=server_ip, port=int(server_port))


