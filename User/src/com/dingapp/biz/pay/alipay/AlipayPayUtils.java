package com.dingapp.biz.pay.alipay;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.util.Log;

/**
 * 支付宝支付方法
 * 
 * @author
 * 
 */
public class AlipayPayUtils {

	/**
	 * 获取完整订单参数
	 * 
	 * @param subject
	 *            商品
	 * @param body
	 *            商品描述
	 * @param price
	 *            价格
	 * @param trade_no
	 *            订单号
	 * @return
	 */
	public static String getOrderParameters(String subject, String body,
			String price, String trade_no) {
		String orderInfo = getOrderInfo(subject, body, price, trade_no);
		// 对订单做RSA 签名
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			Log.d("pb", "sign : " + sign);
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();
		return payInfo;

		// Runnable payRunnable = new Runnable() {
		// @Override
		// public void run() {
		// // 构造PayTask 对象
		// PayTask alipay = new PayTask(PayDemoActivity.this);
		// // 调用支付接口，获取支付结果
		// String result = alipay.pay(payInfo);
		//
		// Message msg = new Message();
		// msg.what = SDK_PAY_FLAG;
		// msg.obj = result;
		// mHandler.sendMessage(msg);
		// }
		// };
		//
		// // 必须异步调用
		// Thread payThread = new Thread(payRunnable);
		// payThread.start();

	}

	/**
	 * 创建订单信息
	 * 
	 * @param subject
	 *            商品名称
	 * @param body
	 *            商品的详细描述
	 * @param price
	 *            价格
	 * @param trade_no
	 *            订单号
	 * @return
	 */
	private static String getOrderInfo(String subject, String body,
			String price, String trade_no) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + AlipayConstants.PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + AlipayConstants.SELLER + "\"";

		// 商户网站唯一订单号
		// orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";
		orderInfo += "&out_trade_no=" + "\"" + trade_no + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ "http://115.159.68.180:8080/sdbt/notify_url.jsp" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\"m.alipay.com\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss",
				Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private static String sign(String content) {
		return SignUtils.sign(content, AlipayConstants.RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private static String getSignType() {
		return "sign_type=\"RSA\"";
	}
}
