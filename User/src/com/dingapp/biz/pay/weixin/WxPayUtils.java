//package com.dingapp.biz.pay.weixin;
//
//import java.util.LinkedList;
//import java.util.List;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//
//import android.util.Log;
//
//import com.dingapp.biz.pay.LogUtils;
//import com.tencent.mm.sdk.modelpay.PayReq;
//import com.tencent.mm.sdk.openapi.IWXAPI;
//
///**
// * 微信支付
// * 
// * @author
// * 
// */
//public class WxPayUtils {
//	// TODO 现在未写获取token的方法
//
//	private static final String TAG = "pb";
//
//	/**
//	 * @param body
//	 *            商品描述
//	 * @param nonce_str
//	 *            32 位内的随机串，防重发
//	 * @param notify_url
//	 *            通知URL
//	 * @param out_trade_no
//	 *            商户订单号
//	 * @param total_fee
//	 *            订单总金额
//	 * @return
//	 */
//	public String genProductArgs(String body, String notify_url,
//			String nonce_str, String out_trade_no, String total_fee) {
//		try {
//			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
//			packageParams.add(new BasicNameValuePair("appid",
//					WxConstants.WX_APP_ID));
//			packageParams.add(new BasicNameValuePair("body", body));
//			packageParams.add(new BasicNameValuePair("mch_id",
//					WxConstants.PARTNER_ID));
//			packageParams.add(new BasicNameValuePair("nonce_str", nonce_str));
//			packageParams.add(new BasicNameValuePair("notify_url", notify_url));
//			packageParams.add(new BasicNameValuePair("out_trade_no",
//					out_trade_no));
//			packageParams.add(new BasicNameValuePair("spbill_create_ip",
//					"127.0.0.1"));
//			packageParams.add(new BasicNameValuePair("total_fee", "1"));
//			packageParams.add(new BasicNameValuePair("trade_type", "APP"));
//			String sign = genPackageSign(packageParams);
//			packageParams.add(new BasicNameValuePair("sign", sign));
//			String xmlstring = toXml(packageParams);
//			return xmlstring;
//		} catch (Exception e) {
//			Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
//			return null;
//		}
//	}
//
//	/**
//	 * 生成package签名
//	 */
//	private String genPackageSign(List<NameValuePair> params) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < params.size(); i++) {
//			sb.append(params.get(i).getName());
//			sb.append('=');
//			sb.append(params.get(i).getValue());
//			sb.append('&');
//		}
//		sb.append("key=");
//		sb.append(WxConstants.API_KEY);
//		String packageSign = MD5.getMessageDigest(sb.toString().getBytes())
//				.toUpperCase();
//		return packageSign;
//	}
//
//	/**
//	 * 生成xml数据
//	 * 
//	 * @param params
//	 * @return
//	 */
//	private String toXml(List<NameValuePair> params) {
//		StringBuilder sb = new StringBuilder();
//		sb.append("<xml>");
//		for (int i = 0; i < params.size(); i++) {
//			sb.append("<" + params.get(i).getName() + ">");
//			sb.append(params.get(i).getValue());
//			sb.append("</" + params.get(i).getName() + ">");
//		}
//		sb.append("</xml>");
//		LogUtils.d(TAG, sb.toString());
//		return sb.toString();
//	}
//
//	/**
//	 * 发送微信请求
//	 * 
//	 * @param prepay_id
//	 *            预支付ID
//	 * @param nonceStr
//	 *            随机字符串，不长于32位
//	 * @param timeStamp
//	 * @param api
//	 */
//	public void sendPayReq(String prepay_id, String nonceStr, String timeStamp,
//			IWXAPI api) {
//		PayReq req = new PayReq();
//		req.appId = WxConstants.WX_APP_ID;
//		req.partnerId = WxConstants.PARTNER_ID;
//		req.prepayId = prepay_id;
//		req.packageValue = "prepay_id=" + prepay_id;
//		req.nonceStr = nonceStr;
//		req.timeStamp = timeStamp;
//		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
//		signParams.add(new BasicNameValuePair("appid", req.appId));
//		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
//		signParams.add(new BasicNameValuePair("package", req.packageValue));
//		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
//		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
//		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
//		req.sign = genAppSign(signParams);
//		LogUtils.d("d", "调起支付的package串：" + req.packageValue);
//		// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//		LogUtils.d("pb", "调起微信支付");
//		api.sendReq(req);
//	}
//
//	/**
//	 * 生成调起App签名
//	 * 
//	 * @param params
//	 * @return
//	 */
//	private String genAppSign(List<NameValuePair> params) {
//		StringBuilder sb = new StringBuilder();
//		for (int i = 0; i < params.size(); i++) {
//			sb.append(params.get(i).getName());
//			sb.append('=');
//			sb.append(params.get(i).getValue());
//			sb.append('&');
//		}
//		sb.append("key=");
//		sb.append(WxConstants.API_KEY);
//		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
//		return appSign;
//	}
//
// }
