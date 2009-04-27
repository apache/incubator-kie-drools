package org.drools.guvnor.server.util;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.*;
import org.drools.util.ReflectiveVisitor;

/**
 * This class persists the rule model to DRL and back
 * 
 * @author etirelli
 */
public class BRDRLPersistence implements BRLPersistence {

	private static final BRLPersistence INSTANCE = new BRDRLPersistence();

	private BRDRLPersistence() {
	}

	public static BRLPersistence getInstance() {
		return INSTANCE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.server.util.BRLPersistence#marshal(org.drools.guvnor
	 * .client.modeldriven.brl.RuleModel)
	 */
	public String marshal(RuleModel model) {
		boolean isDSLEnhanced = model.hasDSLSentences();

		StringBuffer buf = new StringBuffer();
		buf.append("rule \"" + model.name + "\"");
		if (null != model.parentName && model.parentName.length() > 0) {
			buf.append(" extends \"" + model.parentName + "\"\n");
		} else {
			buf.append("\n");
		}
		this.marshalMetadata(buf, model);
		this.marshalAttributes(buf, model);

		buf.append("\twhen\n");
		this.marshalLHS(buf, model, isDSLEnhanced);
		buf.append("\tthen\n");
		this.marshalRHS(buf, model, isDSLEnhanced);
		buf.append("end\n");
		return buf.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.drools.guvnor.server.util.BRLPersistence#unmarshal(java.lang.String)
	 */
	public RuleModel unmarshal(String str) {
		throw new UnsupportedOperationException(
				"Still not possible to convert pure DRL to RuleModel");
	}

	/**
	 * Marshal model attributes
	 * 
	 * @param buf
	 * @param model
	 */
	private void marshalAttributes(StringBuffer buf, RuleModel model) {
		boolean hasDialect = false;
		for (int i = 0; i < model.attributes.length; i++) {
			RuleAttribute attr = model.attributes[i];

			buf.append("\t");
			buf.append(attr);

			buf.append("\n");
			if (attr.attributeName.equals("dialect")) {
				hasDialect = true;
			}
		}
		// Un comment below for mvel
		if (!hasDialect) {
			RuleAttribute attr = new RuleAttribute("dialect", "mvel");
			buf.append("\t");
			buf.append(attr);
			buf.append("\n");
		}
	}

	/**
	 * Marshal model metadata
	 * 
	 * @param buf
	 * @param model
	 */
	private void marshalMetadata(StringBuffer buf, RuleModel model) {
		if (model.metadataList != null) {
			for (int i = 0; i < model.metadataList.length; i++) {
				buf.append("\t").append(model.metadataList[i]).append("\n");
			}
		}
	}

	/**
	 * Marshal LHS patterns
	 * 
	 * @param buf
	 * @param model
	 */
	private void marshalLHS(StringBuffer buf, RuleModel model,
			boolean isDSLEnhanced) {
		IPattern[] lhs = model.lhs;
		LHSPatternVisitor visitor = new LHSPatternVisitor(isDSLEnhanced, buf);
		for (int i = 0; i < lhs.length; i++) {
			final IPattern cond = lhs[i];
			visitor.visit(cond);
		}
	}

	private void marshalRHS(StringBuffer buf, RuleModel model,
			boolean isDSLEnhanced) {
		IAction[] rhs = model.rhs;
		RHSActionVisitor visitor = new RHSActionVisitor(isDSLEnhanced, buf);
		for (int i = 0; i < rhs.length; i++) {
			final IAction action = rhs[i];
			visitor.visit(action);
		}
	}

	public static class LHSPatternVisitor extends ReflectiveVisitor {
		private StringBuffer buf;
		private boolean isDSLEnhanced;

		public LHSPatternVisitor(boolean isDSLEnhanced, StringBuffer b) {
			this.isDSLEnhanced = isDSLEnhanced;
			buf = b;
		}

		public void visitFactPattern(FactPattern pattern) {
			buf.append("\t\t");
			if (isDSLEnhanced) {
				// adding passthrough markup
				buf.append(">");
			}
			generateFactPattern(pattern);
			buf.append("\n");
		}

		public void visitFreeFormLine(FreeFormLine ffl) {

			this.buf.append("\t\t");
			if (isDSLEnhanced) {
				buf.append(">");
			}
			this.buf.append(ffl.text);
			this.buf.append("\n");
		}

		public void visitCompositeFactPattern(CompositeFactPattern pattern) {
			buf.append("\t\t");
			if (isDSLEnhanced) {
				// adding passthrough markup
				buf.append(">");
			}
			if (CompositeFactPattern.COMPOSITE_TYPE_EXISTS.equals(pattern.type)) {
				renderCompositeFOL(pattern);
			} else if (CompositeFactPattern.COMPOSITE_TYPE_NOT
					.equals(pattern.type)) {
				renderCompositeFOL(pattern);
			} else if (CompositeFactPattern.COMPOSITE_TYPE_OR
					.equals(pattern.type)) {
				buf.append("( ");
				if (pattern.patterns != null) {
					for (int i = 0; i < pattern.patterns.length; i++) {
						if (i > 0) {
							buf.append(" ");
							buf.append(pattern.type);
							buf.append(" ");
						}
						renderSubPattern(pattern, i);
					}
				}
				buf.append(" )\n");
			}
		}

		private void renderCompositeFOL(CompositeFactPattern pattern) {
			buf.append(pattern.type);
			if (pattern.patterns != null && pattern.patterns.length > 1) {
				buf.append(" (");
				for (int i = 0; i < pattern.patterns.length; i++) {
					renderSubPattern(pattern, i);
					if (i != pattern.patterns.length - 1) {
						buf.append(" and ");
					}
				}
				buf.append(") \n");
			} else {
				buf.append(" ");
				renderSubPattern(pattern, 0);
				buf.append("\n");
			}
		}

		private void renderSubPattern(CompositeFactPattern pattern, int subIndex) {
			if (pattern.patterns == null || pattern.patterns.length == 0)
				return;
			this.generateFactPattern(pattern.patterns[subIndex]);
		}

		public void visitDSLSentence(final DSLSentence sentence) {
			buf.append("\t\t");
			buf.append(sentence.toString());
			buf.append("\n");
		}

		private void generateFactPattern(FactPattern pattern) {
			if (pattern.boundName != null) {
				buf.append(pattern.boundName);
				buf.append(" : ");
			}
			if (pattern.factType != null) {
				buf.append(pattern.factType);
			}
			buf.append("( ");

			// top level constraints
			if (pattern.constraintList != null) {
				generateConstraints(pattern);
			}
			buf.append(")");
		}

		private void generateConstraints(FactPattern pattern) {
			int printedCount = 0;
			for (int i = 0; i < pattern.getFieldConstraints().length; i++) {
				StringBuffer buffer = new StringBuffer();
				generateConstraint(pattern.constraintList.constraints[i],
						false, buffer);
				if (buffer.length() > 0) {
					if (printedCount > 0) {
						buf.append(", ");
					}
					buf.append(buffer);
					printedCount++;
				}
			}
		}
		/**
		 * Recursively process the nested constraints. It will only put brackets
		 * in for the ones that aren't at top level. This makes for more
		 * readable DRL in the most common cases.
		 */
		private void generateConstraint(FieldConstraint con, boolean nested,
				StringBuffer buf) {
			if (con instanceof CompositeFieldConstraint) {
				CompositeFieldConstraint cfc = (CompositeFieldConstraint) con;
				if (nested)
					buf.append("( ");
				FieldConstraint[] nestedConstraints = cfc.constraints;
				if (nestedConstraints != null) {
					for (int i = 0; i < nestedConstraints.length; i++) {
						generateConstraint(nestedConstraints[i], true, buf);
						if (i < (nestedConstraints.length - 1)) {
							// buf.append(" ) ");
							buf.append(cfc.compositeJunctionType + " ");
							// buf.append(" ( ");
						}
					}
				}
				if (nested)
					buf.append(")");
			} else {
				generateSingleFieldConstraint((SingleFieldConstraint) con, buf);
			}
		}

		private void generateSingleFieldConstraint(
				final SingleFieldConstraint constr, StringBuffer buf) {
			if (constr.constraintValueType == ISingleFieldConstraint.TYPE_PREDICATE) {
				buf.append("eval( ");
				buf.append(constr.value);
				buf.append(" )");
			} else {
				if (constr.fieldBinding != null) {
					buf.append(constr.fieldBinding);
					buf.append(" : ");
				}
				if ((constr.operator != null && constr.value != null)
						|| constr.fieldBinding != null) {
					SingleFieldConstraint parent = (SingleFieldConstraint) constr.parent;
					StringBuffer parentBuf = new StringBuffer();
					while (parent != null) {
						parentBuf.insert(0, parent.fieldName + ".");
						parent = (SingleFieldConstraint) parent.parent;
					}
					buf.append(parentBuf);
					buf.append(constr.fieldName);
				}

				addFieldRestriction(buf, constr.constraintValueType,
						constr.operator, constr.value);

				// and now do the connectives.
				if (constr.connectives != null) {
					for (int j = 0; j < constr.connectives.length; j++) {
						final ConnectiveConstraint conn = constr.connectives[j];
						addFieldRestriction(buf, conn.constraintValueType,
								conn.operator, conn.value);
					}
				}
			}
		}

		private void addFieldRestriction(final StringBuffer buf,
				final int type, final String operator, final String value) {
			if (operator == null) {
				return;
			}

			buf.append(" ");
			buf.append(operator);
			buf.append(" ");
			switch (type) {
			case ISingleFieldConstraint.TYPE_RET_VALUE:
				buf.append("( ");
				buf.append(value);
				buf.append(" )");
				break;
			case ISingleFieldConstraint.TYPE_LITERAL:
				buf.append('"');
				buf.append(value);
				buf.append('"');
				break;
			default:
				buf.append(value);
			}
			buf.append(" ");
		}

	}

	public static class RHSActionVisitor extends ReflectiveVisitor {
		private StringBuffer buf;
		private boolean isDSLEnhanced;
		private int idx = 0;

		public RHSActionVisitor(boolean isDSLEnhanced, StringBuffer b) {
			this.isDSLEnhanced = isDSLEnhanced;
			buf = b;
		}

		public void visitActionInsertFact(final ActionInsertFact action) {
			this.generateInsertCall(action, false);
		}

		public void visitActionInsertLogicalFact(
				final ActionInsertLogicalFact action) {
			this.generateInsertCall(action, true);
		}

		public void visitFreeFormLine(FreeFormLine ffl) {

			this.buf.append("\t\t");
			if (isDSLEnhanced) {
				buf.append(">");
			}
			this.buf.append(ffl.text);
			this.buf.append("\n");
		}

		private void generateInsertCall(final ActionInsertFact action,
				final boolean isLogic) {
			buf.append("\t\t");
			if (isDSLEnhanced) {
				buf.append(">");
			}
			if (action.fieldValues.length == 0 && action.getBoundName() == null) {
				buf.append((isLogic) ? "insertLogical( new " : "insert( new ");

				buf.append(action.factType);
				buf.append("() );\n");
			} else {
				buf.append(action.factType);
				if (action.getBoundName()==null)
				{
					buf.append(" fact");
					buf.append(idx);
				} else {
					buf.append(" " + action.getBoundName());
				}
				buf.append(" = new ");
				buf.append(action.factType);
				buf.append("();\n");
				if (action.getBoundName()==null)
				{
					generateSetMethodCalls("fact" + idx, action.fieldValues);
				}else{
					generateSetMethodCalls(action.getBoundName(), action.fieldValues);
				}
					
				buf.append("\t\t");
				if (isDSLEnhanced) {
					buf.append(">");
				}
				if (isLogic) {
					buf.append("insertLogical(");
					if (action.getBoundName()==null){
						buf.append("fact");
						buf.append(idx++);
					}else{
						buf.append(action.getBoundName());
					}
					buf.append(" );\n");
				} else {
					buf.append("insert(");
					if (action.getBoundName()==null){
						buf.append("fact");
						buf.append(idx++);
					}else {
						buf.append(action.getBoundName());
					}

					buf.append(" );\n");
				}
//				buf.append(idx++);
//				buf.append(" );\n");
			}
		}

		public void visitActionUpdateField(final ActionUpdateField action) {
			this.visitActionSetField(action);
			buf.append("\t\t");
			if (isDSLEnhanced) {
				buf.append(">");
			}
			buf.append("update( ");
			buf.append(action.variable);
			buf.append(" );\n");
		}

		public void visitActionGlobalCollectionAdd(
				final ActionGlobalCollectionAdd add) {
			buf.append("\t\t");
			if (isDSLEnhanced) {
				buf.append(">");
			}
			buf.append(add.globalName + ".add(" + add.factName + ");\n");
		}

		public void visitActionRetractFact(final ActionRetractFact action) {
			buf.append("\t\t");
			if (isDSLEnhanced) {
				buf.append(">");
			}
			buf.append("retract( ");
			buf.append(action.variableName);
			buf.append(" );\n");
		}

		public void visitDSLSentence(final DSLSentence sentence) {
			buf.append("\t\t");
			buf.append(sentence.toString());
			buf.append("\n");
		}

		public void visitActionSetField(final ActionSetField action) {
            if ( action instanceof ActionCallMethod ) {
                this.generateSetMethodCallsMethod( (ActionCallMethod) action,
                                                   action.fieldValues );
            } else {
                this.generateSetMethodCalls( action.variable,
                                             action.fieldValues );
            }
        }

		private void generateSetMethodCalls(final String variableName,
				final ActionFieldValue[] fieldValues) {
			for (int i = 0; i < fieldValues.length; i++) {
				buf.append("\t\t");
				if (isDSLEnhanced) {
					buf.append(">");
				}
				buf.append(variableName);

				ActionFieldValue value = fieldValues[i];
				if (value instanceof ActionFieldFunction) {
					buf.append(".");
					buf.append(value.field);
				} else {
					buf.append(".set");
					buf.append(Character.toUpperCase(fieldValues[i].field
							.charAt(0)));
					buf.append(fieldValues[i].field.substring(1));
				}
				buf.append("( ");
				if (fieldValues[i].isFormula()) {
					buf.append(fieldValues[i].value.substring(1));
				} else if (SuggestionCompletionEngine.TYPE_STRING
						.equals(fieldValues[i].type)) {
					buf.append("\"");
					buf.append(fieldValues[i].value);
					buf.append("\"");
				} else {
					buf.append(fieldValues[i].value);
				}
				buf.append(" );\n");
			}
		}



		private void generateSetMethodCallsMethod(final ActionCallMethod action,
                                                  final ActionFieldValue[] fieldValues) {
            buf.append( "\t\t" );
            if ( isDSLEnhanced ) {
                buf.append( ">" );
            }
            buf.append( action.variable );
            buf.append( "." );

            buf.append( action.methodName );

            buf.append( "(" );
            boolean isFirst = true;
            for ( int i = 0; i < fieldValues.length; i++ ) {
                ActionFieldFunction valueFunction = (ActionFieldFunction) fieldValues[i];
                if ( isFirst == true ) {
                    isFirst = false;
                } else {
                    buf.append( "," );
                }

                buf.append( valueFunction.value );
            }
            buf.append( " );\n" );

        }
	}

}
