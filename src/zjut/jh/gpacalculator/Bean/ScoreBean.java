package zjut.jh.gpacalculator.Bean;

import java.io.Serializable;

public class ScoreBean implements Serializable{

	private String course;
	private String credit;
	private String score;
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	public String getCredit() {
		return credit;
	}
	public void setCredit(String credit) {
		this.credit = credit;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	
}
