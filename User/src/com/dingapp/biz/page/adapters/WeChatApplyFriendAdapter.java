package com.dingapp.biz.page.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.FriendApplyBean.DataEntity;
import com.dingapp.biz.db.orm.FriendApplyBean.DataEntity.DataPicEntity;
import com.dingapp.biz.page.swpiedelete.SwipeLayout;
import com.dingapp.biz.page.swpiedelete.SwipeLayout.OnSwipeStateChangeListener;
import com.dingapp.core.app.BaseFragment;

public class WeChatApplyFriendAdapter extends BaseAdapter implements
		OnSwipeStateChangeListener {
	private Context context;
	private List<DataEntity> ormList;
	private DeleteListener mDeleteListener;
	private BaseFragment basefragment;

	public WeChatApplyFriendAdapter(BaseFragment basefragment, Context context) {
		this.context = context;
		this.basefragment = basefragment;
	}

	public void setData(List<DataEntity> newList) {
		if (newList != null) {
			ormList = newList;
		}
		notifyDataSetChanged();
	}

	public void setMoreData(List<DataEntity> moreList) {
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
	public DataEntity getItem(int position) {
		return ormList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final DataEntity dateEntity = getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.wechat_apply_friend,
					null);
			holder.swipeLayout = (SwipeLayout) convertView
					.findViewById(R.id.swipeLayout);
			holder.img_header = (ImageView) convertView
					.findViewById(R.id.img_header);
			holder.tv_apply_status = (TextView) convertView
					.findViewById(R.id.tv_apply_status);
			holder.tv_apply_status_already = (TextView) convertView
					.findViewById(R.id.tv_apply_status_already);
			holder.tv_apply_status_refuse = (TextView) convertView
					.findViewById(R.id.tv_apply_status_refuse);
			holder.tv_apply_username = (TextView) convertView
					.findViewById(R.id.tv_apply_username);
			holder.tv_delete = (TextView) convertView
					.findViewById(R.id.tv_delete);
			holder.tv_chat_content = (TextView) convertView
					.findViewById(R.id.tv_chat_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_apply_status.setVisibility(View.GONE);
		holder.tv_apply_status_refuse.setVisibility(View.GONE);
		holder.tv_apply_status_already.setVisibility(View.GONE);
		if (dateEntity.getExamine_status().equals("pass")) {
			holder.tv_apply_status.setVisibility(View.GONE);
			holder.tv_apply_status_refuse.setVisibility(View.GONE);
			holder.tv_apply_status_already.setVisibility(View.VISIBLE);
			holder.tv_apply_status_already.setText("已添加");
		} else if (dateEntity.getExamine_status().equals("init")) {
			holder.tv_apply_status.setVisibility(View.VISIBLE);
			holder.tv_apply_status_refuse.setVisibility(View.VISIBLE);
			holder.tv_apply_status_already.setVisibility(View.GONE);
			holder.tv_apply_status.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (mCheckListener != null) {
						mCheckListener.accept(dateEntity.getApply_id());
					}
				}
			});
			holder.tv_apply_status_refuse
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (mCheckListener != null) {
								mCheckListener.refuse(dateEntity.getApply_id());
							}
						}
					});
		} else if (dateEntity.getExamine_status().equals("refuse")) {
			holder.tv_apply_status.setVisibility(View.GONE);
			holder.tv_apply_status_refuse.setVisibility(View.GONE);
			holder.tv_apply_status_already.setVisibility(View.VISIBLE);
			holder.tv_apply_status_already.setText("已拒绝");
		} else if (dateEntity.getExamine_status().equals("reversepass")) {
			holder.tv_apply_status.setVisibility(View.GONE);
			holder.tv_apply_status_refuse.setVisibility(View.GONE);
			holder.tv_apply_status_already.setVisibility(View.VISIBLE);
			holder.tv_apply_status_already.setText("已添加");
		}
		holder.swipeLayout.setTag(position);
		holder.swipeLayout.setOnSwipeStateChangeListener(this);
		holder.tv_apply_username.setText(dateEntity.getMember_nick_name());
		holder.tv_chat_content.setText(dateEntity.getApply_reason());
		DataPicEntity member_pic = dateEntity.getMember_pic();
		if (member_pic != null && member_pic.getOrigin_url() != null) {
			Glide.with(basefragment).load(member_pic.getOrigin_url())
					.diskCacheStrategy(DiskCacheStrategy.ALL)
					.error(R.drawable.ease_default_avatar)
					.into(holder.img_header);
		}
		return convertView;
	}

	private class ViewHolder {
		private ImageView img_header;
		private TextView tv_apply_username;
		private TextView tv_apply_status;
		private TextView tv_apply_status_already;
		private TextView tv_apply_status_refuse;
		private TextView tv_chat_content;
		private TextView tv_delete;
		private SwipeLayout swipeLayout;
	}

	@Override
	public void onOpen(Object tag) {

	}

	@Override
	public void onClose(Object tag) {

	}

	@Override
	public void delete(Object tag) {
		if (ormList != null) {
			DataEntity dataEntity = ormList.get((Integer) tag);
			if (mDeleteListener != null) {
				mDeleteListener.delete((Integer) tag, dataEntity.getApply_id());
			}
		}
	}

	public interface DeleteListener {
		void delete(int pos, int applyId);

		void clickItem(int member_id);
	}

	public void setDeleteListener(DeleteListener mDeleteListener) {
		this.mDeleteListener = mDeleteListener;
	}

	public void removePos(int pos) {
		if (pos == -1) {
			return;
		}
		ormList.remove(pos);
		notifyDataSetChanged();
	}

	@Override
	public void contentClick(Object tag) {
		if (ormList != null) {
			DataEntity dataEntity = ormList.get((Integer) tag);
			int member_id = dataEntity.getMember_id();
			if (mDeleteListener != null) {
				mDeleteListener.clickItem(member_id);
			}
		}
	}

	private CheckFriendStatusListener mCheckListener;

	public interface CheckFriendStatusListener {
		void refuse(int apply_id);

		void accept(int apply_id);
	}

	public void setCheckFriendStatus(CheckFriendStatusListener listener) {
		this.mCheckListener = listener;
	}
}
