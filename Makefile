.PHONY: all clean run

SRCS = $(wildcard *.java)

all:
	javac *.java -Xdiags:verbose
clean:
	rm -f *.class

run: all
	java Launcher
