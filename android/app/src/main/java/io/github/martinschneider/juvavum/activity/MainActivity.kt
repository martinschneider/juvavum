package io.github.martinschneider.juvavum.activity

import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import io.github.martinschneider.juvavum.R
import io.github.martinschneider.juvavum.view.BoardView

class MainActivity : AppCompatActivity() {
    private var boardView: BoardView? = null
    private var mediaPlayer: MediaPlayer? = null
    private var gameOver: Boolean = false
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rules -> {
                startActivity(Intent(this@MainActivity, RulesActivity::class.java))
                true
            }
            R.id.action_game_controls -> {
                startActivity(Intent(this@MainActivity, GameControlsActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        val music = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("music", false)
        if (music && !mediaPlayer!!.isPlaying) {
            mediaPlayer!!.start()
        }
        val newGame: Boolean = boardView !!. reloadIfChanged ()
        findViewById<View>(R.id.buttonUndo).visibility = if (PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("undo", false)) View.VISIBLE else View.GONE
        if (!gameOver || newGame) findViewById<View>(R.id.buttonConfirm).visibility = View.VISIBLE
        super.onResume()
    }

    override fun onPause() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.pause()
        }
        super.onPause()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val music = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("music", false)
        mediaPlayer = MediaPlayer.create(this, R.raw.mozart)
        mediaPlayer?.isLooping = true
        if (music) {
            mediaPlayer?.start()
        }
        boardView = BoardView(this)
        setContentView(R.layout.activity_main)
        val layout = findViewById<RelativeLayout>(R.id.relative_layout)
        layout.addView(boardView)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.setTitle(R.string.title_activity_main)
        }
        val newGameButton = findViewById<Button>(R.id.buttonNewGame)
        newGameButton.setOnClickListener {
            gameOver = false
            findViewById<View>(R.id.buttonConfirm).visibility = View.VISIBLE
            boardView!!.newGame()
        }
        val undoButton = findViewById<Button>(R.id.buttonUndo)
        undoButton.setOnClickListener {
            gameOver = false
            findViewById<View>(R.id.buttonConfirm).visibility = View.VISIBLE
            if (!boardView!!.undoMove()) {
                displayAlert("Error", "No moves to undo")
            }
        }
        val moveButton = findViewById<Button>(R.id.buttonConfirm)
        moveButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val winner = 0
                when (boardView!!.confirmMove()) {
                    BoardView.GAME_OVER -> {}
                    BoardView.INVALID_MOVE -> displayAlert("Invalid move", "Please try again")
                    BoardView.HUMAN_WINS ->
                    {
                        displayWinner("You win")
                        gameOver = true
                    }
                    BoardView.COMPUTER_WINS ->
                    {
                        displayWinner("You lose")
                        gameOver = true
                    }
                }
            }

            private fun displayWinner(message: String) {
                displayAlert("Game over", message)
                moveButton.visibility = View.GONE
                boardView!!.freeze()
            }
        })
    }

    private fun displayAlert(title: String, message: String) {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()
    }
}