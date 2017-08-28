package de.quinscape.jrsfx.jasper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBContext;

import de.quinscape.jrsfx.controller.StaticBase;
import de.quinscape.jrsfx.util.ApplicationIO;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportClient {

	public ReportClient(String orga, String username, String password) {
		this.orga = orga;
		this.user = username;
		this.password = password;
		this.client = new OkHttpClient();// .newBuilder().cookieJar(null).build();
											// To add cookies on init.
	}

	private final String orga;
	private final String user;
	private final String password;
	private static final String SERVER_BASE_URL = StaticBase.instance().getConfig().getProperty("jrs.url");
	private static final String REST_LOGIN = "rest/login";
	private static final String REST_REPORTS = "rest_v2/reports/";
	private static final String REST_RESOURCES = "rest_v2/resources";
	private String sessionID = null;
	private final OkHttpClient client;

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
				ApplicationIO.toErrorStream("WARNING: ", res.code());
			}
		}
		catch (IOException | IllegalStateException e) {
			ApplicationIO.toErrorStream(e);
		}
		return success;
	}

	public String getReportHtml(String resource, Map<String, String> params) {
		String htmlReport = null;

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

		Headers headers = Headers.of("Cookie", "$Version=0; JSESSIONID=" + sessionID + "; $Path=/jasperserver");

		Request req = new Request.Builder().url(url).headers(headers).get().build();
		Response res = null;

		try {
			res = client.newCall(req).execute();
			htmlReport = res.body().string();
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
		}
		return htmlReport;
	}

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

		Headers headers = Headers.of("Cookie", "$Version=0; JSESSIONID=" + sessionID + "; $Path=/jasperserver");

		Request req = new Request.Builder().url(url).headers(headers).get().build();
		Response res = null;
		try {
			res = client.newCall(req).execute();
			data = res.body().bytes();
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
		}
		ApplicationIO.toErrorStream(data.length);
		return data;
	}

	public List<RepositoryResource> getResources(String searchFor, int startFrom, int limit, String type) {
		List<RepositoryResource> data = null;
		if (!this.auth()) {
			ApplicationIO.toErrorStream("Login failed.");
			return data;
		}
		Headers headers = Headers.of("Cookie", "$Version=0; JSESSIONID=" + sessionID + "; $Path=/jasperserver");
		String url = SERVER_BASE_URL + REST_RESOURCES + "?offset=" + startFrom + "&limit=" + limit + (searchFor != null
				? "&q=" + searchFor : "") + (type != null ? "&type=" + type : "");
		Request req = new Request.Builder().url(url).headers(headers).get().build();
		Response res = null;
		try {
			res = client.newCall(req).execute();
			JAXBContext c = JAXBContext.newInstance(RepositoryResourceWrapper.class, RepositoryResource.class,
					ArrayList.class);
			data = RepositoryResourceWrapper.class.cast(c.createUnmarshaller().unmarshal(res.body().charStream()))
					.asList();
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
		}

		return data;
	}

}
