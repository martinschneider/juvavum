package io.github.martinschneider.juvavum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import io.github.martinschneider.juvavum.R;

public class GameControlsActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_rules:
                startActivity(new Intent(GameControlsActivity.this, RulesActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(GameControlsActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(GameControlsActivity.this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_controls);
        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml(getString(R.string.game_controls_text)));
        textView.setMovementMethod(new ScrollingMovementMethod());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_game_controls);
        }
    }

}
