package com.example.franc.newsapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.franc.newsapp.Utils.Utils;

public class NewsDetailActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {


    private ImageView imageView;
    private TextView appBarTitle, appBarSubTitle, date, time, title;
    private boolean isHideToolbarView = false;
    private FrameLayout date_behavior;
    private LinearLayout titleAppBar;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private String mUrl, mImg, mTitle, mDate, mSource, mAuthor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        date_behavior = findViewById(R.id.date_behavior);
        titleAppBar = findViewById(R.id.title_appbar);
        imageView = findViewById(R.id.backdrop);
        appBarTitle = findViewById(R.id.title_on_appbar);

        appBarSubTitle = findViewById(R.id.subtitle_on_appbar);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        title = findViewById(R.id.title);

        Intent intent = getIntent();
        mUrl = intent.getStringExtra("url");
        mTitle = intent.getStringExtra("title");
        mImg = intent.getStringExtra("img");
        mDate = intent.getStringExtra("date");
        mSource = intent.getStringExtra("source");
        mAuthor = intent.getStringExtra("author");

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utils.getRandomDrawbleColor());

        Glide.with(this)
                .load(mImg)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        appBarTitle.setText(mSource);
        appBarSubTitle.setText(mUrl);
        date.setText(Utils.DateFormat(mDate));
        title.setText(mTitle);

        String author = null;
        if (mAuthor != null || mAuthor != "") {
            mAuthor = " \u2022 " + mAuthor;
        } else {
            author = "";
        }
        time.setText(mSource + author + " \u2022 " + Utils.DateToTimeFormat(mDate));

        initWebView(mUrl);
    }


    private void initWebView(String url) {
        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(url);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        supportFinishAfterTransition();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        Log.d("Kiem tra ", percentage + "");

        if (percentage == 1f && isHideToolbarView) {
            date_behavior.setVisibility(View.GONE);
            titleAppBar.setVisibility(View.VISIBLE);
            isHideToolbarView = !isHideToolbarView;
        } else if (percentage < 1f && !isHideToolbarView) {
            date_behavior.setVisibility(View.VISIBLE);
            titleAppBar.setVisibility(View.GONE);
            isHideToolbarView = !isHideToolbarView;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_web: {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(mUrl));
                startActivity(intent);
                break;
            }
            case R.id.share: {
                try {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plan");
                    intent.putExtra(Intent.EXTRA_SUBJECT, mSource);
                    String body = mTitle + "\n" + mUrl + "\n" + "Share from News App" + "\n";
                    intent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(intent.createChooser(intent, "Share with :"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
