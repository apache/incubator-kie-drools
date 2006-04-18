package org.drools.decisiontable.model;
/*
 * Copyright 2005 JBoss Inc
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





import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * This is the top of the parse tree. Represents a package of rules once it has
 * been parsed from the spreadsheet. Also is the launching point for dumping out
 * the DRL.
 */
public class Package implements DRLJavaEmitter {

	private String _name;

	private List _imports;

	private List _variables; // List of the application data Variable Objects

	private List _rules;

	private Functions _functions;

	public Package(String name) {
		_name = name;
		_imports = new LinkedList();
		_variables = new LinkedList();
		_rules = new LinkedList();
		_functions = new Functions();
	}

	public void addImport(Import imp) {
		_imports.add(imp);
	}

	public void addVariable(Global varz) {
		_variables.add(varz);
	}

	public void addRule(Rule rule) {
		_rules.add(rule);
	}

	public void addFunctions(String listing) {
		_functions.setFunctionsListing(listing);
	}



	public String getName() {
		return _name;
	}

	public List getImports() {
		return _imports;
	}

	public List getVariables() {
		return _variables;
	}

	public List getRules() {
		return _rules;
	}

	public void renderDRL(DRLOutput out) {		
        out.writeLine( "package " + _name.replace( ' ', '_' ) + ";" );
        out.writeLine("#generated from Decision Table");
		renderDRL(_imports, out);
		renderDRL(_variables, out);
		_functions.renderDRL(out);
		renderDRL(_rules, out);
		

	}

	private void renderDRL(List list, DRLOutput out) {
		for (Iterator it = list.iterator(); it.hasNext();) {
			DRLJavaEmitter emitter = (DRLJavaEmitter) it.next();
			emitter.renderDRL(out);
		}
	}

}