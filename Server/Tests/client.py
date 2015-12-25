import sys
import zmq
import json

def mogrify(topic, msg):
    """ json encode the message and prepend the topic """
    return topic + ' ' + json.dumps(msg)

def demogrify(topicmsg):
    """ Inverse of mogrify() """
    json0 = topicmsg.find('{')
    topic = topicmsg[0:json0].strip()
    msg = topicmsg[json0:].strip()
    return topic, msg


context = zmq.Context()
subscriber = context.socket(zmq.SUB)
subscriber.connect("tcp://10.42.0.1:5556")
topicfilter = 'Food'
subscriber.setsockopt(zmq.SUBSCRIBE, topicfilter)

while True:
    message = subscriber.recv()
    topic, finalmessage = demogrify(message)
    d = json.loads(finalmessage)
    uuid = d['Uuid']
    requestId = d['RequestId']
    topic = d['Topic']
    message = d['Message']

    d1 = json.loads(message)
    intent_description = d1['Intent_Description']
    deadline = d1['Deadline']
    print topic
    print uuid
    print requestId
    print topic
    print intent_description
    print deadline

