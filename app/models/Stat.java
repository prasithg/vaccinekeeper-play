package models;

public class Stat {
	public long date;
	public int weightkg, heightcm, headcm;
	
	public Stat(long date, int weightkg, int heightcm, int headcm) {
		this.date = date;
		this.weightkg = weightkg;
		this.heightcm = heightcm;
		this.headcm = headcm;
	}
	
	public Stat(){}
	
}
