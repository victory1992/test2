package com.dingapp.core.launcher;

import java.util.LinkedList;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.dingapp.core.util.ThreadUtil;

public class LauncherAgent implements android.os.Handler.Callback {
	private static final String S_AGENT_THREAD_NAME = "agent_thread";
	private static final int toUIMessage = 0;
	private boolean mPreInited;

	private LauncherAgent() {
		mPreInited = false;
	}

	private static class LoaderLoacker {
		private static LauncherAgent sInstance = new LauncherAgent();
	}

	public static LauncherAgent getInstance() {
		return LoaderLoacker.sInstance;
	}

	LinkedList<Job> mJobs = new LinkedList<Job>();
	private HandlerThread mAgentThread;
	private Handler mAgentHandler;
	private Handler mUIHandler;
	protected boolean mPostInited;

	public void register(LauncherCallee callee, LauncherCallback cb)
			throws LauncherException {
		if (!ThreadUtil.isMainThread()) {
			throw new LauncherException("must register callee in ui thread!");
		}
		if (mPreInited) {
			throw new LauncherException("ui initialization has finished!");
		}
		mUIHandler = new Handler(Looper.getMainLooper(), this);
		mJobs.add(new Job(callee, cb));
	}

	public void preInit() {
		if (!ThreadUtil.isMainThread()) {
			throw new LauncherException("must in ui thread!");
		}

		if (mPreInited) {
			return;
		}

		for (Job job : mJobs) {
			try {
				job.getCallee().preInit();
				if (job.getCb() == null) {
					continue;
				}
				job.getCb().stepDone(job.getCallee(),
						LauncherStatus.POST_INIT_DONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		mPreInited = true;
	}

	public void postInit() {
		if (!ThreadUtil.isMainThread()) {
			throw new LauncherException("must in ui thread!");
		}

		if (mPostInited) {
			return;
		}

		if (!mPreInited) {
			throw new LauncherException("pre initialization hasn't finished!");
		}
		mAgentThread = new HandlerThread(S_AGENT_THREAD_NAME);
		mAgentThread.start();
		mAgentHandler = new Handler(mAgentThread.getLooper());
		mAgentHandler.post(new Runnable() {

			@Override
			public void run() {
				doPostInit();
				mAgentThread.quit();
				// 所有的job在handler的消息队列里维护，因此不会出现问题。
				// 这里把job的引用释放掉，方便系统回收job占用的内存。
				mJobs.clear();
				mPostInited = true;
			}
		});
	}

	private void doPostInit() {
		for (Job job : mJobs) {
			try {
				job.getCallee().afterInit();
				if (job.getCb() == null) {
					continue;
				}
				mUIHandler.obtainMessage(toUIMessage, job).sendToTarget();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		return handleToUIMessage(msg);
	}

	private boolean handleToUIMessage(Message msg) {
		int what = msg.what;
		switch (what) {
		case toUIMessage: {
			Job job = (Job) msg.obj;
			job.getCb()
					.stepDone(job.getCallee(), LauncherStatus.POST_INIT_DONE);
			return true;
		}
		}
		return false;
	}
}

class Job {
	private LauncherCallee callee;
	private LauncherCallback cb;

	public Job(LauncherCallee callee, LauncherCallback cb) {
		this.callee = callee;
		this.cb = cb;
	}

	public LauncherCallee getCallee() {
		return callee;
	}

	public LauncherCallback getCb() {
		return cb;
	}
}
