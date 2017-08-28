package de.quinscape.jrsfx.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.quinscape.jrsfx.worker.Callback;
import de.quinscape.jrsfx.worker.CallbackTask;

/**
 * @author trh0 - TKoll
 *
 */
public class DataThread
		implements IController {

	private final ScheduledExecutorService executor;

	/**
	 * @author trh0 - TKoll
	 */
	protected DataThread() {
		super();
		this.executor = Executors.newScheduledThreadPool(5);
		bootup();
	}

	@Override
	public void bootup() {}

	@Override
	public void terminate() throws InterruptedException {
		executor.shutdownNow();
	}

	public <T> void runTask(CallbackTask<T> t) {
		executor.execute(t);
	}

	public void runTask(Runnable r) {
		executor.execute(r);
	}

	public void scheduleTask(Runnable r, long wait) {
		executor.schedule(r, wait, TimeUnit.MILLISECONDS);
	}

	public <T> void scheduleTask(CallbackTask<T> c, long wait) {
		executor.schedule(c, wait, TimeUnit.MILLISECONDS);
	}

	public <T> void scheduleRepeatedTask(CallbackTask<T> r, long delay, long wait, boolean mustWait) {
		if (mustWait) {
			executor.scheduleWithFixedDelay(r, wait, delay, TimeUnit.MILLISECONDS);
		}
		else {
			executor.scheduleAtFixedRate(r, wait, delay, TimeUnit.MILLISECONDS);
		}
	}

	public void scheduleRepeatedTask(Runnable r, long delay, long wait, boolean mustWait) {
		if (mustWait) {
			executor.scheduleWithFixedDelay(r, wait, delay, TimeUnit.MILLISECONDS);
		}
		else {
			executor.scheduleAtFixedRate(r, wait, delay, TimeUnit.MILLISECONDS);
		}
	}

	// Example
	String veryImportantUiData = "";

	protected void example() {
		this.<String> runTask(new CallbackTask<String>(new Callback<String>() {

			@Override
			public void callback(CallbackTask<String> callbackTask) {
				veryImportantUiData = callbackTask.getData().getValue();
				javafx.application.Platform.runLater(() -> {
					// update();
				});

			}

		}) {

			@Override
			public void work() {

			}

		});
	}
}
