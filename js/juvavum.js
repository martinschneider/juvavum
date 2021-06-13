const JUV=0;
const DJUV=1;
const CRAM=2;
const DOM=3;

var vw;
var vh;
var w;
var h;
var board;
var prevBoard;
var succ;
var type;
var misere;
var music;
var sounds;
var bgMusic;
var placePiece;
var takeBackPiece;
var computerMove;

document.addEventListener("DOMContentLoaded", function(event) {
    bgMusic=new Audio("mozart.mp3");
    placePiece=new Audio("domino1.m4a");
    computerMove=new Audio("domino2.m4a");
    takeBackPiece=new Audio("pickup.mp3");

    document.getElementById("width").addEventListener("change", ()=> {
        start();
      }

    );

    document.getElementById("height").addEventListener("change", ()=> {
        start();
      }

    );

    document.getElementById("type").addEventListener("change", ()=> {
        start();
      }

    );

    document.getElementById("misere").addEventListener("change", ()=> {
        start();
      }

    );

    document.getElementById("sounds").addEventListener("change", ()=> {
        updateSettings();
      }

    );

    document.getElementById("music").addEventListener("change", ()=> {
        toggleMusic();
      }

    );

    document.onkeydown=function(evt) {
      evt=evt || window.event;
      console.log(evt.keyCode);

      switch (evt.keyCode) {
        case 67: move();
        break;
        case 78: start();
        break;
        case 72: howto();
        break;
        default: break;
      }
    }

    ;
    start();
    playMusic();
  }

);

window.addEventListener("resize", ()=> {
    updateSize();
    drawBoard()
  }

);

function start() {
  updateSize();
  updateSettings();
  board=Array(h).fill(0).map(x=> Array(w).fill(0));
  prevBoard=Array(h).fill(0).map(x=> Array(w).fill(0));
  drawBoard(board);
  document.getElementById("confirm").disabled=false;
}

function toggleMusic() {
  music= !music;
  playMusic();
}

function playMusic() {
  if (music) {
    bgMusic.play();
  }

  else {
    bgMusic.pause();
  }
}

function updateSettings() {
  w=parseInt(document.getElementById("width").value);
  h=parseInt(document.getElementById("height").value);
  type=parseInt(document.getElementById("type").value);
  misere=document.getElementById("misere").value;
  music=document.getElementById("music").checked;
  sounds=document.getElementById("sounds").checked;
}

function updateSize() {
  vw=0.9 * window.innerWidth;
  vh=0.9 * (window.innerHeight - document.getElementById("controls").clientHeight);
}

function drawBoard() {
  var w=board.length;
  var h=board[0].length;
  fieldSize=Math.min(vw / w, vh / h);
  var tbl=document.getElementById("board");

  while (tbl.firstChild) {
    tbl.removeChild(tbl.lastChild);
  }

  var tbdy=document.createElement('tbody');

  for (var i=0; i < h; i++) {
    var tr=document.createElement('tr');

    for (var j=0; j < w; j++) {
      var td=document.createElement('td');
      var id=i+"_"+j;
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

function toggle(i, j) {
  if (board[i][j]==0) {
    if (sounds) {
      placePiece.play();
    }

    board[i][j]=1;
  }

  else if (board[i][j]==1 && prevBoard[i][j]==0) {
    if (sounds) {
      takeBackPiece.play();
    }

    board[i][j]=0;
  }

  drawBoard();
}

function move() {
  successors=succ(prevBoard, 1, true, type);

  if (successors.has(key(board))) {
    successors=succ(board, 2, false, type);
    board=Array.from(successors)[Math.floor(Math.random() * successors.size)];

    if ( !board) {
      Swal.fire( {
          title: "Hooray!",
          text: "Congratulations, you win!",
          icon: "success",
          confirmButtonText: "Ok"
        }

      );
      document.getElementById("confirm").disabled=true;
    }

    prevBoard=JSON.parse(JSON.stringify(board));
    drawBoard();

    if (succ(board, 1, true, type).size==0) {
      Swal.fire( {
          title: "Oh no!",
          text: "I'm sorry, you've lost. Let's try again!",
          icon: "error",
          confirmButtonText: "Ok"
        }
      );
      document.getElementById("confirm").disabled=true;

      if (sounds) {
        computerMove.play();
      }
    }

    if (sounds) {
      computerMove.play();
    }
  }

  else {
    Swal.fire( {
        title: "Cannot lah!",
        text: "I'm sorry, this moved is not allowed.",
        icon: "warning",
        confirmButtonText: "Try again"
      }

    );
  }
}

function key(b) {
  var key=0;
  var w=b.length;
  var h=b[0].length;

  for (var i=0; i < w * h; i++) {
    if (b[i % w][Math.floor(i / w)]) {
      key+=(1 << i);
    }
  }

  return key
}

function succ(b, player, keys, type) {
  children=new Set();
  succ1(b, player, keys, type, Array(b[0].length).keys(), Array(b.length).keys(), children);
  return children;
}

function succ1(b, player, keys, type, rows, columns, children) {
  size=type==JUV ? 1: 2;

  if (type !=DOM || player==1) {
    for (j of rows) {
      for (i of Array(b.length - size + 1).keys()) {
        if (checkFreeRow(b, i, j, size)) {
          b1=placeRow(b, i, j, size, player);

          if (keys) {
            children.add(key(b1));
          }

          else {
            children.add(b1);
          }

          if (type==DJUV || type==JUV) {
            succ1(b1, player, keys, type, [j], [], children);
          }
        }
      }
    }
  }

  if (type !=DOM || player==2) {
    for (i of columns) {
      for (j of Array(b[0].length - size + 1).keys()) {
        if (checkFreeCol(b, i, j, size)) {
          b1=placeCol(b, i, j, size, player);

          if (keys) {
            children.add(key(b1));
          }

          else {
            children.add(b1);
          }

          if (type==DJUV || type==JUV) {
            succ1(b1, player, keys, type, [], [i], children);
          }
        }
      }
    }
  }
}

function checkFreeRow(b, i, j, size) {
  for (var k=i; k < i + size; k++) {
    if (b[k][j]) {
      return false;
    }
  }

  return true;
}

function placeRow(b, i, j, size, player) {
  var b1=JSON.parse(JSON.stringify(b));

  for (var k=i; k < i + size; k++) {
    b1[k][j]=player;
  }

  return b1;
}

function checkFreeCol(b, i, j, size) {
  for (var k=j; k < j + size; k++) {
    if (b[i][k]) {
      return false;
    }
  }

  return true;
}

function placeCol(b, i, j, size, player) {
  var b1=JSON.parse(JSON.stringify(b));

  for (var k=j; k < j + size; k++) {
    b1[i][k]=player;
  }

  return b1;
}

function howto() {
  Swal.fire( {
      title: "Juvavum",
      html: "In each move you can fill as many fields as you want as long as they are all in the same row or the same column. If you make the last possible move you win.<h3>Domino Juvavum</h3>The rules are the same but you are placing dominoes, that means every piece covers exactly two (horizontally or vertically) neighbouring fields. In each move, you can place multiple dominoes, again only in the same row or column.<h3><a href=\"https://en.wikipedia.org/wiki/Cram_(game)\">Cram</a></h3>Like Domino Juvavum but you can only place a single domino in each move.<h3><a href=\"https://en.wikipedia.org/wiki/Domineering\">Domineering</a></h3>Like Cram but you can only place horizontal dominoes (and the computer only vertical ones).",
      confirmButtonText: "Got it!"
    }

  );
}