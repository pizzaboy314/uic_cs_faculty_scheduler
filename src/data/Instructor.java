package data;

public class Instructor implements Comparable<Instructor>{

	private String name;
	private String firstName;
	private String lastName;
	private String title;
	private String background;
	private String email;
	private String degName;
	private int degYear;
	private int course1 = 99;
	private int course2 = 99;
	private int course3 = 99;
	private int course4 = 99;
	private int course5 = 99;
	private int course6 = 99;
	private int course7 = 99;
	private int course8 = 99;

	public Instructor() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String[] split = name.split(" "); 
		
		this.name = name;
		this.firstName = split[0];
		this.lastName = split[split.length-1];
	}
	
	public String fileBlock(){
		StringBuilder s = new StringBuilder();
		s.append("name\t\t" + name + "\n");
		s.append("title\t\t" + title + "\n");
		s.append("email\t\t" + email + "\n");
		s.append("degName\t\t" + degName + "\n");
		s.append("degYear\t\t" + degYear + "\n");
		s.append("background\t" + background + "\n");
		s.append("course1\t\t" + course1 + "\n");
		s.append("course2\t\t" + course2 + "\n");
		s.append("course3\t\t" + course3 + "\n");
		s.append("course4\t\t" + course4 + "\n");
		s.append("course5\t\t" + course5 + "\n");
		s.append("course6\t\t" + course6 + "\n");
		s.append("course7\t\t" + course7 + "\n");
		s.append("course8\t\t" + course8 + "\n\n");
		
		return s.toString();
	}
	
	public String tsvLine(){
		StringBuilder s = new StringBuilder();
		s.append(name + "\t");
		s.append(title + "\t");
		s.append(email + "\t");
		s.append(degName + "\t");
		s.append(degYear + "\t");
		s.append(background + "\t");
		s.append(course1 + "\t");
		s.append(course2 + "\t");
		s.append(course3 + "\t");
		s.append(course4 + "\t");
		s.append(course5 + "\t");
		s.append(course6 + "\t");
		s.append(course7 + "\t");
		s.append(course8);
		
		return s.toString();
	}
	
	public String toString(){
		return name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDegName() {
		return degName;
	}

	public void setDegName(String degName) {
		this.degName = degName;
	}

	public int getDegYear() {
		return degYear;
	}

	public void setDegYear(int degYear) {
		this.degYear = degYear;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}


	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public int getCourse1() {
		return course1;
	}

	public void setCourse1(int course1) {
		this.course1 = course1;
	}

	public int getCourse2() {
		return course2;
	}

	public void setCourse2(int course2) {
		this.course2 = course2;
	}

	public int getCourse3() {
		return course3;
	}

	public void setCourse3(int course3) {
		this.course3 = course3;
	}

	public int getCourse4() {
		return course4;
	}

	public void setCourse4(int course4) {
		this.course4 = course4;
	}

	public int getCourse5() {
		return course5;
	}

	public void setCourse5(int course5) {
		this.course5 = course5;
	}

	public int getCourse6() {
		return course6;
	}

	public void setCourse6(int course6) {
		this.course6 = course6;
	}

	public int getCourse7() {
		return course7;
	}

	public void setCourse7(int course7) {
		this.course7 = course7;
	}

	public int getCourse8() {
		return course8;
	}

	public void setCourse8(int course8) {
		this.course8 = course8;
	}
	
	@Override
	public int compareTo(Instructor dude) {
		
		if(!this.lastName.equalsIgnoreCase(dude.getLastName())){
			return this.lastName.compareTo(dude.getLastName());
		}
		if(!this.firstName.equalsIgnoreCase(dude.getFirstName())){
			return this.firstName.compareTo(dude.getFirstName());
		}
		return this.degYear - dude.getDegYear();
	}
}
