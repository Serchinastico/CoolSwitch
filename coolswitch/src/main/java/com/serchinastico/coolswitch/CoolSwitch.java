package com.serchinastico.coolswitch;
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
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Custom view representing a switch.
 * It animates a target view with a circular reveal effect when it's enabled or disabled.
 *
 * @author Sergio Gutiérrez Mota.
 */
public class CoolSwitch extends ToggleButton implements CompoundButton.OnCheckedChangeListener {

	private static final long BACKGROUND_OPAQUE_TO_TRANSPARENT_ANIMATION_DURATION_MS = 200;
	private static final long BACKGROUND_TRANSPARENT_TO_OPAQUE_ANIMATION_DURATION_MS = 200;
	private static final int BORDER_WIDTH = 2;
	private static final int MAX_BACKGROUND_ALPHA = 60;
	private static final int MIN_BACKGROUND_ALPHA = 0;
	private static final long MOVEMENT_ANIMATION_DURATION_MS = 200;
	private static final int OPAQUE = 255;
	private static final float SELECTOR_RATIO = 0.85f;

	private final CoolSwitchRevealAnimation animation = new CoolSwitchRevealAnimation(this);
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
			disabledViewId = a.getResourceId(R.styleable.CoolSwitch_disabledView, NO_ID);
			enabledViewId = a.getResourceId(R.styleable.CoolSwitch_enabledView, NO_ID);
		} finally {
			a.recycle();
		}

		initialize();
	}

	private void initialize() {
		backgroundAlpha = isChecked() ? MAX_BACKGROUND_ALPHA : MIN_BACKGROUND_ALPHA;
		setBackgroundColor(Color.argb(0, 0, 0, 0));
		setOnCheckedChangeListener(this);
	}

	public boolean addAnimationListener(AnimationListener listener) {
		return animation.addListener(listener);
	}

	public boolean removeAnimationListener(AnimationListener listener) {
		return animation.removeListener(listener);
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
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		setEnabled(false);

		if (hasValidTargetViewIds() && !hasLoadedTargetViews()) {
			disabledView = getRootView().findViewById(disabledViewId);
			enabledView = getRootView().findViewById(enabledViewId);
			animation.setDisabledView(disabledView);
			animation.setEnabledView(enabledView);
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

		if (animationProgress == 1f && hasLoadedTargetViews()) {
			if (isChecked()) {
				animation.startRevealAnimation(1000, 0, enabledView, disabledView,
						MIN_BACKGROUND_ALPHA, MAX_BACKGROUND_ALPHA,
						BACKGROUND_OPAQUE_TO_TRANSPARENT_ANIMATION_DURATION_MS);
			} else {
				animation.startRevealAnimation(0, 1000, disabledView, enabledView,
						MAX_BACKGROUND_ALPHA, MIN_BACKGROUND_ALPHA,
						BACKGROUND_TRANSPARENT_TO_OPAQUE_ANIMATION_DURATION_MS);
			}
		}

		postInvalidate();
	}

	private int interpolate(float animationProgress, int left, int right) {
		return (int) (left + interpolator.getInterpolation(animationProgress) * (right - left));
	}

	private boolean hasValidTargetViewIds() {
		return disabledViewId != NO_ID && enabledViewId != NO_ID;
	}

	private boolean hasLoadedTargetViews() {
		return disabledView != null && enabledView != null;
	}

	int getSelectorRadius() {
		return selectorRadius;
	}

	/**
	 * Listener to receive notifications about the state of the CoolSwitch.
	 */
	public static interface AnimationListener {
		void onCheckedAnimationFinished();
		void onUncheckedAnimationFinished();
	}

}
