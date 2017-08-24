package com.dingapp.biz.db.orm;

public class CouponBean {
	/**
	 * coupon_id 优惠劵ID Integer money 优惠劵面额 Double start_date 生效日期，格式为yyyy-MM-dd。
	 * String end_date 有效期结束日期，格式为yyyy-MM-dd。 String status
	 * 优惠劵状态。可选值为:已过期，未使用，已使用。 String left_day 优惠劵剩余天数。 Integer create_time
	 * 优惠劵发放时间。格式为yyyy-MM-dd HH:mm:ss。 String
	 */
	private long coupon_id;
	private double money;
	private String describe;
	private String start_date;
	private String end_date;
	private String status;
	private int left_day;
	private String create_time;

	public long getCoupon_id() {
		return coupon_id;
	}

	public void setCoupon_id(long coupon_id) {
		this.coupon_id = coupon_id;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getLeft_day() {
		return left_day;
	}

	public void setLeft_day(int left_day) {
		this.left_day = left_day;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

}
