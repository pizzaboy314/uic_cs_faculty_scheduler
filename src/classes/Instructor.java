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
	private int rank1;
	private int rank2;
	private int rank3;

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
