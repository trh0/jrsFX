package de.quinscape.jrsfx.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import de.quinscape.jrsfx.worker.Callback;
import de.quinscape.jrsfx.worker.CallbackTask;

/**
 * @author trh0 - TKoll
 *
 */
public class UIThread
		implements IController {

	private final ExecutorService executor;

	/**
	 * @author trh0 - TKoll
	 */
	protected UIThread() {
		super();
		this.executor = Executors.newCachedThreadPool();
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
