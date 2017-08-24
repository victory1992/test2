package com.dingapp.biz.page.fourpage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.core.app.BaseFragment;

public class DisOrderListFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_back;
	private ImageView ivLine;
	private ViewPager viewPager;
	private HorizontalScrollView scrollView;
	private RadioGroup radioGroup;
	private int initWidth = 150;
	private String titles[] = new String[] { "所有订单", "进行中", "可提现" };
	private LayoutInflater inflater;
	private FragmentAdapter mFAdapter;
	private int tabDistance = 0; // 用于标记距离
	private int choosePosition = 0;
	int layoutId;
	boolean isMeasured = false;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		layoutId = R.layout.dis_orderlist;
		return inflater.inflate(layoutId, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			choosePosition = getArguments().getInt(AppConstants.KEY);
		}
		initView();
		initListener();
		// 界面画完之后才会获得宽高
		ViewTreeObserver observer = radioGroup.getViewTreeObserver();
		observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				if (!isMeasured) {
					((RadioButton) radioGroup.getChildAt(choosePosition))
							.performClick();
					isMeasured = true;
				}
				return true;
			}
		});

	}

	private void initView() {
		ivLine = (ImageView) getView().findViewById(R.id.iv_line_bg);
		viewPager = (ViewPager) getView().findViewById(R.id.vp_member_mytasks);
		scrollView = (HorizontalScrollView) getView().findViewById(
				R.id.member_mytasks_scrollView);
		radioGroup = (RadioGroup) getView().findViewById(
				R.id.member_mytasks_radioGroup);
		ViewGroup.LayoutParams cursor_Params = ivLine.getLayoutParams(); // 获取滑动下标的LayoutParams
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		initWidth = dm.widthPixels / 3;
		cursor_Params.width = (initWidth);// 初始化滑动下标的宽度
		ivLine.setLayoutParams(cursor_Params); // 加载滑动下标的宽度

		// 获取布局填充器
		inflater = getActivity().getLayoutInflater();

		initNavigationHSV();

		((RadioButton) (radioGroup.getChildAt(0))).setChecked(true);
		mFAdapter = new FragmentAdapter(getChildFragmentManager());
		viewPager.setAdapter(mFAdapter);
		iv_back = (ImageView) getView().findViewById(
				R.id.iv_member_mytasks_back);
	}

	private void initNavigationHSV() {
		radioGroup.removeAllViews();
		for (int i = 0; i < titles.length; i++) {
			// 获取RadioButton
			RadioButton rb = (RadioButton) inflater.inflate(
					R.layout.radio_item_trible, null);
			rb.setId(i);
			rb.setText(titles[i]);
			// 设置RadioButton的长和宽
			rb.setLayoutParams(new LayoutParams(initWidth,
					LayoutParams.MATCH_PARENT));
			// 将RadioButton 添加到RadioGroup中
			radioGroup.addView(rb);
		}
	}

	@SuppressWarnings("deprecation")
	private void initListener() {

		iv_back.setOnClickListener(this);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageSelected(int position) {
				// 判断当前页不是最后一页
				if (radioGroup != null && radioGroup.getChildCount() > position) {
					// 选中与viewPager对应 的RadioButton
					((RadioButton) radioGroup.getChildAt(position))
							.performClick();
				}
			}
		});

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (radioGroup.getChildAt(checkedId) != null) {
					// 创建动画对象
					TranslateAnimation animation = new TranslateAnimation(
					// 起始坐标
							tabDistance,
							// 获取当前RadioButton最左边的坐标值 为最终坐标
							((RadioButton) radioGroup.getChildAt(checkedId))
									.getLeft(), 0f, 0f);
					// 设置此动画的加速度曲线 变化速率恒定
					animation.setInterpolator(new LinearInterpolator());
					// 动画事件
					animation.setDuration(400);
					animation.setFillAfter(true);
					// 执行位移动画
					ivLine.startAnimation(animation);
					viewPager.setCurrentItem(checkedId); // ViewPager 跟随一起 切换
					// 记录当前 下标的距最左侧的 距离
					tabDistance = ((RadioButton) radioGroup
							.getChildAt(checkedId)).getLeft();
					// 设置当标题改变时 HorizontalScrollView 移动的距离
					// 如果标题是第3个及大于第3个时 获取当前标题左端的x坐标值 否则 为 0
					int x1 = checkedId > 1 ? ((RadioButton) radioGroup
							.getChildAt(checkedId)).getLeft() : 0;
					// 获取第2个标题左端的x坐标值
					int x2 = ((RadioButton) radioGroup.getChildAt(1)).getLeft();
					// 计算x轴方向移动的距离
					int x = x1 - x2;
					/**
					 * smoothScrollTo(int x, int y); x x轴方向移动的距离 y y轴方向移动的距离
					 */
					scrollView.smoothScrollTo(x, 0);
				}

			}
		});
	}

	private class FragmentAdapter extends FragmentPagerAdapter {

		public FragmentAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			DisOrderChildListFragment fragment = new DisOrderChildListFragment();
			Bundle bundle = new Bundle();
			if (position == 0) {
				bundle.putString("type", "all");
			}
			if (position == 1) {
				bundle.putString("type", "paying");
			}
			if (position == 2) {
				bundle.putString("type", "withdraw");
			}
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public int getCount() {
			return titles.length;
		}

	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
	}
}
