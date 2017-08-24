package com.dingapp.biz;

import java.util.List;

import com.dingapp.core.db.orm.Member;

public class AppConstants {

	/**
	 * 是否是调试模式,发布上线时，一定要改为false
	 */
	public static boolean TEST_MODE = false;
	public static String configStrings = "qichengyi://xx?param={'key':'value'}";
	public static String EXIT_ACTION = "com.xxx.hulu.exit";
	public static String WX_SHARE_ACTION = "com.xxx.hulu.exit";
	public static String KEY = "key";
	// 是否是vip会员
	public static boolean isVipMember = false;
	public static final String DINGAPP = "hulu.db";
	public static Member member;
	public static List<String> urlList;
	// 正式
	public static String BaseUrl = "http://101.200.161.231:8280";
	// public static String BaseUrl = "http://123.57.12.28:8280";
	public static String PLATFORM = "android";
	public static String NetNotifice = "网络好像断掉了，请检查...";
	/**
	 * 微信支付成功的广播
	 */
	public static String WX_PAY_SUCCESS_ACTION = "com.dingapp.z20.wx_pay_success";
	/**
	 * 更新客户端
	 */
	public static String newset_app = "/api/v1/app/newest_app";
	public static String send_valid_code = "/api/v1/member/send_valid_code";
	public static String login = "/api/v1/member/login";
	public static String logout = "/api/v1/member/logout";
	public static String upload_file = "/api/v1/app/upload_file";
	public static String member_wx_auth = "/api/v1/member/wx_auth";
	public static String member_alipay_auth = "/api/v1/member/alipay_auth";
	public static String withdraw_cash_rec = "/api/v1/member/withdraw_cash_rec";
	public static String withdraw_wx = "/api/v1/member/wx_withdraw_cash";
	public static String withdraw_zfb = "/api/v1/member/ali_withdraw_cash";
	public static String newest_app = "/api/v1/app/newest_app";
	public static String feedback = "/api/v1/app/feedback";
	public static String del_msg = "/api/v1/app/del_msg";
	public static String collect_data = "/api/v1/adv/collect_data";
	/**
	 * 搜索查询好友
	 */
	public static String query_friends = "/api/v1/member/friend_member_query";
	/**
	 * 添加好友
	 */
	public static String bbs_addfriend = "/api/v1/member/friend_apply";
	/**
	 * 好友审核列表
	 */
	public static String bbs_friendlist_auditing = "/api/v1/member/friend_examine_list";
	/**
	 * 审核
	 */
	public static String bbs_auditing = "/api/v1/member/examine_friend";
	/**
	 * 删除好友
	 */
	public static String friend_delete = "/api/v1/member/delete_friend";
	/**
	 * 修改备注
	 */
	public static String friend_beizhu = "/api/v1/member/remark_modify";
	/**
	 * 订单列表
	 */
	public static String ORDER_LIST = "/api/v1/order/order_list";
	/**
	 * 订单详情
	 */
	public static String ORDER_DETAIL = "/api/v1/goods/order_detail";
	/**
	 * 查询会员资料
	 */
	public static String friend_query_info = "/api/v1/member/query_member_profile";
	/**
	 * 收益首页
	 */
	public static String distributor_info = "/api/v1/member/distributor_info";
	public static String income_prj_stat_list = "/api/v1/income/prj_stat_list";
	/**
	 * 首页数据
	 */
	public static String goods_idx = "/api/v1/goods/idx";
	/**
	 * 分类列表
	 */
	public static String category_list = "/api/v1/goods/type_list";
	/**
	 * 商品列表
	 */
	public static String prd_list = "/api/v1/goods/goods_list";
	/**
	 * 商品详情
	 */
	public static String prd_details = "/api/v1/goods/goods_detail";
	/**
	 * 积分商品详情
	 */
	public static String jifen_prd_details = "/api/v1/goods/score_goods_detail";

