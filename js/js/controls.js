var init=false;
document.addEventListener("DOMContentLoaded", function(event) {
    successorsMap.clear();
    grundyValues.clear();
    document.getElementById("width").addEventListener("change", ()=> {
        successorsMap.clear();
        grundyValues.clear();
        start();
      }
    );
    document.addEventListener("click", ()=> {
        // Auto-play can be disabled in the browser. In this case,
        // start the background music on the first user interaction (click).
        if (!init)
        {
          init = true;
          playMusic();
        }
      }
    );
    document.getElementById("height").addEventListener("change", ()=> {
        successorsMap.clear();
        grundyValues.clear();
        start();
      }
    );
    document.getElementById("type").addEventListener("change", ()=> {
        successorsMap.clear();
        grundyValues.clear();
        start();
      }
    );
    document.getElementById("misere").addEventListener("change", ()=> {
        grundyValues = new Map();
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
    document.getElementById("language").addEventListener("change", ()=> {
      initTranslations();
      }
    );
    document.onclick = event => {
      if (event.detail === 2) {
        move();
      }
    };
    document.onkeydown=function(evt) {
      evt=evt || window.event;
      console.log(evt.keyCode);
      switch (evt.keyCode) {
        case 49: move();
        break;
        case 50: start();
        break;
        case 51: settings();
        break;
        case 52: howto();
        break;
        case 53: about();
        break;
        default: break;
      }
    };
    initTranslations();
    start();
    playMusic();
  }
);

window.addEventListener("resize", ()=> {
    updateSize();
    drawBoard(board);
  }
);

function start() {
  updateSize();
  updateSettings();
  gameOver = false;
  var sounds = properties.get("sounds");
  if (sounds && board && key(board)!=0)
  {
    clearBoardPiece.play();
  }
  w = properties.get("width");
  h = properties.get("height");
  board=Array(h).fill(0).map(x=> Array(w).fill(0));
  prevBoard=Array(h).fill(0).map(x=> Array(w).fill(0));
  drawBoard(board);
  document.getElementById("confirm").disabled=false;
}

function toggleMusic() {
  properties.set("music", !properties.get("music"));
  playMusic();
}

function playMusic() {
  if (properties.get("music")) {
    bgMusic.play();
  }
  else {
    bgMusic.pause();
  }
}

function updateSettings() {
  properties.set("width", parseInt(document.getElementById("width").value));
  properties.set("height", parseInt(document.getElementById("height").value));
  properties.set("type", parseInt(document.getElementById("type").value));
  properties.set("misere", document.getElementById("misere").checked);
  properties.set("music", document.getElementById("music").checked);
  properties.set("sounds", document.getElementById("sounds").checked);
}

function updateSize() {
  vw = 0.9 * window.innerWidth;
  vh = 0.9 * (window.innerHeight - document.getElementById("controls").clientHeight);
}

function toggle(i, j) {
  if (!gameOver)
  {
    var sounds = properties.get("sounds");
    if (board[i][j] == 0) {
      if (sounds) {
        placePiece.play();
      }
      board[i][j] = 1;
    }
    else if (board[i][j] == 1 && prevBoard[i][j] == 0) {
      if (sounds) {
        takeBackPiece.play();
      }
      board[i][j] = 0;
    }
    drawBoard(board);
  }
}

function invalidMove()
{
  Swal.fire({
    title: I18n.t("invalid_title"),
    text: I18n.t("invalid_text"),
    icon: "warning",
    confirmButtonText: "Try again"
  });
}

function win()
{
  Swal.fire({
    title: I18n.t("win_title"),
    text: I18n.t("win_text"),
    icon: "success",
    confirmButtonText: "Ok"
  });
}

function loss()
{
  Swal.fire({
    title: I18n.t("loss_title"),
    text: I18n.t("loss_text"),
    icon: "error",
    confirmButtonText: "Ok"
  });
}

function howto() {
  Swal.fire({
      title: I18n.t("howto_title"),
      html: I18n.t("howto_text"),
      confirmButtonText: "Got it!"
  });
}

function about() {
  Swal.fire({
      title: I18n.t("about_title"),
      html: I18n.t("about_text"),
      confirmButtonText: "Ok"
  });
}

function settings() {
  var settings = document.getElementById("settings");
  if (settings.style.display === "none") {
    settings.style.display = "block";
  } else {
    settings.style.display = "none";
  }
}