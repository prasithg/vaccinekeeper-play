package models;

public class Stat {
	public long date;
	public int weightkg, heightcm;
	
	public Stat(long date, int weightkg, int heightcm) {
		this.date = date;
		this.weightkg = weightkg;
		this.heightcm = heightcm;
	}
	
	public Stat(){}
	
}
