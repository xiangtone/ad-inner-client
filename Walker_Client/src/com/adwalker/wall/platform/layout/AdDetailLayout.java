package com.adwalker.wall.platform.layout;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.platform.bean.WalkerDetailBean;
import com.adwalker.wall.platform.bean.AdHintBean;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.layout.AdShowViewFlow.ViewSwitchListener;
import com.adwalker.wall.platform.network.GuImageManager;
import com.adwalker.wall.platform.network.GuNotifyManage;
import com.adwalker.wall.platform.util.FormatTools;
import com.adwalker.wall.platform.util.MobileUtil;
import com.adwalker.wall.platform.util.ImageLoadUtil;

public class AdDetailLayout extends ScrollView {
	public LinearLayout introduceLayout, imageLayout;
	public RelativeLayout blankLayout;
	public RelativeLayout bottomLayout;
	public RelativeLayout contentRelative;

	private ImageView detailIconImage;
	private ImageView listIcon;//安全图片,展开小图标
	public TextView safeText;//安全介绍
	public TextView detailText;
	private TextView detailFirst, detailSecond, detailThird, detailFourth
					, detailSixth, detailSeventh;

	// 滑动距离及坐标
	private float xDistance, yDistance, xLast, yLast;

	private List<ImageView> pointList;
	private int nowIndex = 0;
	private AdShowViewFlowAdapter wallViewFlowAdapter;
	
	private Context context;
	private WalkerAdBean wallInfo;
//	private int page_type;
	private WalkerDetailBean detailInfo;

	public AdDetailLayout(Context context, List<WalkerAdBean> wallInfos,WalkerAdBean wallInfo,
			WalkerDetailBean detailInfo, List<String> detatilList, int page_type) {
		super(context);
		this.context = context;
		this.wallInfo = wallInfo;
		this.detailInfo = detailInfo;
//		this.page_type = page_type;
		init(context, wallInfos,wallInfo, detailInfo, detatilList,page_type);
	}

