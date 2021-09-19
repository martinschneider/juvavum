juvavum
=======

Analyse the games Juvavum, Domino Juvavum and Cram.

# Introduction
Juvavum, Domino Juvavum and Cram are impartial, combinatorical games played on an m x n board.

* In [Cram](https://en.wikipedia.org/wiki/Cram_(game)), two players have a collection of dominoes which they place on the board in turn. Each domino must always cover two squares of the board which are not yet covered by any other domino.
* Domino Juvavum has the same rules except that in every move a player is allowed to put an arbitrary number of dominoes (at least one) in the same row or column.
* Juvavum has the same rules as Domino Juvavum, except that instead of dominoes (which cover two adjacent squares) the players use coins (which cover one square each).

The player who makes the last move wins. In the misère version, the first player who cannot move wins.

Cram has been popularised by [Martin Gardner](https://en.wikipedia.org/wiki/Martin_Gardner). Juvavum and Domino Juvavum have been introduced by [Peter Gerl](https://petergerl-mathematiker.tumblr.com) at the University of Salzburg.

# Installation

Compile the project by running

    mvn package

This will generate juvavum.jar and the native jbliss-library (e.g. libjbliss.jnilib) in the target-folder. The latter is only needed, if you use the program with the -G option (see below).

# Usage

    usage: java -jar juvavum.jar [-c] [-g <arg>] [-G] -h <arg> [-i] [-m] [-n]
       [-s] -w <arg>
     -c,--components       Break game into components and use the
                           Grundy-Sprague theorem for sums of games
     -g,--game <arg>       Game type (Juvavum, Domino Juvavum, Cram), default:
                           Juvavum
     -G,--graph-analysis   Use graph based analysis (Cram only)
     -h,--height <arg>     Height of the game board
     -i,--isomorphisms     Use graph isomorphisms
     -m,--misere           Misere type game
     -n,--normalforms      Use normal forms (Juvavum only)
     -s,--symmetries       Use symmetries
     -w,--width <arg>      Width of the game board

e.g. `java -jar juvavum.jar -h 3 -w 3 -g CRAM -s`

The `-i` option requires `jbliss`. Copy the `jbliss` library (`libjbliss.so` on Linux) from `target/classes` to your `java.library.path` or specify it like this: `java -jar -Djava.library.path=target target/juvavum.jar -h 3 -w 3 -g cram -G -i -c`

This project uses bliss and its java wrapper jbliss: [http://www.tcs.tkk.fi/Software/bliss/index.html](http://www.tcs.tkk.fi/Software/bliss/index.html).

# Requirements
This program requires Java 7 or higher.

The `Makefile` for jbliss has been updated for Java 9 (using `javac -h` instead of `javah`) but can be modified to support older versions.

# Results
I am collecting results in this [Google Sheet](https://docs.google.com/spreadsheets/d/1QFaqaRN4wdvPGEEx9gDphZzy8yk3d-RP9WAN8shMqsU/edit?usp=sharing).
