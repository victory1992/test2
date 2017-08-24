package com.dingapp.biz.db.orm;

/**
 * 修改会员昵称、修改真实姓名、实名认证、收藏商品、取消收藏商品、查询商品收藏状态、新增地址、编辑地址、加入购物车、删除购物车
 * 取消订单、订单收货、评价订单、
 */
public class ModifyBean {
    private String statusCode;
    private String statusMsg;
    private DataEntity data;

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private String suc;

        public void setSuc(String suc) {
            this.suc = suc;
        }

        public String getSuc() {
            return suc;
        }
    }
}
