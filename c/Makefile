all: array.c bitset.c uthash.h
	gcc -o array array.c
	gcc -o bitset bitset.c

uthash.h:
	wget https://raw.githubusercontent.com/troydhanson/uthash/master/src/uthash.h

clean:
	rm -f array bitset uthash.h
