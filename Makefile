.PHONY: all clean run

SRCS = $(wildcard *.java)

all: $(SRCS:.java=.class)

clean:
	rm *.class

run: all
	java Main

%.class : %.java
	javac $^ -Xdiags:verbose
