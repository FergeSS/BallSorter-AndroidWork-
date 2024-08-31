package com.oxylox.sorteball;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;

import com.oxylox.sorteball.databinding.ActivitySettingsBinding;

public class settings extends AppCompatActivity {
    static boolean active = false;
    private ActivitySettingsBinding binding;
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
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        active = true;

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isSoundEnabled = sharedPreferences.getBoolean("sound_enabled", true);
        binding.soundSwitch.setChecked(isSoundEnabled);

        binding.soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                action();
                editor.putBoolean("sound_enabled", isChecked);
                editor.apply();
            }
        });

        boolean isVibroEnabled = sharedPreferences.getBoolean("vibro_enabled", true);
        binding.vibroSwitch.setChecked(isVibroEnabled);

        binding.vibroSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                action();
                editor.putBoolean("vibro_enabled", isChecked);
                editor.apply();
            }
        });
    }

    public void home(View v) {
        action();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
    }

    @Override public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public void action() {
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("vibro_enabled", true)) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(100);
            }
        }

        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("sound_enabled", true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(settings.this, R.raw.click);
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

}