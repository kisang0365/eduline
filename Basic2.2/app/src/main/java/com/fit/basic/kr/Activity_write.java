package com.fit.basic.kr;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.w3c.dom.Element;

import com.fit.basic.kr.Activity_group.on_dialog_click_listener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import android.widget.TextView;
import android.widget.Toast;

/*-----------------------------------------------------------
 * fit 내용보기 액티버티  규약 1.1 
 *  
 *  Codes 시스템레벨이나 공용라이브러리
 *  Code.AaaBbb()  시스템전체에 영향을 끼치는 함수(건드리지마세요)
 *  Code.aaa_bbb() 개발자들의 필요에 의해서 사용하는 공용라이브러리 (함께 사용가능한 함수나 클라스는 여기에 등록함)
 *  Data.java 앱전체에서 공유하는 주요정보 : 예) Data.user_memberid (user table의 memberid 정보가 담겨짐)
 *  주석규약 : 약속함수 주석에 //*가 약속으로써 있는 것은 그대로 사용함 삭제금지, 개발시 참조한 웹사이트주소 주석에 넣기
 *  
 *  form_basic
 *  
 *  actvity_layout.xml, element_layout.xml 관련 디자인규약
 *  시스템함수사용규약 : Codes.InitApp(), Codes.RepairApp(), Codes.GetAppData(), Codes.SetAppData()
 *  사용자함수사용규약 : init(), start_thread()
 *  옵션버튼사용규약 
 *  공지(배너)화면 규약: Codes.show_notice_screen
 *  inflate 하는 Element 사용규약과 사용자이벤트호출규약 : element_header.xml,element_footer.xml, element_footer_clicked()
 *  
 *-----------------------------------------------------------*/

public class Activity_write extends Activity implements OnFocusChangeListener {

	Boolean enable_thread = true; // *
	public static boolean start_thread_enable; // * start_thread의 실행가능여부결정(외부
												// 액티버트나 서비스에서 재 시행을 요구할 수 있도록
												// 외부공개함

	Intent intent;
	private Dialog dialog ;  //*

	
	private form_basic form_basic;

	class form_basic {

		EditText content;
		Boolean content_enable;
		TextView content_title;
		TextView content_title_addon;
		TextView content_init;
	

	}


