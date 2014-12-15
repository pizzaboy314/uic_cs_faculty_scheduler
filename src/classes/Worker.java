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
	
	public static List<Instructor> instructors;
	public static List<Course> courses;
	public static String currName;
	public static String currCourse1;
	public static String currCourse2;
	public static String currCourse3;
	public static String currCourse4;
	public static String currCourse5;
	public static String currCourse6;
	public static String currCourse7;
	public static String currCourse8;
	
	/**
	 * Initializes global variables and checks to see if the main data files are
	 * in existence. If not, it creates the files and does a fresh download on
	 * the data from the web page.
	 * 
	 * @return a boolean (true by default) indicating whether or not there were
	 *         missing data files
	 */
	public static boolean init() {
		boolean hasDataFiles = true;

		instructors = new ArrayList<Instructor>();
		courses = new ArrayList<Course>();
		currName = "";
		currCourse1 = "";
		currCourse2 = "";
		currCourse3 = "";
		currCourse4 = "";
		currCourse5 = "";
		currCourse6 = "";
		currCourse7 = "";
		currCourse8 = "";
		
		if(checkInstructorListFile() == false || checkInstructorTSV() == false){
			updateInstructors();
			hasDataFiles = false;
		} else {
			loadInstructors();
		}
		
		if(checkCourseListFile() == false || checkCourseTSV() == false){
			updateCourses();
			hasDataFiles = false;
		} else {
			loadCourses();
		}
		
		return hasDataFiles;
	}
	
	public static void initPrint(){
		System.out.println("Use this tool to edit the courses each instructor teaches...\n");
		if(checkInstructorListFile() == false || checkInstructorTSV() == false){
			System.out.println("Missing instructor data files -- will reload instructor data...\n");
		} else {
			System.out.println("Reloading of instructor data set to FALSE by default...");
			System.out.println("To reload manually, use the reload button below...\n");
		}
		
		if(checkCourseListFile() == false || checkCourseTSV() == false){
			System.out.println("Missing course data files -- will reload course data...\n");
		} else {
			System.out.println("Reloading of course data set to FALSE by default...");
			System.out.println("To reload manually, use the reload button below...\n");
		}
	}
	
	public static String chooseInstructors(List<Course> offered) {
		StringBuilder sb = new StringBuilder();
		for (Course c : offered) {
			if (c.getNumber() != 99) {
				sb.append("CS " + c.getNumber() + ": " + c.chooseInstructor().getName() + "\n");
			}
		}

		return sb.toString();
	}

	public static String[] instructorsToArray(){
		String[] arr = new String[instructors.size()];
		int i = 0;
		for(Instructor dude : instructors){
			arr[i] = dude.toString();
			i++;
		}
		return arr;
	}
	
	public static String[] coursesToArray(){
		String[] arr = new String[courses.size()];
		int i = 0;
		for(Course c : courses){
			arr[i] = c.toString();
			i++;
		}
		return arr;
	}
	
	public static void saveInfo(){
		for(Instructor dude : instructors){
			if(dude.getName().equals(currName)){
				int course1 = (!currCourse1.equals("n/a")) ? Integer.parseInt(currCourse1) : 99;
				int course2 = (!currCourse2.equals("n/a")) ? Integer.parseInt(currCourse2) : 99;
				int course3 = (!currCourse3.equals("n/a")) ? Integer.parseInt(currCourse3) : 99;
				int course4 = (!currCourse4.equals("n/a")) ? Integer.parseInt(currCourse4) : 99;
				int course5 = (!currCourse5.equals("n/a")) ? Integer.parseInt(currCourse5) : 99;
				int course6 = (!currCourse6.equals("n/a")) ? Integer.parseInt(currCourse6) : 99;
				int course7 = (!currCourse7.equals("n/a")) ? Integer.parseInt(currCourse7) : 99;
				int course8 = (!currCourse8.equals("n/a")) ? Integer.parseInt(currCourse8) : 99;
				dude.setCourse1(course1);
				dude.setCourse2(course2);
				dude.setCourse3(course3);
				dude.setCourse4(course4);
				dude.setCourse5(course5);
				dude.setCourse6(course6);
				dude.setCourse7(course7);
				dude.setCourse8(course8);

				for (Course c : courses) {
					int num = c.getNumber();
					if (num == course1 || num == course2 || num == course3 || num == course4 || num == course5 || num == course6 || num == course7
							|| num == course8) {
						c.addInstructor(dude);
					}
				}
			}
		}
		try {
			writeInstructors();
		} catch (IOException e) {
			e.printStackTrace();
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
		File dataPath = new File(System.getProperty("user.dir") + File.separator + "data");
		File dataFile = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "instructors.tsv");
		
		if (!dataPath.exists()) {
			dataPath.mkdirs();
			return false;
		} else if (!dataFile.exists()) {
			FileOutputStream oFile;
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
		File dataPath = new File(System.getProperty("user.dir") + File.separator + "data");
		File dataFile = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "courses.tsv");
		
		if (!dataPath.exists()) {
			dataPath.mkdirs();
			return false;
		} else if (!dataFile.exists()) {
			FileOutputStream oFile;
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
	
	public static boolean checkInstructorListFile() {
		File dataPath = new File(System.getProperty("user.dir") + File.separator + "data");
		File dataFile = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "instructorList.txt");

		if (!dataPath.exists()) {
			dataPath.mkdirs();
			return false;
		} else if (!dataFile.exists()) {
			FileOutputStream oFile;
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
		File dataPath = new File(System.getProperty("user.dir") + File.separator + "data");
		File dataFile = new File(System.getProperty("user.dir") + File.separator + "data" + File.separator + "courseList.txt");
		
		if (!dataPath.exists()) {
			dataPath.mkdirs();
			return false;
		} else if (!dataFile.exists()) {
			FileOutputStream oFile;
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
	
	public static void loadInstructors() {
		String line;
		InputStream fis;
		BufferedReader br;
		try {
			fis = new FileInputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "instructors.tsv");
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
				dude.setCourse1(Integer.parseInt(fields[6]));
				dude.setCourse2(Integer.parseInt(fields[7]));
				dude.setCourse3(Integer.parseInt(fields[8]));
				dude.setCourse4(Integer.parseInt(fields[9]));
				dude.setCourse5(Integer.parseInt(fields[10]));
				dude.setCourse6(Integer.parseInt(fields[11]));
				dude.setCourse7(Integer.parseInt(fields[12]));
				dude.setCourse8(Integer.parseInt(fields[13]));
				
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
			fis = new FileInputStream(System.getProperty("user.dir") + File.separator + "data" + File.separator + "courses.tsv");
			br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			
			while((line = br.readLine()) != null){
				Course c = new Course();
				String[] fields = line.split("\t");
				
				c.setName(fields[0]);
				c.setNumber(Integer.parseInt(fields[1]));
				c.setUnderGradHours(Integer.parseInt(fields[2]));
				c.setGradHours(Integer.parseInt(fields[3]));
				
				for (Instructor dude : instructors) {
					if (dude.doesTeachCourse(c)) {
						c.addInstructor(dude);
					}
				}
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
		instructors = new ArrayList<Instructor>();
		
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
		System.out.println("Parsing instructor data from web page...");

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
		System.out.println("Finished parsing instructor data from web page...");

		Collections.sort(instructors);
	}
	public static void downloadAndParseCourses() throws IOException {
		courses = new ArrayList<Course>();
		
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
		System.out.println("Parsing course data from web page...");
		
		Course c = new Course();
		c.setNumber(99);
		c.setName("n/a");
		c.setUnderGradHours(0);
		c.setGradHours(0);
		courses.add(c);
		
		String inputLine = in.readLine();
		while (inputLine != null) {
			c = new Course();
			
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
		System.out.println("Finished parsing course data from web page...");
		
		Collections.sort(courses);
	}
	
	public static void writeInstructors() throws IOException{
		System.out.println("Writing instructor data to file...");
		
		StringBuilder text = new StringBuilder();
		StringBuilder tsv = new StringBuilder();
		for (Instructor dude : instructors) {
			text.append(dude.fileBlock() + "\n");
			tsv.append(dude.tsvLine() + "\n");
		}
		Files.write(Paths.get(System.getProperty("user.dir") + File.separator + "data" + File.separator + "instructorList.txt"), text.toString()
				.getBytes());
		Files.write(Paths.get(System.getProperty("user.dir") + File.separator + "data" + File.separator + "instructors.tsv"), tsv.toString()
				.getBytes());
		System.out.println("Finished writing instructor data to file..."); 
	}
	public static void writeCourses() throws IOException{
		System.out.println("Writing course data to file...");
		
		StringBuilder text = new StringBuilder();
		StringBuilder tsv = new StringBuilder();
		for (Course c : courses) {
			text.append(c.fileBlock() + "\n");
			tsv.append(c.tsvLine() + "\n");
		}
		Files.write(Paths.get(System.getProperty("user.dir") + File.separator + "data" + File.separator + "courseList.txt"), text.toString()
				.getBytes());
		Files.write(Paths.get(System.getProperty("user.dir") + File.separator + "data" + File.separator + "courses.tsv"), tsv.toString().getBytes());
		System.out.println("Finished writing course data to file...");
	}

	public static int[] setCurrName(String currName) {
		Worker.currName = currName;
		
		int[] indexes = new int[8];
		for(Instructor dude : instructors){
			if(dude.getName().equals(currName)){
				currCourse1 = dude.getCourse1() + "";
				currCourse2 = dude.getCourse2() + "";
				currCourse3 = dude.getCourse3() + "";
				currCourse4 = dude.getCourse4() + "";
				currCourse5 = dude.getCourse5() + "";
				currCourse6 = dude.getCourse6() + "";
				currCourse7 = dude.getCourse7() + "";
				currCourse8 = dude.getCourse8() + "";
				
				String[] arr = coursesToArray();
				for(int i=0; i<arr.length; i++){
					if(arr[i].equals(currCourse1)){
						indexes[0] = i;
					}
					if(arr[i].equals(currCourse2)){
						indexes[1] = i;
					}
					if(arr[i].equals(currCourse3)){
						indexes[2] = i;
					}
					if(arr[i].equals(currCourse4)){
						indexes[3] = i;
					}
					if(arr[i].equals(currCourse5)){
						indexes[4] = i;
					}
					if(arr[i].equals(currCourse6)){
						indexes[5] = i;
					}
					if(arr[i].equals(currCourse7)){
						indexes[6] = i;
					}
					if(arr[i].equals(currCourse8)){
						indexes[7] = i;
					}
				}
				
			}
		}
		
		
		return indexes;
	}

	public static void setCurrCourse1(String currCourse1) {
		Worker.currCourse1 = currCourse1;
	}
	
	public static void setCurrCourse2(String currCourse2) {
		Worker.currCourse2 = currCourse2;
	}
	
	public static void setCurrCourse3(String currCourse3) {
		Worker.currCourse3 = currCourse3;
	}

	public static void setCurrCourse4(String currCourse4) {
		Worker.currCourse4 = currCourse4;
	}

	public static void setCurrCourse5(String currCourse5) {
		Worker.currCourse5 = currCourse5;
	}

	public static void setCurrCourse6(String currCourse6) {
		Worker.currCourse6 = currCourse6;
	}

	public static void setCurrCourse7(String currCourse7) {
		Worker.currCourse7 = currCourse7;
	}

	public static void setCurrCourse8(String currCourse8) {
		Worker.currCourse8 = currCourse8;
	}



}
