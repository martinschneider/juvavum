package io.github.martinschneider.juvavum.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import io.github.martinschneider.juvavum.R;
import io.github.martinschneider.juvavum.view.BoardView;

public class MainActivity extends AppCompatActivity {

    private BoardView boardView;
    private MediaPlayer mediaPlayer;

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
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            case R.id.action_rules:
                startActivity(new Intent(MainActivity.this, RulesActivity.class));
                return true;
            case R.id.action_game_controls:
                startActivity(new Intent(MainActivity.this, GameControlsActivity.class));
                return true;
            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        boolean music = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("music", false);
        if (music && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        boardView.reloadIfChanged();
        findViewById(R.id.buttonUndo).setVisibility((PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("undo", false)) ? View.VISIBLE : View.GONE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean music = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("music", false);
        mediaPlayer = MediaPlayer.create(this, R.raw.mozart);
        mediaPlayer.setLooping(true);
        if (music) {
            mediaPlayer.start();
        }
        boardView = new BoardView(this);
        setContentView(R.layout.activity_main);
        RelativeLayout layout = findViewById(R.id.relative_layout);
        layout.addView(boardView);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(R.string.title_activity_main);
        }

        Button newGameButton = findViewById(R.id.buttonNewGame);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.buttonConfirm).setVisibility(View.VISIBLE);
                boardView.newGame();
            }
        });

        Button undoButton = findViewById(R.id.buttonUndo);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.buttonConfirm).setVisibility(View.VISIBLE);
                if (!boardView.undoMove())
                {
                    displayAlert("Error", "No moves to undo");
                }
            }
        });

        final Button moveButton = findViewById(R.id.buttonConfirm);
        moveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int winner = 0;
                switch (boardView.confirmMove()) {
                    case BoardView.GAME_OVER:
                        break;
                    case BoardView.INVALID_MOVE:
                        displayAlert("Invalid move", "Please try again");
                        break;
                    case BoardView.HUMAN_WINS:
                        displayWinner("You win");
                        break;
                    case BoardView.COMPUTER_WINS:
                        displayWinner("You lose");
                }
            }

            private void displayWinner(String message) {
                displayAlert("Game over", message);
                moveButton.setVisibility(View.GONE);
                boardView.freeze();
            }
        });
    }

    private void displayAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
