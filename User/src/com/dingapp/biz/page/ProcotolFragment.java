package com.dingapp.biz.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.app.BaseFragment;

public class ProcotolFragment  extends BaseFragment{
	private ImageView iv_back;
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.protocol, null);
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		iv_back = (ImageView) getView().findViewById(R.id.iv_protocol_back);
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				popBack(null);
			}
		});
	}
}