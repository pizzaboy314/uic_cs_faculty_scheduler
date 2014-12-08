package data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Course implements Comparable<Course>{
	
	private String name;
	private int number;
	private int underGradHours;
	private int gradHours;
	private List<Instructor> instructors;
	
	public Course(){
		instructors = new ArrayList<Instructor>();
	}
	
	public Instructor chooseInstructor() {
		Random r = new Random(System.currentTimeMillis());
		int i = r.nextInt(instructors.size());
		Instructor dude = instructors.get(i);
		return dude;
	}

	public void addInstructor(Instructor dude) {
		if (hasInstructor(dude) == false) {
			instructors.add(dude);
		}
	}

	public boolean hasInstructor(Instructor dude) {
		boolean b = false;
		for (Instructor i : instructors) {
			if (i.getName().equals(dude.getName())) {
				b = true;
			}
		}
		return b;
	}

	public String fileBlock(){
		StringBuilder s = new StringBuilder();
		s.append("CS " + number + ": ");
		s.append(name + "\n");
		if(gradHours == 0){
			s.append("hours: " + underGradHours + "\n");
		} else {
			s.append("undergrad hours: " + underGradHours + "\n");
			s.append("grad hours: " + gradHours + "\n");
		}
		
		return s.toString();
	}
	
	public String tsvLine(){
		StringBuilder s = new StringBuilder();
		s.append(name + "\t");
		s.append(number + "\t");
		s.append(underGradHours + "\t");
		s.append(gradHours + "\t");
		
		return s.toString();
	}
	
	public String toString(){
		if(number > 99){
			return "" + number;
		} else {
			return name;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getUnderGradHours() {
		return underGradHours;
	}

	public void setUnderGradHours(int underGradHours) {
		this.underGradHours = underGradHours;
	}

	public int getGradHours() {
		return gradHours;
	}

	public void setGradHours(int gradHours) {
		this.gradHours = gradHours;
	}
	@Override
	public int compareTo(Course c) {
		return number - c.getNumber();
	}
}
