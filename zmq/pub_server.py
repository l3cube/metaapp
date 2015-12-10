import zmq
import random
import sys
import json
import time

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
if len(sys.argv) > 1:
    port =  sys.argv[1]
    int(port)

context = zmq.Context()
socket = context.socket(zmq.PUB)
socket.bind("tcp://0.0.0.0:%s" % port)
message = '{ "topic" : "clothes men shoes" , "message" : "messagedata" }'
while True:
	arr = json.loads(message)
	topic = arr['topic']
	print topic 
	messagedata = arr['message']
	print messagedata
	socket.send_string(mogrify(topic,messagedata))
	time.sleep(1)
