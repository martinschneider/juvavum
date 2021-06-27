// returns a set of valid moves from the given board for the specified player and game type
// if keys == true the binary representation of each position is returned, otherwise the array representation
function succ(b, player, keys, type) {
  var bin = key(b);
  if (keys == true && successorsMap.has(bin) && type != DOM)
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

// rows and columns can be used to limit the search in specific parts of the board
// this is used for juvavum where we can place multiple pieces in every move
function succ1(b, player, keys, type, rows, columns, children) {
  size = type == JUV ? 1 : 2;
  if (type != DOM || player % 2 != 0) {
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
  if (type != DOM || player % 2 == 0) {
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

// checks if a piece of a given size (1xn) can be placed at position (i,j)
function checkFreeRow(b, i, j, n) {
  for (var k = i; k < i + n; k++) {
    if (b[k][j]) {
      return false;
    }
  }
  return true;
}

// sets a piece of a given size (1xn) at position (i,j) for the specified player
function placeRow(b, i, j, n, player) {
  var b1 = JSON.parse(JSON.stringify(b));
  for (var k = i; k < i + size; k++) {
    b1[k][j] = player;
  }
  return b1;
}

// checks if a piece of a given size (nx1) can be placed at position (i,j)
function checkFreeCol(b, i, j, n) {
  for (var k = j; k < j + n; k++) {
    if (b[i][k]) {
      return false;
    }
  }
  return true;
}

// sets a piece of a given size (1xn) at position (i,j) for the specified player
function placeCol(b, i, j, size, player) {
  var b1 = JSON.parse(JSON.stringify(b));
  for (var k = j; k < j + size; k++) {
    b1[i][k] = player;
  }
  return b1;
}

// returns (and caches) the g-value of a position
function grundy(gValues, b, misere, type) {
  var bin = key(b);
  var g;
  if (gValues.has(bin))
  {
    return gValues.get(bin);
  }
  var children = succ(b, 2, false, type);
  if (children.size == 0) {
    g = (misere == true) ? 1 : 0;
  }
  else
  {
    g = mex(children, misere, type);
  }
  gValues.set(bin,g);
  return g;
}

// minimum exclusive, used for g-value calculation
function mex(children, misere, type) {
  var i = 0;
  var mex = -1;
  while(mex == -1) {
    var found = false;
    for (b of children){
      j = grundy(gValues, b, misere, type);
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

var abort = false;

// returns a "good" move for the computer player or null if there is none or the game tree is still too large to analyze
const findBestMove = async (b, misere, type, currMov) => {
  var url = REST_URL + "?g="+(type+1)+"&h="+b.length+"&w="+b[0].length+"&m="+(misere?1:0)+"&b="+key(b);
  var dbMoves = JSON.parse(await fetch(url).then(function(value){
    return value.text();
  }));
  if (!abort) {
    if (dbMoves.length>0) {
      console.log("Using winning move from database");
      return fromKey(b, moves, dbMoves[0]);
    }
    var empty = countEmptyFields(b);
    if (empty <= ENDGAME_LIMITS[type]) {
      if (type == DOM && !misere) {
        best = null;
        max(MIN_MAX_DEPTH, b, currMov);
        console.log("Using winning move based on min-max search");
        return best;
      }
      grundy(gValues, b, misere, type);
      for (var child of succ(b, moves, false, type)) {
        if (grundy(gValues, child, misere, type) == 0) {
          console.log("Using winning move from end-game analysis")
          return child;
        }
      }
    }
  }
  return null;
}

async function aiMove(b, misere, type, currMov)
{
  const timeoutError = Symbol();
  var move = null;
  try {
    move = await timeout(findBestMove(b, misere, type, currMov), NETWORK_TIMEOUT, timeoutError);
  }
  catch(e) {
    if (e === timeoutError) {
      console.log("Timeout while finding AI move");
      abort = true;
    }
    else
    {
      console.log("Error finding AI move: " + e);
    }
  }
  finally {
    return computerMove(move);
  }
}