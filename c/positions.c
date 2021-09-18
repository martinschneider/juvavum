#include <stdio.h>
#include <stdbool.h>
#include <string.h>
#include "uthash.h"

/*
 * Count the number of game positions in CRAM and DJUV
 * Usage: positions width height
 */

struct board {
  int key;
  UT_hash_handle hh;
};

int key(int h, int w, bool b[h][w]) {
  int key = 0;
  for (int i = 0; i < w * h; i++) {
    key += b[i % h][i / h] << i;
  }
  return key;
}

struct board *tilings(int h, int w, bool b[][w], struct board *positions) {
  int bin_key = key(h, w, b);
  struct board *s;
  HASH_FIND_INT(positions, & bin_key, s);
  if (s == NULL) { // new position
    s = malloc(sizeof(struct board));
    s -> key = bin_key;
    HASH_ADD_INT(positions, key, s);
    for (int j = 0; j < w; j++) {
      for (int i = 0; i < h - 1; i++) {
        if (!b[i][j] && !b[i + 1][j]) {
          bool b1[h][w];
          memcpy(&b1[0][0], &b[0][0], h * w * sizeof(bool));
          b1[i][j] = 1;
          b1[i + 1][j] = 1;
          positions = tilings(h, w, b1, positions);
        }
      }
    }
  }
  for (int i = 0; i < h; i++) {
    for (int j = 0; j < w - 1; j++) {
      if (!b[i][j] && !b[i][j + 1]) {
        bool b1[h][w];
        memcpy(&b1[0][0], &b[0][0], h * w * sizeof(bool));
        b1[i][j] = 1;
        b1[i][j + 1] = 1;
        positions = tilings(h, w, b1, positions);
      }
    }
  }
  return positions;
}

int main(int argc, char *argv[]) {
  if (argc != 3) {
    printf("usage: positions height width\n");
    return 1;
  }
  int h = atoi(argv[1]);
  int w = atoi(argv[2]);

  // initialize empty board
  bool b[h][w];
  memset(b, 0, h * w * sizeof(bool));

  // store all possible positions in a hash table
  struct board *positions = NULL;
  positions = tilings(h, w, b, positions);
  printf("%u\n", HASH_COUNT(positions));
  return 0;
}
