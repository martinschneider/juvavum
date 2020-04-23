package io.github.martinschneider.juvavum.model

import android.view.Surface
import juvavum.analyse.AbstractAnalysis
import juvavum.analyse.Board
import java.util.*

class GameBoard {
    private var board: Array<IntArray>
    var height: Int
        private set
    var width: Int
        private set
    private var emptyFields: Int

    constructor(b: GameBoard) {
        width = b.width
        height = b.height
        emptyFields = height * width
        board = Array(width) { IntArray(height) }
        for (i in 1..height) {
            for (j in 1..width) {
                board[j - 1][i - 1] = b.board[j - 1][i - 1]
                if (board[j - 1][i - 1] != 0) {
                    emptyFields--
                }
            }
        }
    }

    constructor(h: Int, w: Int) {
        height = h
        width = w
        emptyFields = h * w
        board = Array(w) { IntArray(h) }
    }

    operator fun get(x: Int, y: Int): Int {
        var ret: Int
        ret = try {
            board[y - 1][x - 1]
        } catch (e: ArrayIndexOutOfBoundsException) {
            return 3
        }
        return ret
    }

    operator fun set(player: Int, x: Int, y: Int) {
        val prev = board[y - 1][x - 1]
        board[y - 1][x - 1] = player
        if (prev != 0 && player == 0) {
            emptyFields++
        } else if (prev == 0 && player != 0) {
            emptyFields--
        }
    }

    fun clear(x: Int, y: Int) {
        val prev = board[y - 1][x - 1]
        board[y - 1][x - 1] = 0
        if (prev != 0) {
            emptyFields++
        }
    }

    fun isFree(x: Int, y: Int): Boolean {
        return board[y - 1][x - 1] == 0
    }

    fun prefill(prefillPerc: Int) {
        val prefill = height * width * (prefillPerc.toDouble() / 100)
        val flattenedArray = IntArray(height * width)
        run {
            var i = 0
            while (i < prefill) {
                flattenedArray[i] = 3
                i++
            }
        }
        shuffleArray(flattenedArray)
        var k = 0
        for (i in board.indices) {
            for (j in 0 until board[i].size) {
                board[i][j] = flattenedArray[k++]
            }
        }
    }

    private fun shuffleArray(array: IntArray) {
        var index: Int
        val random = Random()
        for (i in array.size - 1 downTo 1) {
            index = random.nextInt(i + 1)
            if (index != i) {
                array[index] = array[index] xor array[i]
                array[i] = array[i] xor array[index]
                array[index] = array[index] xor array[i]
            }
        }
    }

    fun flatten(): Long {
        var value: Long = 0
        for (i in 0 until width * height) {
            if (board[i % width][i / width] != 0) {
                value += 1L shl i
            }
        }
        return value
    }

    fun countEmptyFields(): Int {
        return emptyFields
    }

    override fun hashCode(): Int {
        return Arrays.deepHashCode(board)
    }

    override fun equals(o: Any?): Boolean {
        if (o !is GameBoard) {
            return false
        }
        return Arrays.deepEquals(board, o.board)
    }

    fun transform(previousOrientation: Int, orientation: Int): GameBoard {
        val newGameBoard = GameBoard(width, height)
        for (i in 1..height) {
            for (j in 1..width) {
                newGameBoard[this[i, j], j] = i
            }
        }
        if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_270) {
            newGameBoard.fliplr()
        } else if (previousOrientation == Surface.ROTATION_270 && orientation == Surface.ROTATION_0) {
            newGameBoard.flipud()
        } else if (previousOrientation == Surface.ROTATION_0 && orientation == Surface.ROTATION_90) {
            newGameBoard.flipud()
        } else if (previousOrientation == Surface.ROTATION_90 && orientation == Surface.ROTATION_0) {
            newGameBoard.fliplr()
        }
        return newGameBoard
    }

    val simpleBoard: Board
        get() {
            val simpleBoard = Board(width, height)
            for (i in 1..height) {
                for (j in 1..width) {
                    if (!isFree(i, j)) {
                        simpleBoard[i] = j
                    }
                }
            }
            return simpleBoard
        }

    private fun toGameBoard(curr: Board, prev: GameBoard, currentPlayer: Int): GameBoard {
        val next = GameBoard(prev)
        for (i in 1..height) {
            for (j in 1..width) {
                if (prev.isFree(i, j) && !curr.isFree(i, j)) {
                    next[currentPlayer, i] = j
                }
            }
        }
        return next
    }

    fun getSuccessors(analysis: AbstractAnalysis, currentPlayer: Int): Set<GameBoard> {
        val successors: MutableSet<GameBoard> = HashSet()
        val simpleBoard = simpleBoard
        analysis.setBoard(simpleBoard)
        val children = analysis.addChildren(simpleBoard, HashSet())
        for (child in children) {
            successors.add(toGameBoard(child.board, this, currentPlayer))
        }
        return successors
    }

    /**
     * flip board vertically
     */
    fun fliplr(): GameBoard {
        val dy = board.size
        val dx: Int = board[0].size
        var help: Int
        for (xi in 0 until dx) {
            for (yi in 0 until dy / 2) {
                help = board[yi][xi]
                board[yi][xi] = board[dy - 1 - yi][xi]
                board[dy - 1 - yi][xi] = help
            }
        }
        return this
    }

    /**
     * flip board horizontally
     */
    fun flipud(): GameBoard {
        val dy = board.size
        val dx: Int = board[0].size
        var help: Int
        for (xi in 0 until dx / 2) {
            for (yi in 0 until dy) {
                help = board[yi][xi]
                board[yi][xi] = board[yi][dx - 1 - xi]
                board[yi][dx - 1 - xi] = help
            }
        }
        return this
    }

    /** @return string representation
     */
    override fun toString(): String {
        var ret = ""
        for (i in 0 until board[0].size) {
            for (j in board.indices) {
                ret += board[j][i].toString() + " "
            }
            ret += "\n"
        }
        return ret
    }
}