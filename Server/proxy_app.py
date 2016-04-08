from datetime import datetime
from flask import Flask, make_response
from flask_sqlalchemy import SQLAlchemy
from flask import request
from sqlalchemy import and_,or_
import json
import uuid
import requests

proxy_ip = '10.42.0.1'
proxy_port = '5001'
senti_ip = '10.42.0.1'
senti_port = '5000'

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
        return '<Requests %r>' % self.uuid


class UserResponses(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.String(200))
    reqid = db.Column(db.String(200))
    topic = db.Column(db.String(200))
    Offer = db.Column(db.Text)
    seller = db.Column(db.String(200))
    sent = db.Column(db.Boolean)

    def __init__(self, uuid, reqid, topic, Offer, seller, sent):
        self.uuid = uuid
        self.reqid = reqid
        self.topic = topic
        self.Offer = Offer
        self.seller = seller
        self.sent = sent

    def __repr__(self):
        return '<Responses %r>' % self.uuid


class Seller(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    client_key = db.Column(db.String(200))
    name = db.Column(db.String(200))
    filters = db.Column(db.String(200))
    handle = db.Column(db.String(200))
    endpoint = db.Column(db.String(200))
    rank = db.Column(db.Float)
    offers_sent = db.Column(db.Integer)
    offers_accepted = db.Column(db.Integer)
    reputation = db.Column(db.Float)
    rating = db.Column(db.Integer)

    def __init__(self, client_key, name, filters, handle, endpoint, rank):
        self.client_key = client_key
        self.name = name
        self.filters = filters
        self.handle = handle
        self.endpoint = endpoint
        self.rank = rank
        self.offers_sent = 0
        self.offers_accepted = 0
        self.reputation = 0
        self.rating = 0

    def __repr__(self):
        return '<Responses %r>' % self.client_key


class AccessRequests(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.Text)
    seller = db.Column(db.String(200))
    filetype = db.Column(db.String(200))
    sent = db.Column(db.Boolean)

    def __init__(self, uuid, seller, filetype, sent):
        self.uuid = uuid
        self.seller = seller
        self.filetype = filetype
        self.sent = sent

    def __repr__(self):
        return '<Responses %r>' % self.seller


class ListFiles(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    seller = db.Column(db.String(200))
    filetype = db.Column(db.String(200))
    uuid = db.Column(db.String(200))
    fileid = db.Column(db.String(200))

    def __init__(self, seller, filetype, uuid, fileid):
        self.seller = seller
        self.filetype = filetype
        self.uuid = uuid
        self.fileid = fileid

    def __repr__(self):
        return '<Responses %r>' % self.seller


class Users(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    uuid = db.Column(db.Text)

    def __init__(self, uuid):
        self.uuid = uuid

    def __repr__(self):
        return '<Responses %r>' % self.uuid


@app.route('/adduser/', methods=['POST'])
def add_user():
    print "ok"
    if request.method == 'POST':
        print "ok"
        dict_json = request.get_json(force=True)
        new_entry = Users(dict_json['Uuid'])
        db.session.add(new_entry)
        db.session.commit()
        return "Success", 200


@app.route('/register/seller/', methods=['POST'])
def register_seller():
    if request.method == 'POST':
        dict_json = request.get_json(force=True)
        print dict_json
        name = dict_json['Name']
        filters = dict_json['Filter']
        handle = dict_json['Handle']
        endpoint = dict_json['Endpoint']
        curr = Seller.query.filter(or_(Seller.name == name, Seller.handle == handle)).all()
        if len(curr) == 0:
            client_key = uuid.uuid1().hex
            new_entry = Seller(client_key, name, filters, handle, endpoint, 0)
            db.session.add(new_entry)
            db.session.commit()
            response = {}
            response['Client_Key'] = client_key
            return json.dumps(response), 200
        else:
            response = {}
            response['Error'] = "Name or Handle already exists"
            return json.dumps(response), 404


@app.route('/receive/request/', methods=['POST'])
def receive_req():
    if request.method == 'POST':
        dict_json = request.get_json(force=True)
        print dict_json
        uuid = dict_json['Uuid']
        reqid = dict_json['RequestId']
        deadline_s = dict_json['Deadline']
        dict_deadline = json.loads(deadline_s)
        deadline = dict_deadline['Date']
        # Not considering time here. Add Code for it.
        deadline = datetime.strptime(deadline, "%Y-%m-%d")
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
        key = dict_json['Key']
        res = {}

        if not invalidate_seller(key):
            res['Error'] = 'Invalid Client Key'
            return json.dumps(res), 404

        if invalidate_offer(uuid, reqid, topic, offer):
            entry = Seller.query.filter(Seller.client_key == key).first()
            seller = entry.name
            new_entry = UserResponses(uuid, reqid, topic, offer, seller, False)
            db.session.add(new_entry)
            offers_sent = entry.offers_sent
            entry.offers_sent = offers_sent+1
            db.session.commit()
            res['Status'] = 'Offer received'
            return json.dumps(res), 200
        else:
            res['Error'] = 'Offer discarded'
            return json.dumps(res), 404


@app.route('/receive/review/', methods=['POST'])
def receive_review():
    seller = request.form['Seller']
    review = request.form['Review']
    stars = request.form['Stars']
    # Calculate Rmax, Rmin, n, m
    curr = Seller.query.all()
    n = len(curr)
    Rmax = -1
    Rmin = 1
    for i in curr:
        if i.reputation > Rmax:
            Rmax = i.reputation
        if i.reputation < Rmin:
            Rmin = i.reputation
    # m = n / (2 * (Rmin-Rmax))

    # Do sentiment analysis
    entry = Seller.query.filter(Seller.name == seller).first()
    entry.offers_accepted += 1
    entry.rating = entry.rating + int(stars)

    # calculate Ri for each seller
    # Vi is the sentiment value
    Vi = 1

    data = {}
    data['input'] = review
    res = requests.post('http://'+senti_ip+':'+senti_port+'/', data=json.dumps(data))
    value = res.text
    neg = float(value.split(" ")[0])
    pos = float(value.split(" ")[1])
    if pos >= neg:
        Vi = pos
    else:
        Vi = -neg
    print Vi
    Ri = entry.reputation
    Ri = Ri + Vi
    entry.reputation = Ri
    db.session.add(entry)
    db.session.commit()

    # Calculate Rj for each other seller
    entry = Seller.query.all()
    for i in entry:
        if i.name != seller:
            Rj = i.reputation
            term = (float(Vi) / n)
            Rj = Rj - term
            i.reputation = Rj
            db.session.add(i)
            db.session.commit()

    res = []
    # Calculate rank and form a response
    entry = Seller.query.all()
    for i in entry:
        if i.offers_sent != 0:
            Fi = i.offers_accepted / i.offers_sent
        else:
            Fi = 0
        Ri = i.reputation
        rank = Fi * Ri
        i.rank = rank
        db.session.add(i)
        db.session.commit()
        d = {}
        d['Seller'] = i.name
        d['Rank'] = i.rank
        d['Rating'] = i.rating
        res.append(d)
    return json.dumps(res), 200


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
            d['Seller'] = i.seller
            entry = Seller.query.filter(Seller.name == i.seller).first()
            d['Rank'] = entry.rank
            d['Rating'] = entry.rating
            i.sent = True
            l.append(d)
        print json.dumps(l)
        db.session.commit()
        return json.dumps(l), 200


@app.route('/android/refresh/', methods=['POST'])
def refresh_sellers():
    res = []
    entries = Seller.query.all()
    for entry in entries:
        r = {}
        r['Seller'] = entry.name
        r['Rank'] = entry.rank
        r['Rating'] = entry.rating
        res.append(r)
    return json.dumps(res), 200


@app.route('/namespace/access/', methods=['POST'])
def namespace_access():
    dict_json = request.get_json(force=True)
    file_type = dict_json['FileType']
    client_key = dict_json['Key']
    res = {}
    if not invalidate_seller(client_key):
        res['Error'] = 'Invalid Client Key'
        return json.dumps(res), 404

    if not invalidate_filetype(file_type):
        res['Error'] = 'Invalid File Type'
        return json.dumps(res), 404
    else:
        entry = Seller.query.filter(Seller.client_key == client_key).first()
        seller = entry.name
        users = Users.query.all()
        if users is not None:
            for i in users:
                new_entry = AccessRequests(i.uuid, seller, file_type, False)
                db.session.add(new_entry)
                db.session.commit()
        res['Status'] = 'Request Sent'
        return json.dumps(res), 200


@app.route('/namespace/list/', methods=['POST'])
def namespace_list():
    if request.method == 'POST':
        dict_json = request.get_json(force=True)
        file_type = dict_json['File']
        client_key = dict_json['Key']
        print file_type
        print client_key
        if not invalidate_seller(client_key):
            res = {}
            res['Error'] = 'Invalid Client Key'
            return json.dumps(res), 404

        if not invalidate_filetype(file_type):
            res = {}
            res['Error'] = 'Invalid File Type'
            return json.dumps(res), 404
        else:
            res = []
            entry = Seller.query.filter(Seller.client_key == client_key).first()
            seller = entry.name
            curr = ListFiles.query.filter(and_(ListFiles.seller == seller, ListFiles.filetype == file_type)).all()
            for i in curr:
                r = {}
                r['Uuid'] = i.uuid
                r['FileId'] = i.fileid
                res.append(r)
            return json.dumps(res), 200


@app.route('/namespace/notification/', methods=['POST'])
def namespace_notification():
    uuid = request.form['Uuid']
    curr = AccessRequests.query.filter(and_(AccessRequests.uuid == uuid, AccessRequests.sent == False)).all()
    if len(curr) == 0:
        return "No Notifications", 404
    else:
        res = []
        for i in curr:
            r = {}
            r['Seller'] = i.seller
            r['FileType'] = i.filetype
            entry = Seller.query.filter(Seller.name == i.seller).first()
            r['Rank'] = entry.rank
            r['Rating'] = entry.rating
            i.sent = True
            res.append(r)
        db.session.commit()
        return json.dumps(res), 200


@app.route('/namespace/files/', methods=['POST'])
def namespace_files():
    seller = request.form['Seller']
    filetype = request.form['FileType']
    uuid = request.form['Uuid']
    fileids = request.form['FileId']
    files = json.loads(fileids)
    for i in files:
        new_entry = ListFiles(seller, filetype, uuid, i)
        db.session.add(new_entry)
        db.session.commit()
    return "Success", 200


def invalidate_offer(user_id, req_id, topic, offer):
    entry = UserRequests.query.filter(and_(UserRequests.uuid == user_id, UserRequests.reqid == req_id)).first()
    if entry is not None:
        present = datetime.now()
        if (present.date() <= entry.deadline.date()) and (entry.topic == topic):
            return True
    return False


def invalidate_seller(key):
    entry = Seller.query.filter(Seller.client_key == key).first()
    if entry is not None:
        return True
    return False


def invalidate_filetype(file_type):
    if file_type in ['Text Files', 'Photo', 'Video', 'Contacts', 'Message']:
        return True
    return False


if __name__ == '__main__':
    app.run(host=proxy_ip, port=int(proxy_port), debug=True)
