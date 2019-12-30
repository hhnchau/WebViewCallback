package com.hanet.webviewcallback;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        final YtPlayer ytPlayer = findViewById(R.id.yt);
        ytPlayer.setAutoPlayerHeight(this);
        ytPlayer.initPlayer("PJQMdNceRsA");
        ytPlayer.setListener(new YtPlayer.YouTubeListener() {
            @Override
            public void onReady() {
                Toast.makeText(Main2Activity.this, "onReady", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStateChange(YtPlayer.STATE state) {
                Toast.makeText(Main2Activity.this, "onStateChange", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaybackQualityChange(String arg) {
                Toast.makeText(Main2Activity.this, "onPlaybackQualityChange", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaybackRateChange(String arg) {
                Toast.makeText(Main2Activity.this, "onPlaybackRateChange", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String arg) {
                Toast.makeText(Main2Activity.this, "onError: " + arg, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onApiChange(String arg) {
                Toast.makeText(Main2Activity.this, "onApiChange", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCurrentSecond(double second) {
                Toast.makeText(Main2Activity.this, "onCurrentSecond", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDuration(double duration) {
                Toast.makeText(Main2Activity.this, "onDuration", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void logs(String log) {
                Toast.makeText(Main2Activity.this, "logs", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
