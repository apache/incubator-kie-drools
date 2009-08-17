package org.drools.assistant.refactor.drl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.assistant.info.drl.RuleBasicContentInfo;
import org.drools.assistant.info.drl.RuleDRLContentInfo;
import org.drools.assistant.info.drl.RuleLineContentInfo;
import org.drools.assistant.option.AssistantOption;
import org.drools.assistant.option.RenameAssistantOption;

public class VariableRename extends Variable {

	public static AssistantOption execute(RenameAssistantOption assistantOption, String newVariableName) {
		detectCurrentVariables(assistantOption.getContentInfo());
		if (existsVariableWithSameName(newVariableName))
			return null;
		RuleDRLContentInfo ruleDRLContentInfo = ((RuleLineContentInfo)assistantOption.getContentInfo()).getRule();
		String rule = getAllRuleLines(ruleDRLContentInfo);
		Integer offset = getOffsetFirstLine(ruleDRLContentInfo);
		String content = replaceAllVariables(rule, assistantOption.getContent(), newVariableName);
		assistantOption.setContent(content);
		assistantOption.setOffset(offset);
		assistantOption.setLength(rule.length());
		return assistantOption;
	}
	
	public static String isPossible(RuleBasicContentInfo contentInfo, int offset) {
		String line = contentInfo.getContent();
		int offsetStart = detectVariableOffsetStart(line, offset);
		if (offsetStart==-1)
			return null;
		String right = line.substring(offsetStart);
		String variableName = detectVariableToReplace(right);
		if (variableName==null)
			return null;
		RuleDRLContentInfo ruleContentInfo = ((RuleLineContentInfo)contentInfo).getRule();
		String allRule = getAllRuleLines(ruleContentInfo);
		return hasMoreVariableToReplace(allRule, variableName)?variableName:null;
	}
	
	private static Integer getOffsetFirstLine(RuleDRLContentInfo ruleContentInfo) {
		List<RuleLineContentInfo> lhsLines = ruleContentInfo.getLHSRuleLines();
		return lhsLines.get(0).getOffset();
	}
	
	private static String getAllRuleLines(RuleDRLContentInfo ruleContentInfo) {
		String rule = "";
		for (RuleLineContentInfo ruleLine : ruleContentInfo.getAllLines())
			rule = rule.concat(ruleLine.getContent())+"\n";
		return rule.substring(0, rule.length()-1);
	}
	
	private static boolean hasMoreVariableToReplace(String line, String variableName) {
		variableName = createPatternToFoundAndReplace(variableName);
		Pattern pattern = Pattern.compile(variableName+"\\b");
		Matcher matcher = pattern.matcher(line);
		int variableCount = 0;
		while (matcher.find())
			variableCount++;
		return variableCount > 1;
	}
	
	private static String detectVariableToReplace(String right) {
		for (int position = 0; position < right.length(); position++) {
			if (right.charAt(position)==':' || right.charAt(position)=='.' || right.charAt(position)==')')
				return right.substring(0, position).trim();
			if (right.charAt(position)==',' || right.charAt(position)=='(')
				return null;
		}
		return null;
	}
	
	private static String createPatternToFoundAndReplace(String varname) {
		for (int position = 0; position < varname.length(); position++)
			if (varname.charAt(position)=='$')
				return varname.substring(0, position) + "\\$" + varname.substring(position+1, varname.length());
		return varname;
	}
	
	private static String replaceAllVariables(String rule, String variableName, String newVariableName) {
		newVariableName = createPatternToFoundAndReplace(newVariableName);
		variableName = createPatternToFoundAndReplace(variableName);
		if(variableName.charAt(0)=='$' || variableName.charAt(0)=='\\')
			return rule.replaceAll("\\B("+variableName+")\\b", newVariableName);
		rule = rule.replaceAll("\\b"+variableName+"\\s*\\:\\s*", newVariableName + " : ");
		rule = rule.replaceAll("\\b"+variableName+"\\.", newVariableName + ".");
		rule = rule.replaceAll("\\b"+variableName+"\\s*\\,\\s*", newVariableName + " , ");
		rule = rule.replaceAll("\\b"+variableName+"\\s*\\)\\s*", newVariableName + " ) ");
		rule = rule.replaceAll("\\b"+variableName+"\\s*\\+\\s*", newVariableName + " + ");
		return rule;
	}
	
	private static int detectVariableOffsetStart(String line, int offset) {
		for (int position = offset; position > 0; position--) {
			if (line.charAt(position)==',' || line.charAt(position)=='(')
				return position+1;
			if (line.charAt(position)==':' || line.charAt(position)=='.')
				return -1;
		}
		return 0;
	}
	
}
