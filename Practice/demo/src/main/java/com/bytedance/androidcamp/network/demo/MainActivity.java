package com.bytedance.androidcamp.network.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bytedance.androidcamp.network.demo.model.Cat;
import com.bytedance.androidcamp.network.demo.newtork.ICatService;
import com.bytedance.androidcamp.network.demo.utils.NetworkUtils;
import com.bytedance.androidcamp.network.lib.util.ImageHelper;
import com.google.gson.Gson;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static String CAT_JSON =
            "{\"breeds\":[],\"id\":\"293\",\"url\":\"https://cdn2.thecatapi.com/images/293.jpg\",\"width\":429,\"height\":500}";

    private Retrofit retrofit;
    private ICatService catService;
    private Gson gson;
    private Handler messageHandler;

    public TextView tvOut;
    public ImageView ivOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvOut = findViewById(R.id.tv_out);
        ivOut = findViewById(R.id.iv_out);
        Looper looper = Looper.myLooper();
        messageHandler = new MessageHandler(looper);
    }

    public void testJSONObject(View view) {
        // TODO 1: Parse CAT_JSON using JSONObject
        try {
            JSONObject user = new JSONObject(CAT_JSON);
            String id = user.getString("id");
            tvOut.setText(id);
        }catch (JSONException j){
        }
    }

    public void testGson(View view) {
        // TODO 2: Parse CAT_JSON using Gson
        Cat cat = getGson().fromJson(CAT_JSON,Cat.class);
        String url = cat.getUrl();
        ImageHelper.displayWebImage(url, ivOut);
    }

    public void testHttpURLConnectionSync(View view) {
        // TODO 4: Fix crash of NetworkOnMainThreadException
        new Thread(){
            @Override public void run(){
                final String s = NetworkUtils.getResponseWithHttpURLConnection(ICatService.HOST + ICatService.PATH);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvOut.setText(s);
                    }
                });
            }

        }.start();
    }

    public void testRetrofitSync(View view) throws Exception {
        // TODO 5: Making request in retrofit
        Call<List<Cat>> call = getCatService().randomCat(1);
        call.enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if(response.isSuccessful()&&response.body()!=null){
                    List<Cat> cats = response.body();
                    Cat cat = cats.get(0);
                    tvOut.setText(cat.toString());
                }
            }

            @Override
            public void onFailure(Call<List<Cat>> call, Throwable throwable) {
                Toast.makeText(MainActivity.this, "retrofit: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void testUpdateUI(View view) {
        // TODO 6: Fix crash of CalledFromWrongThreadException
        new Thread() {
            @Override public void run() {
                final String s = NetworkUtils.getResponseWithHttpURLConnection(ICatService.HOST + ICatService.PATH);
                Message message = Message.obtain();
                message.obj = s;
                messageHandler.sendMessage(message);

            }
        }.start();
    }

    public void testHttpURLConnectionAsync(View view) {
        // HttpURLConnection Async
        new Thread() {
            @Override public void run() {
                final String s = NetworkUtils.getResponseWithHttpURLConnection(ICatService.HOST + ICatService.PATH);
                try {
                    JSONArray cats = new JSONArray(s);
                    JSONObject cat = cats.getJSONObject(0);
                    final String id = cat.getString("id");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvOut.setText(id);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "retrofit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }.start();
    }

    public void testRetrofitAsync(View view) {
        Call<List<Cat>> call = getCatService().randomCat(1);
        call.enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Cat> cats = response.body();
                    Cat cat = cats.get(0);
                    tvOut.setText(cat.getUrl());
                }
            }

            @Override
            public void onFailure(Call<List<Cat>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "retrofit: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    private ICatService getCatService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ICatService.HOST)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        if (catService == null) {
            catService = retrofit.create(ICatService.class);
        }
        return catService;
    }

    class MessageHandler extends Handler{
        public MessageHandler(Looper looper){
            super(looper);
        }
        @Override
        public void handleMessage(Message msg){
            tvOut.setText((String)msg.obj);
        }
    }
}