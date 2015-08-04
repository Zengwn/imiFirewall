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

	// ���ؼ��ؽ���
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

	// չʾ���ؽ���
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

	// �ı���ؽ�����������
	public void changeSearchPanelContent(Drawable paramDrawable,
			String paramString) {
		mIcon.setImageDrawable(paramDrawable);
		mTitle.setText(paramString);
	}

	// �ı������ͼ��
	public void changeSearchPanelIcon(Drawable paramDrawable) {
		mIcon.setImageDrawable(paramDrawable);
	}

	// �ı��������
	public void changeSearchPanelTitle(String paramString) {
		mTitle.setText(paramString);
	}

	public void hideSearchPanel() {
		hidePanel(true);
	}

	// ���ü���
	public void setButtonClickListener(View.OnClickListener paramOnClickListener) {
		this.mShowBtn = true;
		this.mButton.setOnClickListener(paramOnClickListener);
	}

	// �����������ɼ�
	public void showSearchPanel() {
		showPanel(true);
		mButton.setVisibility(View.INVISIBLE);
	}
}