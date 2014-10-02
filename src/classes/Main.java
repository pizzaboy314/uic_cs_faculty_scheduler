package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
	
	public static List<Instructor> list = new ArrayList<Instructor>();
	public static List<Course> courses = new ArrayList<Course>();

	public static void main(String[] args) {
		try {
			parseInstructors();
			parseCourses();
//			writeInstructors();
			writeCourses();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void checkDataFiles() {
		File dataFile = new File("instructors");
		File dataFile2 = new File("courses");
		FileOutputStream oFile;
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
				oFile = new FileOutputStream(dataFile, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!dataFile2.exists()) {
			try {
				dataFile2.createNewFile();
				oFile = new FileOutputStream(dataFile2, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void parseInstructors() throws IOException {
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
				
				String email = null;
				if (info[3].trim().equals("")) {
					email = info[4].substring(info[4].indexOf("<a href=\"mailto:"), info[4].indexOf("\">email"));
					email = email.replaceAll("<a href=\"mailto:", "");
				} else {
					email = info[3].substring(info[3].indexOf("<a href=\"mailto:"), info[3].indexOf("\">email"));
					email = email.replaceAll("<a href=\"mailto:", "");
				}
				
				dude.setDegName(degName);
				dude.setDegYear(Integer.parseInt(degYear.trim()));
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

		Collections.sort(list);
	}
	public static void parseCourses() throws IOException {
		String url = "https://www.uic.edu/ucat/courses/CS.html"; 
		URL source = null;
		try {
			source = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		URLConnection uc = source.openConnection();
		uc.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
		uc.connect();
		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
		
		String inputLine = in.readLine();
		while (inputLine != null) {
			Course c = new Course();
			
			if (inputLine.contains("<p><b>")) {
				String tmp = inputLine.substring(inputLine.indexOf("<b>"), inputLine.indexOf("</b>"));
				int number = Integer.parseInt(tmp.replaceAll("<b>", ""));
				
				inputLine = in.readLine();
				String name = inputLine.substring(inputLine.indexOf("<b>"), inputLine.indexOf("</b><br>")).replaceAll("<b>", "");
				
				tmp = inputLine.substring(inputLine.indexOf("<br><b>"), inputLine.indexOf(".</b>")).replaceAll("<br><b>", "");
				int underGradHours, gradHours = 0;
				if(tmp.contains("OR")){
					underGradHours = Integer.parseInt(tmp.trim().charAt(0) + "");
					tmp = tmp.replaceAll("\\d OR ", "");
					gradHours = Integer.parseInt(tmp.trim().charAt(0) + "");
				} else {
					underGradHours = Integer.parseInt(tmp.trim().charAt(0) + "");
				}
				
				c.setNumber(number);
				c.setName(name);
				c.setUnderGradHours(underGradHours);
				c.setGradHours(gradHours);
				
				courses.add(c);
			}
			
			if (inputLine.contains("Information provided by the Office of Programs and Academic Assessment.")) {
				inputLine = null;
			} else {
				inputLine = in.readLine();
			}
		}
		
		in.close();
		
		Collections.sort(courses);
	}
	
	public static void writeInstructors() throws IOException{
		checkDataFiles();
		
		StringBuilder s = new StringBuilder();
		for (Instructor dude : list) {
			s.append(dude.fileBlock() + "\n");
		}
		Files.write(Paths.get("instructors"), s.toString().getBytes());
	}
	public static void writeCourses() throws IOException{
		checkDataFiles();
		
		StringBuilder s = new StringBuilder();
		for (Course c : courses) {
			s.append(c.fileBlock() + "\n");
		}
		Files.write(Paths.get("courses"), s.toString().getBytes());
	}

}
