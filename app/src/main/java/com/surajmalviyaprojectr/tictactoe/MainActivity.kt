package com.example.tictactoe

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.surajmalviyaprojectr.tictactoe.R

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnReset: Button

    private lateinit var buttons: List<Button>
    private var currentPlayer = 'X'
    private var gameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnReset = findViewById(R.id.btnReset)

        buttons = listOf(
            findViewById(R.id.b00), findViewById(R.id.b01), findViewById(R.id.b02),
            findViewById(R.id.b10), findViewById(R.id.b11), findViewById(R.id.b12),
            findViewById(R.id.b20), findViewById(R.id.b21), findViewById(R.id.b22),
        )

        buttons.forEach { btn ->
            btn.setOnClickListener { onCellClicked(btn) }
        }

        btnReset.setOnClickListener { resetBoard() }

        updateStatus()
    }

    private fun onCellClicked(btn: Button) {
        if (gameOver) return
        if (btn.text.isNotEmpty()) return

        btn.text = currentPlayer.toString()
        btn.isEnabled = false

        if (checkWin()) {
            gameOver = true
            showWinPopup("Congratulations! Player $currentPlayer wins 🎉")
            disableBoard()
            return
        }

        if (isBoardFull()) {
            gameOver = true
            showWinPopup("It's a Draw 🤝")
            return
        }

        togglePlayer()
        updateStatus()
    }

    private fun updateStatus() {
        tvStatus.text = "Turn: $currentPlayer"
    }

    private fun togglePlayer() {
        currentPlayer = if (currentPlayer == 'X') 'O' else 'X'
    }

    private fun isBoardFull(): Boolean = buttons.all { it.text.isNotEmpty() }

    private fun getCell(r: Int, c: Int): Char {
        val idx = r * 3 + c
        val t = buttons[idx].text
        return if (t.isNullOrEmpty()) '_' else t[0]
    }

    private fun checkWin(): Boolean {
        // Rows & Cols
        for (i in 0..2) {
            if (lineEqual(getCell(i, 0), getCell(i, 1), getCell(i, 2))) return true
            if (lineEqual(getCell(0, i), getCell(1, i), getCell(2, i))) return true
        }
        // Diagonals
        if (lineEqual(getCell(0, 0), getCell(1, 1), getCell(2, 2))) return true
        if (lineEqual(getCell(0, 2), getCell(1, 1), getCell(2, 0))) return true

        return false
    }

    private fun lineEqual(a: Char, b: Char, c: Char): Boolean {
        if (a == '_' || b == '_' || c == '_') return false
        return a == b && b == c
    }

    private fun disableBoard() {
        buttons.forEach { it.isEnabled = false }
    }

    private fun enableBoard() {
        buttons.forEach { it.isEnabled = true }
    }

    private fun resetBoard() {
        buttons.forEach {
            it.text = ""
            it.isEnabled = true
        }
        currentPlayer = 'X'
        gameOver = false
        updateStatus()
    }

    /**
     * Short-lived popup dialog that auto-dismisses after ~1.5 seconds.
     * (Thodi der ke liye "pop" jaisa effect)
     */
    private fun showWinPopup(message: String) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setCancelable(false)
            .create()

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) dialog.dismiss()
            // Auto reset after popup, optional:
            resetBoard()
        }, 1500)
    }
}
