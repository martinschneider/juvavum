package io.github.martinschneider.juvavum.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.martinschneider.juvavum.R;
import io.github.martinschneider.juvavum.model.GameBoard;
import io.github.martinschneider.juvavum.utils.Stack;
import io.github.martinschneider.juvavum.utils.Utils;
import juvavum.analyse.AbstractAnalysis;
import juvavum.analyse.Board;
import juvavum.analyse.CRAMAnalysis;
import juvavum.analyse.DJUVAnalysis;

public class BoardView extends View {

    public static final int HUMAN_WINS = 1;
    public static final int COMPUTER_WINS = 2;
    public static final int GAME_ONGOING = 0;
    public static final int INVALID_MOVE = -1;
    public static final int GAME_OVER = -2;

    private static final int PREFILL_PERCENTAGE = 25;
    private static final int DOUBLE_TAP_TIME = 250;
    // max number of empty fields before end game analysis is triggered
    private static final int END_GAME_ANALYSIS_THRESHOLD = 20;

    private int width;
    private int height;
    private int originalWidth;
    private int originalHeight;
    private int lineWidth;
    private int currentPlayer;
    private boolean humanStarts;
    private boolean misere;
    private boolean sounds;
    private Stack<GameBoard> positions = new Stack<>();
    private String gameType;
    private boolean prefill;
    private int computerStrength = 10;
    private boolean freeze;
    private int previousOrientation;
    private AbstractAnalysis analysis;
    private boolean endGameAnalysisDone;
    private Map<Board, Integer> grundyMap;

    private int lastValue = -1;
    private long lastDown;
    private Set<Pair<Integer, Integer>> currentGesture = new HashSet<>();
    private Set<Pair<Integer, Integer>> currentMove = new HashSet<>();

    private Paint black;
    private Paint[] playerColours = new Paint[4];
    private int xOffset;
    private int yOffset;
    private int fieldSize;

    public BoardView(Context context) {
        super(context);
        initialiseLineWidth();
        initialiseColours();
        loadSettings();
        newGame();
    }

