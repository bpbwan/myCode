package com.flyaudio.backcar.animator;

import com.flyaudio.backcar.BackCarService;

import android.os.SystemProperties;

import com.xdroid.animation.AnimationKit;
import com.xdroid.animation.anim.TelescopicAnimation.TelescopicMode;
import com.xdroid.animation.anim.TelescopicAnimation.TelescopicTargetMode;
import com.xdroid.animation.anim.TelescopicAnimation.TelescopicTargetCallback;

import com.xdroid.animation.interfaces.Direction;
import com.xdroid.animation.interfaces.Orientation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

public class FadeInAnimator {

	View targetView;

	public FadeInAnimator(View view) {

		targetView = view;

	}

	public void telescopicAnimation(int startX, int endX,
			TelescopicTargetCallback callback) {
		if (targetView != null)
			AnimationKit.createTelescopicAnimation(targetView)
					.setDuration(1000)
					.setInterpolator(new AccelerateInterpolator())
					.setTelescopicMode(TelescopicMode.OUT)
					.setTelescopicTargetMode(TelescopicTargetMode.WIDTH)
					.setStart(startX).setEnd(endX).setCallBacks(callback)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							super.onAnimationEnd(animation);

						}
					}).animate();
	}

}
