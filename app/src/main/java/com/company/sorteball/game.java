package com.company.sorteball;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.company.sorteball.databinding.ActivityGameBinding;
import com.company.sorteball.databinding.ActivityMainBinding;

import java.util.Random;

public class game extends AppCompatActivity {
     static boolean active = false;
     boolean isPause = false;
     Dialog dialog;
     long time = 0;
     boolean isSwitchLeft = true;
     ObjectAnimator ballDown;
     ObjectAnimator ballLeftUp;
     ObjectAnimator ballRightUp;
    ObjectAnimator ballLeftDown;
    ObjectAnimator ballRightDown;
    boolean flag = true;
    Random rand = new Random();
    boolean endGame = false;
    int idBall;
    int[] balls = {R.drawable.grey_ball, R.drawable.blue_ball};
    int counterBalls = 0;
     int durationFalling = 10000;
     private ActivityGameBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                    WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        }

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
        binding.switchLeft.setAlpha(0.0f);
        settingsDialog();
        Handler hand = new Handler();
        Runnable run = new Runnable() {
            @Override
            public void run() {
                setAnimations();
                idBall = rand.nextInt(2);
                binding.ball.setImageResource(balls[idBall]);
                ballDown.start();
            }
        };
        hand.postDelayed(run, 500);

        binding.getRoot().setOnTouchListener((v, event) -> gameLogic(event));
        active = true;
    }
    private boolean gameLogic(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            action();
            if (binding.ball.getHeight() + binding.ball.getY() > binding.switchLeft.getY() &&
                    binding.ball.getHeight() + binding.ball.getY() < binding.switchLeft.getY() + binding.switchLeft.getHeight() / 2) {
                return false;
            }
            if (isSwitchLeft) {
                binding.switchLeft.setAlpha(1.0f);
                binding.switchRight.setAlpha(0.0f);
                isSwitchLeft = false;
            } else {
                binding.switchLeft.setAlpha(0.0f);
                binding.switchRight.setAlpha(1.0f);
                isSwitchLeft = true;
            }
            return true;
        }
        return false;
    }
    public void home(View v) {
        action();
        finish();
    }
    public void pause(View v) {
        action();
        isPause = true;
        TextView count = dialog.findViewById(R.id.score);
        TextView timeText = dialog.findViewById(R.id.time);
        count.setText(binding.conuter.getText());
        timeText.setText(binding.timer.getText());
        time = SystemClock.elapsedRealtime() - binding.timer.getBase();
        binding.timer.stop();
        ballDown.pause();
        ballRightUp.pause();
        ballLeftUp.pause();
        ballRightDown.pause();
        ballLeftDown.pause();
        dialog.show();
    }

    public void endGame() {
        endGame = true;
        ballDown.cancel();
        ballRightUp.cancel();
        ballLeftUp.cancel();
        ballRightDown.cancel();
        ballLeftDown.cancel();
        binding.timer.stop();
        durationFalling = 10000;
        TextView count = dialog.findViewById(R.id.score);
        TextView timeText = dialog.findViewById(R.id.time);
        count.setText(binding.conuter.getText());
        timeText.setText(binding.timer.getText());
        dialog.show();
    }
    public void play(View v) {
        dialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
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
    public void settingsDialog() {
        dialog = new Dialog(game.this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();
        wlp.dimAmount = 0.7f;
        dialog.getWindow().setAttributes(wlp);
        dialog.setContentView(R.layout.dialog);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                action();
                if (!isPause) {
                    time = 0;
                    binding.conuter.setText("0");
                    counterBalls = 0;
                    idBall = rand.nextInt(2);
                    binding.ball.setImageResource(balls[idBall]);
                    endGame = false;
                    ballDown.cancel();
                    binding.ball.setTranslationY(0);
                    binding.ball.setTranslationX(0);
                    ballDown.start();
                } else {
                    ballDown.resume();
                    ballRightUp.resume();
                    ballLeftUp.resume();
                    ballRightDown.resume();
                    ballLeftDown.resume();
                }
                binding.timer.setBase(SystemClock.elapsedRealtime() - time);
                binding.timer.start();

                isPause = false;
            }
        });
    }
    public void action() {
        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("vibro_enabled", true)) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(100);
            }
        }

        if (getSharedPreferences("settings", MODE_PRIVATE).getBoolean("sound_enabled", true)) {
            MediaPlayer mediaPlayer = MediaPlayer.create(game.this, R.raw.click);
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

    @Override
    protected void onResume() {
        super.onResume();
        binding.timer.setBase(SystemClock.elapsedRealtime() - time);
        binding.timer.start();
        if (ballDown != null && (dialog == null || !dialog.isShowing())) {
            ballDown.resume();
            ballRightUp.resume();
            ballLeftUp.resume();
            ballRightDown.resume();
            ballLeftDown.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.timer.stop();
        time = SystemClock.elapsedRealtime() - binding.timer.getBase();
        ballDown.pause();
        ballRightUp.pause();
        ballLeftUp.pause();
        ballRightDown.pause();
        ballLeftDown.pause();
    }

    private void setAnimations() {
        ballDown = ObjectAnimator.ofFloat(binding.ball, "translationY", binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight()).setDuration(durationFalling);
        ballDown.setInterpolator(null);
        ballDown.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                if (binding.ball.getY() + binding.ball.getHeight() / 2 > binding.switchLeft.getY() && flag) {
                    if (isSwitchLeft) {
                        flag = false;
                        if (idBall == 0) {
                            ++counterBalls;
                            ballRightUp.start();
                        } else {
                            endGame();
                        }

                    }
                    else {
                        flag = false;
                        if (idBall == 1) {
                            ++counterBalls;
                            ballLeftUp.start();
                        } else {
                            endGame();
                        }
                    }
                }
            }
        });
        ballDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                flag = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationEnd(animation);
                flag = true;
            }

        });


        ballRightUp = ObjectAnimator.ofFloat(binding.ball, "translationX", -dpToPx(120)).setDuration((long)(dpToPx(120) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
        ballRightUp.setInterpolator(null);
        ballRightUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ballRightDown.start();
            }
        });

        ballLeftUp = ObjectAnimator.ofFloat(binding.ball, "translationX", dpToPx(120)).setDuration((long)(dpToPx(120) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
        ballLeftUp.setInterpolator(null);
        ballLeftUp.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ballLeftDown.start();
            }
        });

        ballRightDown = ObjectAnimator.ofFloat(binding.ball, "translationX", -dpToPx(60)).setDuration((long)(dpToPx(60) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
        ballRightDown.setInterpolator(null);

        ballLeftDown = ObjectAnimator.ofFloat(binding.ball, "translationX", dpToPx(60)).setDuration((long)(dpToPx(60) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
        ballLeftDown.setInterpolator(null);

        ballDown.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!endGame) {
                    binding.conuter.setText(counterBalls + "");
                    durationFalling = durationFalling * 20 / 21;
                    ballDown.setDuration(durationFalling);
                    ballRightUp.setDuration((long) (dpToPx(120) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
                    ballLeftUp.setDuration((long) (dpToPx(120) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
                    ballRightDown.setDuration((long) (dpToPx(60) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
                    ballLeftDown.setDuration((long) (dpToPx(60) * durationFalling * 1.0f / (binding.imageView4.getHeight() + dpToPx(160) + binding.downtube.getHeight() + binding.ball.getHeight())));
                    idBall = rand.nextInt(2);
                    binding.ball.setImageResource(balls[idBall]);
                    binding.ball.setTranslationY(0);
                    binding.ball.setTranslationX(0);
                    ballDown.start();
                }
            }
        });
    }

    int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}