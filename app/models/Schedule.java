package models;

public class Schedule {
	
	private String shortName;
	private int shot;
//	private long startDate, endDate;
//	private long scheduledDate;
//	public boolean cancelled, complete = false;
//	public String comment = null;
	private final static long msInMonth = 86400000*30;
	
	Schedule(Vaccine vaccine, int shot, long dob){
		this.shortName = vaccine.shortName;
		this.shot = shot;
//		this.startDate = calcStart(vaccine, dob, shot);
//		this.endDate = calcEnd(vaccine, dob, shot);		
	}
	
	Schedule(){
		this(null, 0, 0);
	}
//	private static long calcStart(Vaccine vaccine, long dob, int shot) {
//		return dob + vaccine.shots.get(shot-1).getStart()*msInMonth;
//	}
//	private static long calcEnd(Vaccine vaccine, long dob, int shot){
//		return dob + vaccine.shots.get(shot-1).getEnd()*msInMonth;
//	}

	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public int getShot() {
		return shot;
	}
	public void setShot(int shot) {
		this.shot = shot;
	}
//	public long getStartDate() {
//		return startDate;
//	}
//	public void setStartDate(long startDate) {
//		this.startDate = startDate;
//	}
//	public long getEndDate() {
//		return endDate;
//	}
//	public void setEndDate(long endDate) {
//		this.endDate = endDate;
//	}
//	public long getScheduledDate() {
//		return scheduledDate;
//	}
//	public void setScheduledDate(long scheduledDate) {
//		this.scheduledDate = scheduledDate;
//	}
	
	
}
