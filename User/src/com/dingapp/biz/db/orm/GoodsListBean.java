package com.dingapp.biz.db.orm;

import java.util.List;

/**
 * 商品列表
 */
public class GoodsListBean {
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
        private GoodsTypeBean type_info;
        private GoodsChildTypeBean child_type_info;
        private String sort;
        private List<PrdsEntity> goods_info;
        public GoodsTypeBean getType_info() {
			return type_info;
		}
		public void setType_info(GoodsTypeBean type_info) {
			this.type_info = type_info;
		}
		public GoodsChildTypeBean getChild_type_info() {
			return child_type_info;
		}
		public void setChild_type_info(GoodsChildTypeBean child_type_info) {
			this.child_type_info = child_type_info;
		}
		public String getSort() {
			return sort;
		}
		public void setSort(String sort) {
			this.sort = sort;
		}
		public List<PrdsEntity> getGoods_info() {
			return goods_info;
		}
		public void setGoods_info(List<PrdsEntity> goods_info) {
			this.goods_info = goods_info;
		}
		public static class PrdsEntity {
			private int goods_id;
			private String goods_name;
			private ImageBigAndMinBean goods_pic;
			private Double goods_price;
			private int pay_cnt;
			public int getGoods_id() {
				return goods_id;
			}
			public void setGoods_id(int goods_id) {
				this.goods_id = goods_id;
			}
			public String getGoods_name() {
				return goods_name;
			}
			public void setGoods_name(String goods_name) {
				this.goods_name = goods_name;
			}
			public ImageBigAndMinBean getGoods_pic() {
				return goods_pic;
			}
			public void setGoods_pic(ImageBigAndMinBean goods_pic) {
				this.goods_pic = goods_pic;
			}
			public Double getGoods_price() {
				return goods_price;
			}
			public void setGoods_price(Double goods_price) {
				this.goods_price = goods_price;
			}
			public int getPay_cnt() {
				return pay_cnt;
			}
			public void setPay_cnt(int pay_cnt) {
				this.pay_cnt = pay_cnt;
			}
		}
    }
}
