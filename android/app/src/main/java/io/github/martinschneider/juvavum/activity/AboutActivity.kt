package io.github.martinschneider.juvavum.activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.martinschneider.juvavum.R

class AboutActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this@AboutActivity, SettingsActivity::class.java))
                true
            }
            R.id.action_game_controls -> {
                startActivity(Intent(this@AboutActivity, GameControlsActivity::class.java))
                true
            }
            R.id.action_rules -> {
                startActivity(Intent(this@AboutActivity, RulesActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = Html.fromHtml(getString(R.string.about_text))
        textView.isClickable = true
        textView.movementMethod = LinkMovementMethod.getInstance()
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.title_activity_about)
        }
    }
}