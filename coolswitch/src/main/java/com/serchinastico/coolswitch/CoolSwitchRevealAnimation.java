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

import android.graphics.Point;
import android.view.View;

import com.nineoldandroids.animation.ObjectAnimator;

import java.util.HashSet;
import java.util.Set;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

/**
 * @author Sergio Gutiérrez Mota.
 */
public class CoolSwitchRevealAnimation {

	private CoolSwitch coolSwitch;
	private View enabledView;
	private View disabledView;
	private final Set<CoolSwitch.AnimationListener> listeners = new HashSet<>();

	CoolSwitchRevealAnimation(CoolSwitch coolSwitch) {
		this.coolSwitch = coolSwitch;
	}

	void startRevealAnimation(float initialRadius, float endRadius,
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

	void setDisabledView(View disabledView) {
		this.disabledView = disabledView;
	}

	void setEnabledView(View enabledView) {
		this.enabledView = enabledView;
	}

	boolean addListener(CoolSwitch.AnimationListener listener) {
		return listeners.add(listener);
	}

	boolean removeListener(CoolSwitch.AnimationListener listener) {
		return listeners.remove(listener);
	}

	private Point getRevealAnimationCenter() {
		int switchCoordinates[] = new int[2];
		coolSwitch.getLocationOnScreen(switchCoordinates);
		int targetViewCoordinates[] = new int[2];
		int offsetX;

		if (coolSwitch.isChecked()) {
			enabledView.getLocationOnScreen(targetViewCoordinates);
			offsetX = coolSwitch.getSelectorRadius();
		} else {
			disabledView.getLocationOnScreen(targetViewCoordinates);
			offsetX = 3 * coolSwitch.getSelectorRadius();
		}

		return new Point(switchCoordinates[0] - targetViewCoordinates[0] + offsetX,
				switchCoordinates[1] - targetViewCoordinates[1] + coolSwitch.getSelectorRadius());
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
			ObjectAnimator.ofInt(coolSwitch, "backgroundAlpha", initialAlpha, endAlpha)
					.setDuration(backgroundAnimationDuration)
					.start();
		}

		@Override
		public void onAnimationEnd() {
			coolSwitch.setEnabled(true);

			if (coolSwitch.isChecked()) {
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

	private void notifyCheckedAnimationFinished() {
		for (CoolSwitch.AnimationListener listener : listeners) {
			listener.onCheckedAnimationFinished();
		}
	}

	private void notifyUncheckedAnimationFinished() {
		for (CoolSwitch.AnimationListener listener : listeners) {
			listener.onUncheckedAnimationFinished();
		}
	}
}
