package org.drools.guvnor.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.PortableObject;

/**
 * This is a decision table model for a guided editor. It is not template or XLS
 * based. (template could be done relatively easily by taking a template, as a
 * String, and then String[][] data and driving the SheetListener interface in
 * the decision tables module).
 * 
 * This works by taking the column definitions, and combining them with the
 * table of data to produce rule models.
 * 
 * 
 * @author Michael Neale
 */
public class GuidedDecisionTable implements PortableObject {

	/**
	 * Number of internal elements before ( used for offsets in serialization )
	 */
	public static final int INTERNAL_ELEMENTS = 2;

	/**
	 * The name - obviously.
	 */
	public String tableName;

	public String parentName;

	// metadata defined for table ( will be represented as a column per table
	// row of DATA
	private List<MetadataCol> metadataCols;

	public List<AttributeCol> attributeCols = new ArrayList<AttributeCol>();

	public List<ConditionCol> conditionCols = new ArrayList<ConditionCol>();

	public List<ActionCol> actionCols = new ArrayList<ActionCol>();

	/**
	 * First column is always row number. Second column is description.
	 * Subsequent ones follow the above column definitions: attributeCols, then
	 * conditionCols, then actionCols, in that order, left to right.
	 */
	public String[][] data = new String[0][0];

	/**
	 * The width to display the description column.
	 */
	public int descriptionWidth = -1;

	public String groupField;

	// TODO: add in precondition(s)

	public GuidedDecisionTable() {
	}

	// /**
	// * Will return an attribute col, or condition or action, depending on what
	// column is requested.
	// * This works through attributes, conditions and then actions, in left to
	// right manner.
	// */
	// public DTColumnConfig getColumnConfiguration(int index) {
	// if (index < attributeCols.size()) {
	// return (DTColumnConfig) attributeCols.get(index);
	// } else if (index < attributeCols.size() + conditionCols.size()) {
	// return (DTColumnConfig) conditionCols.get(index - attributeCols.size());
	// } else {
	// return (DTColumnConfig) actionCols.get(index - attributeCols.size() -
	// conditionCols.size());
	// }
	// }

	/**
	 * This will return a list of valid values. if there is no such
	 * "enumeration" of values, then it will return an empty array.
	 */
	public String[] getValueList(DTColumnConfig col, SuggestionCompletionEngine sce) {
		if (col instanceof AttributeCol) {
			AttributeCol at = (AttributeCol) col;
			if ("no-loop".equals(at.attr) || "enabled".equals(at.attr)) {
				return new String[] { "true", "false" };
			}
		} else if (col instanceof ConditionCol) {
			// conditions: if its a formula etc, just return String[0],
			// otherwise check with the sce
			ConditionCol c = (ConditionCol) col;
			if (c.constraintValueType == ISingleFieldConstraint.TYPE_RET_VALUE
					|| c.constraintValueType == ISingleFieldConstraint.TYPE_PREDICATE) {
				return new String[0];
			} else {
				if (c.valueList != null && !"".equals(c.valueList)) {
					return c.valueList.split(",");
				} else {
					String[] r = sce.getEnumValues(c.factType, c.factField);
					return (r != null) ? r : new String[0];
				}
			}
		} else if (col instanceof ActionSetFieldCol) {
			ActionSetFieldCol c = (ActionSetFieldCol) col;
			if (c.valueList != null && !"".equals(c.valueList)) {
				return c.valueList.split(",");
			} else {
				String[] r = sce.getEnumValues(getBoundFactType(c.boundName), c.factField);
				return (r != null) ? r : new String[0];
			}
		} else if (col instanceof ActionInsertFactCol) {
			ActionInsertFactCol c = (ActionInsertFactCol) col;
			if (c.valueList != null && !"".equals(c.valueList)) {
				return c.valueList.split(",");
			} else {
				String[] r = sce.getEnumValues(c.factType, c.factField);
				return (r != null) ? r : new String[0];
			}
		}

		return new String[0];
	}

	private String getBoundFactType(String boundName) {
		for (Iterator<ConditionCol> iterator = conditionCols.iterator(); iterator.hasNext();) {
			ConditionCol c = iterator.next();
			if (c.boundName.equals(boundName)) {
				return c.factType;
			}
		}
		return null;
	}

	public boolean isNumeric(DTColumnConfig col, SuggestionCompletionEngine sce) {
		if (col instanceof AttributeCol) {
			AttributeCol at = (AttributeCol) col;
			return "salience".equals(at.attr);
		} else if (col instanceof ConditionCol) {
			ConditionCol c = (ConditionCol) col;
			if (c.constraintValueType == ISingleFieldConstraint.TYPE_LITERAL) {
				if (c.operator == null || "".equals(c.operator)) {
					return false;
				}
				String ft = sce.getFieldType(c.factType, c.factField);
				if (ft != null && ft.equals(SuggestionCompletionEngine.TYPE_NUMERIC)) {
					return true;
				}
			}
		} else if (col instanceof ActionSetFieldCol) {
			ActionSetFieldCol c = (ActionSetFieldCol) col;
			String ft = sce.getFieldType(getBoundFactType(c.boundName), c.factField);
			if (ft != null && ft.equals(SuggestionCompletionEngine.TYPE_NUMERIC)) {
				return true;
			}
		} else if (col instanceof ActionInsertFactCol) {
			ActionInsertFactCol c = (ActionInsertFactCol) col;
			String ft = sce.getFieldType(c.factType, c.factField);
			if (ft != null && ft.equals(SuggestionCompletionEngine.TYPE_NUMERIC)) {
				return true;
			}
		}
		// we can reuse text filter from guided editor to enforce this for data
		// entry.
		return false;
	}

	public void setMetadataCols(List<MetadataCol> metadataCols) {
		this.metadataCols = metadataCols;
	}

	public List<MetadataCol> getMetadataCols() {
		if (null == metadataCols) {
			metadataCols = new ArrayList<MetadataCol>();
		}
		return metadataCols;
	}

	/**
	 * Locate index of attribute name if it exists
	 * 
	 * @param attributeName
	 *            Name of metadata we are looking for
	 * @return index of attribute name or -1 if not found
	 */
	public int getMetadataColIndex(String attributeName) {

		for (int i = 0; metadataCols != null && i < metadataCols.size(); i++) {
			if (attributeName.equals(metadataCols.get(i).attr)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Update all rows of metadata with value it attribute is present
	 * 
	 * @param attributeName
	 *            Name of metadata we are looking for
	 * @return true if values update, false if not
	 */
	public boolean updateMetadata(String attributeName, String newValue) {

		// see if metaData exists for
		int metaIndex = getMetadataColIndex(attributeName);
		if (metaIndex < 0)
			return false;

		for (int i = 0; i < data.length; i++) {

			String[] row = data[i];

			row[GuidedDecisionTable.INTERNAL_ELEMENTS + metaIndex] = newValue;
		}
		return true;
	}

}
