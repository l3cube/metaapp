from datetime import datetime
from flask import Flask
from flask import request
from flask import render_template, jsonify
from flask import redirect, url_for
from flask_sqlalchemy import SQLAlchemy
import json
import requests

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/MetaAppSeller.db'
db = SQLAlchemy(app)


class UserRequests(db.Model):
    uuid = db.Column(db.String(200), primary_key=True)
    reqid = db.Column(db.String(200), primary_key=True)
    deadline = db.Column(db.DateTime)
    topic = db.Column(db.String(1000))
    intentdesc = db.Column(db.String(1000))

    def __init__(self, uuid, reqid, deadline, topic, intentdesc):
        self.uuid = uuid
        self.reqid = reqid
        self.deadline = deadline
        self.topic = topic
        self.intentdesc = intentdesc

    def __repr__(self):
        return '<Requests %r>' % self.uuid


@app.route('/seller-display/', methods=['POST'])
def receive_req():
    if request.method == 'POST':
        dict_json = request.get_json(force=True)
        print dict_json
        uuid = dict_json['Uuid']
        reqid = dict_json['RequestId']
        message = dict_json['Message']
        dict1_json = json.loads(message)
        deadline_s = dict1_json['Deadline']
        deadline = datetime.strptime(deadline_s, "%d-%m-%Y")
        intent_desc = dict1_json['Intent_Description']
        topic = dict_json['Topic']
        new_entry = UserRequests(uuid, reqid, deadline, topic, intent_desc)
        db.session.add(new_entry)
        db.session.commit()
        return 'Request received', 200


@app.route('/', methods=['GET'])
def homepage():
    return render_template("index.html")


@app.route('/display/', methods=['GET'])
def display():
    data = UserRequests.query.all()
    entries = [dict(uuid=row.uuid, reqid=row.reqid, deadline=row.deadline, topic=row.topic, intentdesc=row.intentdesc) for row in data]
    return render_template("seller-homepage.html", entries=entries)

@app.route('/login/', methods=['POST', 'GET'])
def login():
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        #Do validation
        if(password == 'test'):
            return redirect(url_for('display'))
        else:
            print "Unsuccesful Login"
    else:
        return render_template("signin.html")


@app.route('/respond/', methods=['POST'])
def make_reply():
    if request.method == 'POST':
        print request.form
        uuid = request.form['uuid']
        reqid = request.form['requestid']
        topic = request.form['topic']
        servicedesc = request.form['servicedesc']
        cost = request.form['cost']
        ttc = request.form['ttc']
        d = {}
        d['Service_Description'] = servicedesc
        d['Cost'] = cost
        d['Time_to_Complete'] = ttc
        s = json.dumps(d)
        d1 = {}
        d1['Uuid'] = uuid
        d1['RequestId'] = reqid
        d1['Topic'] = topic
        d1['Offer'] = s
        result = requests.post('http://192.168.43.81:5000/receive/offer/', data=json.dumps(d1))
        return redirect(url_for('display'))


@app.route('/team/')
def team():
    return render_template("team.html")


@app.route('/check-db/')
def check_db():
    data = UserRequests.query.all()
    print data
    return jsonify(data)


@app.route("/myStatus/")
def get_status():
    return "On"


if __name__ == '__main__':
    app.run(host='192.168.43.81', port=8787)
