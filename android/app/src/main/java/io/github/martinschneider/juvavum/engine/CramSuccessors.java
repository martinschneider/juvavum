package io.github.martinschneider.juvavum.engine;

import java.util.HashSet;
import java.util.Set;

import io.github.martinschneider.juvavum.model.Board;

public class CramSuccessors {

    private Board b;

    public CramSuccessors(Board b) {
        this.b = b;
    }

    public Set<Board> getSuccessors(int currentPlayer) {
        Set<Board> successors = new HashSet<>();
        rowMoves(successors, currentPlayer);
        columnMoves(successors, currentPlayer);
        return successors;
    }

    protected Set<Board> rowMoves(Set<Board> successors, int currentPlayer) {
        for (int j = 1; j <= b.getHeight(); j++) {
            successors = rowMovesInRow(j, successors, currentPlayer);
        }
        return successors;
    }

    protected Set<Board> rowMovesInRow(int j, Set<Board> successors, int currentPlayer) {
        for (int i = 1; i <= b.getWidth(); i++) {
            if (b.get(j, i) == 0 && b.get(j, i + 1) == 0) {
                b.set(currentPlayer, j, i);
                b.set(currentPlayer, j, i + 1);
                successors.add(new Board(b));
                b.clear(j, i);
                b.clear(j, i + 1);
            }
        }
        return successors;
    }

    protected Set<Board> columnMoves(Set<Board> successors, int currentPlayer) {
        for (int i = 1; i <= b.getWidth(); i++) {
            columnMovesInColumn(i, successors, currentPlayer);
        }
        return successors;
    }

    protected Set<Board> columnMovesInColumn(int i, Set<Board> successors, int currentPlayer) {
        for (int j = 1; j <= b.getHeight(); j++) {
            if (b.get(j, i) == 0 && b.get(j + 1, i) == 0) {
                b.set(currentPlayer, j, i);
                b.set(currentPlayer, j + 1, i);
                successors.add(new Board(b));
                b.clear(j, i);
                b.clear(j + 1, i);
            }
        }
        return successors;
    }
}
