package com.dingapp.biz.db.orm;

import java.util.List;

public class ItemOrderBean {
	private int order_id;
	private String order_no;
	private String status;
	private Double pay_price;
	private Double express_price;
	private List<OrederGoodsItemBean> goods_list;
	private String custom_mobile;
	private String custom_name;
	private String return_status;
	private OrderMemberInfoBean member_info;

	public int getOrder_id() {
		return order_id;
	}

	public void setOrder_id(int order_id) {
		this.order_id = order_id;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getPay_price() {
		return pay_price;
	}

	public void setPay_price(Double pay_price) {
		this.pay_price = pay_price;
	}

	public Double getExpress_price() {
		return express_price;
	}

	public void setExpress_price(Double express_price) {
		this.express_price = express_price;
	}

	public List<OrederGoodsItemBean> getGoods_list() {
		return goods_list;
	}

	public void setGoods_list(List<OrederGoodsItemBean> goods_list) {
		this.goods_list = goods_list;
	}

	public String getCustom_mobile() {
		return custom_mobile;
	}

	public void setCustom_mobile(String custom_mobile) {
		this.custom_mobile = custom_mobile;
	}

	public String getCustom_name() {
		return custom_name;
	}

	public void setCustom_name(String custom_name) {
		this.custom_name = custom_name;
	}

	public String getReturn_status() {
		return return_status;
	}

	public void setReturn_status(String return_status) {
		this.return_status = return_status;
	}

	public OrderMemberInfoBean getMember_info() {
		return member_info;
	}

	public void setMember_info(OrderMemberInfoBean member_info) {
		this.member_info = member_info;
	}

}