	private void init(final Context context,final List<WalkerAdBean> wallInfos,final WalkerAdBean wallInfo,
			final WalkerDetailBean detailInfo, List<String> detatilList,final int page_type) {
		this.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		this.setFillViewport(true);
		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		detailIconImage = new ImageView(context);
		RelativeLayout.LayoutParams iconParams = new RelativeLayout.LayoutParams(
				MobileUtil.dip2px(context, 50), MobileUtil.dip2px(context, 50));
		iconParams.topMargin = MobileUtil.dip2px(context, 9);
		iconParams.leftMargin = MobileUtil.dip2px(context, 13);
		iconParams.rightMargin = MobileUtil.dip2px(context, 10);
		iconParams.bottomMargin = MobileUtil.dip2px(context, 16);
		detailIconImage.setLayoutParams(iconParams);
		detailIconImage.setId(2001);
		GuImageManager.INSTANCE.loadBitmap(context,detailInfo.detail_icon_Url,detailIconImage, 100,  100);

		detailFirst = new TextView(context);
		RelativeLayout.LayoutParams firstParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		firstParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		firstParams.addRule(RelativeLayout.RIGHT_OF, 2001);
		firstParams.topMargin = MobileUtil.dip2px(context, 11);
		detailFirst.setLayoutParams(firstParams);
		detailFirst.setTextColor(Color.rgb(52, 52, 53));
		detailFirst.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		TextPaint firstPaint = detailFirst.getPaint();
		firstPaint.setFakeBoldText(true);
		detailFirst.setSingleLine();
		detailFirst.setId(2002);
		detailFirst.setText(detailInfo.detail_first);

		detailSecond = new TextView(context);
		RelativeLayout.LayoutParams secondParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		secondParams.addRule(RelativeLayout.BELOW, 2002);
		secondParams.addRule(RelativeLayout.RIGHT_OF, 2001);
		detailSecond.setLayoutParams(secondParams);
		detailSecond.setTextColor(Color.rgb(92, 90, 90));
		detailSecond.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		detailSecond.setSingleLine();
		detailSecond.setId(2003);
		detailSecond.setText(detailInfo.catagoryName);

		detailThird = new TextView(context);
		RelativeLayout.LayoutParams thirdParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		thirdParams.addRule(RelativeLayout.BELOW, 2003);
		thirdParams.addRule(RelativeLayout.RIGHT_OF, 2001);
		detailThird.setLayoutParams(thirdParams);
		detailThird.setTextColor(Color.rgb(92, 90, 90));
		detailThird.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		detailThird.setSingleLine();
		detailThird.setId(2004);
		String sizeAndVersion = detailInfo.detail_third == null ? "" : detailInfo.detail_third;
		sizeAndVersion = detailInfo.detail_fourth.substring(3)+"|"+ sizeAndVersion;
		detailThird.setText(sizeAndVersion);

		detailFourth = new TextView(context);
		RelativeLayout.LayoutParams fourthParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		fourthParams.addRule(RelativeLayout.BELOW, 2004);
		fourthParams.addRule(RelativeLayout.RIGHT_OF, 2001);
		detailFourth.setLayoutParams(fourthParams);
		detailFourth.setTextColor(Color.rgb(92, 90, 90));
		detailFourth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
		detailFourth.setSingleLine();
		detailFourth.setId(2005);
		detailFourth.setText(detailInfo.detail_fourth);
		
		LinearLayout linearSafe = new LinearLayout(context);
		RelativeLayout.LayoutParams linearSafeParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT);
		linearSafeParams.rightMargin = MobileUtil.dip2px(context, 15);
		linearSafeParams.bottomMargin = MobileUtil.dip2px(context, 17);
		linearSafe.setLayoutParams(linearSafeParams);
		linearSafe.setGravity(Gravity.RIGHT|Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
		linearSafe.setOrientation(LinearLayout.HORIZONTAL);

		contentRelative = new RelativeLayout(context);
		RelativeLayout.LayoutParams contentParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT, MobileUtil.dip2px(
						context, 85));
		contentRelative.setLayoutParams(contentParams);
		contentRelative.addView(detailIconImage);
		contentRelative.addView(detailFirst);
		contentRelative.addView(detailSecond);
		contentRelative.addView(detailThird);
//		contentRelative.addView(detailFourth);
		contentRelative.addView(linearSafe);
		contentRelative.setBackgroundColor(Color.rgb(255, 255, 255));
		linearLayout.addView(contentRelative);
		//详情页图片部分
		AdShowViewFlow flow = new AdShowViewFlow(context);
		LinearLayout.LayoutParams flowParams = new LinearLayout.LayoutParams(
				MobileUtil.dip2px(context, 240), MobileUtil.dip2px(
						context, 400));
		flow.setLayoutParams(flowParams);
		if (detatilList != null && detatilList.size() > 0) {
			flow.setmSideBuffer(detatilList.size());
		} else {
			flow.setmSideBuffer(0);
		}
		if(wallViewFlowAdapter==null){
			 wallViewFlowAdapter = new AdShowViewFlowAdapter(context, detatilList);
			 flow.setAdapter(wallViewFlowAdapter);
		}
		
