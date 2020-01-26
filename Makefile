.PHONY: all clean run

JAVA ?= java
JAVAC ?= javac

SRCS = $(wildcard *.java)

all:
	$(JAVAC) *.java -Xdiags:verbose -Xlint:all
clean:
	rm -f *.class

run: all
	$(JAVA) Launcher
