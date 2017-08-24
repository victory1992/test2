package com.dingapp.biz.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class GetAllAppsUtils {
	public static List<PackageInfo> getAllApps(Context context) {

		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> packlist = pManager.getInstalledPackages(0);
		for (int i = 0; i < packlist.size(); i++) {
			PackageInfo pak = (PackageInfo) packlist.get(i);

			// 判断是否为非系统预装的应用程序
			// 这里还可以添加系统自带的，这里就先不添加了，如果有需要可以自己添加
			// if()里的值如果<=0则为自己装的程序，否则为系统工程自带
			if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 添加自己已经安装的应用程序
				apps.add(pak);
			}

		}
		return apps;
	}
	public static String getAppNames(Context context){
		StringBuilder sb = new StringBuilder();
		try {
			PackageManager pManager = context.getPackageManager();
			List<PackageInfo> apps = getAllApps(context);
			for(int i = 0;i<apps.size();i++){
				sb.append(pManager.getApplicationLabel(apps.get(i).applicationInfo).toString()+",");
			}
		} catch (Exception e) {
			return "获取应用列表失败";
		}
	
		return sb.toString();
	}
}