		flow.setOnViewSwitchListener(new ViewSwitchListener() {
			@Override
			public void onSwitched(View view, int position) {
				if (nowIndex != position) {
					pointList.get(nowIndex).setImageBitmap(ImageLoadUtil.getPointDownBitmap(getContext()));
					pointList.get(position).setImageBitmap(ImageLoadUtil.getPointNormalBitmap(getContext()));
					nowIndex = position;
				}
			}
		});
		LinearLayout pointLayout = new LinearLayout(context);
		LinearLayout.LayoutParams pointParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, MobileUtil.dip2px(
						context, 20));
		pointParams.topMargin = MobileUtil.dip2px(context, 10);
		pointLayout.setLayoutParams(pointParams);
		pointLayout.setOrientation(LinearLayout.HORIZONTAL);
		pointLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		pointList = new ArrayList<ImageView>();
		if (detatilList != null && detatilList.size() > 0) {
			for (int i = 0; i < detatilList.size(); i++) {
				ImageView pointImage = new ImageView(context);
				LinearLayout.LayoutParams pointImageParams = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				pointImageParams.leftMargin = MobileUtil.dip2px(context, 5);
				pointImageParams.rightMargin = MobileUtil.dip2px(context, 5);
				pointImage.setLayoutParams(pointImageParams);
				if (i == 0) {
					pointImage.setImageBitmap(ImageLoadUtil.getPointNormalBitmap(getContext()));
				} else {
					pointImage.setImageBitmap(ImageLoadUtil.getPointDownBitmap(getContext()));
				}
				pointList.add(pointImage);
				pointLayout.addView(pointImage);
			}
		} else {
			ImageView pointImage = new ImageView(context);
			LinearLayout.LayoutParams pointImageParams = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			pointImageParams.leftMargin = MobileUtil.dip2px(context, 5);
			pointImageParams.rightMargin = MobileUtil.dip2px(context, 5);
			pointImage.setLayoutParams(pointImageParams);
			pointImage.setImageBitmap(ImageLoadUtil.getPointDownBitmap(getContext()));
		}

		imageLayout = new LinearLayout(context);
		LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		imageLayout.setLayoutParams(imageParams);
		imageLayout.setOrientation(LinearLayout.VERTICAL);
		imageLayout.setGravity(Gravity.CENTER_HORIZONTAL);
