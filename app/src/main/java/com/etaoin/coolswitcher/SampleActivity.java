package com.etaoin.coolswitcher;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SampleActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sample);

		CoolSwitcher greenSwitcher = (CoolSwitcher) findViewById(R.id.cool_switcher_green);
		greenSwitcher.setEnabledView(findViewById(R.id.enabled_view_green));
		greenSwitcher.setDisabledView(findViewById(R.id.disabled_view_green));

		CoolSwitcher blueSwitcher = (CoolSwitcher) findViewById(R.id.cool_switcher_blue);
		blueSwitcher.setEnabledView(findViewById(R.id.enabled_view_blue));
		blueSwitcher.setDisabledView(findViewById(R.id.disabled_view_blue));
	}

}
