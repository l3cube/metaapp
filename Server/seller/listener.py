import sys
import zmq
import time
import requests
import json


def mogrify(topic, msg):
    """ json encode the message and prepend the topic """
    return topic + ' ' + json.dumps(msg)


def demogrify(topicmsg):
    """ Inverse of mogrify() """
    json0 = topicmsg.find('{')
    topic = topicmsg[0:json0].strip()
    msg = json.loads(topicmsg[json0:])
    return topic, msg


port = "5556"

# Socket to talk to server
context = zmq.Context()
socket = context.socket(zmq.SUB)

socket.connect("tcp://192.168.43.18:%s" % port)


# Subscribe to filter Food
topicfilter = "Food"
socket.setsockopt(zmq.SUBSCRIBE, topicfilter)

while(1):
    time.sleep(1)
    msg = socket.recv()
    topic, messagedata = demogrify(msg)
    requests.post('http://192.168.43.81:8787/seller-display/', data=json.dumps(messagedata))
