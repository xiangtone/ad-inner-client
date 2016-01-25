package com.adwalker.wall.platform.layout;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.widget.FrameLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.adwalker.wall.init.AdInitialization;
import com.adwalker.wall.platform.bean.WalkerDetailBean;
import com.adwalker.wall.platform.bean.AdHintBean;
import com.adwalker.wall.platform.bean.WalkerAdBean;
import com.adwalker.wall.platform.network.GuImageManager;
import com.adwalker.wall.platform.network.GuServierManage;
import com.adwalker.wall.platform.util.AdApkUtil;
import com.adwalker.wall.platform.util.GuLogUtil;
import com.adwalker.wall.platform.util.MobileUtil;


public class AdDetailActivity extends Activity {
	private RelativeLayout topLayout;
	private TextView titleText;
	private LinearLayout bodyLinear;
	private AdLoadingView loadingView;
	private AdDetailLayout showDetail;
	private List<WalkerAdBean> wallInfos = null;
	private WalkerAdBean wallInfo;
	private WalkerDetailBean detailInfo;
	private boolean isInit = false;
	public static  int page_type;
	private int adIndex;
	private FrameLayout frameLayout;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		try {
		if(AdInitialization.notifyList==null){
			finish();
		}
		if (savedInstanceState!=null) {
			adIndex = savedInstanceState.getInt("adIndex");
			page_type = savedInstanceState.getInt("adType");
			wallInfo = (WalkerAdBean) savedInstanceState.getParcelable("wallInfo");
			wallInfos = savedInstanceState.getParcelableArrayList("wallInfos");
			if (wallInfo==null&&wallInfos!=null&&wallInfos.size()>0){
				wallInfo =wallInfos.get(adIndex);
			}
		}else{
			Intent intent = getIntent();
			Bundle bundle =  intent.getExtras();
			adIndex = bundle.getInt("adIndex");
			page_type = bundle.getInt("adType");
			wallInfo = (WalkerAdBean) bundle.getParcelable("wallInfo");
			wallInfos = bundle.getParcelableArrayList("wallInfos");
			if (wallInfo==null&&wallInfos!=null&&wallInfos.size()>0) {
				wallInfo = wallInfos.get(adIndex);
			}
		}
		if (wallInfo==null) {
			Toast.makeText(this, "加载失败...",0).show();
			finish();
		}
		frameLayout = new FrameLayout(AdDetailActivity.this);
		frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.FILL_PARENT));
		frameLayout.addView(getView());
		
		setContentView(frameLayout);
		initData();
		} catch (Exception e) {
			GuLogUtil.e(AdConstants.LOG_ERR, "create: " + e);
			AdDetailActivity.this.finish();
		}
		
	}

	private void initData() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				detailInfo = GuServierManage.detailFromServer(AdDetailActivity.this, wallInfo.id);
				if (detailInfo != null) {
					handler.sendEmptyMessage(AdConstants.DETAIL_LOAD_SUCCESS);
				} else {
					handler.sendEmptyMessage(AdConstants.DETAIL_LOAD_ERROR);
				}
			}
		}).start();
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case AdConstants.DETAIL_LOAD_SUCCESS:
					showDetail = new AdDetailLayout(AdDetailActivity.this, wallInfos,wallInfo,detailInfo, detailInfo.detail_picturesUrl,page_type);
					showDetail.setVerticalScrollBarEnabled(false); //禁用垂直滚动  
					showDetail.setHorizontalScrollBarEnabled(false); //禁用水平滚动 
					bodyLinear.removeAllViews();
					bodyLinear.addView(showDetail);
					frameLayout.addView(showDetail.getBottomView());
					isInit = true;
					break;
				case AdConstants.DETAIL_LOAD_ERROR:
					Toast.makeText(AdDetailActivity.this,
							AdHintBean.data_get_err,
							Toast.LENGTH_SHORT).show();
					AdDetailActivity.this.finish(); 
					break;
				default:
					break;
				}
			} catch (Exception e) {
				GuLogUtil.e(AdConstants.LOG_ERR, e.getLocalizedMessage(),e);
			}
			super.handleMessage(msg);
		}
	};

	private LinearLayout getView() {
		LinearLayout linearLayout = new LinearLayout(AdDetailActivity.this);
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);

		titleText = new TextView(AdDetailActivity.this);
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		titleText.setGravity(Gravity.CENTER);
		titleText.setTextColor(Color.WHITE);
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		TextPaint textPaint = titleText.getPaint();
		textPaint.setFakeBoldText(true);
		titleText.setLayoutParams(titleParams);
		titleText.setShadowLayer(MobileUtil.dip2px(AdDetailActivity.this, 2),
				MobileUtil.dip2px(AdDetailActivity.this, -1),
				MobileUtil.dip2px(AdDetailActivity.this, -1), Color.rgb(47, 48, 50));
		
		topLayout = new RelativeLayout(AdDetailActivity.this);
		LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,MobileUtil.dip2px(AdDetailActivity.this, 44));
		topLayout.setLayoutParams(topParams);
		
		topLayout.addView(titleText);
		topLayout.setGravity(Gravity.CENTER);
		linearLayout.addView(topLayout);

		loadingView = new AdLoadingView(AdDetailActivity.this);

		bodyLinear = new LinearLayout(AdDetailActivity.this);
		bodyLinear.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		
		bodyLinear.setOrientation(LinearLayout.VERTICAL);
		bodyLinear.setBackgroundColor(Color.WHITE);
		bodyLinear.addView(loadingView);
		linearLayout.addView(bodyLinear);
		titleText.setText(AdHintBean.title);
		topLayout.setBackgroundColor(Color.rgb(9, 200, 136));
		return linearLayout;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable("wallInfo", wallInfo);
		outState.putInt("adIndex", adIndex);
		outState.putParcelableArrayList("wallInfos", (ArrayList<? extends Parcelable>) wallInfos);
		super.onSaveInstanceState(outState);
	}

	public List<WalkerAdBean> getWallInfos() {
		return wallInfos;
	}

	
	@Override
	protected void onResume() {
		super.onResume();
		if (isInit) {
			if (AdApkUtil.isInstalled(this,
					wallInfo.packageName)) {
				showDetail.detailText.setText(AdHintBean.installed_long);
			} else {
				showDetail.detailText.setText(AdHintBean.uninstall_long);
			}
		}
	}

	@Override
	protected void onDestroy() {	
		GuImageManager.INSTANCE.recycleCache(); 
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	

	
}
