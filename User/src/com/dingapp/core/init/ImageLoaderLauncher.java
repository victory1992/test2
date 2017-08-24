package com.dingapp.core.init;

import android.content.Context;

import com.dingapp.core.launcher.LauncherCallee;
import com.dingapp.imageloader.core.DisplayImageOptions;
import com.dingapp.imageloader.core.ImageLoader;
import com.dingapp.imageloader.core.ImageLoaderConfiguration.Builder;

public class ImageLoaderLauncher implements LauncherCallee {

	private DisplayImageOptions mDisplayOption;
	private Context mContext;

	public ImageLoaderLauncher(Context appContext, DisplayImageOptions op) {
		this.mContext = appContext;
		this.mDisplayOption = op;
	}

	@Override
	public boolean preInit() {
		Builder loaderBuilder = new Builder(mContext);
		loaderBuilder.denyCacheImageMultipleSizesInMemory();
		loaderBuilder.defaultDisplayImageOptions(mDisplayOption);
		ImageLoader.getInstance().init(loaderBuilder.build());
		return true;
	}

	@Override
	public boolean afterInit() {
		return true;
	}

}
