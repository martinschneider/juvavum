package io.github.martinschneider.juvavum.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.martinschneider.juvavum.R

class GameControlsActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_rules -> {
                startActivity(Intent(this@GameControlsActivity, RulesActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(Intent(this@GameControlsActivity, SettingsActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this@GameControlsActivity, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_controls)
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = Html.fromHtml(getString(R.string.game_controls_text))
        textView.movementMethod = ScrollingMovementMethod()
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.title_activity_game_controls)
        }
    }
}