package com.ktc.setting.view.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ktc.setting.R;
import com.ktc.setting.view.about.AboutActivity;
import com.ktc.setting.view.custom.HomeItemView;
import com.ktc.setting.view.network.NetworkActivity;
import com.ktc.setting.view.others.OthersActivity;
import com.ktc.setting.view.restore.UpdateActivity;
import com.ktc.setting.view.universal.UniversalActivity;
import com.ktc.setting.view.universal.language.LanguageActivity;

public class MainActivity extends Activity implements HomeItemView.OnItemClickListener {

    private HomeItemView networkView;
    private HomeItemView languageView;
    private HomeItemView universalView;
    private HomeItemView restoreView;
    private HomeItemView otherView;
    private HomeItemView aboutView;
	private static final int LANGUAGE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        networkView = (HomeItemView) findViewById(R.id.main_network);
        languageView = (HomeItemView) findViewById(R.id.main_language);
        universalView = (HomeItemView) findViewById(R.id.main_universal);
        restoreView = (HomeItemView) findViewById(R.id.main_restore);
        aboutView = (HomeItemView) findViewById(R.id.main_about);
        otherView = (HomeItemView) findViewById(R.id.main_other);
        networkView.setOnItemClickListener(this);
        languageView.setOnItemClickListener(this);
        universalView.setOnItemClickListener(this);
        restoreView.setOnItemClickListener(this);
        aboutView.setOnItemClickListener(this);
        otherView.setOnItemClickListener(this);
        networkView.requestFocus();
    }

    @Override
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.main_network:
                startActivity(new Intent(MainActivity.this, NetworkActivity.class));
                break;
            case R.id.main_language:
                startActivityForResult(new Intent(MainActivity.this, LanguageActivity.class)
                        , LANGUAGE);
                break;
            case R.id.main_universal:
                startActivity(new Intent(MainActivity.this, UniversalActivity.class));
                break;
            case R.id.main_restore:
                startActivity(new Intent(MainActivity.this, UpdateActivity.class));
                break;
            case R.id.main_about:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.main_other:
                startActivity(new Intent(MainActivity.this, OthersActivity.class));
                break;
        }
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LANGUAGE) {
            recreate();
        }
    }
}