    private void initialiseLineWidth() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        lineWidth = Math.max(2, metrics.densityDpi / 75);
    }

    private void initialiseColours() {
        black = new Paint();
        black.setColor(Color.BLACK);
        black.setStrokeWidth(lineWidth);
        playerColours[0] = new Paint();
        playerColours[0].setColor(Color.WHITE);
        playerColours[1] = new Paint();
        playerColours[1].setColor(Color.RED);
        playerColours[2] = new Paint();
        playerColours[2].setColor(Color.BLUE);
        playerColours[3] = new Paint();
        playerColours[3].setColor(Color.DKGRAY);
    }

    // TODO: listen to the preference change event instead
    public boolean reloadIfChanged() {
        if (gameType != PreferenceManager.getDefaultSharedPreferences(getContext()).getString("gameType", "JUV")) {
            newGame();
            return true;
        }
        if (misere != PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("misere", false)) {
            newGame();
            return true;
        }
        if (computerStrength != Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("computerStrength", "3"))) {
            newGame();
            return true;
        }
        if (prefill != PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefill", false)) {
            newGame();
            return true;
        }
        if (humanStarts != PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("humanStarts", false)) {
            newGame();
            return true;
        }
        sounds = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("sounds", false);
        if (originalHeight != Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("height", "5"))) {
            newGame();
            return true;
        }
        if (originalWidth != Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("width", "5"))) {
            newGame();
            return true;
        }
        return false;
    }

    private void loadSettings() {
        misere = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("misere", false);
        computerStrength = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("computerStrength", "3"));
        gameType = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("gameType", "DJUV");
        if (!gameType.equals("DJUV") && !gameType.equals("CRAM")) {
            gameType = "DJUV";
        }
        humanStarts = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("humanStarts", false);
        sounds = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("sounds", false);
        originalHeight = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("height", "5"));
        originalWidth = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("width", "5"));
        height = originalHeight;
        width = originalWidth;
        prefill = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("prefill", false);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (canvas.getWidth() > canvas.getHeight() && width < height || canvas.getWidth() < canvas.getHeight() && width > height) {
            int orientation = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            int tmp = width;
            width = height;
            height = tmp;
            GameBoard newPos = positions.pop();
            GameBoard oldPos = positions.pop();
            oldPos = oldPos.transform(previousOrientation, orientation);
            newPos = newPos.transform(previousOrientation, orientation);
            positions.push(oldPos);
            positions.push(newPos);
            currentGesture = transform(currentGesture, previousOrientation, orientation);
            currentMove = transform(currentMove, previousOrientation, orientation);
            previousOrientation = orientation;
        }
        int vMargin = Utils.dpToPx(getResources(), 50);
        int hMargin = Utils.dpToPx(getResources(), 20);
        int canvasWidth = canvas.getWidth() - 2 * hMargin;
        int canvasHeight = canvas.getHeight() - 2 * vMargin;
        fieldSize = Math.min(canvasWidth / width, canvasHeight / height);
        xOffset = hMargin + (canvasWidth - width * fieldSize) / 2;
        yOffset = vMargin + (canvasHeight - height * fieldSize) / 2;
        drawGrid(canvas);
        drawBoard(canvas);
    }

    private Set<Pair<Integer, Integer>> transform(Set<Pair<Integer, Integer>> coordinates, int previousOrientation, int orientation) {
        Set<Pair<Integer, Integer>> newCoordinates = new HashSet<>();
        for (Pair<Integer, Integer> pair : coordinates) {
            if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_270) {
                newCoordinates.add(Pair.create(pair.second, width - pair.first + 1));
            } else if (previousOrientation == Surface.ROTATION_270 && orientation == Surface.ROTATION_0) {
                newCoordinates.add(Pair.create(pair.second, width - pair.first + 1));
            } else if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_90) {
                newCoordinates.add(Pair.create(height - pair.second + 1, pair.first));
            } else if (previousOrientation == Surface.ROTATION_90 && orientation == Surface.ROTATION_0) {
                newCoordinates.add(Pair.create(height - pair.second + 1, pair.first));
            }
        }
        return newCoordinates;
    }

    private boolean isValid(GameBoard oldPos, GameBoard newPos) {
        return oldPos.getSuccessors(analysis, currentPlayer).contains(newPos);
    }

    private boolean isInsideBoard(float x, float y) {
        return x >= xOffset && x <= xOffset + width * fieldSize && y >= yOffset && y <= yOffset + height * fieldSize;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        if (!freeze) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!isInsideBoard(x, y) && System.currentTimeMillis() - lastDown < DOUBLE_TAP_TIME) {
                        clearCurrentMove();
                        playSound(R.raw.sweep);
                        return true;
                    }
                    lastDown = System.currentTimeMillis();
                    if (isInsideBoard(x, y)) {
                        int i = (int) (x - xOffset) / fieldSize + 1;
                        int j = (int) (y - yOffset) / fieldSize + 1;
                        Pair coordinates = Pair.create(j, i);
                        int value = positions.peekFirst().get(j, i);
                        if (value == 0) {
                            positions.peekFirst().set(currentPlayer, j, i);
                            currentGesture.add(coordinates);
                            currentMove.add(coordinates);
                            lastValue = 0;
                        } else if (value == currentPlayer && currentMove.contains(coordinates)) {
                            positions.peekFirst().clear(j, i);
                            currentGesture.add(coordinates);
                            currentMove.add(coordinates);
                            lastValue = currentPlayer;
                        }
                        invalidate();
                    }
                    break;
                case MotionEvent.ACTION_MOVE: {
                    if (isInsideBoard(x, y)) {
                        int i = (int) (x - xOffset) / fieldSize + 1;
                        int j = (int) (y - yOffset) / fieldSize + 1;
                        Pair coordinates = Pair.create(j, i);
                        if (!currentGesture.contains(coordinates) && positions.peekFirst().get(j, i) == lastValue) {
                            if (lastValue == 0) {
                                positions.peekFirst().set(currentPlayer, j, i);
                                currentGesture.add(coordinates);
                                currentMove.add(coordinates);
                            } else if (lastValue == currentPlayer && currentMove.contains(coordinates)) {
                                positions.peekFirst().clear(j, i);
                                currentGesture.add(coordinates);
                                currentMove.add(coordinates);
                                lastValue = currentPlayer;
                            }
                            invalidate();
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP: {
                    if (isInsideBoard(x, y) && sounds) {
                        if (lastValue == 0) {
                            playSound(R.raw.domino2);
                        } else {
                            playSound(R.raw.pickup);
                        }
                    }
                    currentGesture.clear();
                }
            }
        }
        invalidate();
        return true;
    }

    private void drawGrid(Canvas canvas) {
        // vertical lines
        for (int i = 0; i <= width; i++) {
            canvas.drawLine(xOffset + i * fieldSize, yOffset, xOffset + i * fieldSize, yOffset + height * fieldSize, black);
        }
        // horizontal lines
        for (int i = 0; i <= height; i++) {
            canvas.drawLine(xOffset - lineWidth / 2, yOffset + i * fieldSize, xOffset + width * fieldSize + lineWidth / 2, yOffset + i * fieldSize, black);
        }
    }

    private void drawBoard(Canvas canvas) {
        for (int i = 1; i <= positions.peekFirst().getHeight(); i++) {
            for (int j = 1; j <= positions.peekFirst().getWidth(); j++) {
                int value = positions.peekFirst().get(i, j);
                fillField(canvas, value, j, i);
            }
        }
    }

    private void fillField(Canvas canvas, int player, int x, int y) {
        canvas.drawRect(xOffset + (x - 1) * fieldSize + lineWidth / 2, yOffset + (y - 1) * fieldSize + lineWidth / 2, xOffset + x * fieldSize - lineWidth / 2, yOffset + y * fieldSize - lineWidth / 2, playerColours[player]);
    }

    public int confirmMove() {
        if (freeze) {
            return GAME_OVER;
        }
        if (isValid(positions.peekSecond(), positions.peekFirst())) {
            if (sounds) {
                playSound(R.raw.domino1);
            }
            currentMove.clear();
            Set<GameBoard> successors = positions.peekFirst().getSuccessors(analysis, currentPlayer);
            if (!successors.isEmpty()) {
                currentPlayer = (currentPlayer == 1) ? 2 : 1;
                computerMove();
                positions.push(new GameBoard(positions.peekFirst()));
                successors = positions.peekFirst().getSuccessors(analysis, currentPlayer);
                invalidate();
                if (misere && !successors.isEmpty()) {
                    boolean misereOver = true;
                    for (GameBoard b : successors) {
                        if (b.getSuccessors(analysis, currentPlayer).size() > 0) {
                            misereOver = false;
                            break;
                        }
                    }
                    if (misereOver) {
                        return COMPUTER_WINS;
                    }
                    return GAME_ONGOING;
                }
                return (successors.isEmpty()) ? (misere) ? HUMAN_WINS : COMPUTER_WINS : GAME_ONGOING;
            } else {
                return (misere) ? COMPUTER_WINS : HUMAN_WINS;
            }
        } else {
            return INVALID_MOVE;
        }
    }

    private void computerMove() {
        Set<GameBoard> successors = positions.peek().getSuccessors(analysis, currentPlayer);
        if (positions.peek().countEmptyFields() <= END_GAME_ANALYSIS_THRESHOLD) {
            if (!endGameAnalysisDone) {
                analysis.grundy();
                grundyMap = analysis.getGrundyMap();
                endGameAnalysisDone = true;
            }
            Set<GameBoard> winningMoves = new HashSet<>();
            for (GameBoard gameBoard : successors) {
                Board simpleBoard = gameBoard.getSimpleBoard();
                if (grundyMap.containsKey(simpleBoard) && grundyMap.get(simpleBoard) == 0) {
                    winningMoves.add(gameBoard);
                }
            }
            Set<GameBoard> loosingMoves = new HashSet<>(successors);
            loosingMoves.removeAll(winningMoves);
            if (loosingMoves.isEmpty()) {
                successors = winningMoves;
            } else if (winningMoves.isEmpty()) {
                successors = loosingMoves;
            } else {
                successors = (Math.random() > ((double) 2 * computerStrength) / 10) ? loosingMoves : winningMoves;
            }
        }
        positions.push(successors.stream().skip((int) (successors.size() * Math.random())).findFirst().get());
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }

    public void newGame() {
        endGameAnalysisDone = false;
        loadSettings();
        currentMove.clear();
        positions.clear();
        GameBoard position = new GameBoard(height, width);
        if (prefill) {
            position.prefill(PREFILL_PERCENTAGE);
        }
        positions.push(position);
        positions.push(new GameBoard(position));
        switch (gameType) {
            case "CRAM":
                analysis = new CRAMAnalysis(positions.peekFirst().getSimpleBoard(), misere);
                break;
            case "DJUV":
                analysis = new DJUVAnalysis(positions.peekFirst().getSimpleBoard(), misere);
        }
        if (humanStarts) {
            currentPlayer = 1;
        } else {
            currentPlayer = 2;
            computerMove();
            positions.push(new GameBoard(positions.peekFirst()));
        }
        unfreeze();
    }

    public void freeze() {
        freeze = true;
        for (int i = 0; i < playerColours.length; i++) {
            playerColours[i].setAlpha(127);
        }
        black.setAlpha(127);
        invalidate();
    }

    public void unfreeze() {
        freeze = false;
        for (int i = 0; i < playerColours.length; i++) {
            playerColours[i].setAlpha(255);
        }
        black.setAlpha(255);
        invalidate();
    }

    private void clearCurrentMove() {
        for (Pair<Integer, Integer> pair : currentMove) {
            positions.peekFirst().clear(pair.first, pair.second);
        }
        currentMove.clear();
    }

    private void playSound(int sound) {
        if (sounds) {
            MediaPlayer.create(getContext(), sound).start();
        }
    }

    private boolean isRotated(int orientation) {
        return orientation == Surface.ROTATION_270 || orientation == Surface.ROTATION_90;
    }

    public boolean undoMove() {
        if ((!humanStarts && positions.size() < 6) || (humanStarts && positions.size() < 3)) // no moves to undo
        {
            return false;
        }
        positions.pop();
        positions.pop();
        positions.pop();
        positions.push(new GameBoard(positions.peekFirst()));
        unfreeze();
        invalidate();
        return true;
    }
}
