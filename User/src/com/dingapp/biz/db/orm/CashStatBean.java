package com.dingapp.biz.db.orm;

import android.os.Parcel;
import android.text.TextUtils;

import com.dingapp.core.db.dao.OrmObject;

/**
 * 收益分析
 * @author king
 *
 */
public class CashStatBean implements OrmObject{
	private Double task_cash_money;
	private String task_cash_rate;
	private Double act_cash_money;
	private String act_cash_rate;
	private Double team_cash_money;
	private String team_cash_rate;
	private Double total_cash_money;
	public Double getTask_cash_money() {
		return task_cash_money;
	}
	public void setTask_cash_money(Double task_cash_money) {
		this.task_cash_money = task_cash_money;
	}
	public String getTask_cash_rate() {
		return task_cash_rate;
	}
	public void setTask_cash_rate(String task_cash_rate) {
		this.task_cash_rate = task_cash_rate;
	}
	public Double getAct_cash_money() {
		return act_cash_money;
	}
	public void setAct_cash_money(Double act_cash_money) {
		this.act_cash_money = act_cash_money;
	}
	public String getAct_cash_rate() {
		return act_cash_rate;
	}
	public void setAct_cash_rate(String act_cash_rate) {
		this.act_cash_rate = act_cash_rate;
	}
	public Double getTeam_cash_money() {
		return team_cash_money;
	}
	public void setTeam_cash_money(Double team_cash_money) {
		this.team_cash_money = team_cash_money;
	}
	public String getTeam_cash_rate() {
		return team_cash_rate;
	}
	public void setTeam_cash_rate(String team_cash_rate) {
		this.team_cash_rate = team_cash_rate;
	}
	public Double getTotal_cash_money() {
		return total_cash_money;
	}
	public void setTotal_cash_money(Double total_cash_money) {
		this.total_cash_money = total_cash_money;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if(task_cash_money!=null){
			dest.writeDouble(task_cash_money.doubleValue());
		}else{
			dest.writeDouble(0);
		}
		if(!TextUtils.isEmpty(task_cash_rate)){
			dest.writeString(task_cash_rate);
		}else{
			dest.writeString("");
		}
		
		if(act_cash_money!=null){
			dest.writeDouble(act_cash_money.doubleValue());
		}else{
			dest.writeDouble(0);
		}
		if(!TextUtils.isEmpty(act_cash_rate)){
			dest.writeString(act_cash_rate);
		}else{
			dest.writeString("");
		}
		
		if(team_cash_money!=null){
			dest.writeDouble(team_cash_money.doubleValue());
		}else{
			dest.writeDouble(0);
		}
		if(!TextUtils.isEmpty(team_cash_rate)){
			dest.writeString(team_cash_rate);
		}else{
			dest.writeString("");
		}
		if(total_cash_money!=null){
			dest.writeDouble(total_cash_money.doubleValue());
		}else{
			dest.writeDouble(0);
		}
	}
	public static final Creator<CashStatBean> CREATOR = new Creator<CashStatBean>() {
		
		@Override
		public CashStatBean[] newArray(int size) {
			return new CashStatBean[size];
		}
		
		@Override
		public CashStatBean createFromParcel(Parcel source) {
			CashStatBean bean = new CashStatBean();
			bean.task_cash_money = source.readDouble();
			bean.task_cash_rate = source.readString();
			bean.act_cash_money = source.readDouble();
			bean.act_cash_rate = source.readString();
			bean.team_cash_money = source.readDouble();
			bean.team_cash_rate = source.readString();
			bean.total_cash_money = source.readDouble();
			return bean;
		}
	};
}
