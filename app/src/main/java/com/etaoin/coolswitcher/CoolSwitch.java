package com.etaoin.coolswitcher;
/*
 * Copyright (C) 2015 Sergio Gutiérrez Mota.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ToggleButton;

import com.nineoldandroids.animation.ObjectAnimator;

import java.util.HashSet;
import java.util.Set;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * Custom view representing a switch.
 * It animates a target view with a circular reveal effect when it's enabled or disabled.
 *
 * @author Sergio Gutiérrez Mota.
 */
public class CoolSwitch extends ToggleButton implements View.OnClickListener {

	private static final long BACKGROUND_OPAQUE_TO_TRANSPARENT_ANIMATION_DURATION_MS = 200;
	private static final long BACKGROUND_TRANSPARENT_TO_OPAQUE_ANIMATION_DURATION_MS = 200;
	private static final int BORDER_WIDTH = 2;
	private static final int INVALID_VIEW_ID = -1;
	private static final int MAX_BACKGROUND_ALPHA = 60;
	private static final int MIN_BACKGROUND_ALPHA = 0;
	private static final long MOVEMENT_ANIMATION_DURATION_MS = 200;
	private static final int OPAQUE = 255;
	private static final float SELECTOR_RATIO = 0.85f;

	private int backgroundAlpha;
	private final RectF backgroundRect = new RectF(0, 0, 0, 0);
	private final Point currentSelectorCenter = new Point(0, 0);
	private final Point disabledSelectorCenter = new Point(0, 0);
	private View disabledView;
	private int disabledViewId;
	private final Point enabledSelectorCenter = new Point(0, 0);
	private View enabledView;
	private int enabledViewId;
	private final Interpolator interpolator = new DecelerateInterpolator(1.0f);
	private final Set<Listener> listeners = new HashSet<>();
	private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private int selectorRadius;

	public CoolSwitch(Context context) {
		super(context);
		initialize();
	}

