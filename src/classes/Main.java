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
	
	public static void main(String[] args) {
		new GUIapp();
		
		Worker.init();
		try {
			if(Worker.initUpdateInstructors){
				Worker.parseInstructors();
				Worker.writeInstructors();
				System.out.println("Finished updating instructor data");
			}
			if(Worker.initUpdateCourses){
				Worker.parseCourses();
				Worker.writeCourses();
				System.out.println("Finished updating course data");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//TODO load from file
		
	}
	
}
