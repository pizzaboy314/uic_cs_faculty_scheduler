package classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Worker {
	
	public static List<Instructor> instructors = new ArrayList<Instructor>();
	public static List<Course> courses = new ArrayList<Course>();
	
	public static void init(){
		if(checkInstructorListFile() == false || checkInstructorTSV() == false){
			System.out.println("Missing instructor data files -- will reload instructor data...\n");
			updateInstructors();
		} else {
			System.out.println("Reloading of instructor data set to FALSE by default...");
			System.out.println("To reload manually, use the reload button below...\n");
			loadInstructors();
		}
		
		if(checkCourseListFile() == false || checkCourseTSV() == false){
			System.out.println("Missing course data files -- will reload course data...\n");
			updateCourses();
		} else {
			System.out.println("Reloading of course data set to FALSE by default...");
			System.out.println("To reload manually, use the reload button below...\n");
			loadCourses();
		}
		
	}
	
	public static void updateInstructors(){
		try {
			downloadAndParseInstructors();
			writeInstructors();
			System.out.println("Finished updating instructor data...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void updateCourses(){
		try {
			downloadAndParseCourses();
			writeCourses();
			System.out.println("Finished updating course data...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkInstructorTSV(){
		File dataFile = new File("instructors.tsv");
		
		FileOutputStream oFile;
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
				oFile = new FileOutputStream(dataFile, false);
				oFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			return true;
		}
		
	}
	
	public static boolean checkCourseTSV(){
		File dataFile2 = new File("courses.tsv");
		
		FileOutputStream oFile;
		if (!dataFile2.exists()) {
			try {
				dataFile2.createNewFile();
				oFile = new FileOutputStream(dataFile2, false);
				oFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean checkInstructorListFile() {
		File dataFile = new File("instructorList.txt");
		FileOutputStream oFile;
		if (!dataFile.exists()) {
			try {
				dataFile.createNewFile();
				oFile = new FileOutputStream(dataFile, false);
				oFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean checkCourseListFile(){
		File dataFile2 = new File("courseList.txt");
		
		FileOutputStream oFile;
		if (!dataFile2.exists()) {
			try {
				dataFile2.createNewFile();
				oFile = new FileOutputStream(dataFile2, false);
				oFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		} else {
			return true;
		}
	}
	
	public static void loadInstructors(){
		String line;
		InputStream fis;
		BufferedReader br;
		try {
			fis = new FileInputStream("instructors.tsv");
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			
			while((line = br.readLine()) != null){
				Instructor dude = new Instructor();
				String[] fields = line.split("\t");
				
				dude.setName(fields[0]);
				dude.setTitle(fields[1]);
				dude.setEmail(fields[2]);
				dude.setDegName(fields[3]);
				dude.setDegYear(Integer.parseInt(fields[4]));
				dude.setBackground(fields[5]);
				dude.setRank1(Integer.parseInt(fields[6]));
				dude.setRank2(Integer.parseInt(fields[7]));
				dude.setRank3(Integer.parseInt(fields[8]));
				
				instructors.add(dude);
			}
			Collections.sort(instructors);
			
			br.close();
			br = null;
			fis = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public static void loadCourses(){
		String line;
		InputStream fis;
		BufferedReader br;
		try {
			fis = new FileInputStream("courses.tsv");
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			
			while((line = br.readLine()) != null){
				Course c = new Course();
				String[] fields = line.split("\t");
				
				c.setName(fields[0]);
				c.setNumber(Integer.parseInt(fields[1]));
				c.setUnderGradHours(Integer.parseInt(fields[2]));
				c.setGradHours(Integer.parseInt(fields[3]));
				
				courses.add(c);
			}
			Collections.sort(courses);
			
			br.close();
			br = null;
			fis = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	public static void downloadAndParseInstructors() throws IOException {
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
		System.out.println("Parsing instructor data...");

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
				
				instructors.add(dude);
			}

			if (inputLine.contains("Faculty_Awards")) {
				inputLine = null;
			} else {
				inputLine = in.readLine();
			}
		}

		in.close();
		System.out.println("Finished parsing instructor data...");

		Collections.sort(instructors);
	}
	public static void downloadAndParseCourses() throws IOException {
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
		System.out.println("Parsing course data...");
		
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
		System.out.println("Finished parsing course data...");
		
		Collections.sort(courses);
	}
	
	public static void writeInstructors() throws IOException{
		System.out.println("Writing instructor data...");
		
		StringBuilder text = new StringBuilder();
		StringBuilder tsv = new StringBuilder();
		for (Instructor dude : instructors) {
			text.append(dude.fileBlock() + "\n");
			tsv.append(dude.tsvLine() + "\n");
		}
		Files.write(Paths.get("instructorList.txt"), text.toString().getBytes());
		Files.write(Paths.get("instructors.tsv"), tsv.toString().getBytes());
		System.out.println("Finished writing instructor data...");
	}
	public static void writeCourses() throws IOException{
		System.out.println("Writing course data...");
		
		StringBuilder text = new StringBuilder();
		StringBuilder tsv = new StringBuilder();
		for (Course c : courses) {
			text.append(c.fileBlock() + "\n");
			tsv.append(c.tsvLine() + "\n");
		}
		Files.write(Paths.get("courseList.txt"), text.toString().getBytes());
		Files.write(Paths.get("courses.tsv"), tsv.toString().getBytes());
		System.out.println("Finished writing course data...");
	}


}
