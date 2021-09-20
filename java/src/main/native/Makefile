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

CC = gcc
CC_FLAGS = -O3 -Wall -Wno-unused-but-set-variable --pedantic -fPIC

BLISS_URL = http://www.tcs.hut.fi/Software/bliss
BLISS_VERSION = 0.50
JBLISS_VERSION = 0.50beta2

BLISS_DIR = ./bliss-$(BLISS_VERSION)
BLISS_SRCS += $(BLISS_DIR)/graph.cc
BLISS_SRCS += $(BLISS_DIR)/partition.cc
BLISS_SRCS += $(BLISS_DIR)/orbit.cc
BLISS_SRCS += $(BLISS_DIR)/uintseqhash.cc
BLISS_SRCS += $(BLISS_DIR)/heap.cc
BLISS_SRCS += $(BLISS_DIR)/timer.cc
BLISS_OBJS = $(addsuffix .o, $(basename $(BLISS_SRCS)))

all:: lib

.cc.o: $(BLISS_SRCS)
	$(CC) $(CC_FLAGS) $(JNI_INCLUDES) -I$(BLISS_DIR) -c -o $@ $<

$(BLISS_DIR):
	wget -O bliss-$(BLISS_VERSION).zip $(BLISS_URL)/bliss-$(BLISS_VERSION).zip
	rm -rf ./bliss-$(BLISS_VERSION)
	unzip bliss-$(BLISS_VERSION).zip
	rm bliss-$(BLISS_VERSION).zip

juvavum_graph_BlissGraph.cc:
	wget -O jbliss-0.50beta2.zip $(BLISS_URL)/jbliss-$(JBLISS_VERSION).zip
	rm -f juvavum_graph_BlissGraph.cc
	unzip -j jbliss-$(JBLISS_VERSION).zip jbliss-$(JBLISS_VERSION)/src-wrapper/fi_tkk_ics_jbliss_Graph.cc
	mv fi_tkk_ics_jbliss_Graph.cc juvavum_graph_BlissGraph.cc
	sed -i -e 's/fi_tkk_ics_jbliss_Graph/juvavum_graph_BlissGraph/g' juvavum_graph_BlissGraph.cc
	rm jbliss-$(JBLISS_VERSION).zip

juvavum_graph_BlissGraph.h:
	javac -h . ../java/juvavum/graph/BlissGraph.java

lib: $(BLISS_DIR) $(BLISS_OBJS) juvavum_graph_BlissGraph.h 
	$(CC) $(CC_FLAGS) $(JNI_INCLUDES) -I./$(BLISS_DIR) -c -o juvavum_graph_BlissGraph.o juvavum_graph_BlissGraph.cc
	$(CC) $(CC_FLAGS) $(JNI_INCLUDES) -I./$(BLISS_DIR) -shared -o ../../../target/$(JNI_LIB_NAME) $(BLISS_OBJS) ./juvavum_graph_BlissGraph.o
	
clean:
	rm -f bliss-0.50.zip
	rm -f jbliss-0.50beta2.zip
	rm -rf ./bliss-0.50
	rm -f ./juvavum_graph_BlissGraph.*