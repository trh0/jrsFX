package de.quinscape.jrsfx.jasper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.util.ApplicationIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class JRSRestClient {

	public JRSRestClient(String orga, String username, String password) {
		this.orga = (orga != null && !orga.trim().isEmpty()) ? orga : null;
		this.user = username;
		this.password = password;
		this.client = new OkHttpClient();// .newBuilder().cookieJar(null).build();
	}

	private final String orga;
	private final String user;
	private final String password;
	public static final String SERVER_HOST = StaticBase.instance().getConfig().getProperty("jrs.host");
	public static final String SERVER_BASE_URL = SERVER_HOST + "/" + StaticBase.instance().getConfig().getProperty(
			"jrs.url");
	private static final String REST_LOGIN = "rest/login";
	private static final String REST_REPORTS = "rest_v2/reports";
	private static final String REST_RESOURCES = "rest_v2/resources";
	private String sessionID = null;
	private final OkHttpClient client;

	/**
	 * @return <code>true</<code> if successful.
	 */
	public boolean auth() {
		boolean success = false;
		String params = "j_username=" + this.user + (this.orga != null ? "|" + this.orga : "") + "&j_password="
				+ this.password;

		RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), params);
		Request req = new Request.Builder().url(SERVER_BASE_URL + REST_LOGIN).post(body).build();

		Response res;

		try {
			res = client.newCall(req).execute();

			if (res.code() == Status.OK.getStatusCode()) {
				success = true;
				for (byte i = 0; i < res.headers().size(); i++) {
					if (res.headers().name(i).equalsIgnoreCase("Set-Cookie")) {
						sessionID = res.headers().value(i);
						break;
					}
				}
				if (sessionID != null) {
					Matcher m = Pattern.compile("JSESSIONID=[^;]+").matcher(sessionID);
					while (m.find()) {
						sessionID = m.group().replaceAll("JSESSIONID=", "");
						break;
					}
				}
			}
			else {
				ApplicationIO.toErrorStream("WARNING", res.code());

			}
		}
		catch (IOException | IllegalStateException e) {
			ApplicationIO.toErrorStream(e);
		}
		return success;
	}

	/**
	 * 
	 * @param keysAndValues
	 * @return
	 */
	private Headers getHeaders(String... keysAndValues) {
		Headers h = null;
		if (keysAndValues != null) {
			h = Headers.of("Cookie", "$Version=0; JSESSIONID=" + sessionID + "; $Path=/jasperserver");
		}
		else {
			h = Headers.of("Cookie", "$Version=0; JSESSIONID=" + sessionID + "; $Path=/jasperserver");
		}
		return h;
	}

	/**
	 * 
	 * @param resource
	 * @param params
	 * @return
	 */
	public String getReportHtml(String resource, Map<String, String> params) {
		String htmlReport = "";

		if (!this.auth()) {
			ApplicationIO.toErrorStream("Authentication failed.");
			return htmlReport;
		}

		String url = SERVER_BASE_URL + REST_REPORTS + resource + ".html";

		if (params != null && params.size() > 0) {
			url += "?";
			boolean first = true;
			for (Entry<String, String> ele : params.entrySet()) {
				url += first ? ele.getKey() + "=" + ele.getValue() : "&" + ele.getKey() + "=" + ele.getValue();
				first = false;
			}
		}

		Request req = new Request.Builder().url(url).headers(getHeaders()).get().build();
		Response res = null;

		try {
			res = client.newCall(req).execute();
			if (res.code() > 400) {
				ApplicationIO.toErrorStream("Resource not found.", url);
			}
			else {
				htmlReport = res.body().string();
			//@formatter:off
			htmlReport = "<!DOCTYPE html>${report}"
						.replace("${report}", 
							htmlReport
							.replaceAll(
									Pattern.quote("\"/jasperserver"), 
									"\"" + JRSRestClient.SERVER_HOST + "/jasperserver")
							.replaceAll(Pattern.quote("\"baseUrl\":\"\""), 
									"\"baseUrl\":\"" + JRSRestClient.SERVER_HOST + "\"")
						);
			//@formatter:on
			}
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
		}
		return htmlReport;
	}

	/**
	 * 
	 * @param resource
	 * @param params
	 * @return
	 */
	public byte[] getReportPdf(String resource, Map<String, String> params) {
		byte[] data = null;
		if (!this.auth()) {
			ApplicationIO.toErrorStream("Authentication failed.");
			return data;
		}
		String url = SERVER_BASE_URL + REST_REPORTS + resource + ".pdf";

		if (params != null && params.size() > 0) {
			url += "?";
			boolean first = true;
			for (Entry<String, String> ele : params.entrySet()) {
				url += first ? ele.getKey() + "=" + ele.getValue() : "&" + ele.getKey() + "=" + ele.getValue();
				first = false;
			}
		}

		Request req = new Request.Builder().url(url).headers(getHeaders()).get().build();
		Response res = null;
		try {
			res = client.newCall(req).execute();
			data = res.body().bytes();
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
		}
		return data;
	}

	/**
	 * 
	 * @param searchFor
	 * @param startFrom
	 * @param limit
	 * @param type
	 * @return
	 */
	public List<RepoTreeResource> getResources(String searchFor, int startFrom, int limit, String type) {
		List<RepoTreeResource> data = null;
		if (!this.auth()) {
			ApplicationIO.toErrorStream(this.getClass().getName(), "[getResources]", "params:", searchFor, startFrom,
					limit, type, "Login failed.");
			return data;
		}
		String url = SERVER_BASE_URL + REST_RESOURCES + "?offset=" + startFrom + "&limit=" + limit + (searchFor != null
				? "&q=" + searchFor : "") + (type != null ? "&type=" + type : "");
		Request req = new Request.Builder().url(url).headers(getHeaders()).get().build();
		Response res = null;
		try {
			res = client.newCall(req).execute();
			JAXBContext c = JAXBContext.newInstance(RepoTreeResourceWrapper.class, RepoTreeResource.class,
					ArrayList.class);
			data = RepoTreeResourceWrapper.class.cast(c.createUnmarshaller().unmarshal(res.body().charStream()))
					.asList();
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
		}

		return data;
	}

	public Image getResourceImage(String uri, boolean viewNested) {
		Image result = null;
		if (!this.auth()) {
			ApplicationIO.toErrorStream(this.getClass().getName(), "[viewResourceDetail]", "params:", uri,
					"Login failed.");
		}
		else {
			String url = SERVER_BASE_URL + REST_RESOURCES + (uri.startsWith("/") ? uri : "/" + uri) + ("?expanded="
					+ viewNested);
			Request req = new Request.Builder().url(url).headers(getHeaders()).get().build();
			Response res = null;
			try {
				res = client.newCall(req).execute();
				InputStream in = new ByteArrayInputStream(res.body().bytes());
				result = SwingFXUtils.toFXImage(ImageIO.read(in), null);
				in.close();

			}
			catch (Exception e) {
				ApplicationIO.toErrorStream(e);
			}
		}
		return result;
	}

	public String getResourceDetail(String uri, boolean viewNested) {
		String result = null;
		if (!this.auth()) {
			ApplicationIO.toErrorStream(this.getClass().getName(), "[viewResourceDetail]", "params:", uri,
					"Login failed.");
		}
		else {
			String url = SERVER_BASE_URL + REST_RESOURCES + (uri.startsWith("/") ? uri : "/" + uri) + ("?expanded="
					+ viewNested);
			Request req = new Request.Builder().url(url).headers(getHeaders()).get().build();
			Response res = null;
			try {
				res = client.newCall(req).execute();
				result = res.body().string();

			}
			catch (Exception e) {
				ApplicationIO.toErrorStream(e);
			}
		}
		return result;
	}
}