	/**
	 * 收藏商品
	 */
	public static String favorite_prd = "/api/v1/goods/collect_goods";
	/**
	 * 取消收藏商品
	 */
	public static String cancel_favorite_prd = "/api/v1/goods/cancel_collect_goods";
	/**
	 * 查询商品收藏状态
	 */
	public static String query_favorite_prd = "/api/v1/member/query_favorite_prd";
	/**
	 * 加入购物车
	 */
	public static String add_cart = "/api/v1/goods/add_cart";
	/**
	 * 购物车数量修改
	 */
	public static String cart_modify = "/api/v1/goods/cart_modify";
	/**
	 * 用户端评价列表
	 */
	public static final String evaluate_list = "/api/v1/goods/evaluate_list";
	/**
	 * 保存用户端订单
	 */
	public static final String order_save = "/api/v1/goods/order_save";
	/**
	 * 商品搜索记录
	 */
	public static String goods_search_history = "/api/v1/goods/search_history";
	/**
	 * 商品清除搜索记录
	 */
	public static String goods_delete_search_history = "/api/v1/goods/delete_search_history";
	/**
	 * 购物车列表
	 */
	public static String cart_list = "/api/v1/goods/cart_list";
	/**
	 * 购物车删除
	 */
	public static String cart_delete = "/api/v1/goods/cart_delete";
	/**
	 * 支付接口
	 */
	public static String pay_order = "/api/v1/order/pay_order";
	/**
	 * 查询物流
	 */
	public static String logistics_detail = "/api/v1/goods/logistics_detail";
	/**
	 * 优惠券列表
	 */
	public static String coupon_list = "/api/v1/goods/coupon_list";
	/**
	 * 无分页优惠券列表
	 */
	public static String coupon_list_nopage = "/api/v1/goods/coupon_list_no_page";
	/**
	 * 匹配的商家列表
	 * 
	 */
	public static String merchant_match = "/api/v1/merchant/match";
	/**
	 * 物流公司列表
	 */
	public static String express_list = "/api/v1/goods/express_list";
	/**
	 * 编辑地址
	 */
	public static final String ADDRESS_EDIT = BaseUrl
			+ "/api/v1/member/address_update";

	/**
	 * 添加地址
	 */
	public static final String ADDRESS_ADD = BaseUrl
			+ "/api/v1/member/address_add";
	/**
	 * 删除地址
	 */
	public static final String ADDRESS_DELETE = BaseUrl
			+ "/api/v1/member/address_delete";
	public static String member_dst_team = "/api/v1/member/dst_team";
	public static String member_rank_list = "/api/v1/member/rank_list";
	/**
	 * 优惠券列表信息
	 */
	public static final String COUPON_LIST = BaseUrl
			+ "/api/v1/goods/coupon_list";
	/**
	 * 我的收藏商品
	 */
	public static String collect_goods = "/api/v1/member/collect_goods";
	/**
	 * 确认收货
	 */
	public static String order_receive = "/api/v1/goods/order_confirm_receipt";
	/**
	 * 订单评价
	 */
	public static String evaluate_add = "/api/v1/order/evaluate_add";
	/**
	 * 会员中心多项查询
	 */
	public static String member_infos = "/api/v1/member/idx";
	/**
	 * 支付宝提现
	 */
	public static String ali_withdraw_cash = "/api/v1/member/ali_withdraw_cash";
	/**
	 * 积分首页数据
	 */
	public static String score_goods_idx = "/api/v1/goods/score_idx";
	/**
	 * 保存用户端积分订单
	 */
	public static final String jifen_order_save = "/api/v1/order/score_save";
	/**
	 * 积分列表
	 */
	public static final String jifen_list = "/api/v1/member/score_list";
	/**
	 * 修改头像
	 */
	public static String modify_profile = "/api/v1/member/header_modification";
	/**
	 * 修改性别
	 */
	public static String gender_modification = "/api/v1/member/gender_modification";
	/**
	 * 修改昵称
	 */
	public static String nickname_modification = "/api/v1/member/nickname_modification";
	/**
	 * 修改生日
	 */
	public static String birth_modification = "/api/v1/member/birth_modification";
	/**
	 * 取消订单
	 */
	public static String cancel_order = "/api/v1/goods/order_cancel";
	/**
	 * 申请退款
	 */
	public static String apply_return = "/api/v1/order/order_return";
	/**
	 * 请求环信好友信息
	 */
	public static String hx_infos = "/api/v1/member/hx_infos";
	/**
	 * 请求信息
	 */
	public static String msg_list = "/api/v1/app/msg_list";
	/**
	 * 请积分商品
	 */
	public static String score_goods_list = "/api/v1/goods/score_goods_list";
	/**
	 * 是否有系统消息
	 */
	public static String has_new_msg = "/api/v1/app/has_new_msg";
}
