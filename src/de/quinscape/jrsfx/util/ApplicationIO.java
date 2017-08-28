package de.quinscape.jrsfx.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * 
 * @author trh0 - TKoll
 *
 */
public class ApplicationIO {

	private ApplicationIO() {}

	private static PrintStream out = System.err;
	private static final Logger LOG = Logger.getGlobal();

	/**
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param fileName
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public static String fromClasspath(String fileName) throws IOException, IllegalArgumentException {

		if (fileName == null || fileName.isEmpty())
			throw new IllegalArgumentException("fileName='" + fileName + "'");

		InputStream file = ApplicationIO.class.getClassLoader().getResourceAsStream(fileName);
		Scanner sc = null;
		if (file != null) {
			try {
				StringBuilder sb = new StringBuilder();
				sc = new Scanner(file);
				while (sc.hasNext()) {
					sb.append(sc.nextLine());
				}
				return sb.toString();
			}
			finally {
				if (sc != null)
					sc.close();
			}
		}
		else {
			throw new IllegalArgumentException("fileName='" + fileName + "' not found.");
		}
	}

	/**
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param args
	 */
	public static void toErrorStream(Object... args) {
		StringBuilder sb = new StringBuilder();
		Throwable ex = null;
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				sb.append("%s\n");
				if (args[i] instanceof Throwable) {
					for (StackTraceElement el : ((Throwable) args[i]).getStackTrace()) {
						sb.append(el.toString());
						sb.append("\n");
					}
					if (ex == null)
						ex = (Throwable) args[i];
				}
			}
		}
		else {
			sb.append("%s\n");
		}
		out.printf(sb.toString(), args);
		// LOG.log(Level.SEVERE, String.format(sb.toString(), args), ex);

	};

	/**
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(byte[] data) throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
		deflater.finish();
		byte[] buffer = new byte[16];
		while (!deflater.finished()) {
			int count = deflater.deflate(buffer);
			outputStream.write(buffer, 0, count);
		}
		outputStream.close();
		data = null;
		buffer = null;
		return outputStream.toByteArray();
	}

	/**
	 * 
	 * @author trh0 - TKoll
	 *
	 * @param data
	 * @return
	 * @throws DataFormatException
	 * @throws IOException
	 */
	public static byte[] expand(byte[] data) throws DataFormatException, IOException {
		Inflater inflator = new Inflater();
		inflator.setInput(data);
		ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
		byte[] buffer = new byte[32];
		while (!inflator.finished()) {
			int count = inflator.inflate(buffer);
			os.write(buffer, 0, count);
		}
		os.close();
		data = null;
		buffer = null;
		return os.toByteArray();
	}
}
