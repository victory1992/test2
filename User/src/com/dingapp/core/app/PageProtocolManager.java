package com.dingapp.core.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class PageProtocolManager {
	static HashMap<String, JSONObject> spageRouter = new HashMap<String, JSONObject>();
	private static HashMap<String, Class<?>> mFrgTable = new HashMap<String, Class<?>>();

	public static BaseFragment translatePageNameToFrg(String pageName,
			String jsonParams) {
		JSONObject targetFrgInfor = getConfigureMap().get(pageName);
		Class<?> clz = null;
		BaseFragment frg = null;
		String type = "";
		try {
			type = targetFrgInfor.getString("Type");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		try {
			if (mFrgTable.containsKey(pageName)) {
				clz = mFrgTable.get(pageName);
			} else {
				clz = Class.forName(targetFrgInfor.getString("Android"));
				mFrgTable.put(pageName, clz);
			}
			frg = (BaseFragment) clz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return frg;
	}

	public static void initConfigureFile() {
		StringBuilder sb = new StringBuilder();
		try {
			InputStream is = Application.getInstance().getAssets()
					.open("configure.conf");
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while (!TextUtils.isEmpty(line = br.readLine())) {
				sb.append(line);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			JSONObject jsonObj = new JSONObject(sb.toString());
			Iterator<String> iter = jsonObj.keys();
			while (iter.hasNext()) {
				String key = iter.next();
				JSONObject valueJson = jsonObj.getJSONObject(key);
				spageRouter.put(key, valueJson);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	public static HashMap<String, JSONObject> getConfigureMap() {
		synchronized (PageProtocolManager.class) {
			if (spageRouter == null || spageRouter.size() <= 0) {
				initConfigureFile();
			}
		}
		return spageRouter;
	}
}