	public CoolSwitch(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs);
	}

	public CoolSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs) {
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CoolSwitch,
				0, 0);

		try {
			disabledViewId = a.getResourceId(R.styleable.CoolSwitch_disabledView, INVALID_VIEW_ID);
			enabledViewId = a.getResourceId(R.styleable.CoolSwitch_enabledView, INVALID_VIEW_ID);
		} finally {
			a.recycle();
		}

		if (disabledViewId == INVALID_VIEW_ID) {
			throw new InvalidTargetView();
		}

		if (enabledViewId == INVALID_VIEW_ID) {
			throw new InvalidTargetView();
		}

		initialize();
	}

	private void initialize() {
		backgroundAlpha = isChecked() ? MAX_BACKGROUND_ALPHA : MIN_BACKGROUND_ALPHA;

		setBackgroundResource(R.color.transparent);
		setOnClickListener(this);
	}

	public boolean addListener(Listener listener) {
		return listeners.add(listener);
	}

	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int minWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
		int width = ViewCompat.resolveSizeAndState(minWidth, widthMeasureSpec, 1);

		int minHeight = MeasureSpec.getSize(width) + getPaddingBottom() + getPaddingTop();
		int height = ViewCompat.resolveSizeAndState(minHeight, heightMeasureSpec, 0);

		selectorRadius = height / 2;
		enabledSelectorCenter.set(width - selectorRadius, height / 2);
		disabledSelectorCenter.set(selectorRadius, height / 2);
		if (isChecked()) {
			currentSelectorCenter.set(disabledSelectorCenter.x, disabledSelectorCenter.y);
		} else {
			currentSelectorCenter.set(enabledSelectorCenter.x, enabledSelectorCenter.y);
		}

		int borderPadding = BORDER_WIDTH / 2;
		backgroundRect.set(borderPadding, borderPadding, width - borderPadding,
				height - borderPadding);

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		drawBackground(canvas);
		drawBorder(canvas);
		drawSelector(canvas);
	}

	@Override
	public void onClick(View v) {
		setEnabled(false);

		if (disabledView == null || enabledView == null) {
			disabledView = getRootView().findViewById(disabledViewId);
			enabledView = getRootView().findViewById(enabledViewId);
		}

		ObjectAnimator.ofFloat(CoolSwitch.this, "animationProgress", 0, 1)
				.setDuration(MOVEMENT_ANIMATION_DURATION_MS)
				.start();
	}

	private void drawBackground(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(backgroundAlpha);
		canvas.drawRoundRect(backgroundRect, selectorRadius, selectorRadius, paint);
	}

	private void drawBorder(Canvas canvas) {
		paint.setStrokeWidth(BORDER_WIDTH);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(backgroundRect, selectorRadius, selectorRadius, paint);
	}

	private void drawSelector(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
		paint.setAlpha(OPAQUE);
		canvas.drawCircle(currentSelectorCenter.x, currentSelectorCenter.y,
				(int) (selectorRadius * SELECTOR_RATIO), paint);
	}

	@SuppressWarnings("unused")
	public void setBackgroundAlpha(int backgroundAlpha) {
		this.backgroundAlpha = backgroundAlpha;

		postInvalidate();
	}

	@SuppressWarnings("unused")
	public void setAnimationProgress(float animationProgress) {
		int left = disabledSelectorCenter.x;
		int right = enabledSelectorCenter.x;

		currentSelectorCenter.x = interpolate(animationProgress, left, right);
		if (isChecked()) {
			currentSelectorCenter.x = getWidth() - currentSelectorCenter.x;
		}


		if (animationProgress == 1f) {
			if (isChecked()) {
				startRevealAnimation(1000, 0, enabledView, disabledView, MIN_BACKGROUND_ALPHA,
						MAX_BACKGROUND_ALPHA,
						BACKGROUND_OPAQUE_TO_TRANSPARENT_ANIMATION_DURATION_MS);
			} else {
				startRevealAnimation(0, 1000, disabledView, enabledView, MAX_BACKGROUND_ALPHA,
						MIN_BACKGROUND_ALPHA,
						BACKGROUND_TRANSPARENT_TO_OPAQUE_ANIMATION_DURATION_MS);
			}
		}

		postInvalidate();
	}

	private void startRevealAnimation(float initialRadius, float endRadius,
			final View invisibleView, View visibleView, int initialAlpha, int endAlpha,
			long backgroundAnimationDuration) {
		Point revealAnimationCenter = getRevealAnimationCenter();

		SupportAnimator anim = ViewAnimationUtils.createCircularReveal(enabledView,
				revealAnimationCenter.x, revealAnimationCenter.y, initialRadius, endRadius);
		anim.addListener(new CircularAnimatorListener(invisibleView, initialAlpha, endAlpha,
				backgroundAnimationDuration));

		visibleView.setVisibility(View.VISIBLE);
		anim.start();
	}

	private Point getRevealAnimationCenter() {
		int switcherScreenCoordinates[] = new int[2];
		getLocationOnScreen(switcherScreenCoordinates);
		int targetViewScreenCoordinates[] = new int[2];
		int offsetX;

		if (isChecked()) {
			enabledView.getLocationOnScreen(targetViewScreenCoordinates);
			offsetX = selectorRadius;
		} else {
			disabledView.getLocationOnScreen(targetViewScreenCoordinates);
			offsetX = 3 * selectorRadius;
		}

		return new Point(switcherScreenCoordinates[0] - targetViewScreenCoordinates[0] + offsetX,
				switcherScreenCoordinates[1] - targetViewScreenCoordinates[1] + selectorRadius);
	}

	private int interpolate(float animationProgress, int left, int right) {
		return (int) (left + interpolator.getInterpolation(animationProgress) * (right - left));
	}

	private void notifyCheckedAnimationFinished() {
		for (Listener listener : listeners) {
			listener.onCheckedAnimationFinished();
		}
	}

	private void notifyUncheckedAnimationFinished() {
		for (Listener listener : listeners) {
			listener.onUncheckedAnimationFinished();
		}
	}

	private final class CircularAnimatorListener implements SupportAnimator.AnimatorListener {

		private final View targetView;
		private final int initialAlpha;
		private final int endAlpha;
		private final long backgroundAnimationDuration;

		private CircularAnimatorListener(View targetView, int initialAlpha, int endAlpha,
				long backgroundAnimationDuration) {
			this.targetView = targetView;
			this.initialAlpha = initialAlpha;
			this.endAlpha = endAlpha;
			this.backgroundAnimationDuration = backgroundAnimationDuration;
		}

		@Override
		public void onAnimationStart() {
			ObjectAnimator.ofInt(CoolSwitch.this, "backgroundAlpha", initialAlpha, endAlpha)
					.setDuration(backgroundAnimationDuration)
					.start();
		}

		@Override
		public void onAnimationEnd() {
			setEnabled(true);

			if (isChecked()) {
				notifyCheckedAnimationFinished();
			} else {
				notifyUncheckedAnimationFinished();
			}

			targetView.setVisibility(View.INVISIBLE);
		}

		@Override
		public void onAnimationCancel() {}

		@Override
		public void onAnimationRepeat() {}

	}

	/**
	 * Listener to receive notifications about the state of the CoolSwitch.
	 */
	public static interface Listener {

		void onCheckedAnimationFinished();

		void onUncheckedAnimationFinished();

	}
}
