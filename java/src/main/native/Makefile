ifndef JAVA_HOME
	$(error JAVA_HOME not set)
endif
JNI_INCLUDES = -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -I$(JAVA_HOME)/include/darwin -I$(JAVA_HOME)/include/windows

JNI_LIB_NAME = libjbliss.so
ifeq ($(OS),Windows_NT)
    detected_OS := Windows
else
    detected_OS := $(shell uname)
endif
ifeq ($(detected_OS),Windows)
    JNI_LIB_NAME = jbliss.dll
endif
ifeq ($(detected_OS),Darwin)
    JNI_LIB_NAME = libjbliss.jnilib
endif


BLISS_DIR = ./bliss-0.50
BLISS_SRCS += $(BLISS_DIR)/graph.cc
BLISS_SRCS += $(BLISS_DIR)/partition.cc
BLISS_SRCS += $(BLISS_DIR)/orbit.cc
BLISS_SRCS += $(BLISS_DIR)/uintseqhash.cc
BLISS_SRCS += $(BLISS_DIR)/heap.cc
BLISS_SRCS += $(BLISS_DIR)/timer.cc
BLISS_OBJS = $(addsuffix .o, $(basename $(BLISS_SRCS)))

all:: lib

.cc.o: $(BLISS_SRCS)
	gcc -O3 -Wall --pedantic -fPIC $(JNI_INCLUDES) -I$(BLISS_DIR) -Wno-unused-but-set-variable -c -o $@ $<

bliss-0.50.zip:
	wget -O bliss-0.50.zip http://www.tcs.hut.fi/Software/bliss/bliss-0.50.zip
	rm -rf ./bliss-0.50
	unzip bliss-0.50.zip

headers:
	javac -h . ../java/juvavum/graph/BlissGraph.java

lib: bliss-0.50.zip $(BLISS_OBJS) headers ./juvavum_graph_BlissGraph.o
	gcc $(JNI_INCLUDES) -I./bliss-0.50 -shared -o ../../../target/$(JNI_LIB_NAME) $(BLISS_OBJS) ./juvavum_graph_BlissGraph.o
	
clean:
	rm -f bliss-0.50.zip
	rm -rf ./bliss-0.50
	rm -f ./juvavum_graph_BlissGraph.o
	rm -f ./juvavum_graph_BlissGraph.h