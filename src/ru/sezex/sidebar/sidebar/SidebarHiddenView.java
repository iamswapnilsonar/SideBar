package ru.sezex.sidebar.sidebar;

import ru.sezex.sidebar.R;
import ru.sezex.sidebar.Util;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

@SuppressLint("ViewConstructor")
public class SidebarHiddenView extends LinearLayout {
	final SidebarService mService;
	final ImageView mTab;

	public SidebarHiddenView(SidebarService service) {
		super(service);
		mService = service;
		LayoutInflater.from(service).inflate(R.layout.sidebar_hidden, this);
		mTab = (ImageView) findViewById(android.R.id.button1);

		if (mService.mBarOnRight) {
			mTab.setImageResource(R.drawable.tab_right_hidden_selector);
		} else {
			mTab.setImageResource(R.drawable.tab_left_hidden_selector);
		}
	}

	public void setMarginFromTop(int top) {
		WindowManager.LayoutParams params = (WindowManager.LayoutParams) this
				.getLayoutParams();
		params.y = top;
		mService.mWindowManager.updateViewLayout(this, params);
	}

	public void setTabSize(int dp) {
		LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) mTab
				.getLayoutParams();
		param.width = Util.dp(dp, getContext());
		;
		mTab.setLayoutParams(param);
	}

	public void setTabAlpha(float decimal) {
		((View) mTab).setAlpha(decimal);
	}

	public void animateView(boolean visible) {
		if (visible) {
			mService.addView(this);
			TranslateAnimation anim = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, mService.mBarOnRight ? 1.0f
							: -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, 0, 0,
					0, 0);
			anim.setDuration(mService.mAnimationTime);
			mTab.startAnimation(anim);
		} else {
			TranslateAnimation anim = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, mService.mBarOnRight ? 1.0f
							: -1.0f, 0, 0, 0, 0);
			anim.setDuration(mService.mAnimationTime);
			anim.setAnimationListener(new Animation.AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					mService.safelyRemoveView(SidebarHiddenView.this);
				}
			});
			mTab.startAnimation(anim);
		}
	}

	final Runnable runnable = new Runnable() {
		@Override
		public void run() {
			mService.showBar();
		}
	};

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
			mService.mHandler.postDelayed(runnable, 200);
			mTab.setImageState(new int[] { android.R.attr.state_pressed },
					false);
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mTab.setImageState(new int[] { android.R.attr.state_empty }, false);
			break;
		}
		if (mService.tabTouchEvent(event)) {
			mService.mHandler.removeCallbacks(runnable);
		}
		;
		return true;
	}
}