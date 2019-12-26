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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YtLink extends WebView {
    private Link link;
    private boolean isCallback = true;

    private OnYtLink callback;

    public void addListener(OnYtLink callback) {
        this.callback = callback;
    }

    public interface OnYtLink {
        void ytLink(Link yt);
    }

    public YtLink(Context context) {
        super(context);
        init();
    }

    public YtLink(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void init() {
        this.setVisibility(INVISIBLE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        this.setLayoutParams(layoutParams);
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
                if (!isCallback)
                    if (url.matches(".*googlevideo.com/videoplayback.*")) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (url.contains("&mime=video")) {
                                    if (link.getLink() == null) {
                                        link.setLink(url);
                                        Log.d("YtLink", "Video Normal: " + url.replaceAll("&range=[\\d-]*&", "&"));
                                    } else {
                                        link.setLinkQuality(url);
                                        Log.d("YtLink", "Video Quality: " + url.replaceAll("&range=[\\d-]*&", "&"));
                                        if (callback != null) {
                                            callback.ytLink(link);
                                        }
                                        isCallback = !isCallback;
                                        stopLoading();
                                        clearCache(true);
                                        clearFormData();
                                        clearHistory();
                                        clearView();
                                    }
                                } else if (url.contains("&mime=audio") && link.getAudio() == null) {
                                    link.setAudio(url);
                                    Log.d("YtLink", "Audio: " + url.replaceAll("&range=[\\d-]*&", "&"));
                                }
                            }
                        });
                    }
                return super.shouldInterceptRequest(view, url);
            }
        });
    }

    public void load(final String videoId) {
        link = new Link();
        isCallback = !isCallback;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadUrl("https://www.youtube.com/watch?v=" + videoId);
            }
        });
    }

    class Link {
        private String audio;
        private String link;
        private String linkQuality;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getLinkQuality() {
            return linkQuality;
        }

        public void setLinkQuality(String linkQuality) {
            this.linkQuality = linkQuality;
        }

        public String getAudio() {
            return audio;
        }

        public void setAudio(String audio) {
            this.audio = audio;
        }
    }

    private int getItag(String url) {
        Matcher siteNameMatcher = Pattern.compile("&itag=([^\"]{3})").matcher(url);
        while (siteNameMatcher.find()) {
            String s = siteNameMatcher.group(1);
            if (s != null) {
                try {
                    return Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
}
