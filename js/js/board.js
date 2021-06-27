// returns a binary representation of a game board. this can be used as a key when storing g-values for boards
function key(b) {
  var key = 0;
  var w = b.length;
  var h = b[0].length;
  for (var i = 0; i < w * h; i++) {
    if (b[i % w][Math.floor(i / w)]) {
      key += (1 << i);
    }
  }
  return key;
}

function fromKey(b, move, key) {
  const h = b.length;
  const w = b[0].length;
  var b1 = JSON.parse(JSON.stringify(b));
  for (var i = 0; i < h * w; i++) {
    if (b1[i % w][Math.floor(i / w)] == 0) {
      b1[i % w][Math.floor(i / w)] = move * ((key & 1 << i) != 0 ? 1 : 0);
    }
  }
  return b1;
}

function countEmptyFields(b) {
  var count = 0;
  var w = b.length;
  var h = b[0].length;
  for (var i = 0; i < w * h; i++) {
    if (!b[i % w][Math.floor(i / w)]) {
      count++;
    }
  }
  return count;
 }

function confirmMove(b, move) {
  var w = b.length;
  var h = b[0].length;
  for (var i = 0; i < h; i++) {
    for (var j = 0; j < w; j++) {
      if (b[j][i] == -1)
      {
        b[j][i] = move;
      }
    }
  }
  return b;
}

function resetMove(b, move) {
  var w = b.length;
  var h = b[0].length;
  for (var i = 0; i < h; i++) {
    for (var j = 0; j < w; j++) {
      if (b[j][i] == move)
      {
        b[j][i] = 0;
      }
    }
  }
  return b;
}

function filledLeftRow(b, i, j) {
  var count = 0;
  for (var k=j-1; k>=0; k--) {
    if (board[k][i]==board[j][i]) {
      count++;
    }
  }
  return count;
}

function filledRightRow(b, i, j) {
  var count = 0;
  for (var k=j+1; k<board.length; k++) {
    if (board[k][i]==board[j][i]) {
      count++;
    }
  }
  return count;
}

function filledLeftColumn(b, i, j) {
  var count = 0;
  for (var k=i-1; k>=0; k--) {
    if (board[j][k]==board[j][i]) {
      count++;
    }
  }
  return count;
}

function filledRightColumn(b, i, j) {
  var count = 0;
  for (var k=i+1; k<board[j].length; k++) {
    if (board[j][k]==board[j][i]) {
      count++;
    }
  }
  return count;
}