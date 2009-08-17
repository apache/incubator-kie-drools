package org.drools.assistant.option;

public class ReplaceAssistantOption extends AssistantOption {
	
	public ReplaceAssistantOption(String display, String content, Integer offset, Integer length, Integer position) {
		this.display = display;
		this.content = content;
		this.offset = offset;
		this.length = length;
		this.position = position;
	}
	
}