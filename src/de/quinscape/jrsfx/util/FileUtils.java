package de.quinscape.jrsfx.util;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FileUtils {

	private FileUtils() {

	}

	public static List<File> search(File root, String[] containments, int searchDepth) {
		if (root == null) {
			throw new NullPointerException("No Root To Search Given -> File root == null");
		}
		else if (containments == null) { throw new NullPointerException(
				"No Match-Pattern Given -> String[] containments == null"); }
		Map<File, Integer> possibleFiles = new HashMap<>(512);
		List<File> filesList = new ArrayList<>(256);
		List<File> defenitiveMatches = new ArrayList<>();
		String currentFolderStr = root.getAbsolutePath();
		File currentFolder = new File(currentFolderStr);
		currentFolderStr = (currentFolder.isDirectory()) ? currentFolderStr : currentFolderStr.endsWith(File.separator)
				? currentFolderStr.substring(0, currentFolderStr.substring(0, currentFolderStr.length() - 1)
						.lastIndexOf(File.separator)) : currentFolderStr.substring(0, currentFolderStr.lastIndexOf(
								File.separator));
		currentFolder = new File(currentFolderStr);
		if (searchDepth > 0) {
			int depth = 0;
			do {
				if (currentFolder.isDirectory() && currentFolder.exists() && currentFolder.canRead())
					for (File f : currentFolder.listFiles()) {
						if (f.isDirectory()) {
							defenitiveMatches.addAll(search(f.getAbsoluteFile(), containments, 0));
						}
						for (int i = 0; i < containments.length; i++) {
							if (f.getName().toLowerCase().contains(containments[i].toLowerCase())) {
								Integer cur = possibleFiles.get(f);
								if (cur != null) {
									cur++;
								}
								else {
									cur = 1;
								}
								if (defenitiveMatches.contains(f)) {
									cur++;
									defenitiveMatches.remove(f);
								}
								possibleFiles.put(f, cur);
								for (File match : defenitiveMatches) {
									if (match.getName().toLowerCase().contains(containments[i].toLowerCase())) {
										cur = possibleFiles.get(match);
										if (cur != null) {
											cur++;
										}
										else {
											cur = 1;
										}
										possibleFiles.put(match, cur);
									}
								}
							}
						}

					}
				int highest = 0;
				for (Entry<File, Integer> e : possibleFiles.entrySet()) {
					int current = e.getValue();
					if (current > highest)
						highest = current;
				}
				for (Entry<File, Integer> e : possibleFiles.entrySet()) {
					int current = e.getValue();
					if (current != highest) {
						filesList.add(e.getKey());
					}
				}
				for (File f : filesList) {
					possibleFiles.remove(f);
				}
				filesList = new ArrayList<>();
				currentFolderStr = currentFolderStr.endsWith(File.separator) ? currentFolderStr.substring(0,
						currentFolderStr.substring(0, currentFolderStr.length() - 1).lastIndexOf(File.separator))
						: currentFolderStr.substring(0, currentFolderStr.lastIndexOf(File.separator));
				currentFolder = new File(currentFolderStr);

				depth++;
			} while (depth < searchDepth);
			for (File file : possibleFiles.keySet()) {
				filesList.add(file);
			}
			possibleFiles = null;
		}
		else {
			if (currentFolder.isDirectory() && currentFolder.exists() && currentFolder.canRead() && currentFolder
					.listFiles() != null)
				for (File f : currentFolder.listFiles()) {
					for (int i = 0; i < containments.length; i++) {
						if (f.getName().toLowerCase().contains(containments[i].toLowerCase())) {
							Integer cur = possibleFiles.get(f);
							if (cur != null) {
								cur++;
							}
							else {
								cur = 1;
							}
							possibleFiles.put(f, cur);

						}
					}
				}
			int highest = 0;
			int current = 0;
			for (Entry<File, Integer> e : possibleFiles.entrySet()) {
				current = e.getValue();
				if (current > highest)
					highest = current;
			}
			for (Entry<File, Integer> e : possibleFiles.entrySet()) {
				current = e.getValue();
				if (current != highest) {
					filesList.add(e.getKey());
				}
			}
			for (File f : filesList) {
				possibleFiles.remove(f);
			}
			filesList = new ArrayList<>();
			for (File file : possibleFiles.keySet()) {
				filesList.add(file);
			}
			possibleFiles = null;
		}
		return filesList;
	}

	/**
	 * 
	 * @param extension
	 *            The extension to look for.
	 * @param folderPath
	 *            The folder to search in.
	 * @return A {@link List} of {@link Files}, matching the extension.
	 */
	public List<File> getFilesByExtension(String extension, String folderPath) {
		File fo = new File(folderPath);
		List<File> files = new ArrayList<>();
		extension = extension.replace('.', ' ').replaceAll(" ", "");
		if (!fo.exists()) {
			ApplicationIO.toErrorStream(fo,"does not exist");
		}
		try {
			File[] f = fo.listFiles();
			for (File file : f) {
				if (file.getName().endsWith(extension)) {
					files.add(file);
				}
			}
		}
		catch (Exception e) {
			ApplicationIO.toErrorStream(e);
		}
		return files;
	}


	/**
	 * Deletes all files with a certain extension.
	 * 
	 * @param extension
	 *            The files with this extension will be deleted.
	 * @param folderPath
	 *            ...whithin this folder & its subfolders
	 * @return <code>true</code>, if success.
	 */
	public boolean clearExtension(String extension, String folderPath) {
		File folder = new File(folderPath);
		if (folder.exists()) {
			File[] files = folder.listFiles();
			extension = extension.replace('.', ' ').replaceAll(" ", "");
			for (File f : files) {
				if (f.getName().endsWith("." + extension)) {
					f.delete();
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Deletes all files with a certain name.
	 * 
	 * @param fileName
	 *            The fileName to look for.
	 * @param folderPath
	 *            ...whithin this folder & its subfolders.
	 * @return <code>true</code>, if success.
	 */
	public boolean clearAll(String fileName, String folderPath) {
		File folder = new File(fileName);
		if (folder.exists()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.getName().equals(fileName)) {
					file.delete();
				}
			}
			return true;
		}
		return false;
	}
}
