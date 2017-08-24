package com.dingapp.biz.page;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.app.BaseFragment;

public class PersonalLetterFragment extends BaseFragment {

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		int layoutId = R.layout.personal_letter;
		return inflater.inflate(layoutId, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}
}
