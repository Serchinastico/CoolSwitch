package com.etaoin.coolswitcher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ToggleButton;

public class CoolSwitcher extends ToggleButton implements View.OnClickListener {

	private static final long BACKGROUND_OPAQUE_TO_TRANSPARENT_ANIMATION_DURATION_MS = 100;
	private static final long BACKGROUND_TRANSPARENT_TO_OPAQUE_ANIMATION_DURATION_MS = 100;
	private static final long MOVEMENT_ANIMATION_DURATION_MS = 300;
	private static final int MIN_ALPHA = 0;
	private static final int MAX_ALPHA = 40;
	private static final int OPAQUE = 255;
	private static final float SELECTOR_RATIO = 0.9f;

	private View enabledView;
	private View disabledView;
	private Paint paint;
	private Interpolator interpolator;
	private int selectorRadius;
	private Point disabledSelectorCenter;
	private Point enabledSelectorCenter;
	private Point currentSelectorCenter;
	private int backgroundAlpha;

	public CoolSwitcher(Context context) {
		super(context);
		initialize();
	}

	public CoolSwitcher(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize();
	}

	public CoolSwitcher(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initialize();
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public CoolSwitcher(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		initialize();
	}

	public void setEnabledView(View enabledView) {
		this.enabledView = enabledView;
	}

	public void setDisabledView(View disabledView) {
		this.disabledView = disabledView;
	}

	private void initialize() {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		interpolator = new DecelerateInterpolator(1.0f);
		enabledSelectorCenter = new Point(0, 0);
		disabledSelectorCenter = new Point(0, 0);
		currentSelectorCenter = new Point(0, 0);
		backgroundAlpha = isChecked() ? MAX_ALPHA : MIN_ALPHA;
		setBackgroundResource(R.color.transparent);
		setOnClickListener(this);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int minWidth = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
		int width = resolveSizeAndState(minWidth, widthMeasureSpec, 1);

		int minHeight = MeasureSpec.getSize(width) + getPaddingBottom() + getPaddingTop();
		int height = resolveSizeAndState(minHeight, heightMeasureSpec, 0);

		selectorRadius = width / 4;
		enabledSelectorCenter.set(width - selectorRadius, height / 2);
		disabledSelectorCenter.set(selectorRadius, height / 2);
		if (isEnabled()) {
			currentSelectorCenter.set(enabledSelectorCenter.x, enabledSelectorCenter.y);
		} else {
			currentSelectorCenter.set(disabledSelectorCenter.x, disabledSelectorCenter.y);
		}

		setMeasuredDimension(width, height);
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		drawBackground(canvas);
		drawBorder(canvas);
		drawSelector(canvas);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void drawBackground(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(backgroundAlpha);
		canvas.drawRoundRect(1, 1, getWidth() - 1, getHeight() - 1, selectorRadius, selectorRadius, paint);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void drawBorder(Canvas canvas) {
		if (isEnabled()) {
			paint.setStrokeWidth(2f);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.WHITE);
			canvas.drawRoundRect(1, 1, getWidth() - 1, getHeight() - 1, selectorRadius, selectorRadius, paint);
		}
	}

	private void drawSelector(Canvas canvas) {
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
		paint.setAlpha(OPAQUE);
		canvas.drawCircle(currentSelectorCenter.x, currentSelectorCenter.y, (int) (selectorRadius * SELECTOR_RATIO), paint);
	}

	@SuppressWarnings("unused")
	public void setBackgroundAlpha(int backgroundAlpha) {
		this.backgroundAlpha = backgroundAlpha;

		postInvalidate();
	}

	@SuppressWarnings("unused")
	public void setAnimationProgress(float currentSelectorXPosition) {
		int left = disabledSelectorCenter.x;
		int right = enabledSelectorCenter.x;

		currentSelectorCenter.x = (int) (left + interpolator.getInterpolation(currentSelectorXPosition) * (right - left));
		if (isChecked()) {
			currentSelectorCenter.x = getWidth() - currentSelectorCenter.x;
		}

		if (currentSelectorXPosition == 1f) {
			float initialRadius;
			float endRadius;
			final View invisibleView;
			View visibleView;
			int initialAlpha;
			int endAlpha;
			long backgroundAnimationDuration;

			if (isChecked()) {
				initialRadius = 1000;
				endRadius = 0;
				invisibleView = enabledView;
				visibleView = disabledView;
				initialAlpha = MIN_ALPHA;
				endAlpha = MAX_ALPHA;
				backgroundAnimationDuration = BACKGROUND_OPAQUE_TO_TRANSPARENT_ANIMATION_DURATION_MS;
			} else {
				initialRadius = 0;
				endRadius = 1000;
				invisibleView = disabledView;
				visibleView = enabledView;
				initialAlpha = MAX_ALPHA;
				endAlpha = MIN_ALPHA;
				backgroundAnimationDuration = BACKGROUND_TRANSPARENT_TO_OPAQUE_ANIMATION_DURATION_MS;
			}

			startAnimation(initialRadius, endRadius, invisibleView, visibleView, initialAlpha,
					endAlpha, backgroundAnimationDuration);
		}

		postInvalidate();
	}

	@Override
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public void onClick(View v) {
		ObjectAnimator.ofFloat(CoolSwitcher.this,
				"animationProgress", 0, 1)
				.setDuration(MOVEMENT_ANIMATION_DURATION_MS)
				.start();
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

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void startAnimation(float initialRadius, float endRadius, final View invisibleView,
			View visibleView, int initialAlpha, int endAlpha, long backgroundAnimationDuration) {
		Point revealAnimationCenter = getRevealAnimationCenter();
		Animator anim = ViewAnimationUtils.createCircularReveal(enabledView,
				revealAnimationCenter.x, revealAnimationCenter.y, initialRadius, endRadius);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				invisibleView.setVisibility(View.INVISIBLE);
			}
		});
		visibleView.setVisibility(View.VISIBLE);
		anim.start();

		ObjectAnimator.ofInt(CoolSwitcher.this,
				"backgroundAlpha", initialAlpha, endAlpha)
				.setDuration(backgroundAnimationDuration)
				.start();
	}
}
