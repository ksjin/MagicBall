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

		handler = new Handler();// ui thread(main thread) �� �۾��Ϸᳪ �۾����࿡ ���Ѱ���
								// �����ϴ� ���� handler
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
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);// ȭ����ȯ
																	// animation
	}
}
