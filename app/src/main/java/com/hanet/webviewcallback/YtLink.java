package com.hanet.webviewcallback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class YtLink extends WebView {
    private boolean isCallback = false;
    private int count = 0;
    private boolean isBlock = true;
    private String videoId = "";

    public YtLink(Context context) {
        super(context);
    }

    public YtLink(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void init(final String videoId, int width, int height, final OnListener listener) {
        this.videoId = videoId;
        final Yt yt = new Yt();
//        this.setVisibility(INVISIBLE);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
//        this.setLayoutParams(layoutParams);
        WebSettings set = this.getSettings();
        set.setJavaScriptEnabled(true);
        set.setUseWideViewPort(true);
        set.setLoadWithOverviewMode(true);
        set.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        set.setCacheMode(WebSettings.LOAD_NO_CACHE);
        set.setPluginState(WebSettings.PluginState.ON_DEMAND);
        set.setAllowContentAccess(true);
        set.setAllowFileAccess(true);
        this.setLayerType(View.LAYER_TYPE_NONE, null);
        this.setWebChromeClient(new WebChromeClient());
        this.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                long delta = 100;
                long downTime = SystemClock.uptimeMillis();
                float x = view.getLeft() + (view.getWidth() / 2);
                float y = view.getTop() + (view.getHeight() / 2);
                MotionEvent tapDownEvent = MotionEvent.obtain(downTime, downTime + delta, MotionEvent.ACTION_DOWN, x, y, 0);
                tapDownEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
                MotionEvent tapUpEvent = MotionEvent.obtain(downTime, downTime + delta + 2, MotionEvent.ACTION_UP, x, y, 0);
                tapUpEvent.setSource(InputDevice.SOURCE_CLASS_POINTER);
                view.dispatchTouchEvent(tapDownEvent);
                view.dispatchTouchEvent(tapUpEvent);
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
                if (!isCallback) {
                    if (url.matches(".*get_video_info?.*")) {
                        Log.d("YtLink", "Get_Video_Info: " + url);
                        isBlock = false;
                    }
                    if (!isBlock && count < 10) {
                        count++;
                        Log.d("YtLink", "Error: " + count);
                    } else if (!isBlock) {
                        isBlock = true;
                        Log.d("YtLink", "Callback Error: ");
                        if (listener != null) listener.yt(null);
                    }
                    if (url.matches(".*googlevideo.com/videoplayback.*")) {
                        isBlock = true;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (url.contains("&mime=video")) {
                                    if (yt.getVideo() == null) {
                                        String lk = url.replaceAll("&range=[\\d-]*&", "&");
                                        Log.d("YtLink", "Video Normal: " + lk);
                                        yt.setVideo(lk);
                                    } else {
                                        String lk = url.replaceAll("&range=[\\d-]*&", "&");
                                        Log.d("YtLink", "Video Quality: " + lk);
                                        if (listener != null) listener.yt(yt);
                                        isCallback = !isCallback;
                                        stopLoading();
                                        clearCache(true);
                                        clearFormData();
                                        clearHistory();
                                        clearView();
                                    }
                                } else if (url.contains("&mime=audio")) {
                                    String lk = url.replaceAll("&range=[\\d-]*&", "&");
                                    Log.d("YtLink", "Audio: " + lk);
                                    yt.setAudio(lk);
                                }
                            }
                        });
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }
        });
        String frameVideo = "<html><body><iframe width='" + width + "' height='" + height + "' src=\"https://www.youtube.com/embed/" + videoId + "\"></iframe></body></html>";
        loadData(frameVideo, "text/html", "utf-8");
    }

    public void reInit() {
        loadUrl("https://www.youtube.com/watch?v=" + videoId);
    }

    public interface OnListener {
        void yt(Yt yt);
    }

    public class Yt {
        private String video;
        private String audio;

        public Yt() {
        }

        public Yt(String video, String audio) {
            this.video = video;
            this.audio = audio;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }
    }
}