//		imageLayout.setPadding(0,GuUserUtil.dip2px(context, 5),0, 0);
//		imageLayout.setBackgroundColor(Color.rgb(231, 231, 231));
		imageLayout.addView(flow);
		imageLayout.addView(pointLayout);
		
		linearLayout.addView(imageLayout);
		
		detailSixth = new TextView(context);
		LinearLayout.LayoutParams sixthParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		sixthParams.topMargin = MobileUtil.dip2px(context, 4);
		sixthParams.leftMargin = MobileUtil.dip2px(context, 19);
		sixthParams.rightMargin = MobileUtil.dip2px(context, 50);
		sixthParams.bottomMargin = MobileUtil.dip2px(context, 1);
		detailSixth.setLayoutParams(sixthParams);
		detailSixth.setTextColor(Color.rgb(92, 90, 90));
		detailSixth.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
		TextPaint detailSixthPaint = detailSixth.getPaint();
		detailSixthPaint.setFakeBoldText(true);
		detailSixth.setSingleLine();
		detailSixth.setText("应用详情");
		
		LinearLayout scoreLayout = new LinearLayout(context);
		LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		scoreLayout.setLayoutParams(scoreParams);
		scoreLayout.setOrientation(LinearLayout.HORIZONTAL);
		scoreLayout.setGravity(Gravity.CENTER_VERTICAL);
		scoreLayout.addView(detailSixth);

		detailSeventh = new TextView(context);
		LinearLayout.LayoutParams seventhParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		seventhParams.topMargin = MobileUtil.dip2px(context, 6);
		seventhParams.leftMargin = MobileUtil.dip2px(context, 19);
		seventhParams.rightMargin = MobileUtil.dip2px(context, 27);
		seventhParams.bottomMargin = MobileUtil.dip2px(context, 0);
		detailSeventh.setLayoutParams(seventhParams);
		detailSeventh.setTextColor(Color.rgb(92, 90, 90));
		detailSeventh.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		detailSeventh.setText(detailInfo.detail_seventh);
		detailSeventh.setLines(3);
		detailSeventh.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
		

		introduceLayout = new LinearLayout(context);
		LinearLayout.LayoutParams introduceParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		
		introduceLayout.setLayoutParams(introduceParams);
		introduceLayout.setOrientation(LinearLayout.VERTICAL);
		introduceLayout.addView(scoreLayout);
		listIcon = new ImageView(context);
		listIcon.setBackgroundDrawable( FormatTools.getImageFromAssetsFile2("gudown.png",context));
		LinearLayout.LayoutParams listIconParams = new LinearLayout.LayoutParams(
				MobileUtil.dip2px(context,  20), MobileUtil.dip2px(context, 15));
		listIconParams.gravity = Gravity.RIGHT;
		listIconParams.topMargin = MobileUtil.dip2px(context, 0);
		listIconParams.bottomMargin = MobileUtil.dip2px(context, 10);
		listIconParams.rightMargin = MobileUtil.dip2px(context, 15);
		listIcon.setLayoutParams(listIconParams);
		
		
		listIcon.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(detailSeventh.getLineCount()>3){
					listIcon.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2("gudown.png",context));
					detailSeventh.setEllipsize(TextUtils.TruncateAt.valueOf("END")); // 展开
					detailSeventh.setLines(3);
				}else{
					listIcon.setBackgroundDrawable(FormatTools.getImageFromAssetsFile2("guup.png",context));
					detailSeventh.setEllipsize(null); // 展开
					detailSeventh.setSingleLine(false);
				}
			}
		});
		introduceLayout.addView(detailSeventh);
		introduceLayout.addView(listIcon);
		LinearLayout line = new LinearLayout(context);
		line.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		line.setBackgroundDrawable( FormatTools.getImageFromAssetsFile2("guline.png", context));
		linearLayout.addView(line);
		linearLayout.addView(introduceLayout);
		
		
		//灰色部分
		LinearLayout tuijianLine = new LinearLayout(context);
		tuijianLine.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
		tuijianLine.setOrientation(LinearLayout.VERTICAL);
		tuijianLine.setBackgroundColor(Color.rgb(231, 231, 231));
		
	
		LinearLayout otherIconLine = new LinearLayout(context);
		LinearLayout.LayoutParams otherIconLineParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		otherIconLineParams.topMargin = MobileUtil.dip2px(context, 5);
		otherIconLineParams.bottomMargin = MobileUtil.dip2px(context, 15);
		otherIconLine.setLayoutParams(otherIconLineParams);
		
		int tag =4;
		if(wallInfos!=null&&wallInfos.size()>0){
			for(int i=0;i<wallInfos.size();i++){
				//过滤掉相同的
				if(wallInfos.get(i).id.equals(wallInfo.id)){
					tag =5;
					continue;
				}
				//最多显示4个
				if(i==tag){
						break;
				}
				LinearLayout iconLine = new LinearLayout(context);
				LinearLayout.LayoutParams iconLineeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				iconLine.setLayoutParams(iconLineeParams);
				iconLineeParams.weight=1;
				iconLine.setOrientation(LinearLayout.VERTICAL);
				iconLine.setGravity(Gravity.CENTER_HORIZONTAL);
				ImageView otherIconImage = new ImageView(context);
				LinearLayout.LayoutParams otherIconImageParams = new LinearLayout.LayoutParams(
						MobileUtil.dip2px(context, 50), MobileUtil.dip2px(context, 50));
				otherIconImage.setLayoutParams(otherIconImageParams);
				GuImageManager.INSTANCE.loadBitmap(context,wallInfos.get(i).generalInfo.wall_icon_Url,otherIconImage, 100,  100);
				
				TextView iconLineText = new TextView(context);
				LinearLayout.LayoutParams iconLineTextParams = new LinearLayout.LayoutParams(
						MobileUtil.dip2px(context, 50),LinearLayout.LayoutParams.WRAP_CONTENT);
				iconLineText.setLayoutParams(iconLineTextParams);
				iconLineText.setTextColor(Color.rgb(92, 90, 90));
				iconLineText.setTextSize(12);
				iconLineText.setText(wallInfos.get(i).title);
				iconLineText.setSingleLine();
				iconLineText.setEllipsize(TextUtils.TruncateAt.valueOf("END"));
				iconLine.addView(otherIconImage);
				iconLine.addView(iconLineText);
				otherIconLine.addView(iconLine);
				final WalkerAdBean myInfo = wallInfos.get(i);
				iconLine.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						bundle.putParcelable("wallInfo",myInfo);
						bundle.putParcelableArrayList("wallInfos",(ArrayList<? extends Parcelable>) wallInfos);
						bundle.putInt("adType",page_type);
						intent.putExtras(bundle);
						intent.setClass(context, AdDetailActivity.class);
						intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
					
					}
				});
			}
			tuijianLine.addView(otherIconLine);
			linearLayout.addView(tuijianLine);
		}

		blankLayout = new RelativeLayout(context);
		RelativeLayout.LayoutParams bottomParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				MobileUtil.dip2px(context, 40));
		blankLayout.setLayoutParams(bottomParams);		
		linearLayout.addView(blankLayout);
		this.addView(linearLayout);
	}
	
	
	
	public RelativeLayout getBottomView(){
		//底部下载和分享按钮
		detailText = new TextView(context);
		LinearLayout.LayoutParams detailTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
				,LinearLayout.LayoutParams.WRAP_CONTENT);
		detailTextParams.leftMargin = MobileUtil.dip2px(context,5);
		detailText.setLayoutParams(detailTextParams);
		detailText.setTextSize(15);
		detailText.setTextColor(Color.WHITE);
	
		if (wallInfo.state == AdConstants.APP_INSTALLED) {
			detailText.setText(AdHintBean.installed_long);
		} else {
			detailText.setText(AdHintBean.uninstall_long);
		}
		
		LinearLayout bottomItemLayout = new LinearLayout(context);
		RelativeLayout.LayoutParams bottomItemParams = new RelativeLayout.LayoutParams(
				MobileUtil.dip2px(context,  176), MobileUtil.dip2px(context, 31));
		bottomItemParams.topMargin = MobileUtil.dip2px(context, 10);
		bottomItemParams.bottomMargin = MobileUtil.dip2px(context, 10);
		bottomItemParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		bottomItemLayout.setLayoutParams(bottomItemParams);
		bottomItemLayout.setGravity(Gravity.CENTER);
		bottomItemLayout.addView(detailText);
		
		bottomLayout = new RelativeLayout(context);
		FrameLayout.LayoutParams bottomParams = new FrameLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT,Gravity.BOTTOM);
		bottomLayout.setLayoutParams(bottomParams);
		bottomLayout.setBackgroundColor(Color.BLACK);
		bottomLayout.addView(bottomItemLayout);
		bottomItemLayout.setBackgroundColor(Color.rgb(9, 200, 136));		
		
//		if(AdConstants.sign_status==0){
//			if (wallInfo.state == AdConstants.APP_INSTALLED) {
//				detailText.setText(AdHintBean.installed);
//			} else {
//				detailText.setText(AdHintBean.uninstall);
//				detailText.setTextColor(Color.WHITE);
//			}
//		}
		//下载
		bottomItemLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(wallInfo.state==AdConstants.APP_DOWNLOADED){
					wallInfo.state = AdConstants.APP_UNDO;
					AdInitialization.notifyList.remove(String.valueOf(wallInfo.id));
				}
				if(wallInfo.state==AdConstants.APP_UNDO||wallInfo.state==AdConstants.APP_INSTALLED){
					GuNotifyManage.getInstance(null).checkDownloadTask(context, wallInfo);
				}
			}
		});		
		return bottomLayout;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDistance = yDistance = 0f;
			xLast = ev.getX();
			yLast = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float curX = ev.getX();
			final float curY = ev.getY();
			xDistance += Math.abs(curX - xLast);
			yDistance += Math.abs(curY - yLast);
			xLast = curX;
			yLast = curY;
			if (xDistance > yDistance) {
				return false;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	public TextView getDetailSeventh() {
		return detailSeventh;
	}
	
}
