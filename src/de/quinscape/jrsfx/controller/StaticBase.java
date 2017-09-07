package de.quinscape.jrsfx.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;

import de.quinscape.jrsfx.config.Configuration;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.application.Platform;
import javafx.stage.Stage;

@SuppressWarnings({ "restriction" })
public class StaticBase
		implements IController {

	private static StaticBase instance;
	private final DataThread dataThread;
	private final HttpServer server;
	private final AtomicBoolean serverContext;

	public DataThread getDataThread() {
		return this.dataThread;
	}

	private final UIThread uiThread;

	public UIThread getUiThread() {
		return this.uiThread;
	}

	private UIController uiController;

	public UIController getUIController() {
		return this.uiController;
	}

	private volatile Configuration config;

	public synchronized Configuration getConfig() {
		return this.config;
	}

	private StaticBase() throws IOException {
		this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 80), 0);
		this.uiThread = new UIThread();
		this.dataThread = new DataThread();
		this.serverContext = new AtomicBoolean(false);
		this.bootup();
	}

	public static StaticBase instance() {
		if (instance == null) {
			try {
				instance = new StaticBase();
			}
			catch (IOException e) {
				ApplicationIO.toErrorStream(e);
				Platform.exit();
			}
		}
		return instance;
	}

	public void reloadConfiguration(Configuration c) {
		synchronized (this.config) {
			this.config = c;
		}
	}

	@Override
	public void bootup() {
		try {
			this.config = new Configuration(Configuration.DEFAULT_CFG, null);
		}
		catch (IOException e) {
			ApplicationIO.toErrorStream("Unable to load configuration", e);
		}
		server.start();
	}

	public void initUI(Stage stage) {
		this.uiController = new UIController(stage);
	}

	public void serveContent(String html) {
		if (this.serverContext.getAndSet(true))
			server.removeContext("/");
		server.createContext("/", e -> {
			e.sendResponseHeaders(200, 0);
			Headers h = e.getResponseHeaders();
			h.set("Content-Type", "text/html; charset=UTF-8");
			OutputStream os = e.getResponseBody();
			os.write(html.getBytes());
			os.close();
		});
	}

	@Override
	public void terminate() throws InterruptedException {
		this.server.stop(0);
		uiThread.terminate();
		dataThread.terminate();
		uiController.terminate();
		Platform.exit();
	}

}
