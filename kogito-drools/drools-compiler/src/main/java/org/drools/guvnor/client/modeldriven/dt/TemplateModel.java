package org.drools.guvnor.client.modeldriven.dt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.modeldriven.brl.ActionFieldList;
import org.drools.guvnor.client.modeldriven.brl.ActionFieldValue;
import org.drools.guvnor.client.modeldriven.brl.CompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.CompositeFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.DSLSentence;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.FieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.FreeFormLine;
import org.drools.guvnor.client.modeldriven.brl.FromAccumulateCompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.FromCollectCompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.FromCompositeFactPattern;
import org.drools.guvnor.client.modeldriven.brl.IAction;
import org.drools.guvnor.client.modeldriven.brl.IPattern;
import org.drools.guvnor.client.modeldriven.brl.ISingleFieldConstraint;
import org.drools.guvnor.client.modeldriven.brl.PortableObject;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.brl.SingleFieldConstraint;

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
		new RuleModelVisitor(result).visit(this);
		return result;
//        for (IPattern pattern : this.lhs) {
//            if (pattern instanceof FactPattern) {
//                FactPattern fact = (FactPattern) pattern;
//                for (FieldConstraint fc : fact.getFieldConstraints()) {
//                    if (fc instanceof ISingleFieldConstraint) {
//                        ISingleFieldConstraint con = (ISingleFieldConstraint) fc;
//                        if (ISingleFieldConstraint.TYPE_TEMPLATE == con.constraintValueType && !result.containsKey(con.value)) {
//                            result.put(con.value, result.size());
//                        }
//                    }
//                }
//            }
//        }
//        for (IAction action : this.rhs) {
//            if (action instanceof ActionInsertFact) {
//                ActionInsertFact fact = (ActionInsertFact) action;
//                for (ActionFieldValue afv : fact.fieldValues) {
//                	if (afv.nature == ActionFieldValue.TYPE_TEMPLATE && !result.containsKey(afv.value)) {
//                		result.put(afv.value, result.size());
//                	}
//                }
//            }
//        }
//        return result;
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
	
	public static class RuleModelVisitor  {

        private Map<String, Integer> vars;

        public RuleModelVisitor(Map<String, Integer> vars) {
            this.vars = vars;
        }

        public void visit(Object o) {
        	if (o == null) {
        		return;
        	}
        	if (o instanceof RuleModel) {
				visitRuleModel((RuleModel) o);
			} else if (o instanceof FactPattern) {
				visitFactPattern((FactPattern) o);
			} else if (o instanceof CompositeFieldConstraint) {
				visitCompositeFieldConstraint((CompositeFieldConstraint) o);
			} else if (o instanceof SingleFieldConstraint) {
				visitSingleFieldConstraint((SingleFieldConstraint) o);
			} else if (o instanceof CompositeFactPattern) {
				visitCompositeFactPattern((CompositeFactPattern) o);
			} else if (o instanceof FromCompositeFactPattern) {
				visitFromCompositeFactPattern((FromCompositeFactPattern) o);
			} else if (o instanceof FreeFormLine) {
				visitFreeFormLine((FreeFormLine) o);
			} else if (o instanceof FromCollectCompositeFactPattern) {
				visitFromCollectCompositeFactPattern((FromCollectCompositeFactPattern) o);
			} else if (o instanceof FromAccumulateCompositeFactPattern) {
				visitFromAccumulateCompositeFactPattern((FromAccumulateCompositeFactPattern) o);
			} else if (o instanceof DSLSentence) {
				visitDSLSentence((DSLSentence) o);
			} else if (o instanceof ActionFieldList) {
				visitActionFieldList((ActionFieldList) o);
			}
        }
        
        private void visitActionFieldList(ActionFieldList afl) {
            for (ActionFieldValue afv : afl.fieldValues) {
            	if (afv.nature == ActionFieldValue.TYPE_TEMPLATE && !vars.containsKey(afv.value)) {
            		vars.put(afv.value, vars.size());
            	}
            }
		}

		public void visitRuleModel(RuleModel model) {
			if (model.lhs != null) {
				for (IPattern pat : model.lhs) {
					visit(pat);
				}
			}
			if (model.rhs != null) {
				for (IAction action : model.rhs) {
					visit(action);
				}
			}
		}

		private void visitFactPattern(FactPattern pattern) {
            for (FieldConstraint fc : pattern.getFieldConstraints()) {
				visit(fc);
			}
        }
        
		private void visitCompositeFieldConstraint(CompositeFieldConstraint cfc) {
			if (cfc.constraints != null) {
				for (FieldConstraint fc : cfc.constraints) {
					visit(fc);
				}
			}
        }
        
		private void visitSingleFieldConstraint(SingleFieldConstraint sfc) {
        	if (ISingleFieldConstraint.TYPE_TEMPLATE == sfc.constraintValueType && !vars.containsKey(sfc.value)) {
        		vars.put(sfc.value, vars.size());
        	}
        }

		private void visitFreeFormLine(FreeFormLine ffl) {
           parseStringPattern(ffl.text);
        }

		private void visitCompositeFactPattern(CompositeFactPattern pattern) {
			if (pattern.patterns != null) {
				for (FactPattern fp : pattern.patterns) {
					visit(fp);
				}
			}
        }

		private void visitFromCompositeFactPattern(FromCompositeFactPattern pattern) {
        	visit(pattern.getFactPattern());
        }

		private void visitFromCollectCompositeFactPattern(FromCollectCompositeFactPattern pattern) {
        	visit(pattern.getFactPattern());
        	visit(pattern.getRightPattern());
        }

		private void visitFromAccumulateCompositeFactPattern(FromAccumulateCompositeFactPattern pattern) {
        	visit(pattern.getFactPattern());
        	visit(pattern.getSourcePattern());
        	
        	parseStringPattern(pattern.getActionCode());
        	parseStringPattern(pattern.getInitCode());
        	parseStringPattern(pattern.getReverseCode());
        }

		private void visitDSLSentence(final DSLSentence sentence) {
            parseStringPattern(sentence.sentence);
        }
		
		private void parseStringPattern(String text) {
        	if (text == null || text.length() == 0) {
        		return;
        	}
        	int pos = 0;
        	while ((pos = text.indexOf("@{", pos)) != -1) {
        		int end = text.indexOf('}', pos + 2);
        		if (end != -1) {
        			String var = text.substring(pos + 2, end);
        			pos = end + 1;
        			if (!vars.containsKey(var)) {
        				vars.put(var, vars.size());
        			}
        		}
        	}
		}
    }	
}
