package ruleml.translator.ruleml2drl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import reactionruleml.AndInnerType;
import reactionruleml.AndQueryType;
import reactionruleml.AssertType;
import reactionruleml.AtomType;
import reactionruleml.DoType;
import reactionruleml.IfType;
import reactionruleml.ImpliesType;
import reactionruleml.IndType;
import reactionruleml.OpAtomType;
import reactionruleml.OrInnerType;
import reactionruleml.QueryType;
import reactionruleml.RelType;
import reactionruleml.RetractType;
import reactionruleml.RuleType;
import reactionruleml.SlotType;
import reactionruleml.ThenType;
import reactionruleml.VarType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Rule;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator.DrlPattern;
import ruleml.translator.ruleml2drl.RuleML2DroolsTranslator.PartType;

public class RuleMLGenericProcessor {

	protected DrlPattern currentDrlPattern;
	protected List<String> boundVars = new ArrayList<String>();
	protected int ruleNumber = 1;
	protected RuleML2DroolsTranslator translator;

	public void setTranslator(RuleML2DroolsTranslator translator) {
		this.translator = translator;
	}

	/*********************** Methods to process single RuleML elements ****************/

	public void processAtom(AtomType atomType) {

		// create the new pattern for this atom
		currentDrlPattern = new DrlPattern();

		translator.dispatchType(atomType.getContent());

		if (translator.getCurrentContext().equals(PartType.WHEN)) {
			translator.getWhenPatterns().add(currentDrlPattern);
		} else {
			translator.getThenPatterns().add(currentDrlPattern);
		}
	}

	public void processSlot(SlotType slotType) {
		List<JAXBElement<?>> content = slotType.getContent();

		// get the slot name
		String slotName = (String) ((IndType) content.get(0).getValue())
				.getContent().get(0);

		// get the slot value
		String slotValue = "";
		String rawSlotValue = "";

		// check if variable (var) or constant(ind)
		if (content.get(1).getValue() instanceof VarType) {
			rawSlotValue = ((VarType) content.get(1).getValue()).getContent()
					.get(0);
			slotValue = "$" + rawSlotValue;
		} else if (content.get(1).getValue() instanceof IndType) {
			rawSlotValue = (String) ((IndType) content.get(1).getValue())
					.getContent().get(0);
			slotValue = "\"" + rawSlotValue + "\"";
		}

		if (translator.getCurrentContext() == PartType.WHEN
				|| translator.getCurrentContext() == PartType.NONE) {
			// current context part = WHEN
			if (currentDrlPattern != null) {
				// check if the var was already bound
				if (boundVars.contains(rawSlotValue)
						|| content.get(1).getValue() instanceof IndType) {
					// set the pattern property ( buyer == $person)
					currentDrlPattern
							.addConstraint(slotName + "==" + slotValue);
				} else {
					// set the pattern property ( $person : buyer)
					currentDrlPattern.addConstraint(slotValue + ":" + slotName);
					// bind the var
					boundVars.add(rawSlotValue);
				}
			} else {
				System.out.println("Error, pattern not initiated !!!");
			}
		} else {
			// current context part = THEN
			if (currentDrlPattern != null) {
				currentDrlPattern.addConstraint(slotValue);
			} else {
				System.out.println("Error, pattern not initiated !!!");
			}
		}
	}

	public void processVar(VarType varType) {
		// List<String> content = varType.getContent();
	}

	public void processInd(IndType indType) {
		// List<Object> content = indType.getContent();
	}

	public void processOpAtom(OpAtomType opAtomType) {
		translator.dispatchType(opAtomType.getRel());
	}

	public void processRel(RelType relType) {
		String relName = relType.getContent().get(0);
		currentDrlPattern.setRelName(relName);
	}

	public void processAnd(AndInnerType andType) {
		translator.dispatchType(andType.getFormulaOrAtomOrAnd());
	}

	public void processAnd(AndQueryType andType) {
		translator.dispatchType(andType.getFormulaOrAtomOrAnd());
	}

