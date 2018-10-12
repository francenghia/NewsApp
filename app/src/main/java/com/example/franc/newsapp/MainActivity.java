package com.example.franc.newsapp;

import android.app.SearchManager;
import android.content.Context;

import android.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.franc.newsapp.Adapter.NewsAdapter;
import com.example.franc.newsapp.Api.ApiClient;
import com.example.franc.newsapp.Api.ApiInterface;
import com.example.franc.newsapp.Models.Articles;
import com.example.franc.newsapp.Models.News;
import com.example.franc.newsapp.Utils.Utils;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String API_KEY = "333380b273e745d6ad33ef2bbaeb2fc4";
    private RecyclerView recyclerView;
    private List<Articles> articles = new ArrayList<>();
    private NewsAdapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView topHeadLine;

    private RelativeLayout relativeError;
    private ImageView imgError;
    private TextView txtTitleError,txtErrorMessage;
    private Button btnTryAgain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Error
        relativeError =findViewById(R.id.errorRelative);
        imgError=findViewById(R.id.imageError);
        txtErrorMessage =findViewById(R.id.errorMessage);
        txtTitleError=findViewById(R.id.errorTitle);
        btnTryAgain =findViewById(R.id.btnEntry);

        swipeRefreshLayout = findViewById(R.id.swip_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        topHeadLine = findViewById(R.id.topHeadLine);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        onLoadingSwipeRefresh("");
    }

    private void loadData(final String keyword) {
        relativeError.setVisibility(View.GONE);
        topHeadLine.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        ApiInterface mService = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utils.getCountry();
        String language = Utils.getLanguage();

        Call<News> call;

        if (keyword.length() > 0) {
            call = mService.getNewsSearch(keyword, language, "publishedAt", API_KEY);
        } else {
            call = mService.getNews(country, API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    adapter = new NewsAdapter(MainActivity.this, articles);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    topHeadLine.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    topHeadLine.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    String errorCode = "";
                    switch (response.code())
                    {
                        case 404:
                            errorCode ="404 not found";
                            break;
                        case 500:
                            errorCode ="500 service broken";
                            break;
                        default:
                            errorCode ="unknown error";
                            break;
                    }

                    ShowErrorMessage(R.drawable.news,"No Result","Please try again !"+"\n"+errorCode);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                ShowErrorMessage(R.drawable.news,"Oops...","Network failed !Please try again !"+"\n"+t.getMessage().toString());
            }
        });


    }

    public void ShowErrorMessage(int imageView,String title,String message){
        if(relativeError.getVisibility() == View.GONE){
            relativeError.setVisibility(View.VISIBLE);
        }
        imgError.setImageResource(imageView);
        txtTitleError.setText(title);
        txtErrorMessage.setText(message);

        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoadingSwipeRefresh("");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search News ...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 2) {
                    onLoadingSwipeRefresh(s);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                onLoadingSwipeRefresh(s);
                return false;
            }
        });

        searchMenuItem.getIcon().setVisible(false, false);
        return true;
    }

    @Override
    public void onRefresh() {
        loadData("");
    }

    private void onLoadingSwipeRefresh(final String keyword){
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                loadData(keyword);
            }
        });
    }
}
