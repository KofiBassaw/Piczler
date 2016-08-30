package com.piczler.piczler;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import java.io.File;

/**
 * Created by bassaw on 24/11/2014.
 */
public class WebPageClass extends AppCompatActivity {

    protected WebView webView;
    TextView tvTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_page);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        webView = ((WebView)findViewById(R.id.webView));
        tvTitle = ((TextView)findViewById(R.id.tvTitle));

        initToolbar();

        tvTitle.setText(getIntent().getStringExtra(StaticVariables.TITLE));
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.getSettings().setJavaScriptEnabled( true );
        webView.setScrollbarFadingEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        // Load the URLs inside the WebView, not in the external web browser
        webView.setWebViewClient(new WebViewClient());

       // Map<String, String> headers = new HashMap<String, String>();
      //  headers.put("Authorization", "Basic" + Base64.encodeToString("kofieassaw" + ':' + "kofieassaw"),Base64.DEFAULT);


        webView.loadUrl(getIntent().getStringExtra(StaticVariables.URL));

    }




    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setTitle("Forgotten Password");
        //mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            onBackPressed();
        }else if(item.getItemId() == R.id.idNext)
        {
            /*
            emailString = functions.getText(etEmail).trim();
            if(functions.isEmpty(emailString))
            {
                etEmail.setError("Email is empty");
            }else
            {


                sendData();
            }

            */
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public File getCacheDir()
    {
        // NOTE: this method is used in Android 2.1

        return getApplicationContext().getCacheDir();
    }

    @Override
    protected void onDestroy() {
        webView.clearCache(true);
        super.onDestroy();
    }
}
