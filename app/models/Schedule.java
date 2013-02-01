package models;

import java.util.Date;

public class Schedule {

	public String shortName;
	public int shot;
	public long startDate, endDate;
	public long scheduledDate;
	public long lastModified;
	public boolean cancelled, complete = false;
	public String comment;
	private final static long msInMonth = 86400000*30;

	Schedule(Vaccine vaccine, int shot, long dob){
		this.shortName = vaccine.shortName;
		this.shot = shot;
		this.startDate = calcStart(vaccine, dob, shot);
		this.endDate = calcEnd(vaccine, dob, shot);
		this.lastModified = new Date().getTime();
	}

	public Schedule(){
	}

	private static long calcStart(Vaccine vaccine, long dob, int shot) {
		return dob + vaccine.shots.get(shot-1).start*msInMonth;
	}

	private static long calcEnd(Vaccine vaccine, long dob, int shot){
		return dob + vaccine.shots.get(shot-1).end*msInMonth;
	}

}