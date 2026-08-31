package model;

public enum Priority {
	LOW("低"),
	MEDIUM("中"),
	HIGH("高");

	public final String display;

	private Priority(String display) {
		this.display = display;
	}
}
