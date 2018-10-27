JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Cliente.java\
	ClientMulticast.java\
	DataHandler.java\
	Interface.java\
	MultisocketServer.java\
	Servidor.java\
	SocketClient.java\
	SocketServer.java

default: server

server: $(CLASSES:.java=.class)

clean:
	$(RM) *.class