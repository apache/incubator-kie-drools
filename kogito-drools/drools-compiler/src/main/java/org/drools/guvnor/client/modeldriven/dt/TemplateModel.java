package org.drools.guvnor.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.ActionInsertFact;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.IAction;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.PortableObject;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;

public class TemplateModel extends RuleModel implements PortableObject {
	private Map<String, List<String>> table = new HashMap<String, List<String>>();
	private int rowsCount = 0;
	
	public int getColsCount() {
		return getInterpolationVariables().size();
	}
	
	public int getRowsCount() {
		return rowsCount;
	}

	public void addRow(String[] row) {
		Map<String, Integer> vars = getInterpolationVariables();
		if (row.length != vars.size()) {
			throw new IllegalArgumentException("Invalid numbers of columns: " + 
					row.length + " expected: " + vars.size());
		}
		for (Map.Entry<String, Integer> entry : vars.entrySet()) {
			List<String> list = table.get(entry.getKey());
			if (list == null) {
				list = new ArrayList<String>();
				table.put(entry.getKey(), list);
			}
			if (rowsCount != list.size() ) {
				throw new IllegalArgumentException("invalid list size for " + entry.getKey() + ", expected: " + rowsCount + " was: " + list.size());
			}
			list.add(row[entry.getValue()]);
		}
		rowsCount++;
	}
	
	public void removeRow(int row) {
		if (row >= 0 && row < rowsCount) {
			for (List<String> col : table.values()) {
				col.remove(row);
			}
			rowsCount--;
		} else {
			throw new ArrayIndexOutOfBoundsException(row);
		}
	}
	
	public void putInSync() {
		Map<String, Integer> vars = getInterpolationVariables();
		table.keySet().retainAll(vars.keySet());
		
		vars.keySet().removeAll(table.keySet());
		
		List<String> aux = new ArrayList<String>(rowsCount);
		for (int i = 0; i < rowsCount; i++) {
			aux.add("");
		}
		for (String varName : vars.keySet()) {
			table.put(varName, new ArrayList<String>(aux));
		}
	}
	
	public String[] getInterpolationVariablesList() {
		Map<String, Integer> vars = getInterpolationVariables();
		String[] ret = new String[vars.size()];
		for (Map.Entry<String, Integer> entry: vars.entrySet()) {
			ret[entry.getValue()] = entry.getKey();
		}
		return ret;
	}
	
	public Map<String, Integer> getInterpolationVariables() {
		Map<String, Integer> result = new HashMap<String, Integer>();
        for (IPattern pattern : this.lhs) {
            if (pattern instanceof FactPattern) {
                FactPattern fact = (FactPattern) pattern;
                for (FieldConstraint fc : fact.getFieldConstraints()) {
                    if (fc instanceof ISingleFieldConstraint) {
                        ISingleFieldConstraint con = (ISingleFieldConstraint) fc;
                        if (ISingleFieldConstraint.TYPE_TEMPLATE == con.constraintValueType && !result.containsKey(con.value)) {
                            result.put(con.value, result.size());
                        }
                    }
                }
            }
        }
        for (IAction action : this.rhs) {
            if (action instanceof ActionInsertFact) {
                ActionInsertFact fact = (ActionInsertFact) action;
                for (ActionFieldValue afv : fact.fieldValues) {
                	if (afv.nature == ActionFieldValue.TYPE_TEMPLATE && !result.containsKey(afv.value)) {
                		result.put(afv.value, result.size());
                	}
                }
            }
        }
        return result;
	}

	public Map<String, List<String>> getTable() {
		return table;
	}

	public String[][] getTableAsArray() {
		if (rowsCount <= 0) {
			return new String[0][0];
		}
		String[][] ret = new String[rowsCount][table.size()];
		Map<String, Integer> vars = getInterpolationVariables();
		for (Map.Entry<String, Integer> entry : vars.entrySet()) {
			String varName = entry.getKey();
			int idx = entry.getValue();
			for (int row = 0; row < rowsCount; row++) {
				ret[row][idx] = table.get(varName).get(row);
			}
		}
		return ret;
	}

	public void setValue(String varName, int rowIndex, String newValue) {
		getTable().get(varName).set(rowIndex, newValue);
	}
	
	
}
