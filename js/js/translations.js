I18n.translations = {};

I18n.translations["en"] = {
  invalid_title: "Cannot lah!",
  invalid_text: "I'm sorry, this move is not allowed.",
  win_title: "Hooray!",
  win_text: "Congratulations, you win!",
  loss_title: "Oh no!",
  loss_text: "I'm sorry, you've lost. Let's try again!",
  about_title: "About",
  about_text: "<h3>History</h3><p>The game <a href=\"https://en.wikipedia.org/wiki/Domineering\">Domineering</a> was proposed by Göran Andersson in 1973 and together with <a href=\"https://en.wikipedia.org/wiki/Cram_(game)\">Cram</a> popularized by <a href=\"https://en.wikipedia.org/wiki/Martin_Gardner\">Martin Gardner</a>. </p><p>Juvavum and Domino Juvavum were introduced by <a href=\"https://petergerl.tumblr.com\">Peter Gerl</a> at the University of Salzburg. Juvavum (or Iuvavum) was the name of the Roman settlement which developed into today\'s city of Salzburg, Austria.</p><h3>Credits</h3><p>Game implementation: <a href=\"https://github.com/martinschneider/\">Martin Schneider</a>, 2021</p><p>Background image: <a href=\"https://www.publicdomainpictures.net/en/view-image.php?image=304923\">\"Wooden Background\"</a> by George Hodan<p>Icon image: <a href=\"https://commons.wikimedia.org/wiki/File:Domino_(PSF).png\">Domino</a> by Pearson Scott Foresman</p><p>Music: <a href=\"https://en.wikipedia.org/wiki/Piano_Sonata_No._11_(Mozart)\">W.A. Mozart: Piano Sonata No. 11 A Major Andante grazioso (KV 331)</a></p>",
  howto_title: "Juvavum",
  howto_text: "You take turns against the computer. In each move you can fill as many fields as you want as long as they are all in the same row or the same column. If you make the last possible move you win.<h3>Domino Juvavum</h3>The rules are the same but you are placing dominoes, this means every piece covers exactly two (horizontally or vertically) neighbouring fields. In each move, you can place multiple dominoes, again only in the same row or column.<h3>Cram</h3>Like Domino Juvavum but you can only place a single domino in each move.<h3>Domineering</h3>Like Cram but you can only place horizontal dominoes (and the computer only vertical ones).<h3>Misère variant</h3>In misère play, the player making the last move loses. In other words, you win if you're the first player no to have any possible moves left.",
  confirm: "[1] Confirm move",
  new_game: "[2] New game",
  settings: "[3] Settings",
  howto: "[4] How to play?",
  about: "[5] About",
  ok: "Ok",
  board_size: "Board size",
  game_type: "Game type",
  misere: "Misère form",
  sounds: "Sounds",
  music: "Background music",
  language: "Language"
};

I18n.translations["de"] = {
  invalid_title: "Versuch's nochmal!",
  invalid_text: "Tut mir leid, dieser Zug ist nicht erlaubt.",
  win_title: "Hurrah!",
  win_text: "Gratuliere, du hast gewonnen!",
  loss_title: "Oh nein!",
  loss_text: "Tut mir leid, du hast verloren. Versuch's nochmal!",
  about_title: "Über",
  about_text: "<h3>Geschichte</h3><p>Das Spiel <a href=\"https://en.wikipedia.org/wiki/Domineering\">Domineering</a> wurde 1973 von Göran Andersson vorgeschlagen und erlangte zusammen mit <a href=\"https://en.wikipedia.org/wiki/Cram_(game)\">Cram</a> Bekanntheit durch <a href=\"https://en.wikipedia.org/wiki/Martin_Gardner\">Martin Gardner</a>. </p><p>Juvavum and Domino Juvavum wurden von <a href=\"https://petergerl.tumblr.com\">Peter Gerl</a> an der Universität Salzburg vorgeschlagen. Juvavum (or Iuvavum) war der Name der römischen Siedlung, die sich in die heutige Stadt Salzburg in Österreich entwicklet hat.</p><h3>Autor</h3><p>Programmierung: <a href=\"https://github.com/martinschneider/\">Martin Schneider</a>, 2021</p><p>Hintergrundbild: <a href=\"https://www.publicdomainpictures.net/en/view-image.php?image=304923\">\"Wooden Background\"</a> von George Hodan<p>Icon: <a href=\"https://commons.wikimedia.org/wiki/File:Domino_(PSF).png\">Domino</a> von Pearson Scott Foresman</p><p>Musik: <a href=\"https://en.wikipedia.org/wiki/Piano_Sonata_No._11_(Mozart)\">W.A. Mozart: Piano Sonata No. 11 A Major Andante grazioso (KV 331)</a></p>",
  howto_title: "Juvavum",
  howto_text: "Du ziehst abwechselnd gegen den Computer. In jedem Zug kannst du beliebig viele Felder füllen, so lange sie in der selben Zeile oder Spalte sind. Wenn du den letzten Zug machst, gewinnst du.<h3>Domino Juvavum</h3>Die Regeln bleiben gleich, allerdings spielen wir mit Dominos. Das bedeutet, dass jeder Spielstein immer zwei (horizontal oder vertikal) benachbarte Felder bedecken muss. Du kannst beliebig viele Dominos pro Zug setzen, allerdings wieder nur in der selben Zeile oder Spalte.<h3>Cram</h3>Wie Domino Juvavum, aber du kannst immer nur ein Domino pro Zug setzen.<h3>Domineering</h3>Wie Cram, aber du kannst Dominos nur horizontal setzen (und der Computer nur vertikal).<h3>Misère Variante</h3>Im Misère Spiel, verliert jener Spieler, der den letzten Zug macht. In anderen Worten du gewinnst, wenn du als erster Spieler keine Zugmöglichkeit mehr hast.",
  confirm: "[1] Zug bestätigen",
  new_game: "[2] Neues Spiel",
  settings: "[3] Einstellungen",
  howto: "[4] Anleitung?",
  about: "[5] Über",
  ok: "Ok",
  board_size: "Spielfeldgröße",
  game_type: "Spielart",
  misere: "Misère",
  sounds: "Soundeffekte",
  music: "Hintergrundmusik",
  language: "Sprache"
};

function initTranslations() {
  I18n.locale = document.getElementById("language").value;
  document.getElementById("confirm").value = I18n.t("confirm");
  document.getElementById("new_game").value = I18n.t("new_game");
  document.getElementById("settings_btn").value = I18n.t("settings");
  document.getElementById("howto").value = I18n.t("howto");
  document.getElementById("about").value = I18n.t("about");
  document.getElementById("ok").value = I18n.t("ok");
  document.getElementById("board_size").innerHTML = I18n.t("board_size");
  document.getElementById("game_type").innerHTML = I18n.t("game_type");
  document.getElementById("misere_lbl").innerHTML = I18n.t("misere");
  document.getElementById("sounds_lbl").innerHTML = I18n.t("sounds");
  document.getElementById("music_lbl").innerHTML = I18n.t("music");
  document.getElementById("language_lbl").innerHTML = I18n.t("language");
}