package com.dingapp.biz.page.thirdpage;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response.Listener;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.AppConstants;
import com.dingapp.biz.db.orm.ExpressListBean;
import com.dingapp.biz.net.RequestDataUtil;
import com.dingapp.biz.page.adapters.ChooseLogisticsAdapter;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.core.util.UIUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ChooseLogisticsFragment extends BaseFragment implements
		OnClickListener {
	private ImageView iv_back;
	private ListView lv;
	private TextView tv_sure;
	private TextView tv_price;
	private TextView tv_numgoods;
	private TextView tv_logistics_name;
	private ChooseLogisticsAdapter adapter;
	private String express_name;
	private int express_id;
	private double total_price;
	private int numGoods;
	private double total_weight;
	private Listener<String> listener = new Listener<String>() {

		@Override
		public void onResponse(String response) {
			parserData(response);
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.choose_logistics, null);
	}

	// Bundle bundle = new Bundle();
	// bundle.putInt("num_goods", numGoods);
	// bundle.putDouble("total_weight", total_weiht);
	// bundle.putDouble("total_price", allPrice);
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getArguments() != null) {
			if (getArguments().containsKey("num_goods")) {
				numGoods = getArguments().getInt("num_goods");
			}
			if (getArguments().containsKey("total_weight")) {
				total_weight = getArguments().getDouble("total_weight");
			}
		}
		initView(getView());
		initListener();
		adapter = new ChooseLogisticsAdapter(getActivity());
		lv.setAdapter(adapter);
		requestData();
	}

	private void initView(View v) {
		iv_back = (ImageView) v.findViewById(R.id.img_chooselogistics_back);
		lv = (ListView) v.findViewById(R.id.lv_choose_logistics);
		lv.setFocusable(false);
		tv_sure = (TextView) v.findViewById(R.id.tv_chooselogistics_sure);
		tv_price = (TextView) v.findViewById(R.id.tv_chooselogistics_yunfei);
		tv_numgoods = (TextView) v
				.findViewById(R.id.tv_chooselogistics_numgoods);
		tv_logistics_name = (TextView) v
				.findViewById(R.id.tv_chooselogistics_name);
		tv_numgoods.setText("共" + numGoods + "件商品");
	}

	private void initListener() {
		iv_back.setOnClickListener(this);
		tv_sure.setOnClickListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ExpressListBean bean = (ExpressListBean) adapter
						.getItem((int) arg3);
				express_id = bean.getExpress_id();
				express_name = bean.getExpress_name();
				tv_logistics_name.setText("物流公司:" + express_name);
				double price = 0;
				if (total_weight >= 1) {
					price = (Math.ceil(total_weight) - 1)
							* bean.getOther_per_price();
				}
				total_price = bean.getInner_price() + price;
				tv_price.setText("￥" + total_price);
				adapter.setSelectedPosition(arg2);
				adapter.notifyDataSetChanged();
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == iv_back) {
			popBack(null);
			return;
		}
		if (v == tv_sure) {
			Bundle bundle = new Bundle();
			bundle.putDouble("total_price", total_price);
			bundle.putString("express_name", express_name);
			bundle.putString("express_id", express_id + "");
			popBack(bundle);
			return;
		}
	}

	private void requestData() {
		HashMap<String, String> postMap = new HashMap<String, String>();
		RequestDataUtil.getRequestInstance().requestData(listener, postMap,
				AppConstants.express_list, getActivity(), null, "normal");
	}

	private void parserData(String json) {
		try {
			JSONObject jsbJson = new JSONObject(json);
			String statusCode = jsbJson.getString("statusCode");
			String statusMsg = jsbJson.getString("statusMsg");
			Gson gson = new Gson();
			if (statusCode.equals("200")) {
				List<ExpressListBean> taskList = gson.fromJson(
						jsbJson.getString("data"),
						new TypeToken<List<ExpressListBean>>() {
						}.getType());
				if (taskList != null && taskList.size() > 0) {
					ExpressListBean bean = taskList.get(0);
					express_id = bean.getExpress_id();
					express_name = bean.getExpress_name();
					tv_logistics_name.setText("物流公司:" + express_name);
					double price = 0;
					if (total_weight >= 1) {
						price = (Math.ceil(total_weight) - 1)
								* bean.getOther_per_price();
					}
					total_price = bean.getInner_price() + price;
					tv_price.setText("￥" + total_price);
					adapter.setData(taskList);
				}

			} else {
				UIUtil.showToast(getActivity(), statusMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
