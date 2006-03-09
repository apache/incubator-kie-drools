package org.drools.decisiontable.model;

/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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
		out.writeLine("#generated from Decision Table");
        out.writeLine( "package " + _name + ";" );
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
