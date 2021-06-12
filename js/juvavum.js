var vw;
var vh;
var board;
var prevBoard;
var succ;
document.addEventListener("DOMContentLoaded", function (event) {
  start();
});

function start() {
  updateSize();
  var w = parseInt(document.getElementById("width").value);
  var h = parseInt(document.getElementById("height").value);
  console.log("Start game " + h + "x" + w);
  board = Array(h).fill(0).map(x => Array(w).fill(0));
  prevBoard = Array(h).fill(0).map(x => Array(w).fill(0));
  console.log(board);
  drawBoard(board);
  window.addEventListener('resize', () => {
    updateSize();
    drawBoard()
  });
  document.getElementById("confirm").disabled = false;
}

function updateSize() {
  vw = 0.9 * window.innerWidth;
  vh = 0.9 * (window.innerHeight - document.getElementById("controls").clientHeight);
}

function drawBoard() {
  console.log("Drawing board");
  console.log(board);
  var w = board.length;
  var h = board[0].length;
  fieldSize = Math.min(vw / w, vh / h);
  var tbl = document.getElementById("board");
  while (tbl.firstChild) {
    tbl.removeChild(tbl.lastChild);
  }
  var tbdy = document.createElement('tbody');
  for (var i = 0; i < h; i++) {
    var tr = document.createElement('tr');
    for (var j = 0; j < w; j++) {
      var td = document.createElement('td');
      var id = i + "_" + j;
      td.setAttribute("width", fieldSize);
      td.setAttribute("height", fieldSize);
      td.setAttribute("class", "player_" + board[j][i]);
      td.setAttribute("onClick", "toggle(" + j + "," + i + ")")
      tr.appendChild(td)
    }
    tbdy.appendChild(tr);
  }
  tbl.appendChild(tbdy);
}

function toggle(i, j) {
  if (board[i][j] == 0) {
    board[i][j] = 1;
  } else if (board[i][j] == 1 && prevBoard[i][j] == 0) {
    board[i][j] = 0;
  }
  drawBoard();
}

function move() {
  if (succ(prevBoard, 1, true).has(key(board))) {
    successors = succ(board, 2, false);
    board = Array.from(successors)[Math.floor(Math.random() * successors.size)];
    if (!board) {
      alert("You win!");
      document.getElementById("confirm").disabled = true;
    }
    prevBoard = JSON.parse(JSON.stringify(board));
    drawBoard();
    if (succ(board, 1, true).size == 0) {
      alert("You lose!");
      document.getElementById("confirm").disabled = true;
    }
  } else {
    alert("Invalid move!");
  }
}

function key(b) {
  var key = 0
  var w = b.length;
  var h = b[0].length;
  for (var i = 0; i < w * h; i++) {
    if (b[i % w][Math.floor(i / w)]) {
      key += (1 << i);
    }
  }
  return key
}

function succ(b, player, keys) {
  children = new Set();
  for (var j = 0; j < b[0].length; j++) {
    for (var i = 0; i < b.length - 1; i++) {
      if (!b[i][j] && !b[i + 1][j]) {
        b1 = JSON.parse(JSON.stringify(b));
        b1[i][j] = player;
        b1[i + 1][j] = player;
        if (keys) {
          children.add(key(b1));
        } else {
          children.add(b1);
        }
      }
    }
  }
  for (var i = 0; i < b.length; i++) {
    for (var j = 0; j < b[0].length - 1; j++) {
      if (!b[i][j] && !b[i][j + 1]) {
        b1 = JSON.parse(JSON.stringify(b));
        b1[i][j] = player;
        b1[i][j + 1] = player;
        if (keys) {
          children.add(key(b1));
        } else {
          children.add(b1);
        }
      }
    }
  }
  console.log(children);
  return children;
}
