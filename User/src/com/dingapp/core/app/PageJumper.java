package com.dingapp.core.app;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class PageJumper implements Parcelable {
	public int[] mAnimation;
	public boolean mNewActivity;
	public Bundle mArgs;
	public int mContainerId;

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		if (mAnimation == null || mAnimation.length == 0) {
			dest.writeInt(0);
		} else {
			dest.writeInt(mAnimation.length);
			dest.writeIntArray(mAnimation);
		}
		dest.writeInt(mNewActivity ? 1 : 0);
		dest.writeBundle(mArgs);
		dest.writeInt(mContainerId);
	}

	public static final Creator<PageJumper> CREATOR = new Creator<PageJumper>() {

		@Override
		public PageJumper createFromParcel(Parcel source) {
			PageJumper jmp = new PageJumper();
			int cnt = source.readInt();
			if (cnt > 0) {
				jmp.mAnimation = new int[cnt];
				source.readIntArray(jmp.mAnimation);
			}
			jmp.mNewActivity = (source.readInt() == 1);
			jmp.mArgs = source.readBundle();
			jmp.mContainerId = source.readInt();
			return jmp;
		}

		@Override
		public PageJumper[] newArray(int size) {
			return new PageJumper[size];
		}
	};
}
