package com.dingapp.biz.hx;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.dingapp.andriod.z20.R;
import com.dingapp.core.util.UIUtil;

public class AddFriendDialog {
	private Dialog dialog;

	public AddFriendDialog(final Activity activity,
			final OnConfirmListener listener) {
		dialog = new Dialog(activity, R.style.dialog_style);
		View inflate = View.inflate(activity, R.layout.bbs_add_friend_dialog,
				null);
		final EditText et = (EditText) inflate.findViewById(R.id.et_input_add);
		final ImageView img = (ImageView) inflate.findViewById(R.id.img_delete);
		inflate.findViewById(R.id.tv_send).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						if (TextUtils.isEmpty(et.getText().toString())) {
							UIUtil.showToast(activity, "申请理由不能为空");
							return;
						}
						listener.click(et.getText().toString());
					}
				});
		inflate.findViewById(R.id.tv_cancle).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialog.dismiss();
					}
				});
		et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				String sb = arg0.toString();
				if(sb.length()>0){
					img.setVisibility(View.VISIBLE);
				}else{
					img.setVisibility(View.GONE);
				}
			}
		});
		img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				et.setText("");
			}
		});
		dialog.setContentView(inflate);
		dialog.setCanceledOnTouchOutside(true);
	}

	public void show() {
		dialog.show();
	}

	public void dismiss() {
		dialog.dismiss();
	}

	public interface OnConfirmListener {
		void click(String content);
	}
}
