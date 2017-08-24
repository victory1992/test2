//package com.dingapp.biz.pay.weixin;
//
//import java.io.StringReader;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.xmlpull.v1.XmlPullParser;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//import android.util.Xml;
//import android.widget.Toast;
//
//import com.tencent.mm.sdk.openapi.IWXAPI;
//
//public class GetPrepayIdTask extends
//		AsyncTask<String, Void, Map<String, String>> {
//
//	private IWXAPI api;
//	private Context context;
//	private Dialog dialog;
//	private String timeStamp;
//	private WxPayUtils payUtils;
//
//	/**
//	 * 生成预支付订单的url
//	 */
//	private static final String GEN_PRE_PAY = "https://api.mch.weixin.qq.com/pay/unifiedorder";
//
//	public GetPrepayIdTask(IWXAPI api, Context context, Dialog dialog,
//			String timeStamp) {
//		super();
//		this.api = api;
//		this.context = context;
//		this.dialog = dialog;
//		this.timeStamp = timeStamp;
//		payUtils = new WxPayUtils();
//	}
//
//	@Override
//	protected void onPreExecute() {
//	}
//
//	@Override
//	protected Map<String, String> doInBackground(String... params) {
//		String entity = payUtils.genProductArgs(params[0], params[1],
//				params[2], params[3], params[4]);
//		byte[] buf = Util.httpPost(GEN_PRE_PAY, entity);
//		String content = new String(buf);
//		Log.e("orion", content);
//		Map<String, String> xml = decodeXml(content);
//		return xml;
//	}
//
//	@Override
//	protected void onPostExecute(Map<String, String> result) {
//		if (dialog != null) {
//			dialog.dismiss();
//		}
//		if (result == null) {
//			Toast.makeText(context, "支付未完成", Toast.LENGTH_LONG).show();
//			return;
//		}
//		String return_code = result.get("return_code");
//		String return_msg = result.get("return_msg");
//		if (return_code.equals("SUCCESS")) {
//			String result_code = result.get("result_code");
//			String err_code_des = result.get("err_code_des");
//			if (result_code.equals("SUCCESS")) {
//				String prepay_id = result.get("prepay_id");
//				String nonce_str = result.get("nonce_str");
//				payUtils.sendPayReq(prepay_id, nonce_str, timeStamp, api);
//			} else {
//				Toast.makeText(context, err_code_des, Toast.LENGTH_LONG).show();
//			}
//		} else {
//			Toast.makeText(context, return_msg, Toast.LENGTH_LONG).show();
//		}
//	}
//
//	public Map<String, String> decodeXml(String content) {
//		try {
//			Map<String, String> xml = new HashMap<String, String>();
//			XmlPullParser parser = Xml.newPullParser();
//			parser.setInput(new StringReader(content));
//			int event = parser.getEventType();
//			while (event != XmlPullParser.END_DOCUMENT) {
//				String nodeName = parser.getName();
//				switch (event) {
//				case XmlPullParser.START_DOCUMENT:
//					break;
//				case XmlPullParser.START_TAG:
//					if ("xml".equals(nodeName) == false) {
//						// 实例化student对象
//						xml.put(nodeName, parser.nextText());
//					}
//					break;
//				case XmlPullParser.END_TAG:
//					break;
//				}
//				event = parser.next();
//			}
//			return xml;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//
//	}
// }
