package ruleml.translator.drl2ruleml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.drools.base.ClassFieldReader;
import org.drools.base.evaluators.ComparableEvaluatorsDefinition.IntegerGreaterEvaluator;
import org.drools.base.evaluators.ComparableEvaluatorsDefinition.IntegerGreaterOrEqualEvaluator;
import org.drools.base.evaluators.ComparableEvaluatorsDefinition.IntegerLessEvaluator;
import org.drools.base.evaluators.ComparableEvaluatorsDefinition.IntegerLessOrEqualEvaluator;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition.IntegerEqualEvaluator;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition.ObjectEqualEvaluator;
import org.drools.base.evaluators.EqualityEvaluatorsDefinition.StringEqualEvaluator;
import org.drools.rule.Declaration;
import org.drools.rule.LiteralConstraint;
import org.drools.rule.Pattern;
import org.drools.rule.VariableConstraint;
import org.drools.spi.Evaluator;

import reactionruleml.AtomType;
import reactionruleml.IndType;
import reactionruleml.RelType;
import reactionruleml.VarType;
import ruleml.translator.drl2ruleml.VariableBindingsManager.PropertyInfo;

/**
 * Class with functions that analyzes the constraint of a pattern from a 
 * drools rule.
 *  
 * @author jabarski
 */
public class ConstraintsAnalyzer {

	private List<JAXBElement<?>> other = new ArrayList<JAXBElement<?>>();
	private WhenPartAnalyzer whenPartAnalyzer;

	public List getOther() {
		return this.other;
	}

	/**
	 * Processes all the constrains of the pattern
	 * 
	 * @param pattern
	 *            The pattern that is being analyzed
	 * @return List of property informations that contain data about the
	 *         constraints
	 */
	public List<PropertyInfo> processConstraints(Pattern pattern,
			WhenPartAnalyzer whenPartAnalyzer) {
		this.whenPartAnalyzer = whenPartAnalyzer;
		// the result list
		List<PropertyInfo> propertyInfos = new ArrayList<PropertyInfo>();

		// iterate over the constraints
		for (Object constraint : pattern.getConstraints()) {
			// process the constraint
			PropertyInfo propertyInfo = null;
			if (constraint instanceof Declaration) {
				propertyInfo = processDeclaration(constraint);
			} else if (constraint instanceof LiteralConstraint) {
				propertyInfo = processLiteralConstraint(constraint);
				((LiteralConstraint) constraint).getEvaluator();
			} else if (constraint instanceof VariableConstraint) {
				propertyInfo = processVarConstraint(constraint);
//			} else if (constraint instanceof OrConstraint) {
				// OrConstraint orConstraint = (OrConstraint) constraint;
				// AlphaNodeFieldConstraint[] alphaConstraints = orConstraint
				// .getAlphaConstraints();
				// for (AlphaNodeFieldConstraint alphaNodeFieldConstraint :
				// alphaConstraints) {
				// if (alphaNodeFieldConstraint instanceof LiteralConstraint) {
				// processLiteralConstraint(alphaNodeFieldConstraint);
				// }
				// }
			}

			// if the property is already in the list merge the value, else add
			if (propertyInfo != null && !propertyInfos.contains(propertyInfo)) {
				propertyInfos.add(propertyInfo);
			} else {
				propertyInfos.get(propertyInfos.indexOf(propertyInfo)).setValue(propertyInfo.getValue());
			}
		}

		return propertyInfos;
	}

