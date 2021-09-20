#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include "uthash.h"

/*
 * Count the number of game positions in CRAM and DJUV using a bitmap
 * Usage: positions width height
 */

const unsigned char h_d = 0b11; // horizontal domino
unsigned char v_d; // vertical domino (value depends on width)

struct board {
  int key;
  UT_hash_handle hh;
};

struct board *positions(int h, int w, long long b, struct board *map) {
  struct board *s;
  HASH_FIND_INT(map, &b, s);
  if (s == NULL) {
    s = malloc(sizeof(struct board));
    s -> key = b;
    HASH_ADD_INT(map, key, s);
    for (int i=0; i<h*w; i++) {
      if (i%w!=w-1 && !(b & h_d << i)) {
        map = positions(h, w, b | h_d << i, map);
      }
      if (i<w*(h-1) && !(b & (v_d << i))) {
        map = positions(h, w, b | v_d << i, map);
      }
    }
  }
  return map;
}

int main(int argc, char *argv[]) {
  if (argc != 3) {
    printf("usage: positions height width\n");
    return 1;
  }
  int h = atoi(argv[1]);
  int w = atoi(argv[2]);
  if (h * w >= 8 * sizeof(long long)) {
    printf("Board is too large\n");
    return 1;
  }
  struct board *map = NULL;
  v_d = 1 | 1<<w;
  map = positions(h,w, 0, map);
  printf("%u\n", HASH_COUNT(map));
  return 0;
}
