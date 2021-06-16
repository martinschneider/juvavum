function move() {
  if (gameOver)
  {
    return;
  }
  var type = properties.get("type");
  var misere = properties.get("misere");
  successors = succ(prevBoard, 1, true, type);
  if (successors.has(key(board))) {
    console.log("Player moves " + key(prevBoard) + "->" + key(board));
    prevBoard = board;
    goodMove = aiMove(board, misere, type);
    if (goodMove!=null)
    {
      board = goodMove;
    }
    else
    {
      successors = succ(board, 2, false, type);
      board = Array.from(successors)[Math.floor(Math.random() * successors.size)];
    }
    if (!board) {
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
      if (succ(board, 1, true, type).size == 0) {
        misere ? win() : loss();
        gameOver = true;
        document.getElementById("confirm").disabled = true;
        if (sounds) {
          computerMove.play();
        }
      }
      if (sounds) {
        computerMove.play();
      }
    }
  }
  else {
    invalidMove();
  }
}

function drawBoard(board) {
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
      var id = i + "_" +j;
      td.setAttribute("width", fieldSize);
      td.setAttribute("height", fieldSize);
      td.setAttribute("class", "player_"+ board[j][i]);
      td.setAttribute("onClick", "toggle("+ j + ","+ i + ")");
      tr.appendChild(td);
    }
    tbdy.appendChild(tr);
  }
  tbl.appendChild(tbdy);
}