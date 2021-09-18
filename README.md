juvavum
=======

Analyse and play the games Juvavum, Domino Juvavum and Cram.

# Introduction
Juvavum, Domino Juvavum and Cram are impartial, [combinatorical games](https://en.wikipedia.org/wiki/Combinatorial_game_theory) played on an m x n board.

* In [Cram](https://en.wikipedia.org/wiki/Cram_(game)), two players have a collection of dominoes which they place on the board in turn. Each domino must always cover two squares of the board which are not yet covered by any other domino.
* Domino Juvavum has the same rules except that in every move a player is allowed to put an arbitrary number of dominoes (at least one) in the same row or column.
* Juvavum has the same rules as Domino Juvavum, except that instead of dominoes (which cover two adjacent squares) the players use coins (which cover one square each).

The player who makes the last move wins. In the misère version, the player who makes the last move loses.

Cram has been popularised by [Martin Gardner](https://en.wikipedia.org/wiki/Martin_Gardner). Juvavum and Domino Juvavum have been introduced by [Peter Gerl](https://petergerl-mathematiker.tumblr.com) at the University of Salzburg.

# Contents

The original implementation is in Java. I add implementations in other languages as I learn them. Feedback and contributions are welcome!

* [Java](java): Analyse Juvavum, Domino and Cram on all board sizes.
* [Go](go): Implementation in Go. More performant but (for now) less functionality.
* [Android](android): Playable version of Cram and Domino Juvavum for Android.
* [Javascript](js): Playable version of all variants using HTML, CSS and Javascript.
* [Python](python): Calculate the number of positions for given board sizes.
* [C](c): Calculate the number of positions for given board sizes.
* [Erlang](erlang): Basic Erlang implementation for some board sizes.
* [8086 Assembler](asm/8086): Pretty-print for 4x4 boards.

# Results
I am collecting results in this [Google Sheet](https://docs.google.com/spreadsheets/d/1QFaqaRN4wdvPGEEx9gDphZzy8yk3d-RP9WAN8shMqsU/edit?usp=sharing).