	/**
	 * Processes Declaration from pattern
	 * 
	 * @param constraint
	 *            Not casted Declaration as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private PropertyInfo processDeclaration(Object constraint) {
		Declaration declaration = (Declaration) constraint;
		ClassFieldReader field = (ClassFieldReader) declaration.getExtractor();

		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(field.getFieldName());
		propertyInfo.setVar(declaration.getIdentifier());
		propertyInfo.setClazz(field.getClassName());
		whenPartAnalyzer.getBindingsManager().put(propertyInfo);
		return propertyInfo;
	}

	/**
	 * Processes LiteralConstraint from pattern (i.e buyer == "John")
	 * 
	 * @param constraint
	 *            Not converted LiteralConstraint as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private PropertyInfo processLiteralConstraint(Object constraint) {
		LiteralConstraint literalConstraint = (LiteralConstraint) constraint;
		ClassFieldReader field = (ClassFieldReader) literalConstraint
				.getFieldExtractor();

		Evaluator evaluator = literalConstraint.getEvaluator();

		String relationName = "";

		if (evaluator instanceof StringEqualEvaluator
				|| evaluator instanceof IntegerEqualEvaluator
				|| evaluator instanceof ObjectEqualEvaluator) {

			// get field name and value from the constraint 
			String fieldName = field.getFieldName();
			String value = literalConstraint.getField().getValue().toString();

			// create the new property Info
			PropertyInfo propertyInfo = new PropertyInfo();
			propertyInfo.setValue(value);
			propertyInfo.setName(fieldName);
			propertyInfo.setClazz(field.getClassName());

			// initiate the bound var in the binding manager
			if (whenPartAnalyzer.getBindingsManager().containsKey(fieldName,value)) {
				whenPartAnalyzer.getBindingsManager().get(fieldName,value).setValue(value);
			}

			return propertyInfo;
		} else if (evaluator instanceof IntegerLessEvaluator) {
			relationName = "LessThan";
		} else if (evaluator instanceof IntegerLessOrEqualEvaluator) {
			relationName = "lessThanOrEqual";
		} else if (evaluator instanceof IntegerGreaterEvaluator) {
			relationName = "greaterThan";
		} else if (evaluator instanceof IntegerGreaterOrEqualEvaluator) {
			relationName = "greaterThanOrEquals";
		}

		List<JAXBElement<?>> content = new ArrayList<JAXBElement<?>>();

		// create the relation
		RelType relType = Drools2RuleMLTranslator.builder
				.createRel(relationName);
		JAXBElement<?> opAtom = Drools2RuleMLTranslator.builder
				.createOp(relType);
		content.add(opAtom);

		// create the data in the relation
		String var = Drools2RuleMLTranslator.builder.createUniqueVar();
		JAXBElement<VarType> varType = Drools2RuleMLTranslator.builder
				.createVar(var);
		content.add(varType);

		JAXBElement<IndType> indType = Drools2RuleMLTranslator.builder
				.createInd(literalConstraint.getField().getValue().toString());
		content.add(indType);

		// create new Atom with the the other relation
		JAXBElement<AtomType> createAtom = Drools2RuleMLTranslator.builder
				.createAtom(content.toArray(new JAXBElement<?>[content.size()]));

		this.other.add(createAtom);

		PropertyInfo propertyInfo = new PropertyInfo();
		propertyInfo.setName(field.getFieldName());
		propertyInfo.setVar(var);
		return propertyInfo;
	}

	/**
	 * Processes VarConstraint from pattern (i.e buyer == $person)
	 * 
	 * @param constraint
	 *            Not casted VarConstraint as constraint
	 * @return The drools reader of the property for the declaration
	 */
	private PropertyInfo processVarConstraint(Object constraint) {
		VariableConstraint variableConstraint = (VariableConstraint) constraint;
		ClassFieldReader field = (ClassFieldReader) variableConstraint
				.getFieldExtractor();

		if (variableConstraint.getRequiredDeclarations().length > 0) {

			String var = variableConstraint.getRequiredDeclarations()[0]
					.getIdentifier();

			PropertyInfo propertyInfo = new PropertyInfo();
			propertyInfo.setVar(var);
			propertyInfo.setName(field.getFieldName());
			propertyInfo.setClazz(field.getClassName());

			if (whenPartAnalyzer.getBindingsManager().containsKey(var)) {
				propertyInfo.setValue(whenPartAnalyzer.getBindingsManager()
						.get(var).getValue());
			}

			return propertyInfo;
		}

		throw new RuntimeException("VariableConstratint is empty !!!"
				+ variableConstraint);
	}

	// /**
	// * Processes OrConstraint from pattern
	// *
	// * @param elements
	// * The content elements (slot, var, rel, ind)
	// * @param constraint
	// * Not casted rConstrainte constraint
	// * @return The drools reader of the property for the declaration
	// */
	// private JAXBElement<SlotType> processOrConstraint(Object constraint) {}
}
