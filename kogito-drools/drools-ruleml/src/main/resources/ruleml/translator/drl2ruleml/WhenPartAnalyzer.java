package ruleml.translator.drl2ruleml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.drools.base.ClassObjectType;
import org.drools.rule.GroupElement;
import org.drools.rule.GroupElement.Type;
import org.drools.rule.Pattern;

import reactionruleml.AndInnerType;
import reactionruleml.IndType;
import reactionruleml.OidType;
import reactionruleml.OpAtomType;
import reactionruleml.RelType;
import reactionruleml.SlotType;
import ruleml.translator.drl2ruleml.VariableBindingsManager.PropertyInfo;
import ruleml.translator.drl2ruleml.VariableBindingsManager.PropertyInfo.ValueType;

/**
 * Analyzer for the WHEN-Part of a drools rule
 * 
 * @author jabarski
 */
public class WhenPartAnalyzer {

	private RuleMLBuilder builder = Drools2RuleMLTranslator.builder;
	private Map<String, JAXBElement<?>> atoms = new HashMap<String, JAXBElement<?>>();
	private VariableBindingsManager bindingsManager = new VariableBindingsManager();

	/**
	 * The main method for a transformation of the lhs-part of drools model.
	 * Transforms the root-groupelement
	 * 
	 * @param groupElement
	 *            The root groupElement
	 * @param thenPart
	 */
	JAXBElement<?> processGroupElement(GroupElement groupElement) {
		
		// check for emtpy when part
		if (groupElement.getChildren().size() == 0) {
			return null;
		}

		// collector for all the atoms in the when part
		List<JAXBElement<?>> elements = new ArrayList<JAXBElement<?>>();

		// iterate over the elements in the element group and collect them in
		// the list (patterns or groups)
		for (Object obj : groupElement.getChildren()) {
			if (obj instanceof Pattern) {
				// process the pattern
				elements.add(processPattern((Pattern) obj));
			} else if (obj instanceof GroupElement) {
				// recursive call to same method
				elements.add(processGroupElement((GroupElement) obj));
			}
		}

		// processes the type of the groupelement (AND,OR,NOT,FORALL,EXISTS)
		Type type = groupElement.getType();
		if (type.equals(Type.AND)) {
			return builder.createAnd(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		} else if (type.equals(Type.OR)) {
			return builder.createOr(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		} else if (type.equals(Type.NOT)) {
			return builder.createNeg(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		} else if (type.equals(Type.EXISTS)) {
			return builder.createExists(elements
					.toArray(new JAXBElement<?>[elements.size()]));
		}
		throw new UnsupportedOperationException();

	}

	/**
	 * Analyzes the pattern.
	 * 
	 * @param pattern
	 *            Pattern to be analyzed
	 * @return Ruleml Atom.
	 */
	private JAXBElement<?> processPattern(Pattern pattern) {

		// add all the constraints to the list (slots)
		List<JAXBElement<?>> atomContent = new ArrayList<JAXBElement<?>>();

		// creates the ruleml REL with the relation name
		JAXBElement<OpAtomType> rel = processRel(pattern);
		atomContent.add(rel);

		// creates the oid if the relation is reified
		if (pattern.getDeclaration() != null) {
			JAXBElement<OidType> oid = builder.createOid(pattern
					.getDeclaration().getIdentifier());
			atomContent.add(oid);

			// add to bound vars
			PropertyInfo propertyInfo = new PropertyInfo();
			propertyInfo.setVar(pattern.getDeclaration().getIdentifier());
			getBindingsManager().put(propertyInfo);
		}

		// process all the constraints of the pattern
		ConstraintsAnalyzer constraintsAnalyzer = new ConstraintsAnalyzer();
		List<PropertyInfo> propertyInfos = constraintsAnalyzer
				.processConstraints(pattern, this);

		// convert the propertyinfos in slots
		List<JAXBElement<SlotType>> slots = convertPropertyInfosInSlots(propertyInfos);
		atomContent.addAll(slots);

		// put the unused relation properties in slots
		List<JAXBElement<SlotType>> unusedProperties = getUnusedProperties(
				slots, pattern);
		atomContent.addAll(unusedProperties);

		if (constraintsAnalyzer.getOther().size() > 0) {
			List<JAXBElement<?>> other = constraintsAnalyzer.getOther();
			other.add(builder.createAtom(atomContent
					.toArray(new JAXBElement<?>[atomContent.size()])));
			JAXBElement<AndInnerType> and = builder.createAnd(other
					.toArray(new JAXBElement<?>[other.size()]));
			return and;
		}

		JAXBElement<?> atom = builder.createAtom(atomContent
				.toArray(new JAXBElement<?>[atomContent.size()]));

		if (pattern.getDeclaration() != null) {
			this.atoms.put(pattern.getDeclaration().getIdentifier(), atom);
		}

		return atom;
	}

	/**
	 * Converts the list with property informations in slots
	 * 
	 * @param propertyInfos
	 *            The list property information created from the pattern
	 *            constraints
	 * @return List with ruleml elements (slots)
	 */
	private List<JAXBElement<SlotType>> convertPropertyInfosInSlots(
			List<PropertyInfo> propertyInfos) {
		List<JAXBElement<SlotType>> result = new ArrayList<JAXBElement<SlotType>>();

		// for all the propertyinfos
		for (PropertyInfo propertyInfo : propertyInfos) {
			JAXBElement<?> content = null;

			if (propertyInfo.getType().equals(ValueType.IND)) {
				content = builder.createInd(propertyInfo.getValue());
			} else if (propertyInfo.getType().equals(ValueType.VAR)) {
				content = builder.createVar(propertyInfo.getVar());
			}

			// create slot
			JAXBElement<SlotType> slot = builder.createSlot(
					builder.createInd(propertyInfo.getName()), content);
			// add to the result list
			result.add(slot);
		}

		return result;
	}

	/**
	 * Returns the unused properties of a relation from a pattern. This
	 * properties has not been seen in any constraint of the pattern.
	 * 
	 * @param slots
	 *            The slots that were found in pattern constraints.
	 * @param pattern
	 *            The pattern that is being analyzed.
	 * @return List of ruleml elements (slots)
	 */
	private List<JAXBElement<SlotType>> getUnusedProperties(
			List<JAXBElement<SlotType>> slots, Pattern pattern) {
		// get the properties of the relation
		List<String> properties = Drools2RuleMLTranslator
				.getPropertiesFromClass(((ClassObjectType) pattern
						.getObjectType()).getClassType());

		// iterate over the slots and remove all the slotted properties
		for (JAXBElement<SlotType> slot : slots) {
			// get the name of the property from the slot (ind)
			Object ind = slot.getValue().getContent().get(0).getValue();
			if (ind instanceof IndType) {
				IndType indType = (IndType) ind;
				String property = (String) indType.getContent().get(0);
				// remove the slotted property from the list
				properties.remove(property);
			}
		}

		// accumulator for the rest slots
		List<JAXBElement<SlotType>> result = new ArrayList<JAXBElement<SlotType>>();

		// iterate over the rest properties and create new slot for each
		for (String property : properties) {
			// create the slot
			result.add(builder.createSlot(builder.createInd(property),
					builder.createVar(builder.createUniqueVar())));
		}

		return result;
	}

	/**
	 * Creates OpAtomType from pattern. This is the name of the relation in
	 * RuleML
	 * 
	 * @param pattern
	 *            The drools pattern that is being analyzed
	 * @return RuleML OpAtomType that represents the name of the relation
	 */
	private JAXBElement<OpAtomType> processRel(Pattern pattern) {
		// create the rel
		String relName = ((ClassObjectType) pattern.getObjectType())
				.getClassType().getSimpleName();
		RelType relType = builder.createRel(relName);
		return builder.createOp(relType);
	}

	public JAXBElement<?> getAtomFromName(String atomName) {
		return this.atoms.get(atomName);
	}

	/**
	 * Getter for the bindings manager
	 * @return The binding manager
	 */
	public VariableBindingsManager getBindingsManager() {
		return bindingsManager;
	}

	/**
	 * Setter for the binding manager.
	 * @param bindingsManager The binding manager to be set.
	 */
	public void setBindingsManager(VariableBindingsManager bindingsManager) {
		this.bindingsManager = bindingsManager;
	}
}
