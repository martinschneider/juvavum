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

long long key(int h, int w, bool b[h][w]) {
  long long key = 0;
  for (int i = 0; i < w * h; i++) {
    key += b[i % h][i / h] << i;
  }
  return key;
}

struct board *positions(int h, int w, bool b[][w], struct board *map) {
  int bin_key = key(h, w, b);
  struct board *s;
  HASH_FIND_INT(map, &bin_key, s);
  if (s == NULL) {
    s = malloc(sizeof(struct board));
    s -> key = bin_key;
    HASH_ADD_INT(map, key, s);
    for (int j = 0; j < w; j++) {
      for (int i = 0; i < h - 1; i++) {
        if (!b[i][j] && !b[i + 1][j]) {
          bool b1[h][w];
          memcpy(&b1[0][0], &b[0][0], h * w * sizeof(bool));
          b1[i][j] = 1;
          b1[i + 1][j] = 1;
          map = positions(h, w, b1, map);
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
        map = positions(h, w, b1, map);
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
  bool b[h][w];
  memset(b, 0, h * w * sizeof(bool));
  struct board *map = NULL;
  map = positions(h, w, b, map);
  printf("%u\n", HASH_COUNT(map));
  return 0;
}
