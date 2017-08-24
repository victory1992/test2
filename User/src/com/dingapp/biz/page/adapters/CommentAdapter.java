package com.dingapp.biz.page.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dingapp.andriod.z20.R;
import com.dingapp.biz.db.orm.CommentBean;
import com.dingapp.biz.page.PictureLookFragment;
import com.dingapp.biz.util.ImageViewHWutils;
import com.dingapp.commonui.widget.CircleImageView;
import com.dingapp.core.app.BaseFragment;
import com.dingapp.imageloader.core.ImageLoader;

public class CommentAdapter extends BaseAdapter {
	private Context context;
	private List<CommentBean> mList = new ArrayList<CommentBean>();
	private ArrayList<String> list = new ArrayList<String>();
	private BaseFragment fg;
	private ImageLoader imageLoader;

	public CommentAdapter(Context context, BaseFragment fg) {
		super();
		this.context = context;
		this.fg = fg;
		imageLoader = ImageLoader.getInstance();
	}

	public void setData(List<CommentBean> data) {
		if (data != null && data.size() > 0) {
			mList.clear();
			mList.addAll(data);
		}
		notifyDataSetInvalidated();
	}

	public void setMoreData(List<CommentBean> moreData) {
		if (moreData != null && moreData.size() > 0) {
			mList.addAll(moreData);
		}
		notifyDataSetInvalidated();
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		final CommentBean commentBean = mList.get(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.shop_prds_list_item,
					null);
			holder.img_1 = (ImageView) convertView.findViewById(R.id.img_1);
			holder.img_2 = (ImageView) convertView.findViewById(R.id.img_2);
			holder.img_3 = (ImageView) convertView.findViewById(R.id.img_3);
			holder.img_4 = (ImageView) convertView.findViewById(R.id.img_4);
			holder.tv_user_name = (TextView) convertView
					.findViewById(R.id.tv_user_name);
			holder.tv_comment_time = (TextView) convertView
					.findViewById(R.id.tv_comment_time);
			holder.tv_comment_detail = (TextView) convertView
					.findViewById(R.id.tv_comment_detail);
			holder.rb_goods_comment = (RatingBar) convertView
					.findViewById(R.id.rb_goods_comment);
			holder.img_user_header = (CircleImageView) convertView
					.findViewById(R.id.img_user_header);
			holder.ll_imgs = (LinearLayout) convertView
					.findViewById(R.id.ll_imgs);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_user_name.setText(commentBean.getOwner_nick_name());
		holder.tv_comment_detail.setText(commentBean.getContent());
		holder.tv_comment_time.setText(commentBean.getCreate_time());
		holder.rb_goods_comment.setRating(commentBean.getScore());

		imageLoader.displayImage(commentBean.getOwner_header().getDetail_url()
				+ ImageViewHWutils.getWHImageVIew(200, 200),
				holder.img_user_header);
		holder.img_1.setVisibility(View.GONE);
		holder.img_2.setVisibility(View.GONE);
		holder.img_3.setVisibility(View.GONE);
		holder.img_4.setVisibility(View.GONE);
		holder.ll_imgs.setVisibility(View.GONE);
		if (commentBean.getComment_pics() != null) {
			if (commentBean.getComment_pics().size() < 1) {
				holder.img_1.setVisibility(View.GONE);
				holder.img_2.setVisibility(View.GONE);
				holder.img_3.setVisibility(View.GONE);
				holder.img_4.setVisibility(View.GONE);
				holder.ll_imgs.setVisibility(View.GONE);
			} else if (commentBean.getComment_pics().size() == 1) {
				holder.ll_imgs.setVisibility(View.VISIBLE);
				holder.img_1.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(0)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_1);
				holder.img_2.setVisibility(View.GONE);
				holder.img_3.setVisibility(View.GONE);
				holder.img_4.setVisibility(View.GONE);

			} else if (commentBean.getComment_pics().size() == 2) {
				holder.ll_imgs.setVisibility(View.VISIBLE);
				holder.img_1.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(0)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_1);
				holder.img_2.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(1)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_2);
				holder.img_3.setVisibility(View.GONE);
				holder.img_4.setVisibility(View.GONE);

			} else if (commentBean.getComment_pics().size() == 3) {
				holder.ll_imgs.setVisibility(View.VISIBLE);
				holder.img_1.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(0)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_1);
				holder.img_2.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(1)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_2);
				holder.img_3.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(2)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_3);
				holder.img_4.setVisibility(View.GONE);

			} else if (commentBean.getComment_pics().size() == 4) {
				holder.ll_imgs.setVisibility(View.VISIBLE);
				holder.img_1.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(0)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_1);
				holder.img_2.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(1)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_2);
				holder.img_3.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(2)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_3);
				holder.img_4.setVisibility(View.VISIBLE);
				imageLoader.displayImage(commentBean.getComment_pics().get(3)
						.getDetail_url()
						+ ImageViewHWutils.getWHImageVIew(200, 200),
						holder.img_4);

			}
		}
		holder.itemPosition = position;
		holder.img_1.setTag(holder);
		holder.img_1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将当前位置和图片集合传递过去
				Bundle bundle = new Bundle();

				int itmPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itmPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itmPosition == -1) {
					return;
				}
				CommentBean bean = (CommentBean) getItem(itmPosition);
				if (bean.getComment_pics() != null) {
					list.clear();
					for (int i = 0; i < bean.getComment_pics().size(); i++) {
						if (bean.getComment_pics().get(i) != null) {
							String url = bean.getComment_pics().get(i)
									.getDetail_url();
							list.add(url);
						}
					}
				}
				bundle.putInt(PictureLookFragment.FLAG,
						PictureLookFragment.BBSPICS_FLAG);
				bundle.putInt("pic_index", 0);
				bundle.putStringArrayList(PictureLookFragment.BBSPICS, list);
				fg.openPage("picture_look", bundle, false);
			}
		});
		holder.img_2.setTag(holder);
		holder.img_2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将当前位置和图片集合传递过去
				Bundle bundle = new Bundle();
				int itmPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itmPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itmPosition == -1) {
					return;
				}
				CommentBean bean = (CommentBean) getItem(itmPosition);
				if (bean.getComment_pics() != null) {
					list.clear();
					for (int i = 0; i < bean.getComment_pics().size(); i++) {
						if (bean.getComment_pics().get(i) != null) {
							String url = bean.getComment_pics().get(i)
									.getDetail_url();
							list.add(url);
						}
					}
				}
				bundle.putInt(PictureLookFragment.FLAG,
						PictureLookFragment.BBSPICS_FLAG);
				bundle.putInt("pic_index", 0);
				bundle.putStringArrayList(PictureLookFragment.BBSPICS, list);
				fg.openPage("picture_look", bundle, false);
			}
		});
		holder.img_3.setTag(holder);
		holder.img_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将当前位置和图片集合传递过去
				Bundle bundle = new Bundle();
				int itmPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itmPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itmPosition == -1) {
					return;
				}
				CommentBean bean = (CommentBean) getItem(itmPosition);
				if (bean.getComment_pics() != null) {
					list.clear();
					for (int i = 0; i < bean.getComment_pics().size(); i++) {
						if (bean.getComment_pics().get(i) != null) {
							String url = bean.getComment_pics().get(i)
									.getDetail_url();
							list.add(url);
						}
					}
				}
				bundle.putInt(PictureLookFragment.FLAG,
						PictureLookFragment.BBSPICS_FLAG);
				bundle.putInt("pic_index", 0);
				bundle.putStringArrayList(PictureLookFragment.BBSPICS, list);
				fg.openPage("picture_look", bundle, false);
			}
		});
		holder.img_4.setTag(holder);
		holder.img_4.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 将当前位置和图片集合传递过去
				Bundle bundle = new Bundle();
				int itmPosition = -1;
				Object tag = v.getTag();
				if (tag instanceof ViewHolder) {
					itmPosition = ((ViewHolder) tag).itemPosition;
				}
				if (itmPosition == -1) {
					return;
				}
				CommentBean bean = (CommentBean) getItem(itmPosition);
				if (bean.getComment_pics() != null) {
					list.clear();
					for (int i = 0; i < bean.getComment_pics().size(); i++) {
						if (bean.getComment_pics().get(i) != null) {
							String url = bean.getComment_pics().get(i)
									.getDetail_url();
							list.add(url);
						}
					}
				}
				bundle.putInt(PictureLookFragment.FLAG,
						PictureLookFragment.BBSPICS_FLAG);
				bundle.putInt("pic_index", 0);
				bundle.putStringArrayList(PictureLookFragment.BBSPICS, list);
				fg.openPage("picture_look", bundle, false);
			}
		});
		return convertView;
	}

	private class ViewHolder {
		private TextView tv_user_name;
		private TextView tv_comment_time;
		private TextView tv_comment_detail;
		private RatingBar rb_goods_comment;
		private CircleImageView img_user_header;
		private LinearLayout ll_imgs;
		private ImageView img_1;
		private ImageView img_2;
		private ImageView img_3;
		private ImageView img_4;
		private int itemPosition;
	}

}
