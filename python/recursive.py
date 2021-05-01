#!/bin/python3

from argparse import ArgumentParser
from copy import deepcopy

# Count the number of game positions in CRAM and DJUV
# Usage: python recursive.py width height

def tilings(b, children):
    children.add(key(b))
    for j in range (0, len(b[0])):
        for i in range(0, len(b)-1):
            if not b[i][j] and not b[i+1][j]:
                b1 = deepcopy(b)
                b1[i][j] = True
                b1[i+1][j] = True
                children.update(tilings(b1, children))
    for i in range(0, len(b)):
        for j in range(0, len(b[0])-1):
            if not b[i][j] and not b[i][j+1]:
                b1 = deepcopy(b)
                b1[i][j] = True
                b1[i][j+1] = True
                children.update(tilings(b1, children))
    return children
    
def key(b):
    key = 0
    w = len(b)
    h = len(b[0])
    for i in range(0, w*h):
        if b[i%w][i//w]:
            key += (1 << i)
    return key

def main():
    parser = ArgumentParser()
    parser.add_argument("height", type=int)
    parser.add_argument("width", type=int)
    args = parser.parse_args()
    board = [[False for i in range(args.height)] for j in range(args.width)]
    print(len(tilings(board, set())))
    
if __name__ == "__main__":
    main()
