.PHONY: all clean run

SRCS = $(wildcard *.java)

all:
	javac *.java
clean:
	rm -f *.class

run: all
	java Launcher
