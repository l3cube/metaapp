from datetime import datetime
from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask import request
from sqlalchemy import and_
import json

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:////tmp/MetaAppProxy.db'
db = SQLAlchemy(app)


class UserRequests(db.Model):
    uuid = db.Column(db.String(200), primary_key=True)
    reqid = db.Column(db.String(200), primary_key=True)
    deadline = db.Column(db.DateTime)
    topic = db.Column(db.String(1000))

    def __init__(self, uuid, reqid, deadline, topic):
        self.uuid = uuid
        self.reqid = reqid
        self.deadline = deadline
        self.topic = topic

    def __repr__(self):
        return '<Requests %r>' % self.token_id


class UserResponses(db.Model):
    uuid = db.Column(db.String(200), primary_key=True)
    reqid = db.Column(db.String(200), primary_key=True)
    topic = db.Column(db.String(200))
    Offer = db.Column(db.Text)
    sent = db.Column(db.Boolean)

    def __init__(self, uuid, reqid, topic, Offer, sent):
        self.uuid = uuid
        self.reqid = reqid
        self.topic = topic
        self.Offer = Offer
        self.sent = sent

    def __repr__(self):
        return '<Responses %r>' % self.token_id


@app.route('/receive/request/', methods=['POST'])
def receive_req():
    if request.method == 'POST':
        dict_json = request.get_json(force=True)
        print dict_json
        uuid = dict_json['Uuid']
        reqid = dict_json['RequestId']
        deadline_s = dict_json['Deadline']
        deadline = datetime.strptime(deadline_s, "%d-%m-%Y")
        topic = dict_json['Topic']
        new_entry = UserRequests(uuid, reqid, deadline, topic)
        db.session.add(new_entry)
        db.session.commit()
        return 'Request received', 200


@app.route('/receive/offer/', methods=['POST'])
def receive_offer():
    if request.method == 'POST':
        dict_json = request.get_json(force=True)
        uuid = dict_json['Uuid']
        reqid = dict_json['RequestId']
        topic = dict_json['Topic']
        offer = dict_json['Offer']
        if invalidate(uuid, reqid, topic, offer):
            new_entry = UserResponses(uuid, reqid, topic, offer, False)
            db.session.add(new_entry)
            db.session.commit()


def invalidate(user_id, req_id, topic, offer):
    entry = UserRequests.query.filter(and_(UserRequests.uuid == user_id, UserRequests.reqid == req_id)).first()
    if entry is not None:
        present = datetime.now()
        if (present.date() <= entry.deadline.date()) and (entry.topic == topic):
            return True
    return False


@app.route('/android/receive', methods=['POST'])
def send_offer():
    user = request.form['Uuid']
    print user
    offers = UserResponses.query.filter(and_(UserResponses.uuid == user, UserResponses.sent == False)).all()
    if len(offers) == 0:
        return "Failure", 404
    else:
        l = []
        for i in offers:
            d = {}
            d['RequestId'] = i.reqid
            d['Topic'] = i.topic
            d['Offer'] = i.Offer
            i.sent = True
            l.append(d)
        print json.dumps(l)
        db.session.commit()
        return json.dumps(l), 200


if __name__ == '__main__':
    app.run(host='10.23.18.144', debug=True)
