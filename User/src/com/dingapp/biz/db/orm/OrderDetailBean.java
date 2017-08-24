package com.dingapp.biz.db.orm;

import java.util.List;


public class OrderDetailBean {
	private int order_id;
	private String order_no;
	private String express_name;
	private String express_id;
	private String create_time;
	private String receiver_name;
	private String receiver_mobile;
	private String receive_address;
	private String buyer_message;
	private List<OrederGoodsItemBean> goods_list;
	private Double coupon_price;
	private Double pay_price;
	private String status;
	private Double express_price;
	private int goods_cnt;
	private String deliver_type;
	private String deliver_address;
	private int give_score;
	private OrderMemberInfoBean member_info;
	private String refuse_msg;
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
	public String getExpress_name() {
		return express_name;
	}
	public void setExpress_name(String express_name) {
		this.express_name = express_name;
	}
	
	public String getExpress_id() {
		return express_id;
	}
	public void setExpress_id(String express_id) {
		this.express_id = express_id;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getReceiver_name() {
		return receiver_name;
	}
	public void setReceiver_name(String receiver_name) {
		this.receiver_name = receiver_name;
	}
	public String getReceiver_mobile() {
		return receiver_mobile;
	}
	public void setReceiver_mobile(String receiver_mobile) {
		this.receiver_mobile = receiver_mobile;
	}
	public String getReceive_address() {
		return receive_address;
	}
	public void setReceive_address(String receive_address) {
		this.receive_address = receive_address;
	}
	public String getBuyer_message() {
		return buyer_message;
	}
	public void setBuyer_message(String buyer_message) {
		this.buyer_message = buyer_message;
	}
	public List<OrederGoodsItemBean> getGoods_list() {
		return goods_list;
	}
	public void setGoods_list(List<OrederGoodsItemBean> goods_list) {
		this.goods_list = goods_list;
	}
	public Double getCoupon_price() {
		return coupon_price;
	}
	public void setCoupon_price(Double coupon_price) {
		this.coupon_price = coupon_price;
	}
	public Double getPay_price() {
		return pay_price;
	}
	public void setPay_price(Double pay_price) {
		this.pay_price = pay_price;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Double getExpress_price() {
		return express_price;
	}
	public void setExpress_price(Double express_price) {
		this.express_price = express_price;
	}
	public int getGoods_cnt() {
		return goods_cnt;
	}
	public void setGoods_cnt(int goods_cnt) {
		this.goods_cnt = goods_cnt;
	}
	public String getDeliver_type() {
		return deliver_type;
	}
	public void setDeliver_type(String deliver_type) {
		this.deliver_type = deliver_type;
	}
	public String getDeliver_address() {
		return deliver_address;
	}
	public void setDeliver_address(String deliver_address) {
		this.deliver_address = deliver_address;
	}
	public int getGive_score() {
		return give_score;
	}
	public void setGive_score(int give_score) {
		this.give_score = give_score;
	}
	public OrderMemberInfoBean getMember_info() {
		return member_info;
	}
	public void setMember_info(OrderMemberInfoBean member_info) {
		this.member_info = member_info;
	}
	public String getRefuse_msg() {
		return refuse_msg;
	}
	public void setRefuse_msg(String refuse_msg) {
		this.refuse_msg = refuse_msg;
	}
	
}
