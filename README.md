juvavum
=======

Analyse the impartial combinatorical games Juvavum, Domino Juvavum and Cram.

# Installation

Compile the project by running

    mvn package

This will generate juvavum.jar and the native jbliss-library (e.g. libjbliss.jnilib) in the target-folder. The latter is only needed, if you use the program with the -G option (see below).

# Usage

    usage: java -jar juvavum.jar [-f] [-g <arg>] [-G] -h <arg> [-i] [-m] [-n]
           [-s] -w <arg>
     -f,--file             Output result and positions to file
     -g,--game <arg>       Game type (Juvavum, Domino Juvavum, Cram), default:
                           Juvavum
     -G,--graph-analysis   Use graph based analysis (Cram only)
     -h,--height <arg>     Height of the game board
     -i,--isomorphisms     Use isomorphisms
     -m,--misere           Misere type game
     -n,--normalforms      Use normal forms (Juvavum only)
     -s,--symmetries       Use symmetries
     -w,--width <arg>      Width of the game board

e.g. `java -jar juvavum.jar -h 3 - w 3 -g CRAM -s`

The `-G` option requires `jbliss`. You need to set `java.library.path` accordingly, e.g. `java -jar -Djava.library.path=. juvavum.jar -h 3 -w 3 -g cram -G -i`

This project uses bliss and its java wrapper jbliss: [http://www.tcs.tkk.fi/Software/bliss/index.html](http://www.tcs.tkk.fi/Software/bliss/index.html).