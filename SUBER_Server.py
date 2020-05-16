import socket
import time
import select
import threading
import json
import pyrebase
import Requests

IP = '10.0.0.16'
PORT = 8820

"""
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server_socket.bind((IP, PORT))
server_socket.listen(1)
client_socket, address = server_socket.accept()
print "Connected by: ", address
"""


class Server(object):
    def __init__(self, ip, port):
        self.ip = ip
        self.port = port
        self.doctorlist = []
        self.clientlist = []
        self.saniterdic = {}
        self.reqdic = {}
        self.current_mac_dic = {}


    def start(self):
        try:
            print('server starting up on ip %s port %s' % (ip, port))
            # Create a TCP/IP socket
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.bind((ip, port))
            sock.listen(1)
            while True:
                print('waiting for a new client')
                clientSocket, client_address = sock.accept()
                print('new client entered')
                # send receive example
                msg = clientSocket.recv(1024)
                print('received message: %s' % msg.decode())
                # implement here your main logic
                self.clientlist.append(clientSocket)
                if msg[0] == "D":
                    self.doctorlist.append(clientSocket)
                    self.handledoc(clientSocket)
                elif msg[0] == "S":
                    msglist = msg.split(" ")
                    self.saniterdic[clientSocket] = msglist[1]
                    self.current_mac_dic[clientSocket] = msglist[2]
                    self.reqdic[clientSocket] = "False"
                    print self.saniterdic
                    self.handlesan(clientSocket)
                else:
                    print "msg failed. it was: "

        except socket.error as e:
            print(e)

    def handledoc(self, clientSock):
        if True:
            print "handledoc print 1"
            thread = threading.Thread(target=self.doc_thread_func, args=(clientSock,))
            thread.start()

    def handlesan(self, clientSock):
        if True:
            print "handlesan print 1"
            thread = threading.Thread(target=self.san_thread_func, args=(clientSock,))
            thread.start()

    def handlereq(self, req):
        if True:
            print "handlereqprint 1"
            thread = threading.Thread(target=self.req_thread_func, args=(req,))
            thread.start()

    def req_thread_func(self, req):
        print "need to filter sans"
        reqcheck = Requests.Request(req["Doctor"], req["Patients_Room"])
        print reqcheck
        print self.saniterdic
        closesock = list(self.current_mac_dic.iterkeys())[0]
        closesan = self.saniterdic[closesock]
        db.child("users").child(closesan).child("Requests").child(req["ID"]).set(req)
        self.reqdic[closesock] = req["ID"]
        print "changed reqdic: "
        while self.reqdic[closesock] == req["ID"]:
            time.sleep(2)
            print "waiting"
        if self.reqdic[closesock] == "done":
            print "request completed"
            db.child("users").child(closesan).child("Requests_compeleted").child(req["ID"]).set(req)
            db.child("users").child(closesan).child("Requests").child(req["ID"]).remove()
            self.reqdic[closesock] = "False"
        elif self.reqdic[closesock] == "nope":
            print "req wasn't done by this dude"
            self.reqdic[closesock] = "False"
            self.handlereq(req)
        exit()

    def doc_thread_func(self, clientSock):
        while True:
            print "doc_thread_func print 1"
            req = clientSock.recv(1024)
            print "req: " + req
            if req == "logout":
                self.logout(clientSock)
            elif self.is_json(req):
                request = json.loads(req)
                #db.child("users").child(request["Doctor"]).child("Requests").child(req["ID"]).set(request)
                self.handlereq(request)
            else:
                print "req failed: " + req
        pass

    def san_thread_func(self, clientSock):
        print "san_thread_func print 1"
        while True:
            while self.reqdic[clientSock] == "False":
                rlist, wlist, xlist = select.select([clientSock], [clientSock], [clientSock])
                if wlist[0]:
                    try:
                        clientSock.send("hmm")
                    except Exception as e:
                        print e
                        print "in weird place"
                        self.disconnect(clientSock)
                time.sleep(1)
            try:
                print "recieved id: " + self.reqdic[clientSock]
                msg = clientSock.recv(1024)
                print msg
                if msg == "im doing it":
                    msg2 = clientSock.recv(1024)
                    print "msg 2: " + msg2
                    if msg2 == "done":
                        self.reqdic[clientSock] = "done"
                if msg == "logout":
                    self.logout()
                if msg == "im not doing it":
                    self.reqdic[clientSock] = "nope"
                print "well it reaches here?"
            except Exception as e:
                print e
                print "entered exeption"
                self.reqdic[clientSock] = "nope"
                self.disconnect(clientSock)

    def disconnect(self, clientSock):
        if clientSock in self.clientlist:
            self.clientlist.remove(clientSock)
        if clientSock in self.saniterdic:
            self.saniterdic.pop(clientSock)
        if clientSock in self.reqdic:
            print "yeet"
        if clientSock in self.current_mac_dic:
            self.current_mac_dic.pop(clientSock)
        print "logout"
        clientSock.close()
        exit()

    def is_json(self, myjson):
        try:
            json_object = json.loads(myjson)
        except ValueError as e:
            return False
        return True


if __name__ == '__main__':
    config = {
        "apiKey": "AIzaSyBz-WGWsj6J8Hq-KOL9JRgaE0u9X7T7WYE",
        "authDomain": "suberdatabase.firebaseapp.com",
        "databaseURL": 'https://suberdatabase.firebaseio.com',
        "projectId": "suberdatabase",
        "storageBucket": "suberdatabase.appspot.com",
        "messagingSenderId": "153977190202",
        "appId": "1:153977190202:web:f9f5047bd267f6c929c203",
        "measurementId": "G-FGQTD4QKTC"
    }
    firebase = pyrebase.initialize_app(config)
    db = firebase.database()
    ip = IP
    port = 8820
    s = Server(ip, port)
    s.start()