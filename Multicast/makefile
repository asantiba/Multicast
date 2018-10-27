JFLAGS = -g
JC = javac
RM = 
.SUFFIXES: .java .class
.java.class:
        $(JC) $(JFLAGS) $*.java

CLASSES = \
        Servidor.java \
        Cliente.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
        $(RM) *.class