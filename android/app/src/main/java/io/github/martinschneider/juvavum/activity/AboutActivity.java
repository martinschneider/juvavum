package io.github.martinschneider.juvavum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import io.github.martinschneider.juvavum.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(AboutActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_game_controls:
                startActivity(new Intent(AboutActivity.this, GameControlsActivity.class));
                return true;
            case R.id.action_rules:
                startActivity(new Intent(AboutActivity.this, RulesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView textView = findViewById(R.id.textView);
        textView.setText(Html.fromHtml(getString(R.string.about_text)));
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        //textView.setMovementMethod(new ScrollingMovementMethod());
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.title_activity_about);
        }
    }
}
