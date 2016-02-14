package com.fit.basic.kr;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;



import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;

import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*-----------------------------------------------------------
 *  fit 리스트 보여주기 액티버티  규약 1.1 
 *  
 *  Codes 시스템레벨이나 공용라이브러리
 *  Code.AaaBbb()  시스템전체에 영향을 끼치는 함수(건드리지마세요)
 *  Code.aaa_bbb() 개발자들의 필요에 의해서 사용하는 공용라이브러리 (함께 사용가능한 함수나 클라스는 여기에 등록함)
 *  Data.java 앱전체에서 공유하는 주요정보 : 예) Data.user_memberid (user table의 memberid 정보가 담겨짐)
 *  주석규약 : 약속함수 주석에 //*가 약속으로써 있는 것은 그대로 사용함 삭제금지, 개발시 참조한 웹사이트주소 주석에 넣기
 *  
 *  -Thread 관련 규약 thread, thread_more, start_thread_enable, enable_thread, thread 선언, 호출, 작성, 핸들러호출, 어댑터 관련 규약, 서버통신방법규약
 *  -데이타에 사용하는 xml문장규약 : xmlRecords
 *  -Dialog 관련 규약   dialog 선언, dialog 생성, 개발자정의 이벤트관리 규약 
 *  -ListViw 관련 규약  lv선언, 초기화, 리스트바닥에 도달했을때 처리 
 *  -통신장애나 서버장애 데이타가 없을시 두가지 처리방법 규약
 *-----------------------------------------------------------*/

//How to hide/show Toolbar when list is scroling - https://mzgreen.github.io/2015/02/15/How-to-hideshow-Toolbar-when-list-is-scroling(part1)/

public class Activity_group extends Activity {

	public static boolean start_thread_enable; // * start_thread의 실행가능여부결정(외부
												// 액티버트나 서비스에서 재 시행을 요구할 수 있도록
												// 외부공개함
	private ListView Lv;
	private Dialog dialog ;  //*
	
	Boolean enable_thread = true; //*
	Intent intent; //*

