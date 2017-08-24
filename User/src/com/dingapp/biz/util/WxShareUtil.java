package com.dingapp.biz.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.dingapp.biz.AppConstants;
import com.dingapp.biz.pay.weixin.WxConstants;
import com.dingapp.core.util.UIUtil;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WxShareUtil {
	/**
	 * 微信分享
	 * 
	 * @param type
	 *            1为分享到好友，2为分享到朋友圈
	 */
	public static void share(final int type, final Context context,
			final String share_url, final String share_desc,
			final ImageView iv, final String title) {
		final IWXAPI wxApi = WXAPIFactory.createWXAPI(context,
				WxConstants.WX_APP_ID, true);
		wxApi.registerApp(WxConstants.WX_APP_ID);
		boolean isInstalled = wxApi.isWXAppInstalled();
		if (!isInstalled) {
			UIUtil.showToast(context, "未安装微信");
			return;
		}
		try {
			new Thread() {
				@Override
				public void run() {
					super.run();
					WXWebpageObject webpage = new WXWebpageObject();
					try {
						webpage.webpageUrl = share_url;
						// webpage.webpageUrl =
						// "http://www.dingapp.com/appzuo-release.action?appId=z8";
						WXMediaMessage msg = new WXMediaMessage(webpage);
						msg.title = title;
						msg.description = share_desc;
						BitmapDrawable d = (BitmapDrawable) iv.getDrawable();
						Bitmap bmp = d.getBitmap();
						if (bmp != null) {
							msg.thumbData = ImageUtils.getCompressBitmap(bmp,
									CompressFormat.JPEG); // 设置图片数据;
						}
						SendMessageToWX.Req req = new SendMessageToWX.Req();
						req.transaction = String
								.valueOf(System.currentTimeMillis());
						req.message = msg;
						if (type == 1) {
							req.scene = SendMessageToWX.Req.WXSceneSession;
						} else {
							req.scene = SendMessageToWX.Req.WXSceneTimeline;
						}
						wxApi.sendReq(req);
					} catch (Exception e) {
						UIUtil.showToast(context, "分享失败");
					}
					
				}
			}.start();
		} catch (Exception e) {
		}
	}

	// 微信授权登录
	public static void wxLogin(Context context) {
		final IWXAPI wxApi = WXAPIFactory.createWXAPI(context,
				WxConstants.WX_APP_ID, true);
		wxApi.registerApp(WxConstants.WX_APP_ID);
		boolean isInstalled = wxApi.isWXAppInstalled();
		if (!isInstalled) {
			UIUtil.showToast(context, "未安装微信");
			return;
		}
		try {
			new Thread() {
				@Override
				public void run() {
					super.run();
					SendAuth.Req send = new SendAuth.Req();
					send.scope = "snsapi_userinfo";
					send.state = AppConstants.member.getSessionId()+Math.random();
					wxApi.sendReq(send);
				}
			}.start();
		} catch (Exception e) {
		}
	}
}
