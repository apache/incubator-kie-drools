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

package org.drools.assistant.refactor.drl;

import org.drools.assistant.info.drl.RuleBasicContentInfo;


public class VariableBinding extends Variable {
	
	private static final String DEFAULT_VARIABLE_NAME = "$default";
	
	public static String execute(RuleBasicContentInfo contentInfo, int offset) {
		detectCurrentVariables(contentInfo);
		return execute(contentInfo.getContent(), offset);
	}

	public static String execute(String line, int offset) {
		if (offset > line.length())
			return line;
		int position;
		for (position = offset-1; position >= 0; position--) {
			if (line.charAt(position)==' ' || line.charAt(position)=='(' || line.charAt(position)=='\t' || line.charAt(position)==',') {
				String left = line.substring(0, position+1);
				String right = line.substring(position+1, line.length());
				if (!VariableBinding.isVariableOrParameter(right, left) && !VariableBinding.hasVariableAssigned(left))
					return left + getVariableName(right) + right; 
				return line;
			}
		}
		// search to the right the first : before ( and ,
		for (position = 0; position < line.length(); position++) {
			if (line.charAt(position)==':')
				return line;
			if (line.charAt(position)=='(' || line.charAt(position)==',')
				return getVariableName(line) + line;
		}
		return line;
	}
	
	// detect if already has an assigned variable or is the right side of the comparator parameter
	private static boolean isVariableOrParameter(String right, String left) {
		int position;
		for (position = 0; position <= right.length()-1; position++) {
			if (right.charAt(position)==',' || right.charAt(position)=='(')
				break;
			if (right.charAt(position)==':')
				return true;
		}
		for (position = left.length()-1; position >=0; position--) {
			if (left.charAt(position)=='>' || left.charAt(position)=='<' || left.charAt(position)=='=')
				return true;
			if (left.charAt(position)==',')
				return false;
		}
		return false;
	}
	
	private static String getVariableName(String right) {
		for (int position = 0; position < right.length(); position++) {
			if (right.charAt(position)=='(' || right.charAt(position)==')' || right.charAt(position)==',' ||
				right.charAt(position)=='<' || right.charAt(position)=='>' || right.charAt(position)=='=') {
				String varname = "$" + right.substring(0, position).toLowerCase().trim();
				return generateVariableName(varname);
			}
		}
		return DEFAULT_VARIABLE_NAME;
	}
	
	private static boolean hasVariableAssigned(String line) {
		for (int position = line.length()-1; position >=0; position--) {
			if (line.charAt(position)==':')
				return true;
			if (line.charAt(position)==',' || line.charAt(position)=='(')
				return false;
		}
		return false;
	}
	
	private static String generateVariableName(String varname) {
		if (!existsVariableWithSameName(varname))
			return varname + " : ";
		// generate a pseudo-random variable name
		for (int count=1; count <= 100; count++) {
			if (!existsVariableWithSameName(varname+count))
				return varname+count + " : ";
		}
		return varname;
	}
	
}