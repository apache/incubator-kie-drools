package org.drools.assistant.info.drl;

public class RuleBasicContentInfo {

	private Integer offset;
	private String content;
	private DRLContentTypeEnum type;
	
	public RuleBasicContentInfo(Integer offset, String content, DRLContentTypeEnum type) {
		this.offset = offset;
		this.content = content;
		this.type = type;
	}
	public Integer getOffset() {
		return offset;
	}
	public String getContent() {
		return content;
	}
	public int getContentLength() {
		return content.length();
	}
	public DRLContentTypeEnum getType() {
		return type;
	}

}
