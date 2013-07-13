package com.example.magicball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

public class IntroActivity extends Activity {
	private Handler handler = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_intro);

		handler = new Handler();// ui thread(main thread) 에 작업완료나 작업진행에 대한것을
								// 통지하는 것이 handler
		handler.postDelayed(new Runnable() {
			public void run() {
				endIntro();
			}
		}, 1000);
	};

	private void endIntro() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);// 화면전환
																	// animation
	}
}
