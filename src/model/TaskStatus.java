package model;

public enum TaskStatus {
	TODO("未着手"),
	IN_PROGRESS("進行中"),
	DONE("完了");

	public final String display;

	private TaskStatus(String display) {
		this.display = display;
	}
}
