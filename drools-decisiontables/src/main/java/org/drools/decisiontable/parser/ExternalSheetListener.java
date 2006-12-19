package org.drools.decisiontable.parser;

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

import java.util.Properties;

import org.drools.WorkingMemory;
import org.drools.decisiontable.model.DRLOutput;

/**
 * SheetListener for creating rules from a template
 * 
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 */
public class ExternalSheetListener implements RuleSheetListener {

	private int startRow = -1;

	private boolean tableFinished = false;

	private Row currentRow;

	private Column[] columns;

	private WorkingMemory wm;

	private TemplateContainer templateContainer;

	private int startCol;

	private Generator generator;

	public ExternalSheetListener(final int startRow, final int startCol,
			final String template) {
		this(startRow, startCol, new DefaultTemplateContainer(template));
	}

	public ExternalSheetListener(final int startRow, final int startCol,
			final TemplateContainer tc) {
		this(startRow, startCol, tc, new DefaultTemplateRuleBase(tc));
	}

	public ExternalSheetListener(final int startRow, final int startCol,
			final TemplateContainer tc, final TemplateRuleBase rb) {
		this(startRow, startCol, tc, rb,
				new DefaultGenerator(tc.getTemplates()));
	}

	public ExternalSheetListener(final int startRow, final int startCol,
			final TemplateContainer tc, final TemplateRuleBase ruleBase,
			final Generator generator) {
		this.startRow = startRow - 1;
		this.startCol = startCol - 1;
		columns = tc.getColumns();
		this.templateContainer = tc;
		wm = ruleBase.newWorkingMemory();
		this.generator = generator;
		wm.setGlobal("generator", generator);
	}

	public Properties getProperties() {
		return null;
	}

	public org.drools.decisiontable.model.Package getRuleSet() {
		return null;
	}

	public void finishSheet() {
		if (currentRow != null) {
			wm.assertObject(currentRow);
		}
		wm.fireAllRules();
		wm.dispose();
	}

	public void newCell(int row, int column, String value, int mergedColStart) {
		if (currentRow != null && column >= startCol && value != null
				&& value.trim().length() > 0) {

			// System.out.println("asserting cell " + row + ", " + column + ": "
			// + value);
			Column col = columns[column - startCol];
			Cell cell = new Cell(currentRow, col, value);
			currentRow.addCell(cell);
			wm.assertObject(cell);

		}
	}

	public void newRow(int rowNumber, int columns) {
		if (!tableFinished && rowNumber >= startRow) {
			if (currentRow != null && currentRow.cells.isEmpty()) {
				currentRow = null;
				tableFinished = true;
			} else {
				if (currentRow != null)
					wm.assertObject(currentRow);
				currentRow = new Row(rowNumber);
			}
		}
	}

	public void startSheet(String name) {

	}

	public String renderDRL() {
		DRLOutput out = new DRLOutput();
		out.writeLine(templateContainer.getHeader());

		out.writeLine(generator.getDrl());
		// System.err.println(out.getDRL());
		return out.getDRL();
	}

}
