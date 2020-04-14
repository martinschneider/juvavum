package io.github.martinschneider.juvavum.model;

import android.view.Surface;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import io.github.martinschneider.juvavum.engine.CramSuccessors;
import io.github.martinschneider.juvavum.engine.DominoJuvavumSuccessors;

public class Board {

    private int[][] board;
    private int h;
    private int w;

    public Board(Board b) {
        w = b.w;
        h = b.h;
        board = new int[w][h];
        for (int i = 1; i <= h; i++) {
            for (int j = 1; j <= w; j++) {
                board[j - 1][i - 1] = b.board[j - 1][i - 1];
            }
        }
    }

    public Board(int h, int w) {
        this.h = h;
        this.w = w;
        board = new int[w][h];
    }

    public int get(int x, int y) {
        int ret = 3;
        try {
            ret = board[y - 1][x - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 3;
        }
        return ret;
    }

    public void set(int player, int x, int y) {
        board[y - 1][x - 1] = player;
    }

    public void clear(int x, int y) {
        board[y - 1][x - 1] = 0;
    }

    public void prefill(int prefillPerc) {
        double prefill = h * w * ((double) prefillPerc / 100);
        int[] flattenedArray = new int[h * w];
        for (int i = 0; i < prefill; i++) {
            flattenedArray[i] = 3;
        }
        shuffleArray(flattenedArray);
        int k = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                board[i][j] = flattenedArray[k++];
            }
        }
    }

    private void shuffleArray(int[] array) {
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            if (index != i) {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
    }

    public Set<Board> getSuccessors(String gameType, int currentPlayer) {
        switch (gameType) {
            case "DJUV":
                return new DominoJuvavumSuccessors(this).getSuccessors(currentPlayer);
            case "CRAM":
                return new CramSuccessors(this).getSuccessors(currentPlayer);
            default:
                return Collections.emptySet();
        }
    }

    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Board)) {
            return false;
        }
        Board b1 = (Board) o;
        return Arrays.deepEquals(board, b1.board);
    }

    public int getHeight() {
        return h;
    }

    public int getWidth() {
        return w;
    }

    public Board transform(int previousOrientation, int orientation) {
        Board newBoard = new Board(this.w, this.h);
        for (int i = 1; i <= h; i++) {
            for (int j = 1; j <= w; j++) {
                newBoard.set(this.get(i, j), j, i);
            }
        }
        if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_270) {
            newBoard.fliplr();
        } else if (previousOrientation == Surface.ROTATION_270 && orientation == Surface.ROTATION_0) {
            newBoard.flipud();
        } else if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_90) {
            newBoard.flipud();
        } else if (previousOrientation == Surface.ROTATION_90 && orientation == Surface.ROTATION_0) {
            newBoard.fliplr();
        }
        return newBoard;
    }

    /**
     * flip board vertically
     */
    public Board fliplr() {
        int dy = board.length;
        int dx = board[0].length;
        int help;
        for (int xi = 0; xi < dx; xi++) {
            for (int yi = 0; yi < dy / 2; yi++) {
                help = board[yi][xi];
                board[yi][xi] = board[dy - 1 - yi][xi];
                board[dy - 1 - yi][xi] = help;
            }
        }
        return this;
    }

    /**
     * flip board horizontally
     */
    public Board flipud() {
        int dy = board.length;
        int dx = board[0].length;
        int help;
        for (int xi = 0; xi < dx / 2; xi++) {
            for (int yi = 0; yi < dy; yi++) {
                help = board[yi][xi];
                board[yi][xi] = board[yi][dx - 1 - xi];
                board[yi][dx - 1 - xi] = help;
            }
        }
        return this;
    }
}
