package org.drools.assistant.option;

import org.drools.assistant.info.drl.RuleBasicContentInfo;

public class RenameAssistantOption extends AssistantOption {
	
	private RuleBasicContentInfo contentInfo;
	
	public RenameAssistantOption(String display, String content, RuleBasicContentInfo contentInfo, Integer position) {
		this.display = display;
		this.content = content;
		this.contentInfo = contentInfo;
		this.offset = position;
		// FIXME: weird assignation
		this.position = position;
	}

	public RuleBasicContentInfo getContentInfo() {
		return contentInfo;
	}

}
