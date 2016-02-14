package com.fit.basic.kr;

import java.util.Locale;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;

// application class에 대해서 고민 http://arabiannight.tistory.com/entry/304 

final public class Data {

	//서비스 기능 환경설정
	public static Boolean setting_login = false;   //로그인-회원가입사용여부
	
	public static String local_database_name   = "fit.db" ; //로컬데이타베이스이름
	public static Locale locale = Locale.KOREA; //시스템언어
	
	
	//시스템정보
	static String directory_path; // 단말기내 데이타를 저장할 곳 경로(주소)
	static String server_path = "http://49.1.216.18:8080/fit01/"; //서버주소(개발서버)

	// 미디어 스케닝을 위한 Intent
	static Intent msIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));

	static String website = "http://cafe.daum.net"; //앱의 홈페이지주소
	static String app_install_url = "install Eduline app https://play.google.com/store/apps/details?id=com.fit.edu.kr"; //설치앱주소(초대용)

	static String country_zipcode; // 국제전화코드
	
	/*유저정보*/
	static String user_id; // 사용자가 등록한 고유아이디
	static String user_password; //*사용자가 등록한 비밀번호
	static String user_nickmame = ""; //사용자닉네임
	static String user_profile_photo = "";  //유저 사용자 사진등록 주소
	static String user_email; // *사용자가 등록한 전자메일주소, 이 3가지 정보를 가지고 사용자를 식별한다.
	static String user_gmail; // 사용자의 가입한 Gmail계정
	static Location location; //사용자위치정보
	static String user_company_area; // 근무처 위치 지역
	static String user_company_address; // 근무처 주소(동단위까지,각국의 우편번호기준)
	static String user_address_code; // 사용자 주소(동단위까지,각국의 우편번호기준)

	static String user_usim_code; // 단말기시리얼넘버 (아이폰에서는 접근불가)

	static String user_nationality; // 사용자 국적
	static String user_nation_code;
	static String user_phone; // 단말기전화번호 (아이폰에서는 접근불가)
	static String user_phone_at_home; // 배송지연락처
	
	static String user_memberid = "000000000"; // DB에 저장된 회원의 고유식별아이디번호 
	static String user_name; // 사용자가 등록한 이름 혹은 닉네임
	static String user_membership = ""; // 사용자가 획득한 멤버쉽아이디
	static String user_type; // 사용자의 타입(사용자프로필사진과 관계됨)
	
	static int user_login_count; // 로그인수
	static int user_level; // 사용자레벨

	static String user_coin;//사용가능포인트

	static String user_auth; //권한
	static String login_date; //가장최근로그인날짜
	
	/* 은행 */
	static String user_bank_name = ""; // 입금은행(거래은행)
	static String user_bank_account = ""; // 입금은행(거래은행)
	static String user_bank_code = ""; // 계좌번호
	static String user_helpdesk_contact = ""; // 비서연락처

	/* 안전결제 */
	static String user_safe_pay_company; // 안전결제사
	static String user_safepay_seller_id;
	static String user_safepay_contact;

	/* 결제회사 */
	static String user_paygate_company;
	static String user_paygate_seller_id;
	static String user_paygate_contact;

	/*이미지서버*/
	public static final String ImageShake_API_KEY = "OR8PZBUL2bb0caed24fcbb5e7641d50505c96aff";
	public static final String ImageShake_AUTH_TOKEN_EXTRA = "a42e7175af5c71747192e75ff9739554";
	public static final String ImageShake_AUTH_OBJECT_EXTRA = "AuthModel [processTime=92, success=true, error=null, authToken=a42e7175af5c71747192e75ff9739554, email=gv73@icloud.com, userId=75350075, username=ilovesasla_kr, membership=free, membershipItemNumber=null, avatar=null";

	/*글꼴*/

	static Typeface font ;
	static String font_path = "fonts/fontello.ttf" ;//사용하는 폰트경로
	
	/* 구글 */
	public static final String DEVELOPER_KEY = "AIzaSyB0e-tuxfbJD27iQ3C0XYQY5x33EbgV4y8";

	/* 카카오톡 */
	
	
	/* 트위터 */
	public static final String LOG_TAG = "TwitterCon";
	public static final String TWITTER_API_KEY = "0XleI6bco3lI2S8ibyPthZLwo";
	public static final String Twitpic_API_KEY = "15f6592e1cd7672d5bb160f0dbcb3fc8";
	public static final String TWITTER_CONSUMER_KEY = "2831371382";
	public static final String TWITTER_CONSUMER_SECRET = "LAT9CW8WGur1bufU2RDqwu2U9xfkt8M83x6yziq63lkzQZTuh1";
	public static final String TWITTER_CALLBACK_URL = "http://49.1.216.89:8080/twitter"; // =
																							// "oauth://t4jsample";
																							// //http://m.daum.net"
	public static final String MOVE_TWITTER_LOGIN = "com.example.h8.TWITTER_LOGIN"; // intet
	public static boolean D = true;
	public static final int TWITTER_LOGIN_CODE = 10;

	
	
}
