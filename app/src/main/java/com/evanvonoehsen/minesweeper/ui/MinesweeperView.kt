package com.evanvonoehsen.minesweeper.ui

import android.R.attr.scaleHeight
import android.R.attr.scaleWidth
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.evanvonoehsen.minesweeper.MainActivity
import com.evanvonoehsen.minesweeper.R
import com.evanvonoehsen.minesweeper.model.MinesweeperModel
import com.google.android.material.snackbar.Snackbar


class MinesweeperView (context: Context?, attrs: AttributeSet?) : View(context, attrs)  {
    private var paintBackground: Paint
    private var paintLine: Paint
    private var paintLineRed: Paint
    private var blackText: Paint
    private var redText: Paint
    private var paintSquare: Paint

    var bitmapMine = BitmapFactory.decodeResource(
            context?.resources, R.drawable.mine
    )

    var bitmapFlag = BitmapFactory.decodeResource(
            context?.resources, R.drawable.flag
    )

    private var tmpFlagPlacer: PointF? = null
    init {

        paintBackground = Paint()
        paintBackground.color = Color.LTGRAY
        paintBackground.style = Paint.Style.FILL

        paintLine = Paint()
        paintLine.color = Color.DKGRAY
        paintLine.strokeWidth = 5f
        paintLine.style = Paint.Style.STROKE

        paintLineRed = Paint()
        paintLineRed.color = Color.RED
        paintLineRed.strokeWidth = 5f
        paintLineRed.style = Paint.Style.STROKE

        blackText = Paint()
        blackText.color = Color.DKGRAY
        blackText.textSize = 60f
        blackText.textAlign = Paint.Align.CENTER
        blackText.typeface = Typeface.DEFAULT_BOLD

        redText = Paint()
        redText.color = Color.RED
        redText.textSize = 60f
        redText.textAlign = Paint.Align.CENTER
        redText.typeface = Typeface.DEFAULT_BOLD

        paintSquare = Paint()
        paintSquare.color = Color.rgb(241, 241, 241)
        paintSquare.style = Paint.Style.FILL

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val gameWidth = MinesweeperModel.getGameWidth()
//        bitmapMine = Bitmap.createScaledBitmap(bitmapMine, width / gameWidth, height / gameWidth, false)
//        bitmapFlag = Bitmap.createScaledBitmap(bitmapFlag, width / gameWidth, height / gameWidth, false)
    }
    override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

//            Log.v(MainActivity.LOGCAT_TAG, "Drawing game area of size ${MinesweeperModel.getWidth()}")