	List_get thread, thread_more;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_group); // 항상 제공되는 activity_layout.xml을
													// 복사해서 만듦

		Codes.InitApp(this); //*

		init(); //* 객체 및 요소객체 등의 초기화작업

	}

	@Override
	protected void onResume() {

		super.onResume();

		Codes.RepairApp(this); //* 치명적 에러발생시 자동복구/재실행
		Codes.GetAppData(this); //* 시스템정보

		// 액티버티가 자체적으로 사용하는 preferences 정보는 여기에서 가져오세요 [예제]
		// SharedPreferences pref =
		// getSharedPreferences(getClass().getSimpleName(), 0);
		// String statue = pref.getString("statue", "");

		if (start_thread_enable) { //* 최초 실행인 경우 (항상 onresume마다 주쓰레드가 실행되면 안되며,
									// 주쓰레드의 중복실행방지)

			start_thread_enable = false; // [주의] 주쓰레드가 정상적으로 종료되지 않으면 다시 true로
											// 전환되어야한다. (예.네트웍에러 등)
			start_thread(); // * 참고4 주요한 쓰레드를 작동시키는 함수

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// 옵션메뉴
		getMenuInflater().inflate(R.menu.activity_client, menu);  //*
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

		// 배너 화면을 사용하지 않음
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
		View element_footer = inflater.inflate(R.layout.element_footer, ll_footer);
		// 주의 이런식으로 앨리먼트가 inflate되기 때문에 element의 소속객체 id끼리 중복될수 있다.
		// element 개발자는 소속객체의 id가 중복되지 않기하기 위해서 노력해야한다.
		// 필요시 독립된 캡술화된 객체로 만들수도 있으나 권장하지않음

		// listview 초기화

		Lv = (ListView) findViewById(R.id.listView1);
		Lv.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

				/*
				Log.i("","(Lv.getLastVisiblePosition() - Lv.getHeaderViewsCount() - Lv.getFooterViewsCount())?" + (Lv.getLastVisiblePosition() - Lv.getHeaderViewsCount() - Lv.getFooterViewsCount()));
				Log.i("","(Lv.getCount() - 2)" + (Lv.getCount() - 2) );
				Log.i("","Lv.getHeaderViewsCount()" + Lv.getHeaderViewsCount() );
				Log.i("","Lv.getFooterViewsCount()" + Lv.getFooterViewsCount() );
				Log.i("","Lv.getCount()" + Lv.getCount() );
				*/
				
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
						&& (Lv.getLastVisiblePosition() - Lv.getHeaderViewsCount() - Lv.getFooterViewsCount()) == (Lv.getCount() -  3 )) {

					// 스크롤 정지상태
					// 리스트가 바닥도착하면 다시쓰레드를 작동시킨다

					if (thread.is_more_data) {

						if (enable_thread) {

							thread.page_number = thread.page_number + 1;
							Log.i("thread.page_number?", thread.page_number + "");

							thread_more = new List_get();

							thread_more.set(Data.user_memberid, "", "get_group_list", "R.layout.adaptor_group_list", "", "", "", "", "",
									thread.page_number, 30, thread, 3);

							thread.progress_dialog.show();
							thread_more.start();

							enable_thread = false;

						}
					}

					Toast.makeText(Activity_group.this, "추가 리스트 데이타 요청....", Toast.LENGTH_SHORT).show();

				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		/*
		 * 쓰레드방식이나 커스텀어댑터를 사용하지않을 경우의 예 ArrayList<String> mArrays; mArrays = new
		 * ArrayList<String>();
		 * 
		 * mArrays.add("손승우"); mArrays.add("정치인그룹(8)");
		 * mArrays.add("안철수 및 문재인"); mArrays.add("살사초급반(7)"); mArrays.add("");
		 * mArrays.add(""); mArrays.add(""); mArrays.add(""); mArrays.add("");
		 * mArrays.add(""); mArrays.add("");
		 * 
		 * ArrayAdapter<String> itemsAdapter = new ArrayAdapter<String>(this,
		 * android.R.layout.simple_list_item_1, mArrays);
		 * 
		 * Lv.setAdapter(itemsAdapter);
		 */

	}

	private void start_thread() {

		Toast.makeText(this, "주쓰레드가 실행됨", Toast.LENGTH_SHORT).show();

		if (enable_thread) {

			thread = null;
			thread = new List_get();
			

			thread.set(Data.user_memberid, "", "get_group_list", "R.layout.adaptor_group_list", "", "", "", "", "", 0, 30, null, 2);
			// Data.user_memberid : 식별자코드
			// get_group_list: 사용할 메서드 지정 메서드에 따라서 다른 방식으로 정보를 가져오고, 지정한 핸들로코드에 따라서 해당결과를 처리함.
			// R.layout.adaptor_group_list : 사용할 어댑터의 이름, 어댑터의 getview()에서 레이아웃별 처리하는 함수를 제작해서 사용함.
			// 0: 첫번째 리스트업데이타인 경우
			// 30: 한번에 가져올 리스트업의 수
			// 2: 데이타를 가져왔을때, 실행될 핸들러코드번호

			thread.adapter = null;
			thread.adapter = new List_get_adapter();

			thread.progress_dialog.show();
			thread.start();

			enable_thread = false; // 중복호출방지(핸들러의 맨앞에서 true로 전환됨)

		}

	}

	@Override
	protected void onPause() {

		super.onPause();

		Codes.SetAppData(this); // *

		// preferences 처리영역

		Toast.makeText(this, "액티버티 활동이 중지됨 | 상태정보를 저장됨", Toast.LENGTH_SHORT).show();

	}


	// element_footer 사용자 이벤트 : 
	// 각 개발자들은 이러한 방식으로 별도의 이벤트핸들러를 만들어서 배포할 수 있다.
	// 복잡한 기능의 경우에은 켭슐라이징된 객체 제작해야하나 디자인단순한 기능을 수행하는 객체는 이런 방식도 가능
	
	
	public void element_footer_clicked(View v) {

		Intent intent;
		switch (v.getId()) {

		case R.id.ll_element_footer_tab1:

			intent = new Intent(this, Activity_client.class);
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

	// ---------------- 사용자발생 이벤트핸들러 --------------------

	
	class on_dialog_click_listener implements OnClickListener {

		@Override
		public void onClick(View v) {

			
			Toast.makeText(getApplicationContext(), "......on_dialog_click_listener....", Toast.LENGTH_SHORT).show();

			
			switch (v.getId()) {

			
			case R.id.tv_menu_item_1:
				
				
				Toast.makeText(getApplicationContext(), "tv_menu_item_1 clicked...", Toast.LENGTH_SHORT).show();

				
				break;
				
			
			case R.id.tv_menu_button_left:
					
				//Dialog D = ((Dialog) v.getTag()) ;
				Codes.dismiss_dialog(dialog); 
				
				
				break;
				
			case R.id.tv_menu_button_right:
				
				
				Toast.makeText(getApplicationContext(), "Done button is clicked...", Toast.LENGTH_SHORT).show();

				
				break;
				
			}
			
			
			

		}

	}
	// xml에서 직접 onclick="button_clicked"에 의해서 작동합니다
	// 소스가 깨끗해지고, 이벤트가 접근 방식이 달라서 편리한 점이 많아서 선호

	public void button_clicked(View v) {

		Toast.makeText(this, "xml내 정의된 onclick button이 눌러짐", Toast.LENGTH_SHORT).show();

		switch (v.getId()) {

		case R.id.ll_footer_right:
			
			Codes.dismiss_dialog(dialog);  //* 다이얼로그 해지, 기존에 다른 다이얼로그가 떠있을수 있기 때문..
			
			dialog = new Dialog(this);
			
			
			View frame_dialog = Codes.make_dialog(dialog, this, "", R.layout.element_dialog_frame, R.id.ll_element_dialog_frame_body, R.layout.element_dialog_menus , ""); //*
			
			//type = reserved
			// dialog_frame : 디자인
			// dailog_ll_content : dialog_element_layout가 삽입될 dialog_frame의 리니어 레이아웃영역
			
			TextView tv_menu_button_left = (TextView) frame_dialog.findViewById(R.id.tv_menu_button_left);
			//tv_menu_button_left.setTag(dialog);
			TextView tv_menu_item_1 = (TextView) frame_dialog.findViewById(R.id.tv_menu_item_1);
			TextView tv_menu_button_right = (TextView) frame_dialog.findViewById(R.id.tv_menu_button_right);
			
			on_dialog_click_listener l = new on_dialog_click_listener();
		
			tv_menu_button_left.setOnClickListener(l);
			tv_menu_item_1.setOnClickListener(l);
			tv_menu_button_right.setOnClickListener(l);

			
			dialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
			dialog.show();
			
			
			break;
			


		}

	}
	
	

	// 메인쓰레드

	class List_get extends Thread {

		// 쓰레드상품
		Boolean is_more_data = true; // * 모든 리스트정보를 가져왔는지 확인여부 true:아직 가져올 리스트가
										// 남아있음 false:더이상 가져올 리스트가 없음
		int handler_code;
		String id = "";
		String navigator = "";
		String method = "";
		String type; // 사용할 어댑터의 종류
		String category;
		String search_keyword;
		String divider;
		String estimate;
		String curation;

		int page; // 현재까지 가져온 리스트의 수
		int page_number = 0;
		int page_size = 0; // 한번에 가져올 리스트의 수

		public Progress_Dialog progress_dialog;
		public List_get_adapter adapter;
		Display_handler handler;

		List_get thread;

		// 받은값
		String request_time = ""; // *최종조회시간
		String category_default = "";// *

		// 배열

		ArrayList<String> al_group_name;
		ArrayList<String> al_group_member;
		ArrayList<String> al_group_message;

		ArrayList<Object> alListViewItemData;

		public void set(String _id, String _navigator, String _method, String _type, String _category, String _search_keyword, String _divider,
				String _estimate, String _curation, int _page_number, int _page_size, List_get thread_, int _handler_code) {

			id = _id;
			navigator = _navigator;
			method = _method;
			type = _type;
			category = _category;
			search_keyword = _search_keyword;
			divider = _divider;
			estimate = _estimate;
			curation = _curation;
			page_number = _page_number;
			page_size = _page_size;
			page = (_page_size * _page_number); // 페이지수
			thread = thread_;
			handler_code = _handler_code;

			progress_dialog = new Progress_Dialog(Activity_group.this);// getApplicationContext()

			handler = new Display_handler(Activity_group.this);// 메모리릭을 피하기 위해서

			if (_page_number == 0) {

				al_group_name = new ArrayList<String>();
				al_group_member = new ArrayList<String>();
				al_group_message = new ArrayList<String>();

			}

		}

		public void run() {

			try {

				if (page == 0) {

					// 배열초기화
					al_group_name.clear();
					al_group_member.clear();
					al_group_message.clear();

				}

				Element root = null;
				


				if (method.equals("get_group_list")) {

					// ...로컬영역 혹은 로컬데이타베이스에서 가져오는 경우

					String xmlRecords =
							
					//*아래 형식이 우리가 사용하는 모든 테이타통신의 데이타형태임 (약속, 편리성때문에..)
					//---------------------------------------------------
					

							"<msg>" 
							+ " <group_name>손승우</group_name><group_name>정치인그룹(3)</group_name><group_name>박태욱,조기상,최준호 및 조성국</group_name>"
							+ " <group_member>손승우</group_member><group_member>박근혜,안철수,문재인</group_member><group_member>박태욱|조기상|최준호|조성국</group_member>"
							+ " <group_message>2015 12월 가정통신문발송</group_message><group_message>12월 고3 수능성적표</group_message><group_message>2016 1월 휴강안내문</group_message>" 
							+ " <request_time></request_time>"
							+ " <divider_default></divider_default>" 
							+ " <curation_default></curation_default>"
							+ " <category_default></category_default>" 
							+ " <estimate_default></estimate_default>" 
							+ "</msg>";
					
					
					Log.i("", "xmlRecords?" + xmlRecords);

					DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					DocumentBuilder db = dbf.newDocumentBuilder();
					InputSource is = new InputSource();
					is.setCharacterStream(new StringReader(xmlRecords));

					Document doc = db.parse(is);
					root = doc.getDocumentElement();

				} else if (method.equals("get_xxx_xxxx")) {
					
					
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {

					}

					
					

					// 서버와 통신할 경우의 예

					URL url;
					String data = "id=" + id;
					data = data + "&navigator=" + URLEncoder.encode(navigator, "UTF-8");
					data = data + "&method=" + URLEncoder.encode(method, "UTF-8");
					data = data + "&type=" + URLEncoder.encode(type, "UTF-8");
					data = data + "&category=" + URLEncoder.encode(category, "UTF-8");
					data = data + "&search_keyword=" + URLEncoder.encode(search_keyword, "UTF-8");
					data = data + "&divider=" + URLEncoder.encode(divider, "UTF-8");
					data = data + "&estimate=" + URLEncoder.encode(estimate, "UTF-8");
					data = data + "&curation=" + URLEncoder.encode(curation, "UTF-8");

					if (page_number != 0) {
						data = data + "&last_request_time=" + URLEncoder.encode(thread.request_time, "UTF-8"); // 재호출시
					}

					data = data + "&page=" + page;
					data = data + "&page_size=" + page_size;

					url = new URL(Data.server_path + "s/GetMsgListData.asp?" + data);
					Log.i("send_data", Data.server_path + "s/GetMsgListData.asp?" + data);

					root = Codes.read_url(url);

				}

				if (root == null) {

					handler.sendEmptyMessage(0); // 통신에러

				} else if (root.getTextContent().toString().equals(null)) {

					handler.sendEmptyMessage(0); // 서버에러

				} else if (root.getTextContent().toString().equals("")) {

					if (page_number == 0) {
						handler.sendEmptyMessage(2); // 서버에 정보가 없는 경우

					} else {
						handler.sendEmptyMessage(4); // 추가정보가 없을 경우
					}

				} else if (!root.getTextContent().toString().equals("")) {

					// * 시스템에서 사용하는 값
					request_time = URLDecoder.decode(root.getElementsByTagName("request_time").item(0).getTextContent(), "UTF-8");
					divider = URLDecoder.decode(root.getElementsByTagName("divider_default").item(0).getTextContent(), "UTF-8");
					curation = URLDecoder.decode(root.getElementsByTagName("curation_default").item(0).getTextContent(), "UTF-8");
					estimate = URLDecoder.decode(root.getElementsByTagName("estimate_default").item(0).getTextContent(), "UTF-8");
					category_default = URLDecoder.decode(root.getElementsByTagName("category_default").item(0).getTextContent(), "UTF-8");

					// 사용자배열

					NodeList nl_group_name = root.getElementsByTagName("group_name");
					NodeList nl_group_member = root.getElementsByTagName("group_member");
					NodeList nl_group_message = root.getElementsByTagName("group_message");

					for (int i = 0; i < nl_group_name.getLength(); i++) {

						Element el_group_name = (Element) nl_group_name.item(i);
						Element el_group_member = (Element) nl_group_member.item(i);
						Element el_group_message = (Element) nl_group_message.item(i);

						String group_name = URLDecoder.decode(el_group_name.getTextContent(), "UTF-8");
						String group_member = URLDecoder.decode(el_group_member.getTextContent(), "UTF-8");
						String group_message = URLDecoder.decode(el_group_message.getTextContent(), "UTF-8");

						if (page_number == 0) { // 최초리스트호출

							Log.i("", "al_group_name?" + group_name);

							al_group_name.add(group_name);
							al_group_member.add(group_member);
							al_group_message.add(group_message);

						} else {

							thread.al_group_name.add(group_name);
							thread.al_group_member.add(group_member);
							thread.al_group_message.add(group_message);

						}
					}

					handler.sendEmptyMessage(handler_code);

				}

			} catch (Exception e) {

				handler.sendEmptyMessage(0);

				e.printStackTrace();
			}
		}
	}

	// http://stackoverflow.com/questions/11407943/this-handler-class-should-be-static-or-leaks-might-occur-incominghandler
	// 참

	static class Display_handler extends Handler {

		private final WeakReference<Activity_group> mActivity;

		Display_handler(Activity_group activity) {
			mActivity = new WeakReference<Activity_group>(activity);
		}

		@Override
		public void handleMessage(Message msg) {

			Activity_group activity = mActivity.get();

			if (activity != null) {

				activity.handle_message(msg);
			}
		}

	}

	private void handle_message(Message msg) {

		Log.i("", "handleMessage?" + msg.what);
		
		thread.progress_dialog.dismiss();
		enable_thread = true;
		

		if (msg.what == 0) {
			// 네트웍 혹은 서버에러

			start_thread_enable = true; // *
			
			View v = Codes.show_notice_screen(this, R.layout.element_notice_screen, R.id.ll_center_content, R.id.ll_center_notice, true);
			TextView tv_element_notice_screen_title = (TextView) v.findViewById(R.id.tv_element_notice_screen_title);
			tv_element_notice_screen_title.setTypeface(Data.font);
			
			//에러의 특성에 따라 노티스화면방식으로 결과보여주기
			
			if( Codes.check_online(getApplicationContext()) ){
				//네트웍연결상태는 정상, 그러므로 시스템, 서버에러
				tv_element_notice_screen_title.setText(new String(new char[] { 0xe87d }) + "\n" + getResources().getString(R.string.app_server_problem) );  
				
				
			}else{
				//네트웍연결상태 비정상 
				tv_element_notice_screen_title.setText(new String(new char[] { 0xe87d }) + "\n" + getResources().getString(R.string.app_network_problem) ); 
					
			}
			
			

		} else if (msg.what == 1) {
			// 받은 데이타가 없을 경우
			
			start_thread_enable = true; 
			
			//데이타가 없음을 노티스화면방식으로 결과보여주기
			View v = Codes.show_notice_screen(this, R.layout.element_notice_screen, R.id.ll_center_content, R.id.ll_center_notice, true);

			TextView tv_element_notice_screen_title = (TextView) v.findViewById(R.id.tv_element_notice_screen_title);
			tv_element_notice_screen_title.setTypeface(Data.font);
			tv_element_notice_screen_title.setText(new String(new char[] { 0xe939 }) + "\n" + getResources().getString(R.string.app_no_data_problem) );  //804(editmode)
			
			
		} else if (msg.what == 2) {
			// 리스트 최초 호출시 정상적으로 데이타를 받았을때

			// 혹시 notice 화면이 보이고 있으면 자동해제. 성공시 null이 리턴됨
			Codes.show_notice_screen(this, R.layout.element_notice_screen, R.id.ll_center_content, R.id.ll_center_notice, false);

			
			View element_listview_header = getLayoutInflater().inflate(R.layout.element_listview_header, null, false);
			Lv.addHeaderView(element_listview_header); 

			View element_listview_footer = getLayoutInflater().inflate(R.layout.element_listview_footer, null, false);
			Lv.addFooterView(element_listview_footer);

			// 헤더 기능바 등 설정
			Lv.setAdapter(thread.adapter);
			
			

		} else if (msg.what == 3) {
			// 추가 리스트정보가 있을때

			thread.adapter.notifyDataSetChanged();// *

		} else if (msg.what == 4) {
			// 추가 리스트정보가 더이상 없는 경우

			thread.is_more_data = false;// *
			thread.adapter.notifyDataSetChanged();// *

		}


	}

	// 리스트뷰의 어뎁터 클래스

	class List_get_adapter extends BaseAdapter {

		@Override
		public int getCount() {

			return thread.al_group_name.size() + 3 ;  //빈리스트아이템을 3개 더 보여주고 싶을때(추가적인 정보가 없음을 알리기 위해서

		}
		
		

		@Override
		public Object getItem(int arg0) {

			return null;
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		void set_adaptor_empty_list(int arg0, View arg1, onListItemClickListener l) {

			// 디자인설정
		
		
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {

			LayoutInflater inflater = getLayoutInflater();

			onListItemClickListener l = new onListItemClickListener();
			
			

			if(thread.al_group_name.size() > arg0){  // .getCount() 참조하면 실제보다 +3 되어있음
				
				if (thread.type.equals("R.layout.adaptor_group_list")) {

					arg1 = inflater.inflate(R.layout.adaptor_group_list, null);
					arg1.setTag("adaptor_group_list");
					set_adaptor_group_list(arg0, arg1, l);

				}
				
			}else{
				
				//데이타가 없거나 더이상의 추가적인 데이타가 없음을 빈 어댑터를 호출해서, 데이타가 하나도 없음을 간접적으로 알림.
				//(쓰레드에서 데이타가 없을 경우의 핸들러메세지 1이 아닌 2로 지정) 
				
				arg1 = inflater.inflate(R.layout.adaptor_empty_list, null);
				arg1.setTag("adaptor_empty_list");
				set_adaptor_empty_list(arg0, arg1, l);

				
				
			}
			

			/* 헤더/풋터 */

			if (arg0 == 0) {
				// set_header_menu(arg1, true);

			}

			return arg1;

		}



		void set_adaptor_group_list(int arg0, View arg1, onListItemClickListener l) {
		
			// 디자인설정
		
			
			TextView tv_adaptor_group_list_left = (TextView) arg1.findViewById(R.id.tv_adaptor_group_list_left);
			TextView tv_adaptor_group_list_center = (TextView) arg1.findViewById(R.id.tv_adaptor_group_list_center);
			TextView tv_adaptor_group_list_card = (TextView) arg1.findViewById(R.id.tv_adaptor_group_list_card);
			TextView tv_adaptor_group_list_right = (TextView) arg1.findViewById(R.id.tv_adaptor_group_list_right);
			
			tv_adaptor_group_list_left.setTypeface(Data.font);
			tv_adaptor_group_list_left.setText(new String(new char[] { 0xe833 }));  //804(editmode)
			
			tv_adaptor_group_list_center.setTypeface(Data.font);
			tv_adaptor_group_list_center.setText( thread.al_group_name.get(arg0));
			
			tv_adaptor_group_list_card.setTypeface(Data.font);
			tv_adaptor_group_list_card.setText(thread.al_group_message.get(arg0) + "\n" + "to" + thread.al_group_member.get(arg0));
			
			tv_adaptor_group_list_right.setTypeface(Data.font);
			tv_adaptor_group_list_right.setText(new String(new char[] { 0xe807 }));
			
			
			LinearLayout ll_adaptor_group_list_view = (LinearLayout) arg1.findViewById(R.id.ll_adaptor_group_listview_content);
			ll_adaptor_group_list_view.setTag(Lv);
			ll_adaptor_group_list_view.setOnClickListener(l);
		
		}
	}

	// 리스트뷰클릭시
	class onListItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			ListView s;

			int position;

			switch (v.getId()) {

			case R.id.ll_adaptor_group_listview_content:

				s = (ListView) v.getTag();
				position = s.getPositionForView(v) - 1;

				Log.i("", "position?" + position);

				Toast.makeText(getApplicationContext(), thread.al_group_name.get(position), Toast.LENGTH_SHORT).show();

				break;

			}

		}
	}

}
