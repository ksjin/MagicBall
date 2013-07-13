package com.example.magicball;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements SensorEventListener {
	private long lastTime;
	private float speed;
	private float lastX;
	private float lastY;
	private float lastZ;

	private float x, y, z;
	private static final int SHAKE_THRESHOLD = 800;

	private SpeechRecognizer recognizer = null;
	private SensorManager sensorManager = null;
	private Sensor sensor = null;

	private ImageView magicBall = null;
	private ImageView mic = null;
	private ImageView shakeit = null;
	private ImageView guide = null;
	private ImageView triangle = null;
	private ImageView share = null;
	private ImageView retryBtn = null;
	private ImageView facebookBtn = null;
	private ImageView twitterBtn = null;
	private TextView answer = null;
	private EditText shareEditText = null;

	// 핸들러 메시지
	private final int READY = 0, END = 1, FINISH = 2, ANIMATION_END = 3;

	private Animation fadeInAnimation1 = null;
	private Animation fadeInAnimation2 = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loadResource();

		Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // 음성인식intent생성
		i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getPackageName()); // 데이터설정
		i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR"); // 음성인식 언어 설정

		//
		recognizer = SpeechRecognizer.createSpeechRecognizer(this); // 음성인식 객체
		recognizer.setRecognitionListener(listener); // 음성인식 리스너 등록
		recognizer.startListening(i); // 음성인식 시작

		// 가속도 센서
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

	}

	private void loadResource() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		fadeInAnimation1 = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.fade_in);
		fadeInAnimation2 = AnimationUtils.loadAnimation(MainActivity.this,
				R.anim.fade_in);
		magicBall = (ImageView) findViewById(R.id.MagicBall);
		mic = (ImageView) findViewById(R.id.mic);
		triangle = (ImageView) findViewById(R.id.tri);
		shakeit = (ImageView) findViewById(R.id.shakeit);
		guide = (ImageView) findViewById(R.id.guide);
		share = (ImageView) findViewById(R.id.shareMod);
		retryBtn = (ImageView) findViewById(R.id.retryBtn);
		retryBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});
		facebookBtn = (ImageView) findViewById(R.id.facebookBtn);
		twitterBtn = (ImageView) findViewById(R.id.twitterBtn);
		twitterBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				KakaoLink kakaoLink = KakaoLink
						.getLink(getApplicationContext());
				if (!kakaoLink.isAvailableIntent())
					return;

				try {
					kakaoLink.openKakaoLink(
							MainActivity.this,
							"http://blog.naver.com/kkssjj1122",
							shareEditText.getText().toString() + "-"
									+ answer.getText().toString(),
							getPackageName(),
							getPackageManager().getPackageInfo(
									getPackageName(), 0).versionName,
							"MagicBall", "UTF-8");
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				/* 안드로이드 기본 os 공유 사용 
				 * Intent intent = new Intent(Intent.ACTION_SEND);
				 * intent.setType("text/plain");
				 * intent.putExtra(Intent.EXTRA_SUBJECT, "MagicBall");
				 * intent.putExtra(Intent.EXTRA_TEXT,
				 * shareEditText.getText().toString() + "-" + answer);
				 * startActivity(intent);
				 */
			}
		});
		answer = (TextView) findViewById(R.id.answerTextView);
		shareEditText = (EditText) findViewById(R.id.shareEditText);

		mic.setVisibility(View.INVISIBLE);
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case READY://음성인식 준비 
				mic.setVisibility(View.VISIBLE);
				break;
			case END://음성인식중 - guide gone, shakeit visible, magicball rotate animation
				mic.setVisibility(View.INVISIBLE);
				guide.setVisibility(View.GONE);
				shakeit.setVisibility(View.VISIBLE);

				Animation animation = AnimationUtils.loadAnimation(
						MainActivity.this, R.anim.rotate);
				magicBall.startAnimation(animation);
				
				if (sensor != null)
					sensorManager.registerListener(MainActivity.this, sensor,
							SensorManager.SENSOR_DELAY_GAME);
				sendEmptyMessageDelayed(FINISH, 5000); // 인식 시간 5초로 설정. 5초 지나면
				
				break;
			case ANIMATION_END: // 애니메이션 종료 
				share.setVisibility(View.VISIBLE);
				shareEditText.setVisibility(View.VISIBLE);
				retryBtn.setVisibility(View.VISIBLE);
				facebookBtn.setVisibility(View.VISIBLE);
				twitterBtn.setVisibility(View.VISIBLE);

				share.startAnimation(fadeInAnimation2);
				shareEditText.startAnimation(fadeInAnimation2);
				retryBtn.startAnimation(fadeInAnimation2);
				facebookBtn.startAnimation(fadeInAnimation2);
				twitterBtn.startAnimation(fadeInAnimation2);

				break;
			}
		}
	};

	// 음성인식 리스너
	private RecognitionListener listener = new RecognitionListener() {
		// 입력 소리 변경
		public void onRmsChanged(float dB) {
			int step = (int) (dB / 7); // 소리 크기에 따라 step을 구함.
		}

		// 음성인식 결과
		public void onResults(Bundle result) {
			handler.removeMessages(END); // 핸들러에 종료 메시지 삭제
		}

		// 음성 인식 준비가 되었으면
		public void onReadyForSpeech(Bundle params) {
			handler.sendEmptyMessage(READY); // 핸들러에 메시지 보냄
		}

		public void onPartialResults(Bundle arg0) {
		}

		public void onEvent(int arg0, Bundle arg1) {
		}

		// 음성 입력이 끝났으면
		public void onEndOfSpeech() {
			handler.sendEmptyMessage(END); // 핸들러에 메시지 보냄
		}

		// 에러가 발생하면
		public void onError(int error) {
			setResult(error); // 전 activity로 에러코드 전송
		}

		public void onBufferReceived(byte[] arg0) {
		}

		public void onBeginningOfSpeech() {
		}
	};

	// 가속도 센서

	// 이벤트 등록 해지, 진동 & 결과
	public void stop() {
		if (sensorManager != null)
			sensorManager.unregisterListener(this);
		Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibe.vibrate(500);
		Answer result = new Answer();
		shakeit.setVisibility(View.GONE);
		triangle.setVisibility(View.VISIBLE);
		answer.setVisibility(View.VISIBLE);
		answer.setText(result.getAnswer());

		// animation
		magicBall.clearAnimation();

		fadeInAnimation1
				.setAnimationListener(new Animation.AnimationListener() {
					public void onAnimationStart(Animation animation) {
					}

					public void onAnimationRepeat(Animation animation) {
					}

					public void onAnimationEnd(Animation animation) {
						handler.sendEmptyMessage(ANIMATION_END);
					}
				});

		triangle.startAnimation(fadeInAnimation1);
		answer.startAnimation(fadeInAnimation1);
	}

	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long currentTime = System.currentTimeMillis();
			long gabOfTime = (currentTime - lastTime);

			if (gabOfTime > 200) {
				lastTime = currentTime;

				x = event.values[SensorManager.AXIS_X - 1];
				y = event.values[SensorManager.AXIS_Y - 1];
				z = event.values[SensorManager.AXIS_Z - 1];

				speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime
						* 10000;

				if (speed > SHAKE_THRESHOLD) {
					stop();
				}
				lastX = event.values[SensorManager.AXIS_X - 1];
				lastY = event.values[SensorManager.AXIS_Y - 1];
				lastZ = event.values[SensorManager.AXIS_Z - 1];
			}
		}
	}

	// 액티비티 소멸시  
	public void finish() {
		if (recognizer != null)
			recognizer.stopListening(); // 음성인식 중지
		handler.removeMessages(READY); // 메시지 삭제
		handler.removeMessages(END); // 메시지 삭제
		handler.removeMessages(ANIMATION_END);
		super.finish();
	}
}
