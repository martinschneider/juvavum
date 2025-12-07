# Makefile for NASM board visualizer

ASM = nasm
LD = ld
TARGET = board
SRC = board.asm
OBJ = board.o

# Architecture-specific settings
ARCH := $(shell uname -m)

ifeq ($(ARCH),x86_64)
    ASMFLAGS = -f elf64
    LDFLAGS = -m elf_x86_64
else
    ASMFLAGS = -f elf32
    LDFLAGS = -m elf_i386
endif

all: $(TARGET)

$(TARGET): $(OBJ)
	$(LD) -s -N $(LDFLAGS) -o $(TARGET) $(OBJ)

$(OBJ): $(SRC)
	$(ASM) $(ASMFLAGS) $(SRC) -o $(OBJ)

clean:
	rm -f $(OBJ) $(TARGET)

test: $(TARGET)
	@echo "Testing 4x4 board (value: 1000):"
	./$(TARGET) 4 4 1000
	@echo ""
	@echo "Testing 5x5 full board (all 25 cells filled):"
	./$(TARGET) 5 5 33554431
	@echo ""
	@echo "Testing 3x3 cross pattern:"
	./$(TARGET) 3 3 186
	@echo ""
	@echo "Testing 8x8 checkerboard pattern:"
	./$(TARGET) 8 8 12273903644374837845

install: $(TARGET)
	install -m 755 $(TARGET) /usr/local/bin/

.PHONY: all clean test install
