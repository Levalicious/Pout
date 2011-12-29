package org.getspout.server.util.thread;

import java.util.WeakHashMap;

import org.getspout.api.Server;
import org.getspout.api.scheduler.Scheduler;
import org.getspout.server.SpoutServer;
import org.getspout.server.scheduler.SpoutScheduler;

public abstract class AsyncManager {
	
	private final Server server; // null means that this AsyncManager is the Server
	private final AsyncExecutor executor;
	private final WeakHashMap<Managed,Boolean> managedSet = new WeakHashMap<Managed,Boolean>();
	private final ManagementTask[] singletonCache = new ManagementTask[ManagementTaskEnum.getMaxId()];
	
	public AsyncManager(AsyncExecutor executor) {
		this.executor = executor;
		this.server = null;
		executor.setManager(this);
	}
	
	public AsyncManager(AsyncExecutor executor, Server server) {
		this.executor = executor;
		this.server = server;
		executor.setManager(this);
		registerWithScheduler(((SpoutServer)server).getScheduler());
	}
	
	public void registerWithScheduler(Scheduler scheduler) {
		((SpoutScheduler)scheduler).addAsyncExecutor(executor);
	}
	
	public Server getServer() {
		if (server == null) {
			if (!(this instanceof Server)) {
				throw new IllegalStateException("Only the Server object itself should have a null server reference");
			} else {
				return (Server)this;
			}
		} else {
			return server;
		}
	}
	
	/**
	 * Gets a singleton task if available.
	 * 
	 * Tasks should be returned to the cache after usage
	 * 
	 * @param taskEnum the enum of the task
	 * @return an instance of task
	 */
	public ManagementTask getSingletonTask(ManagementTaskEnum taskEnum) {
		Thread current = Thread.currentThread();
		if (current instanceof AsyncExecutor) {
			AsyncExecutor executor = (AsyncExecutor)current;
			int taskId = taskEnum.getId();
			ManagementTask[] taskCache = executor.getManager().singletonCache;
			ManagementTask task = taskCache[taskId];
			if (task != null) {
				taskCache[taskId] = null;
				return task;
			}
		}
		return taskEnum.getInstance();
	}
	
	/**
	 * Returns a singleton task to the cache
	 * 
	 * Tasks should be returned to the cache after usage
	 * 
	 * @param taskEnum the enum of the task
	 * @return an instance of task
	 */
	public void returnSingletonTask(ManagementTask task) {
		if (!task.getFuture().isDone()) {
			throw new IllegalArgumentException("Tasks with active futures should not be returned to the cache");
		}
		Thread current = Thread.currentThread();
		if (current instanceof AsyncExecutor) {
			AsyncExecutor executor = (AsyncExecutor)current;
			ManagementTaskEnum e = task.getEnum();
			int taskId = e.getId();
			ManagementTask[] taskCache = executor.getManager().singletonCache;
			taskCache[taskId] = task;
		}
	}
	
	/**
	 * Sets this AsyncManager as manager for a given object
	 *
	 * @param managed the object to give responsibility for
	 */
	public final void addManaged(Managed managed) {
		synchronized (managedSet) {
			managedSet.put(managed, Boolean.TRUE);
		}
	}
	
	/**
	 * Gets the associated AsyncExecutor
	 * 
	 * @return the executor
	 */
	public final AsyncExecutor getExecutor() {
		return executor;
	}
	
	/**
	 * This method is called in order to update the snapshot at the end of each tick
	 */
	public abstract void copySnapshotRun() throws InterruptedException;

	/**
	 * This method is called in order to start a new tick
	 * 
	 * @param delta the time since the last tick
	 */
	public abstract void startTickRun(long delta) throws InterruptedException;
	
}
