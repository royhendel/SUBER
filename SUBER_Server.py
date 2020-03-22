import socket
import time
import threading
import json

IP = '10.0.54.56'
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
        self.clientlist = []
        self.saniterlist = []
        self.doctorlist = []

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
                self.clientlist.append(sock)
                print self.clientlist
                if msg[0] == "D":
                    self.doctorlist.append(sock)
                else:
                    self.clientlist.append(sock)
                self.handleClient(clientSocket)

        except socket.error as e:
            print(e)
    def handleClient(self, clientSock):
        if True:
            print clientSock
            thread = threading.Thread(target=self.thread_func, args=(clientSock,))
            thread.start()

    def thread_func(self, clientSock):
        noreq = true
        if clientSock in self.doctorlist
            noreq = true
            while noreq:
                clientSock.recv(1024)
                #JSONunload, make sure it is req format
                noreq = false
            #check current room, find close routers, find close saniters.
            pass
            #clientSock.send(hash + " " + str(self.startpoint) + " " + str(self.endpoint))
            #self.startpoint += 1000000
            #response = clientSock.recv(1024)
            #if response.__len__() == 10:
                #self.found = True
                #self.end(response)
            #if self.startpoint == self.endpoint:
            #   self.end("the correct num for the hash is not in this range")

    def end(self, msg):
        print "And the number is.......: " + msg + "!"
        for sock in self.socklist:
            sock.close()
        exit()
# implement your logic here
# E.G
# create a thread to handle this client connection and return
# to handle more inside connections
#in the new thread:
# send to client the hash and a range of numbers to check
# if a client found the password close all connections and quit
# else give the client another range of numbers to check
if __name__ == '__main__':
    ip = IP
    port = 8820
    s = Server(ip,port)
    s.start()