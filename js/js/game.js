function move() {
  if (gameOver)
  {
    return;
  }
  var type = properties.get("type");
  var misere = properties.get("misere");
  moves++;
  board = confirmMove(board, moves);
  successors = succ(prevBoard, moves, true, type);
  if (successors.has(key(board))) {
    console.log("Player moves " + key(prevBoard) + "->" + key(board));
    prevBoard = board;
    computerMove();
  }
  else {
    invalidMove();
  }
}

async function computerMove() {
  var type = properties.get("type");
  var misere = properties.get("misere");
  moves++;
  goodMove = aiMove(board, misere, type, moves);
  if (goodMove != null)
  {
    board = goodMove;
  }
  else
  {
    successors = succ(board, moves, false, type);
    board = Array.from(successors)[Math.floor(Math.random() * successors.size)];
  }
  if (!board) {
    board = prevBoard;
    misere ? loss() : win();
    gameOver = true;
    document.getElementById("confirm").disabled=true;
  }
  else
  {
    var sounds = properties.get("sounds");
    console.log("Computer moves " + key(prevBoard) + "->" + key(board));
    prevBoard = JSON.parse(JSON.stringify(board));
    drawBoard(board);
    if (succ(board, moves + 1, true, type).size == 0) {
      board = prevBoard;
      misere ? win() : loss();
      gameOver = true;
      document.getElementById("confirm").disabled = true;
      if (sounds) {
        moveSound.play();
      }
    }
    if (sounds) {
      moveSound.play();
    }
  } 
}

function drawBoard(board) {
  var w = board.length;
  var h = board[0].length;
  var type = properties.get("type");
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
      td.setAttribute("width", fieldSize);
      td.setAttribute("height", fieldSize);
      var player = (board[j][i] % 2 == 0) ? 2 : 1;
      if (type == JUV && board[j][i] != 0)
      {
        td.setAttribute("class", "player_" + player + "_juvavum");
      }
      else
      {
        // current move
        if (board[j][i] == -1)
        {
          td.setAttribute("class", "active"); 
        }
        else if (board[j][i] > 0)
        {
          var dir;
          var a = filledLeftRow(board, i,j);
          var b = filledRightRow(board, i,j);
          var c = filledLeftColumn(board, i,j);
          var d = filledRightColumn(board, i,j);
          if (a % 2 == 0 && b % 2 != 0)
          {
            dir="horizontal1";
          }
          else if (a % 2 != 0 && b % 2 == 0)
          {
            dir="horizontal2";
          }
          else if (c % 2 == 0 && d % 2 != 0)
          {
            dir="vertical1"
          }
          else if (c % 2 != 0 && d % 2 == 0)
          {
            dir="vertical2"
          }
          td.setAttribute("class", "player_" + player + "_"+ dir);
        }
      }
      td.setAttribute("onClick", "toggle("+ j + ","+ i + ")");
      tr.appendChild(td);
    }
    tbdy.appendChild(tr);
  }
  tbl.appendChild(tbdy);
}