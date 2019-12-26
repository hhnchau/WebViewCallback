package com.hanet.webviewcallback;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {
    YtLink ytLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        ytLink = findViewById(R.id.ytLink);
        ytLink.load("Lc3VgVIlu1I");

        ytLink.addListener(new YtLink.OnYtLink() {
            @Override
            public void ytLink(YtLink.Link yt) {
                Toast.makeText(Main3Activity.this, ""+yt.getLink(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void change(View view) {
        ytLink.load("HsgTIMDA6ps");
    }
}
