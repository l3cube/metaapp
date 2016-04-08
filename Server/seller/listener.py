from datetime import datetime
import zmq
import time
import json
from sqlalchemy import Column, ForeignKey, Integer, String, DateTime
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.orm import scoped_session
import thread

Base = declarative_base()

# Create an engine that stores data in the local directory's
engine = create_engine('sqlite:////tmp/MetaAppSeller.db')
Session_factory = sessionmaker(bind=engine)
Session = scoped_session(Session_factory)

class UserRequests:
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


def mogrify(topic, msg):
    """ json encode the message and prepend the topic """
    return topic + ' ' + json.dumps(msg)


def demogrify(topicmsg):
    """ Inverse of mogrify() """
    json0 = topicmsg.find('{')
    topic = topicmsg[0:json0].strip()
    msg = json.loads(topicmsg[json0:])
    return topic, msg


zmq_port = "5556"
zmq_ip = "tcp://10.42.0.1:"
zmq_filter = "Food"


def config(ip, port, filt):
    global zmq_ip
    global zmq_port
    global zmq_filter
    zmq_ip = ip
    zmq_port = port
    zmq_filter = filt


def cofig(filt):
    global zmq_filter
    zmq_filter = filt


def listen():
    # Socket to talk to server
    context = zmq.Context()
    socket = context.socket(zmq.SUB)
    socket.connect(zmq_ip + zmq_port)
    # Subscribe to filter Food
    socket.setsockopt(zmq.SUBSCRIBE, zmq_filter)

    while True:
        time.sleep(1)
        msg = socket.recv()
        topic, messagedata = demogrify(msg)
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
        write_db(uuid, reqid, deadline, topic, intent_desc)
        print "Record added"


def write_db(uuid, reqid, deadline, topic, intent_desc):
    session = Session()
    new_entry = UserRequests(uuid, reqid, deadline, topic, intent_desc)
    session.add(new_entry)
    session.commit()
    Session.remove()


def run():
    try:
        thread.start_new_thread(listen, ())
    except:
        print 'Unable to start thread'
