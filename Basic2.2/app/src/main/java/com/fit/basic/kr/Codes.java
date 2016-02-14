package com.fit.basic.kr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import android.widget.LinearLayout;
import android.widget.TextView;

public class Codes {
	
	public static synchronized String send_url_by_post(URL url, String data) {

		String str = "";
		String re = "";
		URLConnection conn = null;

		try {

			// 접속
			conn = url.openConnection();
			// 데이터를 POST로 처리하기 위해 셋팅
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);

			conn.setDoOutput(true);
			// 데이터 송출을 위한 OutputStream 추출
			OutputStream os = conn.getOutputStream();
			OutputStreamWriter wr = new OutputStreamWriter(os);
			// 데이터 송출
			wr.write(data);
			wr.flush();
			// 서버가 전달하는 응답 결과를 받아주는 작업
			InputStream is = conn.getInputStream();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);

			while ((str = br.readLine()) != null) {

				re = str;
			}

			// connection 종료
			wr.close();
			br.close();

		} catch (Exception e) {
			e.printStackTrace();

			// Log.i("msg", "sendURLbyPost 통신모듈에러");
		}

		return re;

	}


	
	public static void dismiss_dialog(Dialog d) {
	
		if (d != null && d.isShowing()) {
		
			d.dismiss();
			d = null;
		
		}
	
	}
	
	public static View make_dialog(Dialog D, Activity act, String type, int dialog_frame, int dialog_ll_content, int dialog_element_layout, String title) 
	{

		View frame  = act.getLayoutInflater().inflate(dialog_frame, null); 
		LinearLayout ll_content = (LinearLayout) frame.findViewById(dialog_ll_content);

		//개발자가 정의하는 레이아웃
		View module = act.getLayoutInflater().inflate(dialog_element_layout, null); 
		ll_content.addView(module);	
		
		
		//다이얼로그 타이틀 지정
		if (!title.toString().equals("") ) {
			D.setTitle(title);
		} else {
			D.requestWindowFeature(Window.FEATURE_NO_TITLE);
		}
		
		

		
		D.setContentView(frame);
		D.setCancelable(true);

		return frame ;

	}
	
	

	public static boolean check_online(Context act) {
		try {

			ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

			if (wifi.isConnected()) {
				// WIFI, 3G 어느곳에도 연결되지 않았을때
				Log.i("networ state:", "Network connect success  by wifi");
				return true;

			}

			NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

			if (mobile.isConnected()) {
				// WIFI, 3G 어느곳에도 연결되지 않았을때
				Log.i("networ state:", "Network connect success by mobile");
				return true;

			}

		} catch (NullPointerException e) {
			return false;
		}
		Log.i("networ state:", "Network connect fail");

		return false;

	}
	
	
	
	public static synchronized Element read_url(URL url) {
		// 문서 전체 정보를 가지고 있는 Element 객체
		Element root = null;

		URLConnection conn = null;

		try {

			conn = url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			conn.connect();

			InputStream is = conn.getInputStream();

			// InputStream is = url.openStream();
			// DOM 파서 생성
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			// 파싱
			Document doc = builder.parse(new FlushedInputStream(is));
			// 문서 전체 정보 추출
			root = doc.getDocumentElement();

		} catch (Exception e) {

			e.printStackTrace();

			// Log.i("msg", "readURL 통신모듈에러발생함");

		} finally {

			conn = null;

		}

		// 문서 전체 정보를 리턴한다.
		return root;
	}

	// 스트림의 안정을 위해 static InputStreamWapper 를 쓰워준다.
	static class FlushedInputStream extends FilterInputStream {
		public FlushedInputStream(InputStream inputStream) {
			super(inputStream);
		}

		public long skip(long n) throws IOException {
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n) {
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L) {
					int b = read();
					if (b < 0) {
						break;
					} else {
						bytesSkipped = 1;
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	public static void RepairApp(Activity act) {

		if (Data.user_memberid.equals(null)) {

			Intent intent = new Intent(act.getApplicationContext(), Activity_main.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			act.startActivity(intent);
			System.exit(0);

		}

		// if (AutoCheckService.ac == null) {

		// Intent serviceIntent = new Intent(getBaseContext(),
		// AutoCheckService.class);
		// getBaseContext().startService(serviceIntent);

		// }

	}

	public static View show_notice_screen(Activity act, int R_layout, int m_content, int m_notice, boolean mode) {

		LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout ll_module_notice = (LinearLayout) act.findViewById(m_notice);

		View view;

		if (mode) {

			// 컨텐츠화면상태이면,

			// 기존 노티스화면의 차일드뷰를 모두 지우고 새롭게 삽입
			if (null != ll_module_notice && ll_module_notice.getChildCount() > 0) {
				try {
					ll_module_notice.removeViews(0, ll_module_notice.getChildCount());
				} catch (Exception e) {
					Log.i("msg", "노티스모드 child view를 제거 실패");
					e.printStackTrace();
				}
			}

			view = inflater.inflate(R_layout, ll_module_notice, true); // R.layout.notice_system_error

			act.findViewById(m_notice).setVisibility(View.VISIBLE);
			act.findViewById(m_content).setVisibility(View.GONE);
			

		} else {

			if (ll_module_notice.getVisibility() == View.VISIBLE) { // 노티스화면상태

				act.findViewById(m_notice).setVisibility(View.GONE);
				act.findViewById(m_content).setVisibility(View.VISIBLE);

			}

			view = null;
		}

		return view;

	}

	// 공용적인 초기화요소가 있을시
	public static void InitApp(Activity act) {

		// 폰트정의
		Data.font = Typeface.createFromAsset(act.getAssets(), Data.font_path);


	}

	public static boolean GetAppData(Context context) {
		// 환경변수가져오기 onresume에서 작동

		SharedPreferences prefdefault = PreferenceManager.getDefaultSharedPreferences(context);
		// Data. = prefdefault.getString("app_code", Data.AppCode);

		return true;
	}

	public static boolean SetAppData(Context context) {
		// 환경변수저장 onpause에서 작동

		SharedPreferences prefdefault = PreferenceManager.getDefaultSharedPreferences(context);
		// SharedPreferences.Editor sys_edit = prefdefault.edit();

		// sys_edit.putString("app_code", Data.xxx);

		return true;

	}

	// 로그아웃
	public static void LogoutApp(Activity act) {

		Data.user_memberid = null;

		// database 삭제
		act.deleteDatabase(Data.local_database_name);

		File cache = act.getCacheDir();
		File appDir = new File(cache.getParent());
		if (appDir.exists()) {
			String[] children = appDir.list();
			for (String s : children) {

				if (!s.equals("lib")) {
					deleteDir(new File(appDir, s));
					Log.i("", "clear cache memory .. appDir? " + appDir);
				}
			}
		}

		Intent intent = new Intent(act, Activity_main.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		act.startActivity(intent);
		System.exit(0);

	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}

}

class Progress_Dialog extends Dialog {

	// private ImageView iv;
	private TextView tv;

	public Progress_Dialog(Context context) {

		super(context, R.style.progress_dialog);

		WindowManager.LayoutParams wlmp = getWindow().getAttributes();
		wlmp.gravity = Gravity.CENTER_HORIZONTAL;
		getWindow().setAttributes(wlmp);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

		// iv = new ImageView(context);
		tv = new TextView(context);
		tv.setTypeface(Data.font);
		tv.setTextSize(20);
		tv.setText(new String(new char[] { 0xe804 })); // 805곡선미

		// iv.setImageResource(resourceIdOfImage);
		// layout.addView(iv, params);

		layout.addView(tv, params);

		addContentView(layout, params);

	}

	@Override
	public void show() {

		super.show();
		RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(1000);

		tv.setAnimation(anim);
		tv.startAnimation(anim);

	}
}
