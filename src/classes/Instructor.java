package classes;

public class Instructor implements Comparable<Instructor>{

	private String name;
	private String firstName;
	private String lastName;
	private String title;
	private String background;
	private String email;
	private String degName;
	private int degYear;
	private int rank1 = 0;
	private int rank2 = 0;
	private int rank3 = 0;

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
		s.append("rank1\t\t" + rank1 + "\n");
		s.append("rank2\t\t" + rank2 + "\n");
		s.append("rank3\t\t" + rank3 + "\n\n");
		
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
		s.append(rank1 + "\t");
		s.append(rank2 + "\t");
		s.append(rank3);
		
		return s.toString();
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

	public int getRank1() {
		return rank1;
	}

	public void setRank1(int rank1) {
		this.rank1 = rank1;
	}

	public int getRank2() {
		return rank2;
	}

	public void setRank2(int rank2) {
		this.rank2 = rank2;
	}

	public int getRank3() {
		return rank3;
	}

	public void setRank3(int rank3) {
		this.rank3 = rank3;
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
