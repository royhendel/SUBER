import socket
import time
import select
import threading
import json
import pyrebase
import ast

# Server Code

# Server Network Information
doc = open("networkconfig.txt", "r")
IP = doc.readline()[:-1:]
PORT = int(doc.readline())
doc.close()

class Server(object):
    def __init__(self, ip, port):
        self.ip = ip
        self.port = port
        self.mac_room_dic = {}
        self.waitreqlist = []
        self.activereqlist = []
        self.distancedic = {}
        self.saniterdic = {}
        self.reqdic = {}
        self.current_mac_dic = {}

    def start(self):
        try:
            # Giving Server the Room to router and router to router information
            doc = file("Rooms_Mac_Dic.txt", "r")
            dicstr = doc.read()
            self.mac_room_dic = ast.literal_eval(dicstr)
            doc.close()
            doc = file("distancestxt.txt", "r")
            dicstr = doc.read()
            self.distancedic = ast.literal_eval(dicstr)
            doc.close()
            # Updating Database for Rooms
            realroomlist = []
            for roomlist in list(self.mac_room_dic.itervalues()):
                for room in roomlist:
                    if room not in realroomlist:
                        realroomlist.append(room)
            db.child("Rooms").set(list(realroomlist))
            # Setting up server
            print('server starting up on ip %s port %s' % (ip, port))
            self.handlewaitlist()
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            sock.bind((ip, port))
            sock.listen(1)
            while True:
                # start thread for every connection attempt
                print('waiting for a new client')
                clientSocket, client_address = sock.accept()
                print('new client entered')
                msg = clientSocket.recv(1024)
                print('received message: %s' % msg.decode())
                msglist = msg.split(" ")
                # If the client is a Doctor start the doctor thread, otherwise start the Saniter thread
                # in terms of code flow, go to either doc_thread_func or san_thread_func
                if msg[0] == "D":
                    self.handledoc(clientSocket)
                elif msg[0] == "S":
                    msglist = msg.split(" ")
                    self.saniterdic[clientSocket] = msglist[1]
                    self.current_mac_dic[clientSocket] = msglist[2]
                    self.reqdic[clientSocket] = "False"
                    self.handlesan(clientSocket)
                else:
                    print "msg failed. it was: "
        # If the socket fails to connect, this catches and discards it instead of crashing
        except socket.error as e:
            print(e)

    # Thread Handles
    def handledoc(self, clientSock):
        if True:
            thread = threading.Thread(target=self.doc_thread_func, args=(clientSock,))
            thread.start()

    def handlesan(self, clientSock):
        if True:
            thread = threading.Thread(target=self.san_thread_func, args=(clientSock,))
            thread.start()

    def handlereq(self, req, unavailable):
        if True:
            thread = threading.Thread(target=self.req_thread_func, args=(req, unavailable, ))
            thread.start()

    def req_thread_func(self, req, unavailable):
        while not self.current_mac_dic:
            print "No Saniters available"
            time.sleep(3)
        closesock = self.get_closest_san(req["Patients_Room"], unavailable)
        print "close sock done: " + str(closesock)
        closesan = self.saniterdic[closesock]
        db.child("users").child(closesan).child("Requests").child(req["ID"]).set(req)
        self.reqdic[closesock] = req["ID"]
        print "changed reqdic: " + self.reqdic[closesock]
        gotupdate = False
        while not gotupdate:
            if closesock in self.reqdic:
                if self.reqdic[closesock] == req["ID"]:
                    time.sleep(2)
                    print "waiting"
                else:
                    gotupdate = True
            else:
                print "req wasn't done by this dude and also he disconnected"
                self.handlereq(req, unavailable)
                exit()
        if self.reqdic[closesock] == "done":
            print "request completed"
            db.child("users").child(closesan).child("Requests_completed").child(req["ID"]).set(req)
            db.child("users").child(closesan).child("Requests_completed").child(req["ID"]).child(("Status")).set("Completed")
            db.child("users").child(closesan).child("Requests").child(req["ID"]).remove()
            db.child("users").child(req["Doctor"]).child("Requests").child(req["ID"]).child("Status").set("Completed")
            self.reqdic[closesock] = "False"
            self.activereqlist.remove(req)
            self.current_mac_dic[closesock] = closesan
        elif self.reqdic[closesock] == "nope":
            print "req wasn't done by this dude"
            self.reqdic[closesock] = "False"
            self.current_mac_dic[closesock] = closesan
            if closesock not in unavailable:
                unavailable.append(closesock)
            self.handlereq(req, unavailable)
        exit()

    # Doctor function. Receive request, verify its formatted correctly and add it to request queue
    def doc_thread_func(self, clientSock):
        while True:
            req = clientSock.recv(1024)
            print "req: " + req
            if self.is_json(req):
                request = json.loads(req)
                db.child("users").child(request["Doctor"]).child("Requests").child(request["ID"]).set(request)
                self.waitreqlist.append(request)
            else:
                print "req failed: " + req
            self.disconnect(clientSock)
        pass

    # Saniter function. way more complex then doctor. go through it linearly (row row, no jumps)
    def san_thread_func(self, clientSock):
        # While Saniter is connected keep this going
        while True:
            # while saniter has no requests for him, periodically check that he is connected without blocking anything (select)
            while self.reqdic[clientSock] in ["False", "done", "nope"]:
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
                # If san is here that means he has a new request.
                print "recieved id: " + self.reqdic[clientSock]
                check = True
                # While Saniter hasn't responded: check that still connected(without blocking)
                while check:
                    rlist, wlist, xlist = select.select([clientSock], [clientSock], [clientSock])
                    # if Readable (San sent a Message) recieve message than move on.
                    if rlist:
                        print "rlist fine"
                        msg = clientSock.recv(1024)
                        check = False
                    # If writeable is checking that san still connected.
                    # if he disconnects, update the request assignment dictionary and kill this saniter thread
                    if wlist:
                        "wlist fine"
                        try:
                            clientSock.send("hmm")
                        except Exception as e:
                            print e
                            print "in weird place 2"
                            self.reqdic[clientSock] = "nope"
                            self.disconnect(clientSock)
                    time.sleep(1)
                print msg
                # react according to the message.
                # If client clicks decline or times out update request assignment dictionary.
                # If accepts wait for finished
                if msg == "im doing it":
                    # can change this to check and add change here support
                    msg2 = clientSock.recv(1024)
                    print "msg 2: " + msg2
                    if msg2 == "done":
                        self.reqdic[clientSock] = "done"
                if msg == "im not doing it":
                    self.reqdic[clientSock] = "nope"
                print "reqdic after completion: " + self.reqdic[clientSock]
            #
            except Exception as e:
                print e
                print "entered exeption"
                self.reqdic.pop(clientSock)
                self.disconnect(clientSock)

    def disconnect(self, clientSock):
        if clientSock in self.saniterdic:
            self.saniterdic.pop(clientSock)
        if clientSock in self.reqdic:
            self.reqdic.pop(clientSock)
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

    def get_closest_san(self, room, unavailable):
        for router in list(self.mac_room_dic.iterkeys()):
            if room in self.mac_room_dic[router]:
                reqrouter = router
                print "router: " + reqrouter
        distance = 50
        closestsan = False
        for sanitersock in self.current_mac_dic:
            if reqrouter == self.current_mac_dic[sanitersock]:
                if sanitersock not in unavailable:
                    self.current_mac_dic.pop(sanitersock)
                    return sanitersock
            print self.saniterdic[sanitersock] + "'s distance is: " + str(self.saniterdistance(self.current_mac_dic[sanitersock], reqrouter))
            if self.saniterdistance(self.current_mac_dic[sanitersock], reqrouter) < distance:
                if sanitersock not in unavailable:
                    closestsan = sanitersock
                    distance = self.saniterdistance(self.current_mac_dic[sanitersock], reqrouter)
            if not closestsan:
                closestsan = list(self.current_mac_dic.iterkeys())[0]
                print "closest san: " + str(closestsan)
        self.current_mac_dic.pop(closestsan)
        return closestsan

    def saniterdistance(self, sanitermac, reqrouter):
        for key in list(self.distancedic.iterkeys()):
            if sanitermac in key and reqrouter in key:
                return self.distancedic[key]

    def handlewaitlist(self):
        if True:
            thread = threading.Thread(target=self.wait_thread_func, args=())
            thread.start()

    def wait_thread_func(self):
        while True:
            if len(self.activereqlist) < len(self.saniterdic) and len(self.waitreqlist) > 0:
                print "in wait req is: " + str(self.waitreqlist[0])
                self.activereqlist.append(self.waitreqlist[0])
                self.handlereq(self.waitreqlist[0], [])
                self.waitreqlist.remove(self.waitreqlist[0])

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
    port = PORT
    s = Server(ip, port)
    s.start()