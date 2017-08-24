package com.dingapp.biz.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.database.MergeCursor;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.dingapp.biz.db.orm.ImageBean;

/**
 * post请求
 * 
 * @author
 * 
 */
public class StringPostRequest extends StringRequest {

	private final static String TWO_HYPHENS = "--";
	private final static String LINE_END = "\r\n";
	// private final static String LINE_END =
	// System.getProperty("line.separator");
	private String boundary = "aladfnaljaljfnblja234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRnflnglajafljlajdlfjl";

	private final Listener<String> mListener;

	/**
	 * post请求参数
	 */
	private Map<String, String> postMap;

	/**
	 * 上传的图片
	 */
	private List<ImageBean> imageList;

	public StringPostRequest(Map<String, String> postMap, String url,
			Listener<String> listener, ErrorListener errorListener) {
		this(postMap, url, listener, errorListener, null);
	}

	public StringPostRequest(Map<String, String> postMap, String url,
			Listener<String> listener, ErrorListener errorListener,
			List<ImageBean> imageList) {
		super(Method.POST, url, listener, errorListener);
		mListener = listener;
		this.imageList = imageList;
		// 超时限制
		setRetryPolicy(new DefaultRetryPolicy(30000, 0, 1));
		this.postMap = postMap;
	}

	@Override
	protected void deliverResponse(String response) {
		try {
			JSONObject json = new JSONObject(response);
			if (json.getString("statusCode").equals("200")) {
				mListener.onResponse(response);
			} else {
				JSONObject responseHeader = new JSONObject();
				responseHeader.put("statusCode", json.getString("statusCode"));
				responseHeader.put("statusMsg", json.getString("statusMsg"));
				mListener.onResponse(responseHeader.toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getBodyContentType() {
		if (imageList == null || imageList.size() == 0) {
			return "application/x-www-form-urlencoded; charset="
					+ getParamsEncoding();
		} else {
			return "multipart/form-data; boundary=" + boundary;
		}
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		byte[] data = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			if (imageList == null && postMap != null && postMap.size() > 0) {
				byte[] postData = encodeParameters(postMap, getParamsEncoding());
				bos.write(postData);
			} else if (imageList != null && imageList.size() > 0) {
				if (postMap != null && postMap.size() > 0) {
					byte[] startBtye = (TWO_HYPHENS + boundary + LINE_END)
							.getBytes(getParamsEncoding());
					bos.write(startBtye);
					StringBuilder sb = new StringBuilder();
					for (Map.Entry<String, String> entry : postMap.entrySet()) {
						sb.append("Content-Disposition: form-data; name=\"")
								.append(URLEncoder.encode(entry.getKey(),
										getParamsEncoding())).append("\"")
								.append(LINE_END).append(LINE_END);
						sb.append(
								URLEncoder.encode(entry.getValue(),
										getParamsEncoding())).append(LINE_END);
						sb.append(TWO_HYPHENS).append(boundary)
								.append(LINE_END);
					}
					bos.write(sb.toString().getBytes(getParamsEncoding()));
				}
				byte[] bb = (TWO_HYPHENS + boundary + LINE_END)
						.getBytes(getParamsEncoding());
				bos.write(bb);
				int count = 0;
				for (ImageBean bean : imageList) {
					StringBuilder builder = new StringBuilder();
					builder.append("Content-Disposition: form-data; name=\"")
							.append(URLEncoder.encode(bean.getName(),
									getParamsEncoding()))
							.append("\"; filename=\"")
							.append(URLEncoder.encode(bean.getFileName(),
									getParamsEncoding())).append("\"")
							.append(LINE_END);
					builder.append("Content-Type: ")
							.append("application/octet-stream")
							.append(LINE_END);
					builder.append(LINE_END);
					byte[] buffer = builder.toString().getBytes(
							getParamsEncoding());
					bos.write(buffer);
					bos.write(bean.getData());
					count++;
					if (count < imageList.size()) {
						StringBuilder imageSb = new StringBuilder();
						imageSb.append(LINE_END).append(TWO_HYPHENS)
								.append(boundary).append(LINE_END);
						bos.write(imageSb.toString().getBytes(
								getParamsEncoding()));
					}
				}
				StringBuilder endSb = new StringBuilder();
				endSb.append(LINE_END).append(TWO_HYPHENS).append(boundary)
						.append(TWO_HYPHENS).append(LINE_END);
				bos.write(endSb.toString().getBytes(getParamsEncoding()));

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			data = bos.toByteArray();
			bos.flush();
			bos.close();
		} catch (Exception e) {
		}

		return data;
	}

	/**
	 * Converts <code>params</code> into an application/x-www-form-urlencoded
	 * encoded string.
	 */
	private byte[] encodeParameters(Map<String, String> params,
			String paramsEncoding) {
		StringBuilder encodedParams = new StringBuilder();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				encodedParams.append(URLEncoder.encode(entry.getKey(),
						paramsEncoding));
				encodedParams.append('=');
				encodedParams.append(URLEncoder.encode(entry.getValue(),
						paramsEncoding));
				encodedParams.append('&');
			}
			return encodedParams.toString().getBytes(paramsEncoding);
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: "
					+ paramsEncoding, uee);
		}
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return Collections.emptyMap();
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		return Response.success(parsed,
				HttpHeaderParser.parseCacheHeaders(response));
	}

}
