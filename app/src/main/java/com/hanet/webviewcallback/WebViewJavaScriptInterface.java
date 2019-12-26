package com.hanet.webviewcallback;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebViewJavaScriptInterface {
    private MainActivity context;

    public WebViewJavaScriptInterface(MainActivity context){
        this.context = context;
    }

    @JavascriptInterface
    public void makeToasts(String message, boolean lengthLong){
        Toast.makeText(context, message, lengthLong ? Toast.LENGTH_LONG: Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void goToSecondActivity(){
        context.goToSecondActivity();
    }
}
