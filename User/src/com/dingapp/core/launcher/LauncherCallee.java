package com.dingapp.core.launcher;

/**
 * 这个接口用于定义在LauncherThread中进行初始化的任务。
 * 
 * @author panda
 * 
 */
public interface LauncherCallee {
	/**
	 * 在启动主页之前执行的方法。 不要添加耗时操作。 如果成功，那么返回的是true。如果失败，返回false。
	 */
	public boolean preInit();

	/**
	 * 在启动主页之后执行的方法。 如果成功，那么返回的是true。如果失败，返回false。
	 */
	public boolean afterInit();
}
