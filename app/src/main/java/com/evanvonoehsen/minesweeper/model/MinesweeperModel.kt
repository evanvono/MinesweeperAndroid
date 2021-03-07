package com.evanvonoehsen.minesweeper.model

import android.util.Log
import com.evanvonoehsen.minesweeper.MainActivity
import com.evanvonoehsen.minesweeper.ui.MinesweeperView

object MinesweeperModel {
    public val VEILED_EMPTY: Short = 0
    public val MINE: Short = 1
    public val FLAG_ON_MINE: Short = 2
    public val FLAG_ON_EMPTY: Short = 3
    public val EMPTY: Short = 5
    public val LOST_TILE: Short = 4
    public val NUM_BUFFER: Short = 5

    public var isFlagMode = false
    private var model: Array<ShortArray>? = null

    const val MAX_WIDTH = 15
    const val MIN_WIDTH = 5
    const val MINE_RATIO = 0.25

    private var gameInProgress = false
    private var gameWidth: Short = 0
    private var minesLeft: Short = 0

    fun isModelInitialized(): Boolean {
        return (model != null)
    }

    //returns the value of a square and its surroundings. [MM, TL, TM, TR, ML, MR, BL, BM, MR]
    fun getFieldContent(x: Int, y:Int): Short {
        return model!![y][x]
    }

    fun getNumSurroundingMines(x:Int, y:Int) : Short {
        var numMines:Short = 0
        val minX = Math.max(x-1, 0)
        val maxX = Math.min(x+1, gameWidth.toInt()-1)
        val minY = Math.max(y-1, 0)
        val maxY = Math.min(y+1, gameWidth.toInt()-1)

        for (i in minY..maxY) {
            for (j in minX..maxX) {
                val content = getFieldContent(j, i)
                if (content == MINE || content == FLAG_ON_MINE) {
                    numMines++
                }
            }
        }
        return numMines
    }

    fun setFieldContent(x: Int, y: Int, value: Short) {
        model!![y][x] = value
    }

    private fun setGameWidth(width: Short) {
        gameWidth = width
    }

    fun getGameWidth() = gameWidth

    private fun calculateMines(): Int {
        return (MINE_RATIO * (gameWidth * gameWidth).toFloat()).toInt()
    }

    //tracks whether we are placing flags or not
    fun getFlagMode() = isFlagMode

    fun toggleFlagMode(): Boolean {
        isFlagMode = !isFlagMode
        return isFlagMode
    }

    fun isGameInProgress() = gameInProgress

    fun endGame() {
        gameInProgress = false
    }
    //creates our model dynamically to be the size of the board
    private fun initializeModel(width: Short) {
        Log.v(MainActivity.LOGCAT_TAG, "initializing our model")
        model = Array(width.toInt()) { ShortArray(width.toInt()) {0} }
    }

    fun removeMineAndCheckWon(): Boolean {
        minesLeft--
        return (minesLeft == 0.toShort())
    }
    //generates random mines at random locations, to the ratio specified in MINE_RATIO
    private fun placeMines() {
        val totalMines = calculateMines()
        minesLeft = totalMines.toShort()
        val totalSquares = gameWidth * gameWidth

        var minesMap: HashMap<Short, Boolean> = HashMap()
        Log.v(MainActivity.LOGCAT_TAG, "${calculateMines()} mines to be generated")

        while (minesMap.size < totalMines) {
            val nextMineLocation = (Math.random() * totalSquares).toShort()
            if (!minesMap.containsKey(nextMineLocation)) {
                val row = nextMineLocation / gameWidth
                val col = nextMineLocation % gameWidth

                //adding the mine to our model
                model!![row][col] = MINE
                minesMap.put(nextMineLocation, true)
            }
        }
//        Log.v(MainActivity.LOGCAT_TAG, "$minesMap")
    }

    fun resetModel(width: Short) {
        setGameWidth(width)
        initializeModel(width)
        placeMines()
        isFlagMode = false
        gameInProgress = true
    }
}