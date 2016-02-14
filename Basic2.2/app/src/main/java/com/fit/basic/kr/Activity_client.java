package com.fit.basic.kr;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
 *  actvity_layout.xml, element_layout.xml 관련 디자인규약
 *  시스템함수사용규약 : Codes.InitApp(), Codes.RepairApp(), Codes.GetAppData(), Codes.SetAppData()
 *  사용자함수사용규약 : init(), start_thread()
 *  옵션버튼사용규약 
 *  공지(배너)화면 규약: Codes.show_notice_screen
 *  inflate 하는 Element 사용규약과 사용자이벤트호출규약 : element_header.xml,element_footer.xml, element_footer_clicked()
 *  
 *-----------------------------------------------------------*/

public class Activity_client extends Activity {

	public static boolean start_thread_enable; // * start_thread의 실행가능여부결정(외부
												// 액티버트나 서비스에서 재 시행을 요구할 수 있도록
												// 외부공개함

	Intent intent ;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_client); // 항상 제공되는
													// activity_layout.xml을 복사해서
													// 만듦

		//this.overridePendingTransition( R.anim.anim_slide_in_left, R.anim.anim_slide_in_right);
		
		
		
		Codes.InitApp(this); // *

		init(); // * 객체 및 요소객체 등의 초기화작업

	}

	@Override
	protected void onResume() {

		super.onResume();

		Codes.RepairApp(this); // * 치명적 에러발생시 자동복구/재실행
		Codes.GetAppData(this); // * 시스템정보

		// 		액티버티가 자체적으로 사용하는 preferences 정보는 여기에서 가져오세요 [예제]
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

		View v = Codes.show_notice_screen(this, R.layout.element_notice_screen, R.id.ll_center_content, R.id.ll_center_notice, true);
		// 개발자가 만드는 element_는 element_layout.xml을 복사해서 만듬
		// 가능하면 다른 개발자도 공용 디자인객체로 사용할 수 있도록 만듬

		TextView tv_element_notice_screen_title = (TextView) v.findViewById(R.id.tv_element_notice_screen_title);
		tv_element_notice_screen_title.setText("advitising banner or network failed \n reconnect");

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

	}

	private void start_thread() {

		Toast.makeText(this, "주쓰레드가 실행됨", Toast.LENGTH_SHORT).show();

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

	
	
	//element_footer 사용자 이벤트
	public void element_footer_clicked(View v) {

		
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
	

	public void button_clicked(View v) {

		Toast.makeText(this, "xml내 정의된 onclick button이 눌러짐", Toast.LENGTH_SHORT).show();

		switch (v.getId()) {

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

			Toast.makeText(this, "편집모드로전환[삭제/편집/(수정)]", Toast.LENGTH_SHORT).show();

			intent = new Intent(this, Activity_write.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("adaptor", R.layout.adaptor_form_basic);
			intent.putExtra("msgid", "000000");
			
			startActivity(intent);
			
			
			
			break;

		case R.id.ll_element_header_right: // element_header에서
			
			intent = new Intent(this, Activity_write.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("adaptor", R.layout.adaptor_form_basic);
			intent.putExtra("msgid", "");
			
			startActivity(intent);
			
			break;

		}

	}

}
