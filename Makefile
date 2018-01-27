.PHONY: all clean run

SRCS = $(wildcard *.java)

all: $(SRCS:.java=.class)

clean:
	rm -f *.class

run: all
	java Launcher

%.class : %.java
	javac $^ -Xdiags:verbose
