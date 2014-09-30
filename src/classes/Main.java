package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
		
		URLConnection uc = source.openConnection();
        uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        uc.connect();

        List<Instructor> list = new ArrayList<Instructor>();
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));

		String inputLine = in.readLine();
		while (inputLine != null) {
			Instructor dude = new Instructor();
			
			if (inputLine.contains("<!--EDIT WITH CARE. NO NEWLINES BEFORE noinclude TAG-->")) {
				String name = inputLine.substring(inputLine.indexOf("<strong>"), inputLine.indexOf("</strong>,"));
				name = name.replaceAll("</?strong>", "");
				dude.setName(name);
				
				String title = inputLine.substring(inputLine.indexOf("</strong>, "), inputLine.indexOf("<br />"));
				title = title.replaceAll("</?strong>, ?", "");
				dude.setTitle(title);
				
				String[] info = inputLine.split("<br />");
				
				String degName = info[1].substring(0, info[1].length()-6);
				String degYear = info[1].substring(info[1].length()-5, info[1].length());
				String background = info[2].trim();
				
				String email = info[3].substring(info[3].indexOf("<a href=\"mailto:"), info[3].indexOf("\">email"));
				email = email.replaceAll("<a href=\"mailto:", "");
				
				dude.setDegName(degName);
				dude.setDegYear(Integer.parseInt(degYear));
				dude.setBackground(background);
				dude.setEmail(email);
				
				System.out.println("");
				
				list.add(dude);
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