            canvas?.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintBackground)
            drawGameArea(canvas, MinesweeperModel.getGameWidth())
            fillGameAreaSquares(canvas)
            drawTmpFlagPlacer(canvas)

    }

    private fun drawGameArea(canvas: Canvas, cols: Short) {
        val rows = cols
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paintLine)

        for (i in 1 until rows) {
            canvas.drawLine(
                0f,
                ((height / rows) * i).toFloat(),
                width.toFloat(),
                ((height / rows) * i).toFloat(),
                paintLine
            )
        }
        for (i in 1 until cols) {
            canvas.drawLine(
                ((width / cols) * i).toFloat(),
                0f,
                ((width / cols) * i).toFloat(),
                height.toFloat(),
                paintLine
            )
        }
    }

    private fun drawTmpFlagPlacer(canvas: Canvas) {
        if (tmpFlagPlacer != null) {
            val bitmapScale = width / MinesweeperModel.getGameWidth()
            val resizedBitmap = Bitmap.createScaledBitmap(bitmapFlag, bitmapScale,bitmapScale, false)
            canvas?.drawBitmap(resizedBitmap, tmpFlagPlacer!!.x - (bitmapScale / 2), tmpFlagPlacer!!.y - (bitmapScale / 2), null)
//            canvas.drawCircle(tmpFlagPlacer!!.x, tmpFlagPlacer!!.y, 20.0F, paintLine)
        }
    }

    private fun fillGameAreaSquares(canvas: Canvas) {
        if (MinesweeperModel.isModelInitialized()) {
            val bitmapScale = width / MinesweeperModel.getGameWidth()
            val resizedFlagBitmap = Bitmap.createScaledBitmap(bitmapFlag, bitmapScale,bitmapScale, false)
            val resizedMineBitmap = Bitmap.createScaledBitmap(bitmapMine, bitmapScale,bitmapScale, false)
            val showMines = !MinesweeperModel.isGameInProgress()
            redText.textSize = bitmapScale.toFloat()
            blackText.textSize = bitmapScale.toFloat()
            for (i in 0..MinesweeperModel.getGameWidth()-1) {
                for (j in 0..MinesweeperModel.getGameWidth()-1) {
                    val content = MinesweeperModel.getFieldContent(i, j)
                    if (content == MinesweeperModel.MINE) {
//                        Log.v(MainActivity.LOGCAT_TAG, "Found a mine location")
                        if (showMines) {
                            canvas?.drawBitmap(resizedMineBitmap, (bitmapScale * i).toFloat(), (bitmapScale * j).toFloat(), null)
                        }

                    } else if (content == MinesweeperModel.FLAG_ON_EMPTY) {
//                        Log.v(MainActivity.LOGCAT_TAG, "Found a flag location")
                        canvas?.drawBitmap(resizedFlagBitmap, (bitmapScale * i).toFloat(), (bitmapScale * j).toFloat(), null)

                    } else if (content == MinesweeperModel.FLAG_ON_MINE) {
                        canvas?.drawRect(
                                (bitmapScale * i).toFloat() + 5f,
                                (bitmapScale * j).toFloat() + 5f,
                                (bitmapScale * i).toFloat() + bitmapScale - 5f,
                                (bitmapScale * j).toFloat() + bitmapScale - 5f,
                                paintSquare)
                        if (showMines) {
                            canvas?.drawBitmap(resizedMineBitmap, (bitmapScale * i).toFloat(), (bitmapScale * j).toFloat(), null)
                        }
                        canvas?.drawBitmap(resizedFlagBitmap, (bitmapScale * i).toFloat(), (bitmapScale * j).toFloat(), null)
                    } else if (content == MinesweeperModel.LOST_TILE) {
//                        Log.v(MainActivity.LOGCAT_TAG, "Found a mine location")
                        if (showMines) {
                            canvas?.drawBitmap(resizedMineBitmap, (bitmapScale * i).toFloat(), (bitmapScale * j).toFloat(), null)
                            val tileWidth = width / MinesweeperModel.getGameWidth()
                            canvas.drawLine(
                                    (i * tileWidth).toFloat(), (j * tileWidth).toFloat(),
                                    ((i + 1) * tileWidth).toFloat(),
                                    ((j + 1) * tileWidth).toFloat(), paintLineRed
                            )

                            canvas.drawLine(
                                    ((i + 1) * tileWidth).toFloat(), (j * tileWidth).toFloat(),
                                    (i * tileWidth).toFloat(), ((j + 1) * tileWidth).toFloat(), paintLineRed
                            )
                        }

                    } else if (content == MinesweeperModel.EMPTY) {
                        canvas?.drawRect(
                                (bitmapScale * i).toFloat() + 5f,
                                (bitmapScale * j).toFloat() + 5f,
                                (bitmapScale * i).toFloat() + bitmapScale - 5f,
                                (bitmapScale * j).toFloat() + bitmapScale - 5f,
                                paintSquare)
                    } else if (content > MinesweeperModel.NUM_BUFFER) {
                        canvas?.drawRect(
                                (bitmapScale * i).toFloat() + 5f,
                                (bitmapScale * j).toFloat() + 5f,
                                (bitmapScale * i).toFloat() + bitmapScale - 5f,
                                (bitmapScale * j).toFloat() + bitmapScale - 5f,
                                paintSquare)
                        val proximityNumber = content - MinesweeperModel.NUM_BUFFER
                        if (proximityNumber > 2.toShort()) {
                            canvas?.drawText(proximityNumber.toString(), (bitmapScale * i).toFloat() + bitmapScale/2, (bitmapScale * j).toFloat() + bitmapScale, redText)
                        } else {
                            canvas?.drawText(proximityNumber.toString(), (bitmapScale * i).toFloat() + bitmapScale/2, (bitmapScale * j).toFloat() + bitmapScale, blackText)
                        }
                    }
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (MinesweeperModel.isGameInProgress()) {
            if (MinesweeperModel.isFlagMode) {
                if (event?.action == MotionEvent.ACTION_MOVE) {
                    tmpFlagPlacer = PointF(event.x, event.y)
                    invalidate()

                } else if (event?.action == MotionEvent.ACTION_UP) {
                    tmpFlagPlacer = null
                    val numCols = MinesweeperModel.getGameWidth()

                    val tX = event.x.toInt() / (width / MinesweeperModel.getGameWidth())
                    val tY = event.y.toInt() / (height / MinesweeperModel.getGameWidth())

                    if (tX < numCols && tY < numCols) {
                        val fieldContent = MinesweeperModel.getFieldContent(tX, tY)
                        if (fieldContent == MinesweeperModel.MINE) {
                            MinesweeperModel.setFieldContent(tX, tY, MinesweeperModel.FLAG_ON_MINE)
                            invalidate()
                            if (MinesweeperModel.removeMineAndCheckWon()) {
                                MinesweeperModel.endGame()
                                Snackbar.make(MainActivity.layout!!,R.string.won_msg,
                                        Snackbar.LENGTH_LONG).setDuration(10000).show()
                            }

                        } else if (fieldContent == MinesweeperModel.VEILED_EMPTY) {
                            MinesweeperModel.setFieldContent(tX, tY, MinesweeperModel.FLAG_ON_EMPTY)
                            MinesweeperModel.endGame()
                            invalidate()
                            Snackbar.make(MainActivity.layout!!,R.string.lost_flag_msg,
                                    Snackbar.LENGTH_LONG).setDuration(10000).show()
                        }
//                    var textStatus = context.getString(R.string.text_next_player, next)
//                    (context as MainActivity).setStatusText(textStatus)
                    }
                }
            } else {
                if (event?.action == MotionEvent.ACTION_UP) {
                    tmpFlagPlacer = null
                    val numCols = MinesweeperModel.getGameWidth()

                    val tX = event.x.toInt() / (width / MinesweeperModel.getGameWidth())
                    val tY = event.y.toInt() / (height / MinesweeperModel.getGameWidth())

                    if (tX < numCols && tY < numCols) {
                        invalidate()
                        if (MinesweeperModel.getFieldContent(tX, tY) == MinesweeperModel.VEILED_EMPTY) {
                            //SURROUNDINGS LOGIC
                            val newContent = (MinesweeperModel.getNumSurroundingMines(tX, tY) + MinesweeperModel.EMPTY).toShort()
                            MinesweeperModel.setFieldContent(tX, tY, newContent)
                        } else if (MinesweeperModel.getFieldContent(tX, tY) == MinesweeperModel.MINE) {
                            MinesweeperModel.endGame()
                            MinesweeperModel.setFieldContent(tX, tY, MinesweeperModel.LOST_TILE)
                            Snackbar.make(MainActivity.layout!!,R.string.lost_mine_msg,
                                    Snackbar.LENGTH_LONG).setDuration(10000).show()
                        }
                    }
                }
            }
        }
        return true
    }

    public fun resetGame(width: Short){
        MinesweeperModel.resetModel(width)
        invalidate()
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        val w = View.MeasureSpec.getSize(widthMeasureSpec)
//        val h = View.MeasureSpec.getSize(heightMeasureSpec)
//        val d = if (w == 0) h else if (h == 0) w else if (w < h) w else h
//        setMeasuredDimension(d, d)
//    }
}