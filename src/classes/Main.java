package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {

	public static void main(String[] args) {
		try {
			parseAndInitializeFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkDataFile() {
		File dataFile = new File("data");
		FileOutputStream oFile;
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
				oFile = new FileOutputStream(dataFile, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void parseAndInitializeFile() throws IOException {
		checkDataFile();

		String url = "http://www.cs.uic.edu/Main/Faculty";
		URL source = null;
		try {
			source = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(source.openStream()));

		String inputLine = in.readLine();
		while (inputLine != null) {
			if (inputLine.contains("<!--EDIT WITH CARE. NO NEWLINES BEFORE noinclude TAG-->")) {
				// GET PEOPLE INFO
				
			}

			if (inputLine.contains("Faculty_Awards")) {
				inputLine = null;
			} else {
				inputLine = in.readLine();
			}
		}

		in.close();

	}

}
