package com.fit.basic.kr;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Vector;

/**
 * Created by Administrator on 2016-02-04.
 */
public class Activity_join extends Activity {

    private form_basic form_basic;
    class form_basic {
        EditText et_id;
        EditText et_password;
        EditText et_nickName;
        EditText et_phoneNumber;
        EditText et_email;

        Button btn_submit;

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_join); // 항상 제공되는
        // activity_layout.xml을 복사해서
        // 만듦
        final form_basic form_basic = new form_basic();

        form_basic.et_id = (EditText) findViewById(R.id.et_join_id);
        form_basic.et_password = (EditText) findViewById(R.id.et_join_password);
        form_basic.et_nickName = (EditText) findViewById(R.id.et_join_nickName);
        form_basic.et_phoneNumber = (EditText) findViewById(R.id.et_join_phoneNumber);
        form_basic.et_email = (EditText) findViewById(R.id.et_join_email);

        form_basic.btn_submit = (Button) findViewById(R.id.btn_join_submit);
        //this.overridePendingTransition( R.anim.anim_slide_in_left, R.anim.anim_slide_in_right);
        View.OnClickListener downloadListener = new View.OnClickListener() {
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    String id = form_basic.et_id.getText().toString();
                    String password = form_basic.et_password.getText().toString();
                    String nickName = form_basic.et_nickName.getText().toString();
                    String phoneNumber = form_basic.et_phoneNumber.getText().toString();
                    String email = form_basic.et_email.getText().toString();

                    id=urlEncodeUTF8(id);
                    password = urlEncodeUTF8(password);
                    nickName = urlEncodeUTF8(nickName);
                    phoneNumber = urlEncodeUTF8(phoneNumber);
                    email = urlEncodeUTF8(email);

                    String url = "http://14.63.223.92/member_Insert.php?id="+id+"&password="+password+"&nickName="+nickName+"&phoneNumber="+phoneNumber+"&email="+email;

                    phpTask phpTask = new phpTask();
                    phpTask.execute(url);
                }
            }
            // Codes.InitApp(this); // *
        };
        form_basic.btn_submit.setOnClickListener(downloadListener);
    }

    private boolean isNetworkAvailable(){
        boolean available = false;
        ConnectivityManager connMgr= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null & networkInfo.isAvailable())
            available = true;

        return available;
    }

    private void downloadUrl(String php) throws IOException{
        try {
            URL url = new URL(php);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream iStream = urlConnection.getInputStream();
            //if (iStream == null)
                //show.setText("ono");
            //else
                //show.setText("two");

        } catch(Exception e){
            Log.d("Exception ", e.toString());

        }
    }

    private class phpTask extends AsyncTask<String, Integer, String>{
        protected String doInBackground(String... url){

            try{
                Log.d("url",url[0].toString());
                downloadUrl(url[0]);
            }
            catch(Exception e){
                Log.d("Background Task", e.toString());
            }
            return null;
        }

        protected void onPostExecute(String result){
            AlertDialog.Builder alert = new AlertDialog.Builder(Activity_join.this);
            alert.setTitle("성공");
            alert.setMessage("가입이 완료되셨습니다.");
            alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Activity_join.this, Activity_login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    finish();
                }
            });
            alert.show();
        }
    }
    public static String urlEncodeUTF8(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

}