	Set_data thread_set;
	Get_data thread_get;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_write); // 항상 제공되는
													// activity_layout.xml을 복사해서
		// 만듦

		// this.overridePendingTransition( R.anim.anim_slide_in_left,
		// R.anim.anim_slide_out_right);

		this.overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

		intent = getIntent();

		Codes.InitApp(this); // *

		init(); // * 객체 및 요소객체 등의 초기화작업

	}
	
	@Override
	public void finish() {
		super.finish();
	
		this.overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);

	
	}
	

	@Override
	protected void onResume() {

		super.onResume();

		Codes.RepairApp(this); // * 치명적 에러발생시 자동복구/재실행
		Codes.GetAppData(this); // * 시스템정보

		// 액티버티가 자체적으로 사용하는 preferences 정보는 여기에서 가져오세요 [예제]
		// SharedPreferences pref =
		// getSharedPreferences(getClass().getSimpleName(), 0);
		// String statue = pref.getString("statue", "");

		if (start_thread_enable) { // * 최초 실행인 경우 (항상 onresume마다 주쓰레드가 실행되면 안되며,
									// 주쓰레드의 중복실행방지)

			start_thread_enable = false; // [주의] 주쓰레드가 정상적으로 종료되지 않으면 다시 true로
											// 전환되어야한다. (예.네트웍에러 등)
			start_thread(); // * 참고4 주요한 쓰레드를 작동시키는 함수

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// 옵션메뉴
		getMenuInflater().inflate(R.menu.activity_client, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		// 참고5 디자인이나 기획이 완성되기전에는 옵션메뉴바를 이용해서 개발 및 테스트를 하세요.

		switch (item.getItemId()) {

		case R.id.send_msg:

			Toast.makeText(this, "선택된 클라이언트에게 양식메세지를 보냅니다.", Toast.LENGTH_SHORT).show();

			break;

		case R.id.call_contact:

			Toast.makeText(this, "클라이언트 등록을 위해서 주소록을 호출합니다.", Toast.LENGTH_SHORT).show();

			break;

		case R.id.app_logout:

			// 메뉴 res/menu/activity_client.xml 참조

			Toast.makeText(this, "로그아웃합니다.", Toast.LENGTH_SHORT).show();

			Codes.LogoutApp(this); // * 로그아웃

			break;

		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {

		case KeyEvent.KEYCODE_BACK:

			Toast.makeText(this, "백버튼클릭-정리할 것이 있으면 정리하세요.", Toast.LENGTH_SHORT).show();

		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	private void init() {

		Toast.makeText(this, "액티버티를 초기화합니다.", Toast.LENGTH_SHORT).show();

		// 디자인 초기화
		start_thread_enable = true; // * start_thread()를 실행가능하게 지정함

		// 액티버티의 초기화면이 필요할때 사용한다. 예) 배너광고, 추가적인 가입요구, 카톡의 친구자동추천, 공사중, 서버장애,
		// 무선네트웍연결실패시, 치명적인 에러 발생시
		// true면 m_notice가 보여지고, false면 m_content가 보여짐
		// 다음은 사용예제며 사용하지 않을 수 있으나 미사용시 네트웍에러, 장애, 보여줄 데이타가 아직 없을때 처리해야함.

		// View v = Codes.show_notice_screen(this,
		// R.layout.element_notice_screen, R.id.ll_center_content,
		// R.id.ll_center_notice, true);
		// 개발자가 만드는 element_는 element_layout.xml을 복사해서 만듬
		// 가능하면 다른 개발자도 공용 디자인객체로 사용할 수 있도록 만듬

		// TextView tv_element_notice_screen_title = (TextView)
		// v.findViewById(R.id.tv_element_notice_screen_title);
		// tv_element_notice_screen_title.setText("advitising banner or network failed \n reconnect");

		LayoutInflater inflater = getLayoutInflater();

		// 헤더와 풋터 초기화
		LinearLayout ll_header = (LinearLayout) findViewById(R.id.ll_header);
		LinearLayout ll_footer = (LinearLayout) findViewById(R.id.ll_footer);
		// 헤더메뉴 R.layout.element_header과 같은 디자인 앨리먼트는 직접 수정하지않음
		// 수정이 필요하면 요구하고, 수정전에는 수정이 되었다고 가정하고 작업함
		// 당연히 프로그램적으로 메모리에 로딩된 것을 수정하는 것은 개발자의 자유

		View element_header = inflater.inflate(R.layout.element_header, ll_header);

		TextView tv_element_header_left = (TextView) findViewById(R.id.tv_element_header_left);

		tv_element_header_left.setTypeface(Data.font);
		tv_element_header_left.setText(new String(new char[] { 0xe966 })); // <

		TextView tv_element_header_right = (TextView) findViewById(R.id.tv_element_header_right);
		tv_element_header_right.setTypeface(Data.font);
		tv_element_header_right.setText(new String(new char[] { 0xe95e })); // <

		View element_footer = inflater.inflate(R.layout.element_write_functions, ll_footer);
		// 주의 이런식으로 앨리먼트가 inflate되기 때문에 element의 소속객체 id끼리 중복될수 있다.
		// element 개발자는 소속객체의 id가 중복되지 않기하기 위해서 노력해야한다.
		// 필요시 독립된 캡술화된 객체로 만들수도 있으나 권장하지않음

		switch (intent.getExtras().getInt("adaptor")) { // =
														// R.layout.adaptor_form_basic

		case R.layout.adaptor_form_basic:

			set_adaptor_form_basic(R.id.ll_activity_write_center, inflater); // edit1개

			break;

		}

	}

	private void set_adaptor_form_basic(int area, LayoutInflater inflater) {

		LinearLayout ll_area = (LinearLayout) findViewById(area);

		View adaptor_form_basic = inflater.inflate(R.layout.adaptor_form_basic, ll_area);

		/* form */

		form_basic = new form_basic();

		form_basic.content_enable = false;
		form_basic.content = (EditText) adaptor_form_basic.findViewById(R.id.et_form_basic_content);
		form_basic.content_init = (TextView) adaptor_form_basic.findViewById(R.id.tv_form_basic_content_init);
		form_basic.content_title_addon = (TextView) adaptor_form_basic.findViewById(R.id.tv_adaptor_form_basic_content_title_addon);

		form_basic.content_init.setTypeface(Data.font);
		form_basic.content_init.setText(new String(new char[] { 0xe972 })); // X모양으로변경

		// 독립적인 이벤트 핸들러
		on_adaptor_form_click_listener l = new on_adaptor_form_click_listener();

		form_basic.content_init.setOnClickListener(l);

		form_basic.content_title_addon.setTypeface(Data.font);
		form_basic.content_title_addon.setText("10"); // 글자수지정

		// focus
		// adaptor_form_basic.findViewById(R.id.ll_adaptor_form_basic_content).requestFocus();

		form_basic.content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (s.toString().length() <= 0 || s.toString().length() > 10) {

					form_basic.content_enable = false;

				} else {

					form_basic.content_enable = true;

				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

				form_basic.content_title_addon.setText("" + (10 - s.length()));

				if (s.length() > 10) {
					form_basic.content_title_addon.setTextColor(getResources().getColor(R.color.app_ci_3));

				} else {
					form_basic.content_title_addon.setTextColor(getResources().getColor(R.color.app_ci_4));
				}

			}

		});

		form_basic.content.setOnFocusChangeListener(this);

		// thread_set.form_basic.content.requestFocus();

	}

	class on_adaptor_form_click_listener implements OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.tv_form_basic_content_init:

				form_basic.content.setText("");
				form_basic.content_title_addon.setText("10");

				break;

			}

		}

	}

	private void start_thread() {

		if (enable_thread) {

			// 저장하기
			if (intent.getExtras().getInt("adaptor") == R.layout.adaptor_form_basic) {

				
				if (!intent.getExtras().get("msgid").toString().equals("")) {

					Toast.makeText(this, "수정모드라서 초기데이타를 가져옴 ", Toast.LENGTH_SHORT).show();

					thread_get = null;

					thread_get = new Get_data();
					thread_get.set(intent.getExtras().getInt("adaptor"), intent.getExtras().get("msgid").toString(), 2);
					thread_get.progress_dialog.show();
					thread_get.start();

					enable_thread = false; // 중복호출방지(핸들러의 맨앞에서 true로 전환됨)

				} else {

					Toast.makeText(this, "시작모드라서 아무것도 가져오지 않음", Toast.LENGTH_SHORT).show();

				}
			}

		}

	}

	@Override
	protected void onPause() {

		super.onPause();

		Codes.SetAppData(this); // *

		// preferences 처리영역

		Toast.makeText(this, "액티버티 활동이 중지됨 | 상태정보를 저장됨", Toast.LENGTH_SHORT).show();

	}

	// ---------------- 사용자발생 이벤트핸들러 --------------------

	// xml에서 직접 onclick="button_clicked"에 의해서 작동합니다
	// 소스가 깨끗해지고, 이벤트가 접근 방식이 달라서 편리한 점이 많아서 선호

	// element_footer 사용자 이벤트
	public void element_footer_clicked(View v) {

		switch (v.getId()) {

		case R.id.ll_element_footer_tab1:

			intent = new Intent(this, Activity_write.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);

			break;

		case R.id.ll_element_footer_tab2:

			intent = new Intent(this, Activity_group.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);

			break;

		}

	}

	public void button_clicked(View v) {

		Toast.makeText(this, "xml내 정의된 onclick button이 눌러짐", Toast.LENGTH_SHORT).show();
		
		switch (v.getId()) {
		

		case R.id.tv_element_write_funtions_run:

			// 내용체크

			// 강제로 포커스를 변경함 (에러여부보여주기)
			v.setFocusable(true);
			v.setFocusableInTouchMode(true);
			v.requestFocus();
			
			
			if (!form_basic.content_enable) {

				Toast.makeText(this, getResources().getString(R.string.app_input_data_problem), Toast.LENGTH_SHORT).show();
				form_basic.content.requestFocus();

				
			} else {
				// 아무론 문제가 없으면 thread_set실행.
				
				thread_set = null;
				thread_set = new Set_data();
				thread_set.set(R.layout.adaptor_form_basic, intent.getExtras().get("msgid").toString(), 6);

				
				if (thread_set.getState() == Thread.State.NEW)
				{
					thread_set.progress_dialog.show();
					thread_set.start();
					
				}

			}

			break;

		case R.id.tv_element_notice_screen_title:

			// element_notice_screen 화면이 나타났을때, 버튼을 누르면 나오는 화면
			// 예제로써 해제한 것임
			// 보통 주 쓰레드가 성공하면.. 아래 구문으로 해제시킴

			Codes.show_notice_screen(this, R.layout.element_notice_screen, R.id.ll_center_content, R.id.ll_center_notice, false);
			// start_thread();

			break;

		case R.id.tv_call_contact:

			Toast.makeText(this, "액터버티호출[주소록연락처..import]", Toast.LENGTH_SHORT).show();

			break;

		case R.id.ll_element_header_left: // element_header 에서

			intent = new Intent(this, Activity_client.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);

			finish();

			break;

		case R.id.ll_element_header_right: // element_header에서
			

			

			break;

		}

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub

		switch (v.getId()) {

		case R.id.et_form_basic_content:

			form_basic.content_init.setTypeface(Data.font);

			Toast.makeText(this, "포커스변동" + hasFocus, Toast.LENGTH_SHORT).show();

			if (hasFocus) {

				if (form_basic.content.getText().equals(getResources().getString(R.string.adaptor_form_basic_content_comment))) {

					form_basic.content_init.setText("");// 안보이게함.

				} else {
					// 기존에 작성된 내용이 있으면
					form_basic.content_init.setText(new String(new char[] { 0xe972 })); // X모양으로변경

				}

			} else {

				if (form_basic.content_enable) {

					form_basic.content_init.setText(new String(new char[] { 0xe92b })); // 체크문양

				} else {

					if (form_basic.content.getText().length() == 0) {
						// 입력한 것이 전혀 없으면

						form_basic.content.setText(getResources().getString(R.string.adaptor_form_basic_content_comment));
						form_basic.content_title_addon.setText("");

					}
					form_basic.content_init.setText(new String(new char[] { 0xe87a })); // !문양

				}

			}

			break;

		}

	}

	/* 문의하기는 한번 발생 */
	class Set_data extends Thread {

		public Progress_Dialog progress_dialog;

		int adaptor, handler_code;
		String msgid;



		Display_handler handler;

		public void set(int _adaptor, String _msgid, int _handler_code) {

			adaptor = _adaptor;
			handler_code = _handler_code;

			progress_dialog = new Progress_Dialog(Activity_write.this);

			handler = new Display_handler(Activity_write.this);// 메모리릭을 피하기 위해서

			

		}

		public void run() {

			try {

				String result = "";

				URL url = null;

				switch (adaptor) { 

				// 어댑터에 따라 form이 달라짐
				case R.layout.adaptor_form_basic:

					// 저장모드일때 set
					String data = "method=" + adaptor;//
					data = data + "&msgid=" + msgid;
					data = data + "&msg_parent_msgid=";
					data = data + "&user_memberid=" + Data.user_memberid;

					/* msg-object interface */
					data = data + "&object_type="; // ?
					data = data + "&objectid=";
					data = data + "&partid="; // ?
					data = data + "&secret=";
					data = data + "&private="; // ? 받은사람
					data = data + "&user_membership=";
					data = data + "&content=" + URLEncoder.encode(form_basic.content.getText().toString(), "UTF-8");

					data = data + "&subject="; // ?
					data = data + "&category_id=";

					Log.i("msg", "send_data:" + data);

					url = new URL(Data.server_path + "s/set_msg.asp");

					result = Codes.send_url_by_post(url, data);
					result = URLDecoder.decode(result, "UTF-8");

					if (result.toString().equals("")) {

						handler.sendEmptyMessage(3);  //서버에러나 통신에러

					} else if (result.toString().equals("SUCCESS")) {

						Log.i("알림", "정상적으로 저장되었습니다");
						handler.sendEmptyMessage(handler_code); // code: 5

					}
					else {
						handler.sendEmptyMessage(4); // 등록실패에러
					}

					break;

				
				
				
				}

			} catch (Exception e) {

				e.printStackTrace();

				handler.sendEmptyMessage(3);

			}

		}

	}

	/* 문의하기는 한번 발생 */
	class Get_data extends Thread {

		public Progress_Dialog progress_dialog;

		int method, handler_code;
		String msgid;

		private form_basic form_basic;

		Display_handler handler;

		class form_basic {

			String content;

		}

		public void set(int adaptor, String _msgid, int _handler_code) {

			method = adaptor;
			handler_code = _handler_code;

			progress_dialog = new Progress_Dialog(Activity_write.this);

			handler = new Display_handler(Activity_write.this);// 메모리릭을 피하기 위해서

			switch (adaptor) {

			// 어댑터에 따라 form이 달라짐
			case R.layout.adaptor_form_basic:

				form_basic = new form_basic();

				break;

			}

		}

		public void run() {

			try {

				Element root = null;
				URL url = null;

				switch (method) { // adaptor

				// 어댑터에 따라 form이 달라짐
				case R.layout.adaptor_form_basic:

					String data = "method=" + method;//
					data = data + "&msgid=" + msgid;

					Log.i("msg", "send_data:" + data);

					url = new URL(Data.server_path + "s/get_msg.asp?" + data);
					Log.i("send_data", Data.server_path + "s/get_msg.asp?" + data);

					root = Codes.read_url(url);

					if (root == null) {

						handler.sendEmptyMessage(0); // 통신에러

					} else if (root.getTextContent().toString().equals(null)) {

						handler.sendEmptyMessage(0); // 서버에러

					} else if (root.getTextContent().toString().equals("")) {

						handler.sendEmptyMessage(1); // 서버에 정보가 없는 경우

					} else if (!root.getTextContent().toString().equals("")) {

						form_basic.content = URLDecoder.decode(root.getElementsByTagName("content").item(0).getTextContent(), "UTF-8");

						handler.sendEmptyMessage(handler_code); // 핸들러2

					}
					break;

				}

			} catch (Exception e) {

				e.printStackTrace();

				handler.sendEmptyMessage(0);

			}

		}

	}

	static class Display_handler extends Handler {

		private final WeakReference<Activity_write> mActivity;

		Display_handler(Activity_write activity) {

			mActivity = new WeakReference<Activity_write>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			Activity_write activity = mActivity.get();

			if (activity != null) {

				activity.handle_message(msg);
			}
		}

	}

	// ---------------- 사용자발생 이벤트핸들러 --------------------
	
	
	class on_dialog_click_listener implements OnClickListener {
	
		@Override
		public void onClick(View v) {
	
			
			Toast.makeText(getApplicationContext(), "......on_dialog_click_listener....", Toast.LENGTH_SHORT).show();
	
			
			switch (v.getId()) {
	
			
	
			
			case R.id.tv_dialog_msgbox_button_left:
					
				//Dialog D = ((Dialog) v.getTag()) ;
				Codes.dismiss_dialog(dialog); 
				
				
				break;
				
			case R.id.tv_dialog_msgbox_button_right:
				
				
				Codes.dismiss_dialog(dialog); 
				
				
				break;
				
			}
			
			
			
	
		}
	
	}

	private void handle_message(Message msg) {

		Log.i("", "handleMessage?" + msg.what);

		enable_thread = true; // 쓰레드작동가능

		if (msg.what == 0) {
			// 네트웍 혹은 서버에러

			start_thread_enable = true; // onresume시 start_thread 진행 *

			thread_get.progress_dialog.dismiss();

			View v = Codes.show_notice_screen(this, R.layout.element_notice_screen, R.id.ll_center_content, R.id.ll_center_notice, true);
			TextView tv_element_notice_screen_title = (TextView) v.findViewById(R.id.tv_element_notice_screen_title);
			tv_element_notice_screen_title.setTypeface(Data.font);

			// 에러의 특성에 따라 노티스화면방식으로 결과보여주기

			if (Codes.check_online(getApplicationContext())) {
				// 네트웍연결상태는 정상, 그러므로 시스템, 서버에러
				tv_element_notice_screen_title.setText(new String(new char[] { 0xe87d }) + "\n"
						+ getResources().getString(R.string.app_server_problem));

			} else {
				// 네트웍연결상태 비정상
				tv_element_notice_screen_title.setText(new String(new char[] { 0xe87d }) + "\n"
						+ getResources().getString(R.string.app_network_problem));

			}

		} else if (msg.what == 1) {
			// 받은 데이타가 없을 경우

			thread_get.progress_dialog.dismiss();
			start_thread_enable = true;

			// 데이타가 없음을 노티스화면방식으로 결과보여주기
			View v = Codes.show_notice_screen(this, R.layout.element_notice_screen, R.id.ll_center_content, R.id.ll_center_notice, true);

			TextView tv_element_notice_screen_title = (TextView) v.findViewById(R.id.tv_element_notice_screen_title);
			tv_element_notice_screen_title.setTypeface(Data.font);
			tv_element_notice_screen_title.setText(new String(new char[] { 0xe939 }) + "\n" + getResources().getString(R.string.app_no_data_problem)); // 804(editmode)

		} else if (msg.what == 2) {

			start_thread_enable = true;
			thread_get.progress_dialog.dismiss();

			// * 시스템에서 사용하는 값
			form_basic.content.setText(thread_get.form_basic.content);

		} else if (msg.what == 3) {

			// 등록 네트웍에러
			start_thread_enable = true;
			thread_set.progress_dialog.dismiss();

			Codes.dismiss_dialog(dialog);  //* 다이얼로그 해지, 기존에 다른 다이얼로그가 떠있을수 있기 때문..
			
			dialog = new Dialog(this);
			
			
			View frame_dialog = Codes.make_dialog(dialog, this, "", R.layout.element_dialog_frame, R.id.ll_element_dialog_frame_body, R.layout.element_dialog_msgbox , ""); //*
			
			TextView tv_dialog_msgbox_title = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_title);
			tv_dialog_msgbox_title.setText("Network Error");
		
			TextView tv_dialog_msgbox_content = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_content);
			tv_dialog_msgbox_content.setText("Network error occurred.");
					
			
			
			//type = reserved
			// dialog_frame : 디자인
			// dailog_ll_content : dialog_element_layout가 삽입될 dialog_frame의 리니어 레이아웃영역
			
			TextView tv_dialog_msgbox_button_left = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_button_left);
			//tv_menu_button_left.setTag(dialog);
			TextView tv_dialog_msgbox_button_right = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_button_right);
			
			on_dialog_click_listener l = new on_dialog_click_listener();
		
			tv_dialog_msgbox_button_left.setOnClickListener(l);
			tv_dialog_msgbox_button_right.setOnClickListener(l);

			
			dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
			dialog.show();
			

			
			
			

		} else if (msg.what == 4) {

			// 등록서버에러
			start_thread_enable = true;
			thread_set.progress_dialog.dismiss();

			
			
			
			Codes.dismiss_dialog(dialog);  //* 다이얼로그 해지, 기존에 다른 다이얼로그가 떠있을수 있기 때문..
			
			dialog = new Dialog(this);
			
			
			View frame_dialog = Codes.make_dialog(dialog, this, "", R.layout.element_dialog_frame, R.id.ll_element_dialog_frame_body, R.layout.element_dialog_msgbox , ""); //*
			
			TextView tv_dialog_msgbox_title = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_title);
			tv_dialog_msgbox_title.setText("Server Error");
		
			TextView tv_dialog_msgbox_content = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_content);
			tv_dialog_msgbox_content.setText("Server Error");
					
			
			
			//type = reserved
			// dialog_frame : 디자인
			// dailog_ll_content : dialog_element_layout가 삽입될 dialog_frame의 리니어 레이아웃영역
			
			TextView tv_dialog_msgbox_button_left = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_button_left);
			//tv_menu_button_left.setTag(dialog);
			TextView tv_dialog_msgbox_button_right = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_button_right);
			
			on_dialog_click_listener l = new on_dialog_click_listener();
		
			tv_dialog_msgbox_button_left.setOnClickListener(l);
			tv_dialog_msgbox_button_right.setOnClickListener(l);

			
			dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
			dialog.show();
			
			
			
		} else if (msg.what == 5) {
			// 등록성공

			start_thread_enable = true;
			thread_set.progress_dialog.dismiss();
			
			

			Codes.dismiss_dialog(dialog);  //* 다이얼로그 해지, 기존에 다른 다이얼로그가 떠있을수 있기 때문..
			
			dialog = new Dialog(this);
			
			
			View frame_dialog = Codes.make_dialog(dialog, this, "", R.layout.element_dialog_frame, R.id.ll_element_dialog_frame_body, R.layout.element_dialog_msgbox , ""); //*
			
			TextView tv_dialog_msgbox_title = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_title);
			tv_dialog_msgbox_title.setText("Success");
		
			TextView tv_dialog_msgbox_content = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_content);
			tv_dialog_msgbox_content.setText("Data is saved...");
					
			
			
			//type = reserved
			// dialog_frame : 디자인
			// dailog_ll_content : dialog_element_layout가 삽입될 dialog_frame의 리니어 레이아웃영역
			
			TextView tv_dialog_msgbox_button_left = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_button_left);
			//tv_menu_button_left.setTag(dialog);
			TextView tv_dialog_msgbox_button_right = (TextView) frame_dialog.findViewById(R.id.tv_dialog_msgbox_button_right);
			
			on_dialog_click_listener l = new on_dialog_click_listener();
		
			tv_dialog_msgbox_button_left.setOnClickListener(l);
			tv_dialog_msgbox_button_right.setOnClickListener(l);

			
			dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
			dialog.show();
			
			

		}

	}

}
