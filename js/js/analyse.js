function succ(b, player, keys, type) {
  var bin = key(b);
  if (keys == true && successorsMap.has(bin))
  {
    return successorsMap.get(bin);
  }
  children = new Set();
  succ1(b, player, keys, type, Array(b[0].length).keys(), Array(b.length).keys(), children);
  if (keys)
  {
    successorsMap.set(bin,children);
  }
  return children;
}

function succ1(b, player, keys, type, rows, columns, children) {
  size = type == JUV ? 1: 2;
  if (type != DOM || player == 1) {
    for (j of rows) {
      for (i of Array(b.length - size + 1).keys()) {
        if (checkFreeRow(b, i, j, size)) {
          b1 = placeRow(b, i, j, size, player);
          children.add(keys ? key(b1) : b1);
          if (type == DJUV || type == JUV) {
            succ1(b1, player, keys, type, [j], [], children);
          }
        }
      }
    }
  }
  if (type != DOM || player==2) {
    for (i of columns) {
      for (j of Array(b[0].length - size + 1).keys()) {
        if (checkFreeCol(b, i, j, size)) {
          b1 = placeCol(b, i, j, size, player);
          children.add(keys ? key(b1) : b1);
          if (type == DJUV || type == JUV) {
            succ1(b1, player, keys, type, [], [i], children);
          }
        }
      }
    }
  }
}

function checkFreeRow(b, i, j, size) {
  for (var k = i; k < i + size; k++) {
    if (b[k][j]) {
      return false;
    }
  }
  return true;
}

function placeRow(b, i, j, size, player) {
  var b1 = JSON.parse(JSON.stringify(b));
  for (var k = i; k < i + size; k++) {
    b1[k][j] = player;
  }
  return b1;
}

function checkFreeCol(b, i, j, size) {
  for (var k = j; k < j + size; k++) {
    if (b[i][k]) {
      return false;
    }
  }
  return true;
}

function placeCol(b, i, j, size, player) {
  var b1 = JSON.parse(JSON.stringify(b));
  for (var k = j; k < j + size; k++) {
    b1[i][k] = player;
  }
  return b1;
}

function grundy(grundyValues, b, misere, type) {
  var bin = key(b);
  var g;
  if (grundyValues.has(bin))
  {
    return grundyValues.get(bin);
  }
  var children = succ(b, 2, false, type);
  if (children.size == 0) {
    g = (misere == true) ? 1 : 0;
  }
  else
  {
    g = mex(children, misere, type);
  }
  grundyValues.set(bin,g);
  return g;
}

function mex(children, misere, type) {
  var i = 0;
  var mex = -1;
  while(mex == -1) {
    var found = false;
    for (b of children){
      j = grundy(grundyValues, b, misere, type);
      if (j == i) {
        found = true;
        break;
      }
    }
    if (found == false) {
      mex = i;
    }
    i++;
  }
  return mex;
}

function aiMove(b, misere, type) {
  var empty = countEmptyFields(b);
  if (empty <= ENDGAME_LIMITS[type]) {
    if (type == DOM && !misere) {
      best = null;
      max(MIN_MAX_DEPTH, b, misere);
      return best;
    }
    grundy(grundyValues, b, misere, type);
    for (var child of succ(b, 2, false, type)) {
      if (grundy(grundyValues, child, misere, type) == 0) {
        return child;
      }
    }
  }
  return null;
}