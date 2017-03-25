package kultalaaki.vpkapuri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class harjoitukset extends AppCompatActivity {

    private WebView Webview;
    WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_harjoitukset);

        Webview = (WebView) findViewById(R.id.webView);
        settings();
        Webview.loadUrl("http://www.kyroskoskenvpk.fi/img/Harjoitusohjelma2016.png");
    }

    public void settings() {
        // Enable Javascript
        webSettings = Webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
}
