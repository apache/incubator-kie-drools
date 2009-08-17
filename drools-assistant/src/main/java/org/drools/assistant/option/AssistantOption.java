package org.drools.assistant.option;

public abstract class AssistantOption {
	
	protected String display;
	protected String content;
	protected Integer length;
	protected Integer offset;
	protected Integer position;

	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public void setPosition(Integer position) {
		this.position = position;
	}
	public Integer getPosition() {
		return position;
	}
	
}
