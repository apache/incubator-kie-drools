/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.assistant.info.drl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.drools.assistant.info.RuleRefactorInfo;

public class DRLRuleRefactorInfo implements RuleRefactorInfo {

	private Map<Integer, RuleBasicContentInfo> contents;
	
	public DRLRuleRefactorInfo() {
		contents = new TreeMap<Integer, RuleBasicContentInfo>();
	}
	
	public void addContent(DRLContentTypeEnum type, Integer offset, String content) {
		this.contents.put(offset, new RuleBasicContentInfo(offset, content, type));
	}
	
	public void addContent(DRLContentTypeEnum type, int offset, String content, String ruleName, List<RuleLineContentInfo> lhs, List<RuleLineContentInfo> rhs) {
		RuleDRLContentInfo contentInfo = new RuleDRLContentInfo(offset, content, type, ruleName, lhs, rhs);
		for (RuleLineContentInfo ruleLine : contentInfo.getLHSRuleLines())
			ruleLine.setRule(contentInfo);
		for (RuleLineContentInfo ruleLine : contentInfo.getRHSRuleLines())
			ruleLine.setRule(contentInfo);
		this.contents.put(offset, contentInfo);
	}
	
	public RuleBasicContentInfo getContentAt(int offset) {
		if (contents.containsKey(offset))
			return contents.get(offset);
		int previousKey = 0;
		for (Integer key : contents.keySet()) {
			if (key > offset) {
				RuleBasicContentInfo info = contents.get(previousKey);
				if ((previousKey + info.getContentLength()) >= offset) {
					if (info.getType().equals(DRLContentTypeEnum.RULE))
						return searchInsideTheRule(offset, (RuleDRLContentInfo) info);
					return info;
				}
				return null;
			}
			previousKey = key;
		}
		RuleBasicContentInfo info = contents.get(previousKey);
		if (info.getType().equals(DRLContentTypeEnum.RULE))
			return searchInsideTheRule(offset, (RuleDRLContentInfo) info);
		return (info.getContentLength() + previousKey > offset)?info:null;
	}
	
	private RuleBasicContentInfo searchInsideTheRule(int offset, RuleDRLContentInfo info) {
		// search if inside the rulename
		if (offset <= info.getRuleNameLength() + info.getOffset())
			return null;
		// search in LHS
		for(RuleLineContentInfo ruleLine : info.getLHSRuleLines()) {
			if (offset <= ruleLine.getOffset() + ruleLine.getContentLength())
				return ruleLine;
		}
		// search in RHS
		for(RuleLineContentInfo ruleLine : info.getRHSRuleLines()) {
			if (offset <= ruleLine.getOffset() + ruleLine.getContentLength())
				return ruleLine;
		}
		return null;
	}
	
	public List<RuleBasicContentInfo> getImports() {
		List<RuleBasicContentInfo> imports = new ArrayList<RuleBasicContentInfo>();
		for (Integer key : contents.keySet()) {
			RuleBasicContentInfo info = contents.get(key);
			if (info.getType().equals(DRLContentTypeEnum.IMPORT))
				imports.add(info);
		}
		return imports;
	}
	
}
