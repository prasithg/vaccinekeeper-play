package models;

import java.util.Date;

public class Schedule {

	public String shortName;
	public int shot;
	public long startDate, endDate;
	public long scheduledDate;
	public long lastModified;
	public Boolean cancelled, complete = false;
	public String comment;
	private final static long msInMonth = 86400000*30;

	public Schedule(Vaccine vaccine, int shot, long dob){
		this.shortName = vaccine.shortName;
		this.shot = shot;
		this.startDate = calcStart(vaccine, dob, shot);
		this.endDate = calcEnd(vaccine, dob, shot);
		this.lastModified = new Date().getTime();
	}

	public Schedule(){
	}
	
	public String validate(){
		if(shortName==null) return "Missing shortName";
		if(shot==0) return "Missing shot";
		if(startDate==0 | endDate==0) return "Missing startDate or endDate";
		if(lastModified==0) return "Missing lastModified";
		if(cancelled==null | complete ==null) return "Missing cancelled or complete fields";
		return null;
	}

	private static long calcStart(Vaccine vaccine, long dob, int shot) {
		return dob + vaccine.shots.get(shot-1).start*msInMonth;
	}

	private static long calcEnd(Vaccine vaccine, long dob, int shot){
		return dob + vaccine.shots.get(shot-1).end*msInMonth;
	}
	
	public boolean updateDetails(Schedule schedule){
		if(schedule.lastModified > this.lastModified){
			this.scheduledDate = schedule.scheduledDate;
			this.lastModified = schedule.lastModified;
			this.cancelled = schedule.cancelled;
			this.complete = schedule.complete;
			this.comment = schedule.comment;
			return true;
		} else {
			return false;
		}
	}

}