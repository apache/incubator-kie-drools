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

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * Represents a rule.
 */
public class Rule extends DRLElement implements DRLJavaEmitter {

	private static final int MAX_ROWS = 65535;

	private Integer _salience; // Integer as it may be null

	private String _name;

	private Duration _duration; // RIK: New variable to the Rule class (Defines
								// a Duration tag for the rule)

	private String _description; // RIK: New variable to the Rule class (Set
									// the description parameter of the rule
									// tag)

	private String _noLoop; // RIK: New variable to the Rule class (Set the
							// no-loop parameter of the rule tag)

	private String _xorGroup; // RIK: New variable to the Rule class (Set the
								// xor-group parameter of the rule tag)

	private List _lhs;

	private List _rhs;

	private int _spreadsheetRow;

	/**
	 * Create a new rule. Note that the rule name should be post-fixed with the row number,
	 * as one way of providing tracability for errors back to the originating spreadsheet.
	 * @param name The name of the rule. This may be used to calculate DRL row error 
	 * to Spreadsheet row error (just need to keep track of output lines, and map spreadsheetRow to a start
	 * and end range in the rendered output).
	 * @param salience 
	 * @param spreadsheetRow The phyical row number from the spreadsheet.
	 */
	public Rule(String name, Integer salience, int spreadsheetRow) {
		_name = name;
		_salience = salience;
		_description = "";

		_lhs = new LinkedList();
		_rhs = new LinkedList();
		_spreadsheetRow = spreadsheetRow;
	}

	public void addCondition(Condition con) {
		_lhs.add(con);
	}

	public void addConsequence(Consequence con) {
		_rhs.add(con);
	}

	public void renderDRL(DRLOutput out) {
		if (isCommented()) out.writeLine("#" + getComment());
		out.writeLine("rule " + "\"" + _name + "\"");
		if (_description != null) out.writeLine("\t" + _description);
		if (_salience != null) out.writeLine("\tsalience " + _salience);
		if (_xorGroup != null) out.writeLine("\txor-group" + _xorGroup);
		if (_noLoop != null) out.writeLine("\tno-loop" + _noLoop);
		if (_duration != null) out.writeLine("\tduration" + _duration);
		
		out.writeLine("\twhen");
		renderDRL(_lhs, out);
		out.writeLine("\tthen");
		renderDRL(_rhs, out);
		
		out.writeLine("end\n");
	}	

	
	private void renderDRL(List list, DRLOutput out) {
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			DRLJavaEmitter item = (DRLJavaEmitter) iter.next();
			item.renderDRL(out);
		}
	}

	public static int calcSalience(int rowNumber) {
		if (rowNumber > MAX_ROWS) {
			throw new IllegalArgumentException(
					"That row number is above the max: " + MAX_ROWS);
		}
		return MAX_ROWS - rowNumber;
	}

	/**
	 * @param col -
	 *            the column number. Start with zero.
	 * @return The spreadsheet name for this col number, such as "AA" or "AB" or
	 *         "A" and such and such.
	 */
	public static String convertColNumToColName(int i) {

		String result;
		int div = i / 26;
		int mod = i % 26;

		if (div == 0) {
			byte[] c = new byte[1];
			c[0] = (byte) (mod + 65);
			result = byteToString(c);
		} else {
			byte[] firstChar = new byte[1];
			firstChar[0] = (byte) ((div - 1) + 65);

			byte[] secondChar = new byte[1];
			secondChar[0] = (byte) (mod + 65);
			String first = byteToString(firstChar);
			String second = byteToString(secondChar);
			result = first + second;
		}
		return result;

	}

	private static String byteToString(byte[] secondChar) {
		try {
			return new String(secondChar, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unable to convert char to string.", e);
		}
	}

	public List getConditions() {
		return _lhs;
	}

	public List getConsequences() {
		return _rhs;
	}

	public void setSalience(Integer value) // Set the salience of the rule
	{
		_salience = value;
	}

	public Integer getSalience() {
		return _salience;
	}

	public void setName(String value) // Set the name of the rule
	{
		_name = value;
	}

	public String getName() {
		return _name;
	}

	public void setDescription(String value) // Set the description of the
												// rule
	{
		_description = value;
	}

	public void appendDescription(String value) // Set the description of the
												// rule
	{
		_description += value;
	}

	public String getDescription() {
		return _description;
	}

	public void setDuration(Duration value) // Set the duration of the rule
	{
		_duration = value;
	}

	public String getDuration() {
		return _duration.getSnippet();
	}

	public void setXorGroup(String value) // Set the duration of the rule
	{
		_xorGroup = value;
	}

	public String getXorGroup() {
		return _xorGroup;
	}

	public void setNoLoop(String value) // Set the no-loop attribute of the rule
	{
		_noLoop = value;
	}

	public boolean getNoLoop() {
		String value = "false";
		if (_noLoop.compareTo("true") != 0)
			value = _noLoop;
		Boolean b = new Boolean(value);
		return b.booleanValue();
	}
	
	/**
	 * @return The row in the spreadsheet this represents. 
	 * This can be handy when mapping a line error from Parser back to the rule row.
	 * Will need to have a map of ranges of line numbers that each rule covers.
	 * Then can find out the rule that cause it, and this will give the row number to report.
	 */
	public int getSpreadsheetRowNumber() {
		return this._spreadsheetRow;
	}


}
