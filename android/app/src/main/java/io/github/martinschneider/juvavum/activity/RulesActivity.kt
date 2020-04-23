package io.github.martinschneider.juvavum.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.github.martinschneider.juvavum.R

class RulesActivity : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this@RulesActivity, SettingsActivity::class.java))
                true
            }
            R.id.action_game_controls -> {
                startActivity(Intent(this@RulesActivity, GameControlsActivity::class.java))
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this@RulesActivity, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        val textView = findViewById<TextView>(R.id.textView)
        textView.text = Html.fromHtml(getString(R.string.help_text), ImageGetter(), null)
        textView.movementMethod = ScrollingMovementMethod()
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setTitle(R.string.title_activity_rules)
        }
    }

    private inner class ImageGetter : Html.ImageGetter {
        override fun getDrawable(source: String): Drawable {
            val id = this@RulesActivity.resources.getIdentifier(source, "drawable", this@RulesActivity.packageName)
            val d = resources.getDrawable(id)
            d.setBounds(0, 0, d.intrinsicWidth, d.intrinsicHeight)
            return d
        }
    }
}