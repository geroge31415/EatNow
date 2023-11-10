package com.example.eatnow;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static android.view.View.inflate;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

public class MainActivity extends AppCompatActivity {

    //spinnerの中身を宣言
    private final String[] spinnerItems = {"300", "500", "1000", "2000","3000"};
    private MainActivity HotPepperUtils;

    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //spinerを宣言
        Spinner spinner = findViewById(R.id.spinner);

        //spinnerの中身を設定
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                spinnerItems
        );
        spinner.setAdapter(adapter);


        //https://webservice.recruit.co.jp/doc/hotpepper/reference.html#a1to参照
        double  mLat=35.118223; //緯度
        double  mLng=137.088432; //経度
        int mLunch=0; //ランチの有無　0:絞り込まない（初期値）1:絞り込む
        int mRange=1000; //1: 300m 2: 500m 3: 1000m (初期値) 4: 2000m 5: 3000m
        ArrayList<String> mGenreCdList = new ArrayList<String>(Arrays.asList("G001", "G002", "G003"));//ジャンル選択(https://webservice.recruit.co.jp/hotpepper/genre/v1/?key=sampleを参照)
        int mMidnight_meal=0; //23時以降食事OK	0:絞り込まない（初期値） 1:絞り込む
        ArrayList<String> mKeywordList = new ArrayList<String>(Arrays.asList("海鮮")); //いずれか最低1つが必要。// 店名かな、店名、住所、駅名、お店ジャンルキャッチ、キャッチのフリーワード検索(部分一致)が可能です。文字コードはUTF8。半角スペース区切りの文字列を渡すことでAND検索になる。複数指定可能

        // HotPepperグルメAPIの呼び出し
        HotPepperGourmetSearch hotPepperGourmetSearch = new HotPepperGourmetSearch();
        hotPepperGourmetSearch.setLat(mLat); // 画面でセットした緯度
        hotPepperGourmetSearch.setLng(mLng); // 画面でセットした経度
        hotPepperGourmetSearch.setLunch(mLunch); // 画面でセットしたランチ有無
        hotPepperGourmetSearch.setRange(mRange); // 画面でセットした検索範囲距離
        hotPepperGourmetSearch.setGenreCdList(mGenreCdList); // 画面でセットしたジャンルのリスト
        hotPepperGourmetSearch.setMidnight_meal(mMidnight_meal); // 画面でセットした23時以降食事OK
        hotPepperGourmetSearch.setKeywordList(mKeywordList); // 画面でセットしたキーワードのリスト
        // APIコール
        HotPepperUtils.callHotPepperGourmetRestaurant(MainActivity.this, hotPepperGourmetSearch);
        Log.d("SAMPLE", "APIコール");

    }

    /**
     * ホットペッパーAPI呼び出し後のコールバック処理
     *
     * @param hotPepperGourmetArray
     */
    //@Override
    public void onRestaurantAsyncCallBack(ArrayList<HotPepperGourmet> hotPepperGourmetArray) {
        // hotPepperGourmetArrayに飲食店の情報がセットされている
         textView1=findViewById(R.id.textView1);
         textView1.setText(hotPepperGourmetArray.toString());
         Log.d("SAMPLE", hotPepperGourmetArray.toString());
    }




    //ホットペッパーAPIのパラメータクラスを作成
    public class HotPepperGourmetSearch {
        private Double lat; // 緯度
        private Double lng; // 経度
        private int lunch; // ランチ営業有無
        private int range; // 検索範囲距離
        private int midnight_meal; // // 23時以降食事OK
        private ArrayList<String> keywordList; // キーワードのリスト
        private ArrayList<String> genreCdList; // ジャンルのリスト

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public int getLunch() {
            return lunch;
        }

        public void setLunch(int lunch) {
            this.lunch = lunch;
        }

        public int getRange() {
            return range;
        }

        public void setRange(int range) {
            this.range = range;
        }

        public int getMidnight_meal() {
            return midnight_meal;
        }

        public void setMidnight_meal(int midnight_meal) {
            this.midnight_meal = midnight_meal;
        }

        public ArrayList<String> getGenreCdList() {
            return genreCdList;
        }

        public void setGenreCdList(ArrayList<String> genreCdList) {
            this.genreCdList = genreCdList;
        }

        public ArrayList<String> getKeywordList() {
            return keywordList;
        }

        public void setKeywordList(ArrayList<String> keywordList) {
            this.keywordList = keywordList;
        }
    }


    //APIコール後の戻り値のクラスを作成
    public static class HotPepperGourmet {
        private String name; // 飲食店の名前
        private String address; // 住所
        private Double lat; // お店の緯度
        private Double lng; // お店の経度
        private String lunch; // ランチ有無
        private String url; // お店のURL
        private String id; // お店コード

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }

        public String getLunch() {
            return lunch;
        }

        public void setLunch(String lunch) {
            this.lunch = lunch;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    //APIを呼び出すクラスを作成
    /**
     * ホットペッパーグルメAPIの呼び出し
     */
    public static void callHotPepperGourmetRestaurant(Activity activity, HotPepperGourmetSearch hotPepperGourmetSearch) {
        // URLの作成

        // ジャンルの切り取り
        StringBuilder genreSb = new StringBuilder();
        genreSb.append("&genre=");

        for (int i = 0; i < hotPepperGourmetSearch.getGenreCdList().size(); i++) {
            if (i > 0) {
                genreSb.append("&genre=");
            }

            String genreCd = hotPepperGourmetSearch.getGenreCdList().get(i);
            genreSb.append(genreCd);
        }

        // キーワードの切り取り
        StringBuilder keywordSb = new StringBuilder();
        keywordSb.append("&keyword=");

        for (int i = 0; i < hotPepperGourmetSearch.getKeywordList().size(); i++) {
            if (i > 0) {
                keywordSb.append("&keyword=");
            }

            String keyword = hotPepperGourmetSearch.getKeywordList().get(i);
            keywordSb.append(keyword);
        }

        // URLの生成
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("http://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=");
        urlStringBuilder.append("508dba382aa41e5c");
        urlStringBuilder.append(genreSb.toString()); // 飲食店のジャンル
        urlStringBuilder.append("&midnight_meal="); // 23時以降食事OK
        urlStringBuilder.append(hotPepperGourmetSearch.getMidnight_meal());
        urlStringBuilder.append(keywordSb.toString()); // キーワード
        urlStringBuilder.append("&lunch="); // ランチ営業
        urlStringBuilder.append(hotPepperGourmetSearch.getLunch());
        urlStringBuilder.append("&lat="); // 緯度
        urlStringBuilder.append(hotPepperGourmetSearch.getLat());
        urlStringBuilder.append("&lng="); // 経度
        urlStringBuilder.append(hotPepperGourmetSearch.getLng());
        urlStringBuilder.append("&range="); // 検索範囲距離
        urlStringBuilder.append(hotPepperGourmetSearch.getRange());
        urlStringBuilder.append("&count=100"); // 1ページあたりの取得数
        urlStringBuilder.append("&format=json") // レスポンス形式
        ;

        URL url = null;

        try {
            Log.d("SAMPLE", "try");
            url = new URL(urlStringBuilder.toString());
            // 非同期処理
            new RestaurantAsync(activity).execute(url).get(10000, TimeUnit.MILLISECONDS);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
            String mToastMessage = "接続がタイムアウトになったため、お店の情報を取得できませんでした";
            Toast toast = Toast.makeText(activity, mToastMessage, Toast.LENGTH_LONG);
            toast.show();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            // プログレスバーを閉じる
            if (RestaurantAsync.sProgressDialog != null && RestaurantAsync.sProgressDialog.isShowing()) {
                RestaurantAsync.sProgressDialog.dismiss();
            }
        }
        Log.d(TAG, url.toString());
    }



    //非同期処理とJSONのパース
    public static class RestaurantAsync extends AsyncTask<URL, Void, String> {

        private Activity mActivity;
        private StringBuffer mBuffer = new StringBuffer();

        private static final String TAG = "RestaurantAsync";

        public static ProgressDialog sProgressDialog;

        /**
         * コンストラクタ
         * @param activity
         */
        public RestaurantAsync(Activity activity) {
            mActivity = activity;
        }

        /**
         * 非同期処理の前処理
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("SAMPLE", "プログレスバー");
            // プログレスバーを表示する
            sProgressDialog = new ProgressDialog(mActivity);
            sProgressDialog.setCancelable(false); // キャンセルさせない
            sProgressDialog.setMessage("お店を検索中...");
            sProgressDialog.show();
        }

        /**
         * 非同期処理
         * @param url
         * @return
         */
        @Override
        protected String doInBackground(URL... url) {
            HttpURLConnection con = null;
            URL urls = url[0];

            try {
                con = (HttpURLConnection) urls.openConnection();
                // JSONダウンロード
                con.setRequestMethod("GET");
                // タイムアウト3秒
                con.setConnectTimeout(3000);
                con.setReadTimeout(3000);
                // 接続
                con.connect();

                // レスポンスコードの確認
                int resCd = con.getResponseCode();

                if (resCd != HttpURLConnection.HTTP_OK) {
                    // 接続NG
                    throw new IOException("HTTP responseCode:" + resCd);
                }

                InputStream inputStream = con.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                while (true) {
                    line = reader.readLine();

                    if (line == null) {
                        break;
                    }

                    mBuffer.append(line);
                }

                // クローズ
                inputStream.close();
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 接続をクローズ
                con.disconnect();
            }

            Log.d(TAG, mBuffer.toString());
            return mBuffer.toString();
        }

        /**
         * 非同期処理の後処理
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            try {
                Log.d("SAMPLE", "try2");
                // JSONをパースして各飲食店の情報を取得する
                JSONObject jsonObject = new JSONObject(result);
                Log.d("SAMPLE", "1");
                JSONArray jsonArray = jsonObject.getJSONObject("results").getJSONArray("shop");
                Log.d("SAMPLE", "2");
                ArrayList<HotPepperGourmet> hotPepperGourmetArray = new ArrayList<>();
                Log.d("SAMPLE", "3");
                Log.d("SAMPLE", hotPepperGourmetArray.toString());


                Log.d("SAMPLE",jsonArray.toString() );
                for (int i = 0; i < jsonArray.length(); i++) {

                    HotPepperGourmet hotPepperGourmet = new HotPepperGourmet();
                    JSONObject json = jsonArray.getJSONObject(i);
                    String id = json.getString("id"); // お店ID
                    String name = json.getString("name"); // 店名
                    String address = json.getString("address"); // 住所
                    Double lat = json.getDouble("lat"); // 緯度
                    Double lng = json.getDouble("lng"); // 経度
                    String lunch = json.getString("lunch"); //ランチありなし
                    String url = json.getJSONObject("urls").getString("pc"); // URL

                    Log.d(TAG, "お店ID:" + id);
                    Log.d(TAG, "店名:" + name);
                    Log.d(TAG, "住所:" + address);
                    Log.d(TAG, "緯度:" + lat.toString());
                    Log.d(TAG, "経度:" + lng.toString());
                    Log.d(TAG, "ランチありなし:" + lunch);
                    Log.d(TAG, "URL:" + url);

                    hotPepperGourmet.setId(id);
                    hotPepperGourmet.setName(name);
                    hotPepperGourmet.setAddress(address);
                    hotPepperGourmet.setLat(lat);
                    hotPepperGourmet.setLng(lng);
                    hotPepperGourmet.setLunch(lunch);
                    hotPepperGourmet.setUrl(url);

                    hotPepperGourmetArray.add(hotPepperGourmet);
                }

                if (mActivity instanceof ConfirmAsyncListener) {
                    // コールバック処理
                    ((ConfirmAsyncListener) mActivity).onRestaurantAsyncCallBack(hotPepperGourmetArray);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                // プログレスバーを閉じる
                if (sProgressDialog != null && sProgressDialog.isShowing()) {
                    sProgressDialog.dismiss();
                }
            }
        }

        interface ConfirmAsyncListener {
            void onRestaurantAsyncCallBack(ArrayList<HotPepperGourmet> hotPepperGourmetArray);
        }

    }


}

