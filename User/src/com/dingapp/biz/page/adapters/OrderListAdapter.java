package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.ItemOrderBean;
import com.dingapp.biz.db.orm.OrderPrdAttrsBean;
import com.dingapp.biz.db.orm.OrederGoodsItemBean;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.imageloader.core.ImageLoader;

public class OrderListAdapter extends BaseAdapter {
	private Context context;
	private OrderItemClick click;
	private List<ItemOrderBean> list = new ArrayList<ItemOrderBean>();
	private BaseFragment mfragment;
	// type:设计师和分销订单以及普通订单:normal,designer,distributor
	private String type;

	public OrderListAdapter(Context context, OrderItemClick click,
			BaseFragment mfragment, String type) {
		this.context = context;
		this.click = click;
		this.mfragment = mfragment;
		this.type = type;
	}

	public void setList(List<ItemOrderBean> list2) {
		if (list2 != null) {
			list.clear();
			list.addAll(list2);
			notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.order_item, null);
			holder.rl_pay = (LinearLayout) convertView
					.findViewById(R.id.rl_pay);
			holder.tv_pay_status1 = (TextView) convertView
					.findViewById(R.id.tv_status2);
			holder.tv_pay_status2 = (TextView) convertView
					.findViewById(R.id.tv_status1);
			holder.tv_order_num = (TextView) convertView
					.findViewById(R.id.order_num);
			holder.tv_order_status = (TextView) convertView
					.findViewById(R.id.tv_status);
			holder.lv_goods = (ListView) convertView
					.findViewById(R.id.lv_orderlist);
			holder.ll_parent = (LinearLayout) convertView
					.findViewById(R.id.ll_parent);
			holder.rl_dis_constant = (RelativeLayout) convertView
					.findViewById(R.id.rl_disorder_contants);
			holder.tv_dis_constantname = (TextView) convertView
					.findViewById(R.id.tv_item_disorder_contantsname);
			holder.tv_dis_conteantphone = (TextView) convertView
					.findViewById(R.id.tv_item_disorder_contantsphone);
			holder.tv_contact_shopper = (TextView) convertView
					.findViewById(R.id.tv_contact_shopper);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ItemOrderBean bean = (ItemOrderBean) getItem(position);
		String statusText = "";
		if (bean != null) {
			if (bean.getGoods_list() != null && bean.getGoods_list().size() > 0) {
				holder.lv_goods.setVisibility(View.VISIBLE);
				OrderGoodsAdapter goodsAdapter = new OrderGoodsAdapter(
						bean.getGoods_list());
				holder.lv_goods.setAdapter(goodsAdapter);
			} else {
				holder.lv_goods.setVisibility(View.GONE);
			}
			holder.tv_order_num.setText("订单编号: " + bean.getOrder_no());
			// 订单状态判断，区别分销订单
			String order_status = bean.getStatus();
			String return_status = bean.getReturn_status();
			holder.rl_pay.setVisibility(View.GONE);
			holder.tv_pay_status1.setVisibility(View.GONE);
			holder.tv_pay_status2.setVisibility(View.GONE);
			holder.tv_contact_shopper.setVisibility(View.VISIBLE);
			if (order_status.equals("shipping")) {
				statusText = "待发货";
				holder.tv_order_status.setText("待发货");
				if (return_status != null && !TextUtils.isEmpty(return_status)
						&& return_status.equals("normal")) {
					holder.tv_pay_status1.setText("申请退货");
					holder.tv_pay_status1.setVisibility(View.VISIBLE);
					holder.rl_pay.setVisibility(View.VISIBLE);
				}
				if (bean.getMember_info() == null) {
					holder.tv_contact_shopper.setVisibility(View.GONE);
				}
			}
			if (order_status.equals("paying")) {
				statusText = "待付款";
				holder.tv_order_status.setText("待付款");
				if (type.equals("normal")) {
					holder.tv_contact_shopper.setVisibility(View.GONE);
					holder.tv_pay_status1.setText("取消订单");
					holder.tv_pay_status1.setVisibility(View.VISIBLE);
					holder.tv_pay_status2.setText("去支付");
					holder.rl_pay.setVisibility(View.VISIBLE);
					holder.tv_pay_status2.setVisibility(View.VISIBLE);
				}
			}
			if (order_status.equals("receipting")) {
				statusText = "待收货";
				holder.tv_order_status.setText("待收货");
				if (type.equals("normal")) {
					holder.tv_pay_status2.setText("确定收货");
					holder.tv_pay_status1.setText("申请退货");
					if (return_status != null
							&& !TextUtils.isEmpty(return_status)
							&& return_status.equals("normal")) {
						holder.rl_pay.setVisibility(View.VISIBLE);
						holder.tv_pay_status2.setVisibility(View.VISIBLE);
						holder.tv_pay_status1.setVisibility(View.VISIBLE);
					} else if (return_status != null
							&& !TextUtils.isEmpty(return_status)
							&& return_status.equals("fail")) {
						holder.rl_pay.setVisibility(View.VISIBLE);
						holder.tv_pay_status2.setVisibility(View.VISIBLE);
					}
				}
			}
			if (order_status.equals("evaluating") && type.equals("normal")) {
				statusText = "待评价";
				holder.tv_order_status.setText("待评价");
				if (type.equals("normal")) {
					holder.tv_pay_status2.setText("去评价");
					holder.rl_pay.setVisibility(View.VISIBLE);
					holder.tv_pay_status2.setVisibility(View.VISIBLE);
				}
			}
			if (order_status.equals("finished")) {
				statusText = "已完成";
				holder.tv_order_status.setText("已完成");
				holder.rl_pay.setVisibility(View.VISIBLE);
			}
			if (order_status.equals("cancelled")) {
				statusText = "已取消";
				holder.tv_order_status.setText("已取消");
			}
			if (order_status.equals("withdraw")) {
				holder.tv_order_status.setText("交易完成");
			} else if (type.equals("distributor")) {
				holder.tv_order_status.setText("进行中");
			}
			if (type.equals("distributor")) {
				holder.rl_dis_constant.setVisibility(View.VISIBLE);
				holder.tv_dis_constantname.setText(bean.getCustom_name());
				holder.tv_dis_conteantphone.setText(bean.getCustom_mobile());
				holder.ll_parent.setEnabled(false);
			} else if (type.equals("normal")) {
				holder.rl_dis_constant.setVisibility(View.GONE);
				holder.lv_goods.setClickable(false);
				holder.lv_goods.setFocusable(false);
				holder.lv_goods.setEnabled(false);
				// 申请退款的状态
				if (return_status != null && !TextUtils.isEmpty(return_status)
						&& !return_status.equals("normal")) {
					if (return_status.equals("suc")) {
						holder.tv_order_status.setText("退货成功");
						holder.tv_pay_status2.setVisibility(View.GONE);
						if (bean.getMember_info() == null) {
							holder.tv_contact_shopper.setVisibility(View.GONE);
						} else {
							holder.rl_pay.setVisibility(View.VISIBLE);
						}
					}
					if (return_status.equals("applying")) {
						holder.tv_order_status.setText("退货申请中" + "("
								+ statusText + ")");
						if (bean.getMember_info() == null) {
							holder.tv_contact_shopper.setVisibility(View.GONE);
						} else {
							holder.rl_pay.setVisibility(View.VISIBLE);
						}
					}
					if (return_status.equals("fail")) {
						holder.tv_order_status.setText("退货失败" + "("
								+ statusText + ")");
						if (bean.getMember_info() == null) {
							holder.tv_contact_shopper.setVisibility(View.GONE);
						} else {
							holder.rl_pay.setVisibility(View.VISIBLE);
						}
					}
				}
			} else {
				holder.rl_dis_constant.setVisibility(View.GONE);
				holder.ll_parent.setEnabled(false);
			}
		}

		holder.itemPosition = position;
		initAdapterListener(holder);
		return convertView;
	}

