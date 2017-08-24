package com.dingapp.andriod.z20.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.pay.weixin.WxConstants;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, WxConstants.WX_APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		setIntent(intent);
		api.handleIntent(intent, this);
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq req) {

	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		Log.d("wx", "onPayFinish, errCode = " + resp.errCode);
		Log.d("wx", resp.toString());
		finish();
		int result = 0;

		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			if (resp instanceof SendMessageToWX.Resp) {
				Editor edit = getSharedPreferences("wx_callback",
						Context.MODE_PRIVATE).edit();
				edit.putString("wx_callback", "true");
				edit.commit();
			}
			if (resp instanceof SendAuth.Resp) {
				Editor edit = getSharedPreferences("wx_login",
						Context.MODE_PRIVATE).edit();
				edit.putString("wx_login", ((SendAuth.Resp) resp).code);
				edit.commit();
			}

			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}

		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
	}
}