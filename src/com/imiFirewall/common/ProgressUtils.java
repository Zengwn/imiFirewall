package com.imiFirewall.common;

import com.imiFirewall.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProgressUtils {
	public Button mButton;
	private Context mContext;
	private ImageView mIcon;
	private View mSearchPanel;
	@SuppressWarnings("unused")
	private boolean mShowBtn;
	private TextView mTitle;

	public ProgressUtils(Context context, ViewStub viewstub) {
		mContext = context;
		View view = viewstub.inflate();
		mSearchPanel = view;
		mButton = (Button) mSearchPanel.findViewById(R.id.hidden);
		mIcon = (ImageView) mSearchPanel.findViewById(R.id.stub_image);
		mTitle = (TextView) mSearchPanel.findViewById(R.id.stub_text);
		mShowBtn = true;
	}

	// 隐藏加载界面
	private void hidePanel(boolean paramBoolean) {
		View view = mSearchPanel;
		if (paramBoolean) {
			Animation animation = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_in);
			view.startAnimation(animation);
			mSearchPanel.setVisibility(8);
			return;
		}
	}

	// 展示加载界面
	private void showPanel(boolean paramBoolean) {
		View view = mSearchPanel;

		if (paramBoolean) {
			Animation animation = AnimationUtils.loadAnimation(mContext,
					R.anim.slide_out);
			view.startAnimation(animation);
			mSearchPanel.setVisibility(0);
			return;
		}
	}

	// 改变加载进度条的内容
	public void changeSearchPanelContent(Drawable paramDrawable,
			String paramString) {
		mIcon.setImageDrawable(paramDrawable);
		mTitle.setText(paramString);
	}

	// 改变进度条图标
	public void changeSearchPanelIcon(Drawable paramDrawable) {
		mIcon.setImageDrawable(paramDrawable);
	}

	// 改变进度文字
	public void changeSearchPanelTitle(String paramString) {
		mTitle.setText(paramString);
	}

	public void hideSearchPanel() {
		hidePanel(true);
	}

	// 设置监听
	public void setButtonClickListener(View.OnClickListener paramOnClickListener) {
		this.mShowBtn = true;
		this.mButton.setOnClickListener(paramOnClickListener);
	}

	// 设置搜索面板可见
	public void showSearchPanel() {
		showPanel(true);
		mButton.setVisibility(View.INVISIBLE);
	}
}