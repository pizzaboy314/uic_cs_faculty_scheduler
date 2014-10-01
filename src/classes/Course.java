package classes;

public class Course {
	
	private String name;
	private int number;
	private int hours;
	
	public Course(){
		
	}
	
	public String fileBlock(){
		StringBuilder s = new StringBuilder();
		s.append("CS " + number + ": ");
		s.append("name" + name + "\t\t");
		s.append("hours: " + hours + "\n");
		
		return s.toString();
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

	public int getHours() {
		return hours;
	}

	public void setHours(int hours) {
		this.hours = hours;
	}

}