	public void processOr(OrInnerType orType) {
		translator.dispatchType(orType.getFormulaOrAtomOrAnd());
	}

	public void processIf(IfType ifType) {
		translator.setCurrentContext(PartType.WHEN);

		if (ifType.getAnd() != null) {
			translator.dispatchType(ifType.getAnd());
		}

		if (ifType.getOr() != null) {
			translator.dispatchType(ifType.getOr());
		}

		if (ifType.getAtom() != null) {
			translator.dispatchType(ifType.getAtom());
		}
	}

	public void processThen(ThenType thenType) {
		translator.setCurrentContext(PartType.THEN);
		translator.dispatchType(thenType.getAtom());
	}

	public void processDo(DoType doType) {
		translator.setCurrentContext(PartType.THEN);
		translator.dispatchType(doType.getUpdatePrimitivesContent());
	}

	public void processImplies(ImpliesType impliesType) {
		translator.dispatchType(impliesType.getContent());
	}

	public void processRule(RuleType ruleType) {
		translator.dispatchType(ruleType.getContent());
	}

	private String varToRetract = "$var";

	public void processRetract(RetractType retractType) {
		List<Object> formulaOrRulebaseOrAtom = retractType
				.getFormulaOrRulebaseOrAtom();

		for (Object o : formulaOrRulebaseOrAtom) {
			if (o instanceof AtomType) {
				// create a empty rule
				Rule currentRule = new Rule();

				// simulate the WEHEN-part
				translator.setCurrentContext(PartType.WHEN);

				// forward
				translator.dispatchType(o);

				// set the bound variable for the pattern
				currentDrlPattern.setVariable(varToRetract);

				// set the name of the rule
				currentRule.setRuleName("rule" + ruleNumber++);

				// if (translator.getThenPatterns().size() != 1) {
				// throw new
				// IllegalStateException("The retract has not the right format: only one pattern is for the translation permited.");
				// }

				// process the specific part of the retract or assert
				// translator.getWhenPatterns().addAll(translator.getThenPatterns());
				currentRule.setWhenPart(translator.getWhenPatterns().toArray());
				currentRule.setThenPart(new String[] { "retract ("
						+ varToRetract + ");" });

				// add the rule to the drools object
				translator.getDrl().addRule(currentRule);

				// reset the patterns and state
				translator.getWhenPatterns().clear();
				translator.getThenPatterns().clear();
				translator.setCurrentContext(PartType.NONE);

			} else if (o instanceof RuleType) {
				// only dispatch forward
				translator.dispatchType(o);
			}
		}
	}

	public void processAssert(AssertType assertType) {
		List<Object> formulaOrRulebaseOrAtom = assertType
				.getFormulaOrRulebaseOrAtom();

		for (Object o : formulaOrRulebaseOrAtom) {
			if (o instanceof AtomType) {
				// create a empty rule
				Rule currentRule = new Rule();

				// simulate THEN-part
				translator.setCurrentContext(PartType.THEN);
				
				// forward
				translator.dispatchType(o);

				currentRule.setRuleName("rule" + ruleNumber++);

				// process the specific part of the retract or assert
				// check for empty WHEN-part of the current rule
				if (translator.getWhenPatterns().isEmpty()) {
					currentRule.setWhenPart(new String[] { "eval(true)" });
				} else {
					currentRule.setWhenPart(translator.getWhenPatterns()
							.toArray());
				}

				for (DrlPattern pattern : translator.getThenPatterns()) {
					pattern.setPrefix("insert");
				}

				currentRule.setThenPart(translator.getThenPatterns().toArray());

				// add the rule to the drools object
				translator.getDrl().addRule(currentRule);

				// reset the patterns and state
				translator.getWhenPatterns().clear();
				translator.getThenPatterns().clear();
				translator.setCurrentContext(PartType.NONE);

			} else if (o instanceof RuleType) {
				// only dispatch forward
				translator.dispatchType(o);
			}
		}
	}

	public void processSpecific(Rule currentRule) {
		// noop
	}

	public void processQuery(QueryType queryType) {
		// noop
	}
}
