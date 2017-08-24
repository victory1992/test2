package com.dingapp.andriod.z20.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dingapp.biz.AppConstants;
import com.dingapp.biz.pay.weixin.WxConstants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "pb";

	// IWXAPI 是第三方app和微信通信的openapi接口
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		api = WXAPIFactory.createWXAPI(this, WxConstants.WX_APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		Log.d(TAG, "onPayFinish, resp.getType()  = " + resp.getType());
		Log.d("wx", "onPayFinish, resp.getType()  = " + resp.errStr);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int errCode = resp.errCode;
			switch (errCode) {
			case 0:
				Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
				break;
			case -1:
				Toast.makeText(this, "支付错误", Toast.LENGTH_SHORT).show();
				break;
			case -2:
				Toast.makeText(this, "用户取消", Toast.LENGTH_SHORT).show();
				break;

			}
			Intent intent = new Intent(AppConstants.WX_PAY_SUCCESS_ACTION);
			sendBroadcast(intent);
		}
		finish();
	}
}