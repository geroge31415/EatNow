package com.example.eatnow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

public class MainActivity extends AppCompatActivity implements LocationListener {

    static String API_KEY = "508dba382aa41e5c"; //APIKEY
    //spinnerの中身を宣言
    int mRange = 3; //距離の初期値
    private MainActivity HotPepperUtils;

    private LocationManager manager; //位置情報
    double mLat; //緯度
    double mLng; //経度

    static TextView textView;
    static ImageView imageView;

    static TextView pageNum1; //画面上部のページ数を表示するテキスト
    static TextView pageNum2; //画面下部のページ数を表示するテキスト

    static TextView shopSum; //検索結果の件数

    static Button leftButton1; //画面上部の左のボタン
    static Button rightButton1; //画面上部の右のボタン
    static Button leftButton2; //画面下部の左のボタン
    static Button rightButton2; //画面下部の右のボタン

    static int pageNumInt = 0; //現在のページ数-1
    static int pageSumInt = 0; //合計ページ数




    static ArrayList<String> photourl_list = new ArrayList<String>(); //店舗情報の記載されているlist
    static ArrayList<String> textview_item_list = new ArrayList<String>(); //サムネイル画像のurlが記載されているリスト

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE); //LocationManagerクラスのインスタンスを取得します。

        rightButton1 = (Button) findViewById(R.id.rightButton1);
        rightButton2 = (Button) findViewById(R.id.rightButton2);
        leftButton1 = (Button) findViewById(R.id.leftButton1);
        leftButton2 = (Button) findViewById(R.id.leftButton2);


        //ボタンを見えなくする
        leftButton1.setVisibility(View.INVISIBLE);
        leftButton2.setVisibility(View.INVISIBLE);
        rightButton1.setVisibility(View.INVISIBLE);
        rightButton2.setVisibility(View.INVISIBLE);


        //ラジオボタンを操作した際に動く
        RadioGroup group = (RadioGroup) findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener((view, id) -> {

            //ラジオボタンで選択された距離を設定
            if (id == R.id.radioButton1) {
                mRange = 1; //300m
            } else if (id == R.id.radioButton2) {
                mRange = 2; //500m
            } else if (id == R.id.radioButton3) {
                mRange = 3; //1000m
            } else if (id == R.id.radioButton4) {
                mRange = 4; //2000m
            } else if (id == R.id.radioButton5) {
                mRange = 5; //3000m
            }

            callHotPepperGroumetAPI(); //API呼び出し

            pageNumInt=0; //ページ数を0に設定

            pageNum1=(TextView)findViewById(R.id.pageNum1);
            pageNum2=(TextView)findViewById(R.id.pageNum2);

            //ページ数を画面に表示(ページ1)
            pageNum1.setText("ページ"+(pageNumInt+1));
            pageNum2.setText("ページ"+(pageNumInt+1));

            ScrollView scrollView = findViewById(R.id.scrollView); // ScrollViewのIDを
            scrollView.scrollTo(0, 0); // xとyのオフセットを0に設定して一番上にスクロール

        });


        //一つ前のページに遷移する場合
        findViewById(R.id.leftButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pageNumInt -= 1; //現在のページから1つ左に移動
                    pageChange(); //ページ遷移関数
                    rightButton1.setVisibility(View.VISIBLE); //画面上部右ボタンを見えるようにする
                    rightButton2.setVisibility(View.VISIBLE); //画面下部右ボタンを見えるようにする
            }
        });
        //一つ前のページに遷移する場合
        findViewById(R.id.leftButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pageNumInt -= 1; //現在のページから1つ左に移動
                    pageChange();  //ページ遷移関数
                    rightButton1.setVisibility(View.VISIBLE); //画面上部右ボタンを見えるようにする
                    rightButton2.setVisibility(View.VISIBLE); //画面下部右ボタンを見えるようにする
            }
        });
        //一つ後ろのページに遷移する場合
        findViewById(R.id.rightButton1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pageNumInt += 1; //現在のページから1つ右に移動

                    pageChange(); //ページ遷移関数
                    leftButton1.setVisibility(View.VISIBLE); //画面上部左ボタンを見えるようにする
                    leftButton2.setVisibility(View.VISIBLE); //画面下部左ボタンを見えるようにする

            }
        });
        //一つ後ろのページに遷移する場合
        findViewById(R.id.rightButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    pageNumInt += 1; //現在のページから1つ右に移動

                    pageChange(); //ページ遷移関数
                    leftButton1.setVisibility(View.VISIBLE); //画面上部左ボタンを見えるようにする
                    leftButton2.setVisibility(View.VISIBLE); //画面下部左ボタンを見えるようにする

            }
        });


    }

    public void pageChange() {

        int[] textViews = new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9, R.id.textView10};
        int[] imageViews = new int[]{R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8, R.id.imageView9, R.id.imageView10};

        pageNum1=(TextView)findViewById(R.id.pageNum1);
        pageNum2=(TextView)findViewById(R.id.pageNum2);


        //現在のページ数を表示
        pageNum1.setText("ページ"+(pageNumInt+1));
        pageNum2.setText("ページ"+(pageNumInt+1));


        //画面をクリア
        for (int i = 0; i < 10; i++) {
            imageView = (ImageView)findViewById(imageViews[i]);
            textView = (TextView) findViewById(textViews[i]);
            textView.setText("");
            imageView.setImageDrawable(null);
        }


        if (pageNumInt < pageSumInt - 1) { //1～最終ページの一つ前のページ
            for (int i = pageNumInt * 10; i < (pageNumInt + 1) * 10; i++) {

                imageView = (ImageView) findViewById(imageViews[i%10]);
                textView = (TextView) findViewById(textViews[i%10]);
                //画像セット
                Picasso.get()
                        .load(photourl_list.get(i))
                        .resize(1000, 1000)
                        .into(imageView);
                //textセット
                textView.setText(textview_item_list.get(i));

            }
        }else{ //最終ページ
            for (int i = pageNumInt * 10; i < photourl_list.size(); i++) {
                imageView = (ImageView) findViewById(imageViews[i % 10]);
                textView = (TextView) findViewById(textViews[i % 10]);
                //画像セット
                Picasso.get()
                        .load(photourl_list.get(i))
                        .resize(1000, 1000)
                        .into(imageView);
                //textセット
                textView.setText(textview_item_list.get(i));
            }

            Log.d(TAG, "pageChange: "+pageNumInt+":"+pageSumInt);
            //最終ページの場合,右ボタンを見えなくする
            rightButton1.setVisibility(View.INVISIBLE);
            rightButton2.setVisibility(View.INVISIBLE);

        }

        //1ページ目の場合
        if(pageNumInt==0){
            //左ボタンを非表示
            leftButton1.setVisibility(View.INVISIBLE);
            leftButton2.setVisibility(View.INVISIBLE);
        }

    }


    public void callHotPepperGroumetAPI() {
        //https://webservice.recruit.co.jp/doc/hotpepper/reference.html#a1to参照
        int mLunch = 0; //ランチの有無　0:絞り込まない（初期値）1:絞り込む

//        ArrayList<String> mGenreCdList = new ArrayList<String>(Arrays.asList("G001", "G002", "G003"));//ジャンル選択(https://webservice.recruit.co.jp/hotpepper/genre/v1/?key=sampleを参照)
//        int mMidnight_meal = 0; //23時以降食事OK	0:絞り込まない（初期値） 1:絞り込む
//        ArrayList<String> mKeywordList = new ArrayList<String>(Arrays.asList("海鮮")); //いずれか最低1つが必要。// 店名かな、店名、住所、駅名、お店ジャンルキャッチ、キャッチのフリーワード検索(部分一致)が可能です。文字コードはUTF8。半角スペース区切りの文字列を渡すことでAND検索になる。複数指定可能
        // HotPepperグルメAPIの呼び出し
        HotPepperGourmetSearch hotPepperGourmetSearch = new HotPepperGourmetSearch();
        hotPepperGourmetSearch.setLat(mLat); // 画面でセットした緯度
        hotPepperGourmetSearch.setLng(mLng); // 画面でセットした経度
        hotPepperGourmetSearch.setLunch(mLunch); // 画面でセットしたランチ有無
        hotPepperGourmetSearch.setRange(mRange); // 画面でセットした検索範囲距離//1: 300m 2: 500m 3: 1000m (初期値) 4: 2000m 5: 3000m
//        hotPepperGourmetSearch.setGenreCdList(mGenreCdList); // 画面でセットしたジャンルのリスト
//        hotPepperGourmetSearch.setMidnight_meal(mMidnight_meal); // 画面でセットした23時以降食事OK
//        hotPepperGourmetSearch.setKeywordList(mKeywordList); // 画面でセットしたキーワードのリスト
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

    }


    //位置情報のパーミッション許可
    @Override
    protected void onResume() {
        super.onResume();

        //指定したパーミッションが許可されているか確認します。
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1); //パーミッションの許可を依頼します。
            return;
        }

        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1, this);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1, this);
    }

    //終了処理
    @Override
    protected void onStop() {
        super.onStop();

        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.removeUpdates(this); //位置情報の取得処理を終了します。
        }
    }


    //位置情報が変更された場合取得
    @Override
    public void onLocationChanged(@NonNull Location location) {
        mLat = location.getLatitude();
        mLng = location.getLongitude();
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
//        StringBuilder genreSb = new StringBuilder();
//        genreSb.append("&genre=");

//        for (int i = 0; i < hotPepperGourmetSearch.getGenreCdList().size(); i++) {
//            if (i > 0) {
//                genreSb.append("&genre=");
//            }
//
//            String genreCd = hotPepperGourmetSearch.getGenreCdList().get(i);
//            genreSb.append(genreCd);
//        }

        // キーワードの切り取り
//        StringBuilder keywordSb = new StringBuilder();
//        keywordSb.append("&keyword=");

//        for (int i = 0; i < hotPepperGourmetSearch.getKeywordList().size(); i++) {
//            if (i > 0) {
//                keywordSb.append("&keyword=");
//            }
//
//            String keyword = hotPepperGourmetSearch.getKeywordList().get(i);
//            keywordSb.append(keyword);
//        }

        // URLの生成
        StringBuilder urlStringBuilder = new StringBuilder();
        urlStringBuilder.append("https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key="); //28以降はhttps
        urlStringBuilder.append(API_KEY);
//        urlStringBuilder.append(genreSb.toString()); // 飲食店のジャンル
//        urlStringBuilder.append("&midnight_meal="); // 23時以降食事OK
//        urlStringBuilder.append(hotPepperGourmetSearch.getMidnight_meal());
//        urlStringBuilder.append(keywordSb.toString()); // キーワード
//        urlStringBuilder.append("&lunch="); // ランチ営業
//        urlStringBuilder.append(hotPepperGourmetSearch.getLunch());
        urlStringBuilder.append("&lat="); // 緯度
        urlStringBuilder.append(hotPepperGourmetSearch.getLat());
        urlStringBuilder.append("&lng="); // 経度
        urlStringBuilder.append(hotPepperGourmetSearch.getLng());
        urlStringBuilder.append("&range="); // 検索範囲距離
        urlStringBuilder.append(hotPepperGourmetSearch.getRange());
        urlStringBuilder.append("&count=100"); // 1ページあたりの取得数
        urlStringBuilder.append("&format=json"); // レスポンス形式


        URL url = null;

        //url接続
        try {
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

    }


    //非同期処理とJSONのパース
    public static class RestaurantAsync extends AsyncTask<URL, Void, String> {

        private Activity mActivity;
        private StringBuffer mBuffer = new StringBuffer();

        private static final String TAG = "RestaurantAsync";

        public static ProgressDialog sProgressDialog;

        /**
         * コンストラクタ
         *
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
         *
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
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                // JSONをパースして各飲食店の情報を取得する
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONObject("results").getJSONArray("shop");
                ArrayList<HotPepperGourmet> hotPepperGourmetArray = new ArrayList<>();

                int[] textViews = new int[]{R.id.textView1, R.id.textView2, R.id.textView3, R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7, R.id.textView8, R.id.textView9, R.id.textView10};
                int[] imageViews = new int[]{R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5, R.id.imageView6, R.id.imageView7, R.id.imageView8, R.id.imageView9, R.id.imageView10};
                pageNum1 = (TextView) mActivity.findViewById(R.id.pageNum1);
                pageNum2 = (TextView) mActivity.findViewById(R.id.pageNum2);
                leftButton1 = (Button) mActivity.findViewById(R.id.leftButton1);
                leftButton2 = (Button) mActivity.findViewById(R.id.leftButton2);
                rightButton1 = (Button) mActivity.findViewById(R.id.rightButton1);
                rightButton2 = (Button) mActivity.findViewById(R.id.rightButton2);



                textview_item_list.clear(); //店舗情報のtextlistをクリア
                photourl_list.clear(); //サムネイル画像のlistをクリア

                //画面リセット
                for (int i = 0; i < 10; i++) {
                    imageView = (ImageView) mActivity.findViewById(imageViews[i]);
                    textView = (TextView) mActivity.findViewById(textViews[i]);
                    textView.setText("");
                    imageView.setImageDrawable(null);
                }

                //店の数の分だけJSONから抽出
                for (int i = 0; i < jsonArray.length(); i++) {

                    HotPepperGourmet hotPepperGourmet = new HotPepperGourmet();
                    JSONObject json = jsonArray.getJSONObject(i);
                    String photourl = json.getJSONObject("photo").getJSONObject("mobile").getString("l");//サムネイル画像url
                    String id = json.getString("id"); // お店ID
                    String name = json.getString("name"); // 店名
//                    String address = json.getString("address"); // 住所
//                    Double lat = json.getDouble("lat"); // 緯度
//                    Double lng = json.getDouble("lng"); // 経度
//                    String lunch = json.getString("lunch"); //ランチありなし
                    String url = json.getJSONObject("urls").getString("pc"); // ホットペッパーグルメの店舗URL
                    String mobile_access = json.getString("mobile_access"); //アプリ用の店舗へのアクセス

//                    Log.d(TAG, "画像url:" + photourl);//サムネイル画像url
//                    Log.d(TAG, "お店ID:" + id);// お店ID
//                    Log.d(TAG, "店名:" + name);
//                    Log.d(TAG, "住所:" + address);
//                    Log.d(TAG, "緯度:" + lat.toString());
//                    Log.d(TAG, "経度:" + lng.toString());
//                    Log.d(TAG, "ランチありなし:" + lunch);//ランチありなし
//                    Log.d(TAG, "URL:" + url); // ホットペッパーグルメの店舗URL
//                    Log.d(TAG, "アクセス" + mobile_access); //アプリ用の店舗へのアクセス

//                    hotPepperGourmet.setId(id);// お店ID
//                    hotPepperGourmet.setName(name); // 店名
//                    hotPepperGourmet.setAddress(address);
//                    hotPepperGourmet.setLat(lat);
//                    hotPepperGourmet.setLng(lng);
//                    hotPepperGourmet.setLunch(lunch);//ランチありなし
//                    hotPepperGourmet.setUrl(url); // ホットペッパーグルメの店舗URL

//                    hotPepperGourmetArray.add(hotPepperGourmet);

                    textview_item_list.add(name + "\n" + url + "\n" + mobile_access + "\n"); //表示するテキストリストに追加
                    photourl_list.add(photourl); //表示するサムネイル画像のをリストに追加

                    //最初の10件を表示
                    if (i < 10) {
                        imageView = (ImageView) mActivity.findViewById(imageViews[i]);
                        textView = (TextView) mActivity.findViewById(textViews[i]);
                        //画像表示
                        Picasso.get()
                                .load(photourl_list.get(i))
                                .resize(1000, 1000)
                                .into(imageView);
                        //テキスト表示
                        textView.setText(textview_item_list.get(i));

                    }
                }


                Log.d(TAG, "pageChange: photo"+photourl_list.size());
                pageSumInt = (int) Math.ceil(photourl_list.size() / 10.0); //検索結果の店数を設定


                //検索結果数が0～10の場合
                if (photourl_list.size() >= 0 && photourl_list.size() <= 10) {
                    leftButton1.setVisibility(View.INVISIBLE);
                    leftButton2.setVisibility(View.INVISIBLE);
                    rightButton1.setVisibility(View.INVISIBLE);
                    rightButton2.setVisibility(View.INVISIBLE);
                } else {
                    leftButton1.setVisibility(View.INVISIBLE);
                    leftButton2.setVisibility(View.INVISIBLE);
                    rightButton1.setVisibility(View.VISIBLE);
                    rightButton2.setVisibility(View.VISIBLE);
                }

                //検索結果の店数を表示
                shopSum=(TextView)mActivity.findViewById(R.id.shopSum);
                shopSum.setText("検索結果："+photourl_list.size()+"件");


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

