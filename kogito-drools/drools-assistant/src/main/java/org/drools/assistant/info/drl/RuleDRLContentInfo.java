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

public class RuleDRLContentInfo extends RuleBasicContentInfo {
	
	private String ruleName;
	private List<RuleLineContentInfo> lhs;
	private List<RuleLineContentInfo> rhs;

	public RuleDRLContentInfo(Integer offset, String content, DRLContentTypeEnum type, String ruleName, List<RuleLineContentInfo> lhs, List<RuleLineContentInfo> rhs) {
		super(offset, content, type);
		this.setRuleName(ruleName);
		this.lhs = lhs;
		this.rhs = rhs;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getRuleName() {
		return ruleName;
	}
	
	public Integer getRuleNameLength() {
		return ruleName.length();
	}
	
	public void addLHSRuleLine(RuleLineContentInfo ruleLine) {
		this.lhs.add(ruleLine);
	}

	public List<RuleLineContentInfo> getLHSRuleLines() {
		return lhs;
	}
	
	public void addRHSRuleLine(RuleLineContentInfo ruleLine) {
		this.rhs.add(ruleLine);
	}

	public List<RuleLineContentInfo> getRHSRuleLines() {
		return rhs;
	}
	
	public List<RuleLineContentInfo> getAllLines() {
		List<RuleLineContentInfo> all = new ArrayList<RuleLineContentInfo>(lhs);
		all.addAll(rhs);
		return all;
	}

}
