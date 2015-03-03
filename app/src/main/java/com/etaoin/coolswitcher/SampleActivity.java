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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ImageView;
import android.widget.TextView;


public class SampleActivity extends ActionBarActivity implements CoolSwitch.Listener {

	private CoolSwitch coolSwitch;
	private ImageView iconBlue;
	private TextView connectContactsTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		mapViews();
		coolSwitch.addListener(this);
	}

	@Override
	public void onCheckedAnimationFinished() {
		// Empty
	}

	@Override
	public void onUncheckedAnimationFinished() {
		// Empty
	}

	private void mapViews() {
		connectContactsTextView = (TextView) findViewById(R.id.connect_contacts_text);
		coolSwitch = (CoolSwitch) findViewById(R.id.cool_switcher_blue);
		iconBlue = (ImageView) findViewById(R.id.icon_blue);
	}

	@Override
	protected void onDestroy() {
		coolSwitch.removeListener(this);

		super.onDestroy();
	}
}
