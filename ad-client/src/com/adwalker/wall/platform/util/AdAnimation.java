package com.adwalker.wall.platform.util;

import java.util.ArrayList;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AdAnimation {
	private ArrayList<AnimationSet> inList;//进入的动画
	private ArrayList<AnimationSet> outList;//出来的动画
	
	//获取进入的动画
	public AnimationSet getInAnimationSet(int index){
		return inList.get(index);
	}
	//获取出来的动画
	public AnimationSet getOutAnimationSet(int index){
		return outList.get(index);
	}
	
	public ArrayList<AnimationSet> getInAnimationSets(){
		return inList;
	}
	public ArrayList<AnimationSet> getOutAnimationSets(){
		return outList;
	}

	public  AdAnimation(Context context) {
		inList = new ArrayList<AnimationSet>();
		inList.add(alphaIn(context));
		inList.add(pushIn(context));
		inList.add(translateIn(context));
		inList.add(zoomIn(context));

		outList = new ArrayList<AnimationSet>();
		outList.add(alphaOut(context));
		outList.add(pushOut(context));
		outList.add(translateOut(context));
		outList.add(zoomOut(context));
	}

	// 淡入
	public AnimationSet alphaIn(Context context) {
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alpha);
		return animationSet;
	}

	// 淡出
	public AnimationSet alphaOut(Context context) {
		AlphaAnimation alpha = new AlphaAnimation(1, 0);
		alpha.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alpha);
		return animationSet;
	}

	// 上下-滑入
	public AnimationSet pushIn(Context context) {
		TranslateAnimation translate = new TranslateAnimation(0, 0,
				MobileUtil.dip2px(context,
						126), 0);
		translate.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(translate);
		return animationSet;
	}

	// 上下-滑出
	public AnimationSet pushOut(Context context) {
		TranslateAnimation translate = new TranslateAnimation(0, 0, 0,
				-MobileUtil.dip2px(context,
						126));
		translate.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(translate);
		return animationSet;
	}

	// 左右-滑入
	public AnimationSet translateIn(Context context) {
		TranslateAnimation translate = new TranslateAnimation(
				MobileUtil.getSCREEN_WIDTH(context), 0, 0, 0);
		translate.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(translate);
		return animationSet;
	}

	// 左右-滑出
	public AnimationSet translateOut(Context context) {
		TranslateAnimation translate = new TranslateAnimation(0,
				-MobileUtil.getSCREEN_WIDTH(context), 0, 0);
		translate.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(translate);
		return animationSet;
	}

	// 伸缩-进
	public AnimationSet zoomIn(Context context) {
		AlphaAnimation alpha = new AlphaAnimation(0, 1);
		alpha.setDuration(2500);
		ScaleAnimation scale = new ScaleAnimation(2, 1, 2, 1, 0.5f, 0.5f);
		scale.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alpha);
		animationSet.addAnimation(scale);
		return animationSet;
	}

	// 伸缩-出
	public AnimationSet zoomOut(Context context) {
		AlphaAnimation alpha = new AlphaAnimation(1, 0);
		alpha.setDuration(2500);
		ScaleAnimation scale = new ScaleAnimation(1, 0, 1, 0, 0, 0);
		scale.setDuration(2500);
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alpha);
		animationSet.addAnimation(scale);
		return animationSet;
	}
	
}
