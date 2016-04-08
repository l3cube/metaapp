from datetime import datetime
import json
import requests
import zmq
import time
import threading
from sqlalchemy import Column, ForeignKey, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm import scoped_session

server_ip = '10.42.0.1'
proxy_ip = '10.42.0.1'
proxy_port = '5001'

proxy_final = proxy_ip+":"+proxy_port
Base = declarative_base()

# Create an engine that stores data in the local directory's
engine = create_engine('sqlite:////tmp/MetaAppSeller.db')
Session_factory = sessionmaker(bind=engine)
Session = scoped_session(Session_factory)


class UserRequests(Base):
    __tablename__ = 'user_requests'
    uuid = Column(String(200), primary_key=True)
    reqid = Column(String(200), primary_key=True)
    deadline = Column(DateTime)
    topic = Column(String(1000))
    intentdesc = Column(String(1000))

    def __init__(self, uuid, reqid, deadline, topic, intentdesc):
        self.uuid = uuid
        self.reqid = reqid
        self.deadline = deadline
        self.topic = topic
        self.intentdesc = intentdesc

    def __repr__(self):
        return '<Requests %r>' % self.uuid


class Listener(threading.Thread):
    zmq_port = "5556"
    zmq_ip = "tcp://"+server_ip+":"
    zmq_filter = []

    def __init__(self, filt, ip="tcp://"+server_ip+":", port="5556"):
        threading.Thread.__init__(self)
        self.zmq_filter = filt
        self.zmq_ip = ip
        self.zmq_port = port

    def mogrify(self, topic, msg):
        """ json encode the message and prepend the topic """
        return topic + ' ' + json.dumps(msg)

    def demogrify(self, topicmsg):
        """ Inverse of mogrify() """
        json0 = topicmsg.find('{')
        topic = topicmsg[0:json0].strip()
        msg = json.loads(topicmsg[json0:])
        return topic, msg

    def listen(self):
        # Socket to talk to server
        context = zmq.Context()
        socket = context.socket(zmq.SUB)
        socket.connect(self.zmq_ip + self.zmq_port)
        # Subscribe to filter Food
        for filt in self.zmq_filter:
            socket.setsockopt(zmq.SUBSCRIBE, filt)

        while True:
            time.sleep(1)
            msg = socket.recv()
            topic, messagedata = self.demogrify(msg)
            dict_json = messagedata
            print dict_json
            uuid = dict_json['Uuid']
            reqid = dict_json['RequestId']
            message = dict_json['Message']
            dict1_json = json.loads(message)
            deadline_s = dict1_json['Deadline']
            dict_deadline = json.loads(deadline_s)
            deadline = dict_deadline['Date']
            # Not considering time here. Add Code for it.
            deadline = datetime.strptime(deadline, "%Y-%m-%d")
            intent_desc = dict1_json['Intent_Description']
            topic = dict_json['Topic']
            session = Session()
            new_entry = UserRequests(uuid, reqid, deadline, topic, intent_desc)
            session.add(new_entry)
            session.commit()
            Session.remove()
            print "Record added"

    def run(self):
        self.listen()


# Perform User Registration and return a seller client key to be used for each further requests
# Name and handle should be unique otherwise an error will occur
def register(name, filters, handle, endpoint):
    cred = {}
    cred['Name'] = name
    cred['Filter'] = filters
    cred['Handle'] = handle
    cred['Endpoint'] = endpoint
    result = requests.post('http://'+proxy_final+'/register/seller/', data=json.dumps(cred))
    return result


# Returns an json array of user requests in json format
def get_requests(filters):
    session = Session()
    filters = filters.split(" ")
    reqs = []
    for topic in filters:
        entries = session.query(UserRequests).filter(UserRequests.topic == topic).all()
        if entries:
            for i in entries:
                reqs.append(i)
    Session.remove()
    user_requests = []
    for i in reqs:
        req = {}
        req['Uuid'] = i.uuid
        req['RequestId'] = i.reqid
        req['Topic'] = i.topic
        req['Intent'] = i.intentdesc
        req['Deadline'] = i.deadline.strftime("%Y-%m-%d")
        user_requests.append(req)
    return json.dumps(user_requests)


# Returns an json array of user requests in json format for a particular user
# Perform exception handling for incorrect uuid
def get_requests_user(uuid, filters):
    session = Session()
    filters = filters.split(" ")
    reqs = []
    for topic in filters:
        reqs.append(session.query(UserRequests).filter(UserRequests.topic == topic).all())
    Session.remove()
    user_requests = []
    for i in reqs:
        req = {}
        req['Uuid'] = i.uuid
        req['RequestId'] = i.reqid
        req['Topic'] = i.topic
        req['Intent'] = i.intentdesc
        req['Deadline'] = i.deadline.strftime("%Y-%m-%d")
        user_requests.append(req)
    return json.dumps(user_requests)


# Send a reply where the parameters are UserId, RequestId, Topic and Offer in a Json format
# The compulsory fields in offer are Service description, Cost and Time to complete
# Add response code in proxy_app for different responses
def make_reply(uuid, reqid, topic, offer, client_key):
    reply = {}
    reply['Uuid'] = uuid
    reply['RequestId'] = reqid
    reply['Topic'] = topic
    reply['Offer'] = offer
    reply['Key'] = client_key
    result = requests.post('http://'+proxy_final+'/receive/offer/', data=json.dumps(reply))
    return result


# Send an Access request where the parameters are File type and Client key
# The proxy will store the pending requests and then send them the app when fetched
# Json with field either Status or Error
def access_request(file_type, client_key):
    req = {}
    req['FileType'] = file_type
    req['Key'] = client_key
    result = requests.post('http://'+proxy_final+'/namespace/access/', data=json.dumps(req))
    return result


# Send an List request where the parameters are File type and Client key
# The proxy will fetch the list of files and send back
# A json array containing json objects with fields uuid and fileid
def list_request(file_type, client_key):
    req = {}
    req['File'] = file_type
    req['Key'] = client_key
    result = requests.post('http://'+proxy_final+'/namespace/list/', data=json.dumps(req))
    return result


# Accepts a filter to configure listener and start listening
def start_listener_service(filt):
    l = Listener(filt)
    l.listen()


def init():
    Base.metadata.drop_all(engine)
    Base.metadata.create_all(engine)


if __name__ == '__main__':
    start_listener_service(["Clothes","Electronics","Food","Books","Shoes","Bags","Watches","Taxi"])



