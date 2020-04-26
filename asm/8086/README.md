# 8086 16-bit program to pretty-print 4x4 boards

# Explanation and usage

Each field of a 4x4 grid is represented by a power of 2:

    2^0  2^1  2^2  2^3
    2^4  2^5  2^6  2^7
    2^8  2^9  2^10 2^11
    2^12 2^13 2^14 2^15

The sum of these powers is the numerical representation of the board.

This program prints the board represented by the number passed as its argument.

For example, `board 1000` will print:


    - - - X
    - X X X
    X X - -
    - - - -

This is because 1000 = 2^3 + 2^5 + 2^6 + 2^7 + 2^8 + 2^9.

# Compile and link

Tested on [DOSBox](https://www.dosbox.com) 0.74-3 using [MASM 5.00](https://winworldpc.com/product/macro-assembler/5x):

    masm board.asm
    link board.obj
