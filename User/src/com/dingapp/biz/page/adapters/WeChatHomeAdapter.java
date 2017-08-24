package com.dingapp.biz.page.adapters;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.page.swpiedelete.SwipeLayout;
import com.dingapp.biz.page.swpiedelete.SwipeLayout.OnSwipeStateChangeListener;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.imageloader.core.ImageLoader;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.DateUtils;

public class WeChatHomeAdapter extends BaseAdapter implements
		OnSwipeStateChangeListener {
	private Context context;
	private List<EMConversation> ormList;
	private ImageLoader imageLoader;
	private BaseFragment baseFragment;

	public WeChatHomeAdapter(BaseFragment baseFragment, Context context) {
		this.context = context;
		imageLoader = ImageLoader.getInstance();
		this.baseFragment = baseFragment;
	}

	public void setData(List<EMConversation> newList) {
		if (newList != null) {
			ormList = newList;
		}
		notifyDataSetChanged();
	}

	public void setMoreData(List<EMConversation> moreList) {
		if (moreList != null && moreList.size() > 0) {
			if (ormList != null) {
				ormList.addAll(moreList);
			} else {
				ormList = moreList;
			}
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ormList == null ? 0 : ormList.size();
	}

	@Override
	public EMConversation getItem(int arg0) {
		return ormList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(context, R.layout.wechat_home_item, null);
			holder.img_header = (ImageView) convertView
					.findViewById(R.id.img_header);
			holder.tv_chat_content = (TextView) convertView
					.findViewById(R.id.tv_chat_content);
			holder.tv_chat_username = (TextView) convertView
					.findViewById(R.id.tv_chat_username);
			holder.tv_chat_time = (TextView) convertView
					.findViewById(R.id.tv_chat_time);
			holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);
			holder.swipeLayout = (SwipeLayout) convertView
					.findViewById(R.id.swipeLayout);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.swipeLayout.setTag(position);
		holder.swipeLayout.setOnSwipeStateChangeListener(this);
		holder.tv_msg.setVisibility(View.GONE);
		EMConversation emConversation = ormList.get(position);
		String userName = emConversation.getUserName();

		if (emConversation.getUnreadMsgCount() > 0) {
			holder.tv_msg.setVisibility(View.VISIBLE);
			holder.tv_msg.setText(emConversation.getUnreadMsgCount() + "");
		}
		holder.tv_chat_time.setText(DateUtils.getTimestampString(new Date(
				emConversation.getLastMessage().getMsgTime())) + "");
		holder.tv_chat_content.setText(
				EaseSmileUtils.getSmiledText(
						context,
						EaseCommonUtils.getMessageDigest(
								emConversation.getLastMessage(), (context))),
				BufferType.SPANNABLE);
		EaseUser userInfo = EaseUserUtils.getUserInfo(userName, context);
		if (userInfo != null && userInfo.getAvatar() != null) {
			imageLoader.displayImage(
					EaseUserUtils.getUserInfo(userName, context).getAvatar(),
					holder.img_header);
		} else {
			holder.img_header.setImageResource(R.drawable.icon);
		}
		if (userInfo != null && userInfo.getNickName() != null) {
			holder.tv_chat_username.setText(userInfo.getNickName());
		} else {
			holder.tv_chat_username.setText(userName);
		}
		if (userName.equals("admin")) {
			holder.tv_chat_username.setText("葫芦红包官方客服");
		}
		return convertView;
	}

	private class ViewHolder {
		private SwipeLayout swipeLayout;
		private ImageView img_header;
		private TextView tv_msg;
		private TextView tv_chat_username;
		private TextView tv_chat_content;
		private TextView tv_chat_time;

	}

	@Override
	public void onOpen(Object tag) {

	}

	@Override
	public void onClose(Object tag) {

	}

	@Override
	public void delete(Object tag) {
		if (mContentListener != null) {
			mContentListener.deleteClick(ormList.get((Integer) tag));
		}
	}

	@Override
	public void contentClick(Object tag) {
		if (mContentListener != null) {
			mContentListener.onItemClick(ormList.get((Integer) tag));
		}
	}

	public interface ContentClickListener {
		void onItemClick(EMConversation conversation);

		void deleteClick(EMConversation conversation);
	}

	public void setContentClickListener(ContentClickListener contentListener) {
		this.mContentListener = contentListener;
	}

	private ContentClickListener mContentListener;
}
