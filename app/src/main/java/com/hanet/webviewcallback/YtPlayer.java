package com.hanet.webviewcallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class YtPlayer extends WebView {

    private YouTubeListener listener;

    public void setListener(YouTubeListener listener) {
        this.listener = listener;
    }

    public YtPlayer(Context context) {
        super(context);
    }

    public YtPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private boolean autoPlay = true;

    @SuppressLint("SetJavaScriptEnabled")
    public void initPlayer(String videoId) {
        WebSettings set = this.getSettings();
        set.setJavaScriptEnabled(true);
        set.setUseWideViewPort(true);
        set.setLoadWithOverviewMode(true);
        set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        set.setCacheMode(WebSettings.LOAD_NO_CACHE);
        set.setPluginState(WebSettings.PluginState.ON);
        set.setPluginState(WebSettings.PluginState.ON_DEMAND);
        set.setAllowContentAccess(true);
        set.setAllowFileAccess(true);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                performClick();
                return true;
            }
        });


        this.addJavascriptInterface(new JsInterface(), "app");

        this.setLayerType(View.LAYER_TYPE_NONE, null);

        this.setWebChromeClient(new WebChromeClient());
        this.setWebViewClient(new WebViewClient());

        String htmlFile = readFileToString(videoId);

        this.loadDataWithBaseURL("http://www.youtube.com", htmlFile, "text/html", "utf-8", null);

    }

    public void play() {
        loadUrl("javascript:onVideoPlay()");
    }

    public void onCueVideo(String videoId) {
        this.loadUrl("javascript:cueVideo('" + videoId + "')");
    }

    private class JsInterface {
        @JavascriptInterface
        public void onReady(String arg) {
            if (listener != null) listener.onReady();
            if (autoPlay) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        play();
                    }
                }, 1000);
            }
        }

        @JavascriptInterface
        public void onStateChange(String arg) {
            if (listener != null) {
                if ("UNSTARTED".equalsIgnoreCase(arg)) {
                    listener.onStateChange(STATE.UNSTARTED);
                } else if ("ENDED".equalsIgnoreCase(arg)) {
                    listener.onStateChange(STATE.ENDED);
                } else if ("PLAYING".equalsIgnoreCase(arg)) {
                    listener.onStateChange(STATE.PLAYING);
                } else if ("PAUSED".equalsIgnoreCase(arg)) {
                    listener.onStateChange(STATE.PAUSED);
                } else if ("BUFFERING".equalsIgnoreCase(arg)) {
                    listener.onStateChange(STATE.BUFFERING);
                } else if ("CUED".equalsIgnoreCase(arg)) {
                    listener.onStateChange(STATE.CUED);
                }
            }
        }

        @JavascriptInterface
        public void onPlaybackQualityChange(String arg) {
            if (listener != null) {
                listener.onPlaybackQualityChange(arg);
            }
        }

        @JavascriptInterface
        public void onPlaybackRateChange(String arg) {
            if (listener != null) {
                listener.onPlaybackRateChange(arg);
            }
        }

        @JavascriptInterface
        public void onError(String arg) {
            if (listener != null) {
                listener.onError(arg);
            }
        }

        @JavascriptInterface
        public void onApiChange(String arg) {
            if (listener != null) {
                listener.onApiChange(arg);
            }
        }

        @JavascriptInterface
        public void duration(String seconds) {
            if (listener != null) {
                listener.onDuration(Double.parseDouble(seconds));
            }
        }

        @JavascriptInterface
        public void logs(String arg) {
            if (listener != null) {
                listener.logs(arg);
            }
        }
    }

    public interface YouTubeListener {
        void onReady();

        void onStateChange(STATE state);

        void onPlaybackQualityChange(String arg);

        void onPlaybackRateChange(String arg);

        void onError(String arg);

        void onApiChange(String arg);

        void onCurrentSecond(double second);

        void onDuration(double duration);

        void logs(String log);
    }

    public enum STATE {
        UNSTARTED,
        ENDED,
        PLAYING,
        PAUSED,
        BUFFERING,
        CUED,
        NONE
    }

    public void setAutoPlayerHeight(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        this.getLayoutParams().height = (int) (displayMetrics.widthPixels * 0.5625);
    }

    private String readFileToString(String videoId) {
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            reader = new BufferedReader(new InputStreamReader(this.getContext().getAssets().open("player.html"), "UTF-8"));
            String myLine;
            while ((myLine = reader.readLine()) != null) {
                stringBuilder.append(myLine);
                stringBuilder.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String html = stringBuilder.toString().replace("[VIDEO_ID]", videoId);

        return html;
    }
}
