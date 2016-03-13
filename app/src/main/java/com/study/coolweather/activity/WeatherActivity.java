package com.study.coolweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.study.coolweather.R;
import com.study.coolweather.util.HttpCallbackListener;
import com.study.coolweather.util.HttpUtil;
import com.study.coolweather.util.Utility;


/**
 * Created by WXB506 on 2016/3/13.
 */
public class WeatherActivity extends Activity {

    private LinearLayout weatherInfoLayout;

    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDatetext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText = (TextView) findViewById(R.id.city_name);
        publishText = (TextView) findViewById(R.id.publish_text);
        weatherDespText = (TextView) findViewById(R.id.weather_desp);
        temp1Text = (TextView) findViewById(R.id.temp1);
        temp2Text = (TextView) findViewById(R.id.temp2);
        currentDatetext = (TextView) findViewById(R.id.current_date);

        String countyCode = getIntent().getStringExtra("county_code");
        if(!TextUtils.isEmpty(countyCode)) {
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(countyCode);
        } else {
            showWeather();
        }
    }

    /*
    *  查询县级代号所对应的天气代号
    */
    private void queryWeatherCode(String countyCode) {
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address, "countyCode");
    }

    /*
    *  查询天气代号所对应的天气
    */
    private void queryWeatherInfo(String weatherCode) {
        String address = "http://www.weather.com.cn/data/cityinfo/" + weatherCode + ".html";
        queryFromServer(address, "weatherCode");
    }

    /*
    *  根据传入的地址和类型去向服务器查询天气代号或者天气信息
    */
    private void queryFromServer(final String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countyCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }
                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this, response);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /*
    *  从SharedPreferences文件中读取存储的天气信息，并显示到界面上
    */
    private void showWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        cityNameText.setText(prefs.getString("city_name", ""));
        temp1Text.setText(prefs.getString("temp1", ""));
        temp2Text.setText(prefs.getString("temp2", ""));
        weatherDespText.setText(prefs.getString("weather_desp", ""));
        publishText.setText("今天" + prefs.getString("publish_time", "") + "发布");
        currentDatetext.setText(prefs.getString("current_date", ""));

        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(WeatherActivity.this, ChooseAreaActivity.class);
        startActivity(intent);
    }
}