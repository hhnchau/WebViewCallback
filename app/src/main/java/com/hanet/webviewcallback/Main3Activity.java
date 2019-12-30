package com.hanet.webviewcallback;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        final YtLink ytLink = findViewById(R.id.ytLink);
        ytLink.init("TFnEL2CmavU", 1280, 720, new YtLink.OnListener() {
            @Override
            public void yt(YtLink.Yt yt) {
                if (yt == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ytLink.reInit();
                        }
                    });
                } else {
                    Toast.makeText(Main3Activity.this, yt.getVideo() + "\n" + yt.getAudio(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void change(View view) {

    }
}
