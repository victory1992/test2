package com.dingapp.core.launcher;

/**
 * 阶段性成果回调通知, 此方法在UI线程中回调，不要做耗时操作。
 */
public interface LauncherCallback {
	public void stepDone(LauncherCallee callee, LauncherStatus status);
}
