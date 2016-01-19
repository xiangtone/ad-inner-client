package com.adwalker.wall.platform.layout;

import com.adwalker.wall.platform.util.MobileUtil;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AdLoadingView extends LinearLayout {
	
	public AdLoadingView(Context context) {
		super(context);
		this.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundColor(Color.WHITE);
		this.setGravity(Gravity.CENTER);
		ProgressBar progressBar = new ProgressBar(context);
		LinearLayout.LayoutParams barParams = new LayoutParams(
				MobileUtil.dip2px(context, 60), MobileUtil.dip2px(context, 60));
		progressBar.setLayoutParams(barParams);

		TextView loadingText = new TextView(context);
		LinearLayout.LayoutParams loadingParams = new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		loadingParams.topMargin = 10;
		loadingText.setLayoutParams(loadingParams);
		loadingText.setText("正在加载   请稍后...");
		loadingText.setSingleLine();
		loadingText.setTextColor(Color.GRAY);
		loadingText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		addView(progressBar);
		addView(loadingText);
	}
}
