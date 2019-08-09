package com.nakul.wikiapi.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.nakul.wikiapi.R;
import com.nakul.wikiapi.databinding.ActivityWebViewBinding;

public class SelectedResultViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWebViewBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view);
        binding.wikiWebView.loadUrl("https://en.m.wikipedia.org/?curid=" + getIntent().getStringExtra("pageid"));
    }
}
