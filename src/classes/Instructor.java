package classes;

public class Instructor {

	private String name;
	private String email;
	private String degName;
	private int degYear;
	private int rank1;
	private int rank2;
	private int rank3;

	public Instructor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

}
