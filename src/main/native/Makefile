#
# Modify these two flags according to your system
#
# The directory where jni.h is to be found
JNI_H_PATH = /Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home/include
JNI_MD_H_PATH = /Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home/include/darwin

JNI_INCLUDE = -I$(JNI_H_PATH) -I$(JNI_MD_H_PATH)

# Where the true bliss is to be found
BLISS_DIR = ./bliss-0.50
# bliss sources, objects, and compiler options
BLISS_SRCS += $(BLISS_DIR)/graph.cc
BLISS_SRCS += $(BLISS_DIR)/partition.cc
BLISS_SRCS += $(BLISS_DIR)/orbit.cc
BLISS_SRCS += $(BLISS_DIR)/uintseqhash.cc
BLISS_SRCS += $(BLISS_DIR)/heap.cc
BLISS_SRCS += $(BLISS_DIR)/timer.cc
BLISS_OBJS = $(addsuffix .o, $(basename $(BLISS_SRCS)))
BLISS_CC = g++
BLISS_CCFLAGS = -O3 -Wall --pedantic -fPIC

# The wrapper sources, objects, and compiler options
WRAPPER_DIR = ./wrapper
WRAPPER_SRCS += $(WRAPPER_DIR)/juvavum_graph_BlissGraph.cc
WRAPPER_OBJS = $(addsuffix .o, $(basename $(WRAPPER_SRCS)))
WRAPPER_CC = g++
WRAPPER_CCFLAGS = -O3 -Wall -fPIC
WRAPPER_INCLUDES = $(JNI_INCLUDE) -I$(BLISS_DIR)

#
CLASSPATHOPT = -classpath ../../../target/classes

all:: lib

gmp:    LIBS += -lgmp
gmp:    BLISS_CCFLAGS += -DBLISS_USE_GMP

.SUFFIXES: .java .class

.java.class: $@
	javac $(CLASSPATHOPT) $<

$(WRAPPER_DIR)/%.o: $(WRAPPER_DIR)/%.cc
	$(WRAPPER_CC) $(WRAPPER_CCFLAGS) $(WRAPPER_INCLUDES) -c -o $@ $<

.cc.o: $(BLISS_SRCS)
	$(BLISS_CC) $(BLISS_CCFLAGS) -c -o $@ $<

bliss: ./bliss-0.50.zip
	rm -rf ./bliss-0.50
	unzip bliss-0.50.zip

headers: $(JAVA_CLASSFILES)
	javac -h . ../java/juvavum/graph/BlissGraph.java

lib: bliss $(BLISS_OBJS) headers $(WRAPPER_OBJS)
	$(WRAPPER_CC) $(JNI_INCLUDE) -I$(BLISS_DIR) -shared -o ../../../target/libjbliss.jnilib $(BLISS_OBJS) $(WRAPPER_OBJS) $(LIBS)

jbliss: lib JBliss.class