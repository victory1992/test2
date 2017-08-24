package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.CouponBean;
import com.dingapp.biz.util.Utils;

public class CouponAdapter extends BaseAdapter {
	private Context context;
	private List<CouponBean> mList = new ArrayList<CouponBean>();

	public CouponAdapter(Context context) {
		this.context = context;
	}

	public void setData(List<CouponBean> newList) {
		if (newList != null) {
			mList.clear();
			mList.addAll(newList);
		}
		notifyDataSetInvalidated();
	}

	public void setMoreData(List<CouponBean> moreList) {
		if (moreList != null) {
			mList.addAll(moreList);
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public CouponBean getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CouponBean bean = mList.get(position);
		if (bean == null) {
			return null;
		}
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context,
					R.layout.member_center_coupon_list_item, null);
			holder.img_coupon_statu = (ImageView) convertView
					.findViewById(R.id.img_coupon_statu);
			holder.tv_coupon_value = (TextView) convertView
					.findViewById(R.id.tv_coupon_value);
			holder.tv_coupon_des = (TextView) convertView
					.findViewById(R.id.tv_coupon_des);
			holder.tv_coupon_begin = (TextView) convertView
					.findViewById(R.id.tv_coupon_begin);
			holder.tv_coupon_end = (TextView) convertView
					.findViewById(R.id.tv_coupon_end);
			holder.ll_coupon = (LinearLayout) convertView
					.findViewById(R.id.ll_coupon);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.img_coupon_statu.setVisibility(View.INVISIBLE);
		holder.ll_coupon.setBackgroundResource(R.color.color_a1a1a1);
		if ("已过期".equals(bean.getStatus())) {
			holder.img_coupon_statu.setVisibility(View.VISIBLE);
			holder.img_coupon_statu
					.setBackgroundResource(R.drawable.coupon_overdue);
			holder.ll_coupon.setBackgroundResource(R.color.color_a1a1a1);
		} else if ("已使用".equals(bean.getStatus())) {
			holder.img_coupon_statu.setVisibility(View.VISIBLE);
			holder.img_coupon_statu
					.setBackgroundResource(R.drawable.coupon_used);
			holder.ll_coupon.setBackgroundResource(R.color.color_a1a1a1);
		} else if ("未使用".equals(bean.getStatus())) {
			holder.img_coupon_statu.setVisibility(View.INVISIBLE);
			// holder.img_coupon_statu.setBackgroundResource(R.drawable.coupon_);
			holder.ll_coupon.setBackgroundResource(R.color.app_color);
		}
		holder.tv_coupon_value.setText("￥" + Utils.keepDouble2(bean.getMoney())
				+ "元");
		holder.tv_coupon_des.setText("商家赠送优惠券");
		holder.tv_coupon_begin.setText(bean.getStart_date());
		holder.tv_coupon_end.setText(bean.getEnd_date());
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_coupon_value;
		private TextView tv_coupon_des;
		private TextView tv_coupon_begin;
		private ImageView img_coupon_statu;
		private TextView tv_coupon_end;
		private LinearLayout ll_coupon;
	}

}
