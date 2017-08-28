package de.quinscape.jrsfx.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Useful class for dynamically changing the classpath, adding classes during
 * runtime.
 */
public class ThisClasspath {
	/**
	 * Parameters of the method to add an URL to the System classes.
	 */
	private static final Class<?>[] parameters = new Class[] { URL.class };

	/**
	 * Adds a resource to the classpath.
	 * 
	 * @param path
	 *            Absolute path to the file.
	 * @throws Exception
	 */
	public static void add(String path) throws Exception {
		File f = new File(path);
		add(f);
	}

	/**
	 * Adds a resource to the classpath.
	 * 
	 * @param file
	 *            The resource to be added.
	 * @throws Exception
	 */
	public static void add(File file) throws Exception {
		add(file.toURI().toURL());
	}

	/**
	 * Adds a resource to the classpath.
	 * 
	 * @param url
	 *            URL directiong to the resource to be added.
	 * @throws Exception
	 */
	public static void add(URL url) throws Exception {
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			boolean oldState = method.isAccessible();
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] { url });
			method.setAccessible(oldState);
		}
		catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			String tar = url.toExternalForm();
			Exception ex = new Exception("Error loading resource from " + tar + ".", e);
			throw ex;
		}
	}

	public static Object load(String fullyQualifiedClassname, Object... initArgs) throws Exception {
		Object instance = null;
		try {
			Constructor<?> cs = ClassLoader.getSystemClassLoader().loadClass("test.DymamicLoadingTest").getConstructor(
					String.class);
			instance = cs.newInstance(initArgs);
		}
		catch (Exception e) {
			throw e;
		}
		return instance;
	}
//
//add("C:\\dynamicloading.jar");
//		Constructor<?> cs = ClassLoader.getSystemClassLoader().loadClass("test.DymamicLoadingTest").getConstructor(
//				String.class);
        
}