	private void initAdapterListener(ViewHolder holder) {
		holder.lv_goods.setTag(holder);
		holder.lv_goods.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int itemPosition = -1;
				Object tag = arg0.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itemPosition == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				ItemOrderBean bean = (ItemOrderBean) getItem(itemPosition);
				int design_id = bean.getGoods_list().get(arg2)
						.getDesign_info_id();
				int prd_id = bean.getGoods_list().get(arg2).getGoods_id();
				bundle.putInt("prd_id", prd_id);
				bundle.putInt("design_id", design_id);
				mfragment.openPage("goods_detail_pager", bundle, true);
			}
		});

		holder.ll_parent.setTag(holder);
		holder.ll_parent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itemPosition == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				ItemOrderBean bean = (ItemOrderBean) getItem(itemPosition);
				String return_status = bean.getReturn_status();
				String isVisableSureReceive = "true";
				// 判断是否显示确认收货按钮
				if (bean.getStatus().equals("receipting")) {
					if (return_status != null
							&& !TextUtils.isEmpty(return_status)
							&& (return_status.equals("suc") || return_status
									.equals("applying"))) {
						isVisableSureReceive = "false";
					}
				}
				bundle.putString("isVisableSureReceive", isVisableSureReceive);
				bundle.putString("order_id", bean.getOrder_id() + "");
				mfragment.openPage("order_detail", bundle, true);
			}
		});

		holder.tv_pay_status1.setTag(holder);
		holder.tv_pay_status1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itemPosition == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				ItemOrderBean bean = (ItemOrderBean) getItem(itemPosition);
				bundle.putInt("order_id", bean.getOrder_id());
				bundle.putString("order_status", bean.getStatus());
				bundle.putString("return_status", bean.getReturn_status());
				bundle.putString("isCancal", "true");
				click.setOnclick(bundle, bean);
			}
		});
		holder.tv_pay_status2.setTag(holder);
		holder.tv_pay_status2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itemPosition == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				ItemOrderBean bean = (ItemOrderBean) getItem(itemPosition);
				bundle.putInt("order_id", bean.getOrder_id());
				bundle.putString("order_status", bean.getStatus());
				bundle.putString("price", bean.getPay_price() + "");
				bundle.putString("isCancal", "false");
				click.setOnclick(bundle, bean);
			}
		});
		holder.tv_contact_shopper.setTag(holder);
		holder.tv_contact_shopper.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int itemPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itemPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itemPosition == -1) {
					return;
				}
				Bundle bundle = new Bundle();
				ItemOrderBean bean = (ItemOrderBean) getItem(itemPosition);
				bundle.putString("contact_shopper", "true");
				click.setOnclick(bundle, bean);
			}
		});

	}

	public interface OrderItemClick {
		public abstract void setOnclick(Bundle bundle, ItemOrderBean bean);
	}

	class ViewHolder {
		private LinearLayout rl_pay;
		private TextView tv_pay_status1;
		private TextView tv_pay_status2;
		private TextView tv_order_num;
		private TextView tv_order_status;
		private ListView lv_goods;
		private int itemPosition;
		private LinearLayout ll_parent;
		private RelativeLayout rl_dis_constant;
		private TextView tv_dis_constantname;
		private TextView tv_dis_conteantphone;
		private TextView tv_contact_shopper;
	}

	private class OrderGoodsAdapter extends BaseAdapter {
		private List<OrederGoodsItemBean> goodsList = new ArrayList<OrederGoodsItemBean>();

		public OrderGoodsAdapter(List<OrederGoodsItemBean> goodsList) {
			this.goodsList = goodsList;
		}

		@Override
		public int getCount() {
			return goodsList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return goodsList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			InnerViewHolder holder = null;
			if (convertView == null) {
				holder = new InnerViewHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.item_sureorder_cart, null);
				holder.iv_pic = (ImageView) convertView
						.findViewById(R.id.iv_item_carts);
				holder.tv_title = (TextView) convertView
						.findViewById(R.id.tv_item_carts_title);
				holder.tv_guige = (TextView) convertView
						.findViewById(R.id.tv_item_carts_guige);
				holder.tv_price = (TextView) convertView
						.findViewById(R.id.tv_item_carts_price);
				holder.tv_num = (TextView) convertView
						.findViewById(R.id.tv_goods_num);
				convertView.setTag(holder);
			} else {
				holder = (InnerViewHolder) convertView.getTag();
			}
			OrederGoodsItemBean bean = (OrederGoodsItemBean) getItem(position);
			if (bean != null) {
				if (bean.getGoods_pic() != null) {
					ImageLoader.getInstance()
							.displayImage(
									bean.getGoods_pic().getDetail_url()
											+ ImageViewHWutils.getWHImageVIew(
													250, 250), holder.iv_pic);
				}
				holder.tv_title.setText(bean.getGoods_name());
				holder.tv_price.setText("￥" + bean.getGoods_price());
				List<OrderPrdAttrsBean> attrList = bean.getGoods_attrs();
				if (attrList != null) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < attrList.size(); i++) {
						String name = attrList.get(i).getAttr_name();
						String value = attrList.get(i).getAttr_value();
						sb.append(name + ":" + value);
					}
					holder.tv_guige.setText(sb.toString());
				}
				holder.tv_num.setText("x" + bean.getGoods_cnt());
			}
			return convertView;
		}

		class InnerViewHolder {
			private ImageView iv_pic;
			private TextView tv_title;
			private TextView tv_guige;
			private TextView tv_price;
			private TextView tv_num;
		}
	}
}
