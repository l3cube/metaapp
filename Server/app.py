from flask import Flask
from flask import render_template
from flask import request
import json
import sqlite3
from contextlib import closing
from flask import g
from crypto import *
from aes import AESCipher

# configuration
DATABASE = '/tmp/MetaApp.db'
DEBUG = True
USERNAME = 'admin'
PASSWORD = 'admin'

privateKeyString = ''

app = Flask(__name__)
app.config.from_object(__name__)


def connect_db():
    return sqlite3.connect(app.config['DATABASE'])


def init_db():
    with closing(connect_db()) as db:
        with app.open_resource('schema.sql', mode='r') as f:
            db.cursor().executescript(f.read())
        db.commit()


@app.before_request
def before_request():
    g.db = connect_db()


@app.teardown_request
def teardown_request(exception):
    db = getattr(g, 'db', None)
    if db is not None:
        db.close()


@app.route('/', methods=['POST', 'GET'])
def start():
    if request.method == 'POST':
        return reg_user(request.form['Message'], request.form['Seed'])
    else:
        curr = g.db.execute('select id,uuid,public_key from users')
        entries = [dict(id=row[0], uuid=row[1], public_key=row[2]) for row in curr.fetchall()]
        return render_template('display.html', entries=entries)


def reg_user(message, seed):
    with open('private.pem', 'rb') as f:
        privateKeyString = f.read()
    privateKey = load_private_key_string(privateKeyString)
    seed = decrypt_message(privateKey, seed)
    message = message.replace('-', '+');
    message = message.replace('_', '/');
    print seed
    print message
    obj = AESCipher(seed)
    message = obj.decrypt(message)

    d = json.loads(message)
    uuid = d['UUID']
    public_key = d['Pubkey']
    print uuid
    print public_key
    g.db.execute('insert into users (uuid,public_key) values (?, ?)', [uuid, public_key])
    g.db.commit()
    return 'Client registered with uuid ' + uuid + ' and public key ' + public_key


@app.route('/receive/', methods=['POST'])
def receive_msg():
    with open('private.pem', 'rb') as f:
        privateKeyString = f.read()
    privateKey = load_private_key_string(privateKeyString)
    message = request.form['Message']
    message = decrypt_message(privateKey, message)
    print "Received :"+message
    curr = g.db.execute('select public_key from users where uuid=8446992752')
    row = curr.fetchone()
    if row:
        public_key_string = row[0]
        public_key = load_public_key_string(public_key_string)
        emessage = encrypt_message(public_key, message)
        emessage = emessage.replace('+', '-')
        emessage = emessage.replace('/', '_')
        print "Encrypted Message:"
        print emessage
    return message


@app.route('/display/', methods=['GET'])
def display():
    if request.method == 'GET':
        curr = g.db.execute('select * from users')
        entries = [dict(id=row[0], phone_number=row[1], msg=row[2], time=row[3], sender=row[4]) for row in
                   curr.fetchall()]
        return render_template('display.html', entries=entries)


if __name__ == '__main__':
    app.run(host='0.0.0.0', debug=True)
