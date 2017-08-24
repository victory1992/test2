package com.dingapp.biz.page;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.page.adapters.MyPageAdapter;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.IsImageRotate;
import com.dingapp.imageloader.core.DisplayImageOptions;
import com.dingapp.imageloader.core.ImageLoader;

public class PictureLookFragment extends BaseFragment {
	private ViewPager vp;
	private MyPageAdapter adapter;
	private List<View> views;
	private TextView tv_select, tv_total;
	private List<String> list_pics;
	private ImageView iv_backImageView;
	public static final String FLAG = "pics_look";
	public static final int FLAGCREAT = 1;
	public static final int FLAGLAKESAY = 2;
	public static final int CHATTING = 3;
	public static final int MEMBER = 4;
	public static final int BBSPICS_FLAG = 5;
	private String picPathString;
	private int flag;
	public static final String CREATINFO = "creatinfo";
	public static final String LAKESAYINFO = "lakeinfo";
	public static final String CHATTINGINFO = "chatting";
	public static final String MEMBERHEAD = "member";
	public static final String BBSPICS = "bbspics";
	// 图片位置
	private int idxPosition = 0;
	private DisplayImageOptions.Builder builder;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.look_pic, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		vp = (ViewPager) getView().findViewById(R.id.vp_pic);
		tv_select = (TextView) getView().findViewById(R.id.num_pic);
		tv_total = (TextView) getView().findViewById(R.id.num_total);
		iv_backImageView = (ImageView) getView().findViewById(R.id.iv_picback);
		builder = new DisplayImageOptions.Builder();
		builder.showImageOnLoading(R.drawable.default_list_loading_look);
		builder.showImageOnFail(R.drawable.default_list_fail_look);
		builder.showImageForEmptyUri(R.drawable.default_list_look);
		builder.cacheOnDisk(true);
		builder.cacheInMemory(true);
		if (getArguments() != null) {
			flag = getArguments().getInt(FLAG);
			if (getArguments().containsKey("pic_index")) {
				idxPosition = getArguments().getInt("pic_index");
			}
			if (flag == CHATTING) {
				picPathString = getArguments().getString(CHATTINGINFO);
				list_pics = new ArrayList<String>();
				list_pics.add("file:" + "///" + picPathString);
			}
			if (flag == MEMBER) {
				picPathString = getArguments().getString(MEMBERHEAD);
				list_pics = new ArrayList<String>();
				list_pics.add(picPathString);
			}
			if (flag == BBSPICS_FLAG) {
				list_pics = getArguments().getStringArrayList(BBSPICS);
			}
			
		}
		if (list_pics != null && list_pics.size() > 0) {
			tv_total.setText(list_pics.size() + "");
			initView();
		}
		initListener();
		adapter = new MyPageAdapter(views);
		vp.setAdapter(adapter);
		vp.setCurrentItem(idxPosition);
	}

	public void initView() {
		views = new ArrayList<View>();
		for (int i = 0; i < list_pics.size(); i++) {
			PhotoView iv = new PhotoView(getActivity());
			String url = list_pics.get(i);
			if (!TextUtils.isEmpty(url)) {
				Bitmap bm = IsImageRotate.getRotateBitmap(url.substring(8));
				if (bm != null) { // 如果照片出现了 旋转 那么 就更改旋转度数
					iv.setImageBitmap(bm);
				} else {
					ImageLoader.getInstance().displayImage(url, iv, builder.build());
				}
			}
			views.add(iv);
		}
	}

	@SuppressWarnings("deprecation")
	public void initListener() {
		iv_backImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				popBack(null);
			}
		});
		vp.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				tv_select.setText((arg0 + 1) + "");
				
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
}
