package models;

public class Shot {
	private int no, start, end;

	public Shot(int no, int start, int end) {
		this.no = no;
		this.start = start;
		this.end = end;
	}

	public Shot() {
		this(0, 0, 0);
	}

	public Shot setAll(int no, int start, int end) {
		this.no = no;
		this.start = start;
		this.end = end;
		return this;
	}

	public int getNo() {
		return this.no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public int getEnd() {
		return this.end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getStart() {
		return this.start;
	}

	public void setStart(int start) {
		this.start = start;
	}
}