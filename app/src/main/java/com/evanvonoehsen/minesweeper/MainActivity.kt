package com.evanvonoehsen.minesweeper

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import com.evanvonoehsen.minesweeper.model.MinesweeperModel
import com.evanvonoehsen.minesweeper.ui.MinesweeperView
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Error

class MainActivity : AppCompatActivity() {
    companion object {
        const val LOGCAT_TAG = "PRINT_DEBUGGING"
        var layout:LinearLayout? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnReset.setOnClickListener{
            val size = etSize.text.toString()
            Log.v(LOGCAT_TAG, "Tapped the new game button")
            if (size == "") {
                etSize.setError("Please enter the board dimension.")
            } else if (size.toShort() > MinesweeperModel.MAX_WIDTH || size.toShort() < MinesweeperModel.MIN_WIDTH) {
                etSize.setError("The board size must be from ${MinesweeperModel.MIN_WIDTH} to ${MinesweeperModel.MAX_WIDTH}.")
            } else {
                btnFlag.setBackgroundColor(resources.getColor(R.color.purple_500))
                minesweeperView.resetGame(size.toShort())
            }
        }
        layout = contentLayout

        btnFlag.setOnClickListener{
            if (MinesweeperModel.toggleFlagMode()) {
                btnFlag.setBackgroundColor(Color.RED)
            } else {
                btnFlag.setBackgroundColor(resources.getColor(R.color.purple_500))
            }
        }

    }

}