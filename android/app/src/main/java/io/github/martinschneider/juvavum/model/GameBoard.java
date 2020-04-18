package io.github.martinschneider.juvavum.model;

import android.view.Surface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import juvavum.analyse.AbstractAnalysis;
import juvavum.analyse.Board;
import juvavum.analyse.Position;

public class GameBoard {

    private int[][] board;
    private int h;
    private int w;
    private int emptyFields;

    public GameBoard(GameBoard b) {
        w = b.w;
        h = b.h;
        emptyFields = h*w;
        board = new int[w][h];
        for (int i = 1; i <= h; i++) {
            for (int j = 1; j <= w; j++) {
                board[j - 1][i - 1] = b.board[j - 1][i - 1];
                if (board[j-1][i-1]!=0)
                {
                    emptyFields--;
                }
            }
        }
    }

    public GameBoard(int h, int w) {
        this.h = h;
        this.w = w;
        emptyFields = h*w;
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
        int prev = board[y - 1][x - 1];
        board[y - 1][x - 1] = player;
        if (prev != 0 && player == 0)
        {
            emptyFields++;
        }
        else if (prev==0 && player!=0)
        {
            emptyFields--;
        }
    }

    public void clear(int x, int y) {
        int prev = board[y - 1][x - 1];
        board[y - 1][x - 1] = 0;
        if (prev!=0)
        {
            emptyFields++;
        }
    }

    public boolean isFree(int x, int y) {
        return board[y - 1][x - 1] == 0;
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

    public long flatten() {
        long value = 0;
        for (int i = 0; i < w * h; i++) {
            if (board[i % w][i / w]!=0) {
                value += (1L << i);
            }
        }
        return value;
    }

    public int countEmptyFields()
    {
        return emptyFields;
    }

    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    public boolean equals(Object o) {
        if (!(o instanceof GameBoard)) {
            return false;
        }
        GameBoard b1 = (GameBoard) o;
        return Arrays.deepEquals(board, b1.board);
    }

    public int getHeight() {
        return h;
    }

    public int getWidth() {
        return w;
    }

    public GameBoard transform(int previousOrientation, int orientation) {
        GameBoard newGameBoard = new GameBoard(this.w, this.h);
        for (int i = 1; i <= h; i++) {
            for (int j = 1; j <= w; j++) {
                newGameBoard.set(this.get(i, j), j, i);
            }
        }
        if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_270) {
            newGameBoard.fliplr();
        } else if (previousOrientation == Surface.ROTATION_270 && orientation == Surface.ROTATION_0) {
            newGameBoard.flipud();
        } else if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_90) {
            newGameBoard.flipud();
        } else if (previousOrientation == Surface.ROTATION_90 && orientation == Surface.ROTATION_0) {
            newGameBoard.fliplr();
        }
        return newGameBoard;
    }

    public Board getSimpleBoard()
    {
        Board simpleBoard = new Board(w, h);
        for (int i = 1; i <= h; i++) {
            for (int j = 1; j <= w; j++) {
                if (!this.isFree(i, j))
                {
                    simpleBoard.set(i, j);
                }
            }
        }
        return simpleBoard;
    }

    private GameBoard toGameBoard(Board curr, GameBoard prev, int currentPlayer)
    {
        GameBoard next = new GameBoard(prev);
        for (int i = 1; i <= h; i++) {
            for (int j = 1; j <= w; j++) {
                if (prev.isFree(i, j) && !curr.isFree(i,j))
                {
                    next.set(currentPlayer, i,j);
                }
            }
        }
        return next;
    }

    public Set<GameBoard> getSuccessors(AbstractAnalysis analysis, int currentPlayer)
    {
        Set<GameBoard> successors = new HashSet<>();
        Board simpleBoard = getSimpleBoard();
        analysis.setBoard(simpleBoard);
        Set<Position> children = analysis.addChildren(simpleBoard, new HashSet<Position>());
        for (Position child : children)
        {
           successors.add(toGameBoard(child.getBoard(), this, currentPlayer));
        }
        return successors;
    }

    /**
     * flip board vertically
     */
    public GameBoard fliplr() {
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
    public GameBoard flipud() {
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

    /** @return string representation */
    @Override
    public String toString() {
        String ret = "";
        for (int i = 0; i < board[0].length; i++) {
            for (int j = 0; j < board.length; j++) {
                    ret += (board[j][i] +" ");
            }
            ret += "\n";
        }
        return ret;
    }
}
