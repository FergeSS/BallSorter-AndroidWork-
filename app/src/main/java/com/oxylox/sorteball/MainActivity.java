package com.oxylox.sorteball;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;

import com.oxylox.sorteball.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);

        if (game.active) {
            Intent intent = new Intent(this, game.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return;
        }
        if (settings.active) {
            Intent intent = new Intent(this, settings.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return;
        }
        if (policy.active) {
            Intent intent = new Intent(this, game.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            finish();
            return;
        }
    }

    public void settings(View v) {
        action();
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    public void start(View v) {
        action();
        Intent intent = new Intent(this, game.class);
        startActivity(intent);
    }

    public void policy(View v) {
        action();
        Intent intent = new Intent(this, policy.class);
        startActivity(intent);
    }

    public void exit(View v) {
        action();
        finishAffinity();
    }

    public void action() {
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("vibro_enabled", true)) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(100);
            }
        }

        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("sound_enabled", true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.click);
            mediaPlayer.setVolume(0.2f, 0.2f);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
        }
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

}