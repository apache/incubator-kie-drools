package ruleml.translator.drl2ruleml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import reactionruleml.AndInnerType;
import reactionruleml.AssertType;
import reactionruleml.AtomType;
import reactionruleml.DoType;
import reactionruleml.EqualType;
import reactionruleml.ExistsType;
import reactionruleml.IfType;
import reactionruleml.ImpliesType;
import reactionruleml.IndType;
import reactionruleml.NegType;
import reactionruleml.ObjectFactory;
import reactionruleml.OidType;
import reactionruleml.OpAtomType;
import reactionruleml.OrInnerType;
import reactionruleml.QueryType;
import reactionruleml.RelType;
import reactionruleml.RetractType;
import reactionruleml.RuleMLType;
import reactionruleml.RuleType;
import reactionruleml.SlotType;
import reactionruleml.ThenType;
import reactionruleml.UpdateType;
import reactionruleml.VarType;

/**
 * Class with functions to build JAXB-elements for the translation of
 * drools rules.
 * 
 * @author jabarski
 */
public class RuleMLBuilder {
	private ObjectFactory factory = new ObjectFactory();
	private int uniqueVarNum = 1;

	public JAXBElement<SlotType> createSlot(JAXBElement<?> slotName,
			JAXBElement<?> slotValue) {
		SlotType slotType = factory.createSlotType();
		slotType.getContent().add(slotName);
		slotType.getContent().add(slotValue);
		return factory.createSlot(slotType);
	}

	public JAXBElement<VarType> createVar(String content) {
		// transform the content: if $i -> I
		if (content.startsWith("$")) {
			content = content.substring(1);
		}
		content = content.substring(0, 1).toUpperCase() + content.substring(1);

		VarType varType = factory.createVarType();
		varType.getContent().add(content);
		return factory.createVar(varType);
	}

	public JAXBElement<IndType> createInd(String content) {
		IndType indType = factory.createIndType();
		indType.getContent().add(content);
		return factory.createInd(indType);
	}

	public JAXBElement<AtomType> createAtom(JAXBElement<?>[] content) {
		AtomType atomType = factory.createAtomType();
		atomType.getContent().addAll(Arrays.asList(content));
		return factory.createAtom(atomType);
	}

	public JAXBElement<AndInnerType> createAnd(JAXBElement<?>[] content) {
		AndInnerType andType = factory.createAndInnerType();
		andType.getFormulaOrAtomOrAnd().addAll(convertJAXBArray(content));
		return factory.createAnd(andType);
	}

	public JAXBElement<OrInnerType> createOr(JAXBElement<?>[] content) {
		OrInnerType orType = factory.createOrInnerType();
		orType.getFormulaOrAtomOrAnd().addAll(convertJAXBArray(content));
		return factory.createOr(orType);
	}

	public JAXBElement<NegType> createNeg(JAXBElement<?>[] content) {
		NegType negType = factory.createNegType();

		for (JAXBElement<?> jaxbElement : content) {
			if (jaxbElement.getValue() instanceof AtomType) {
				negType.setAtom((AtomType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof EqualType) {
				negType.setEqual((EqualType) jaxbElement.getValue());
			}
		}
		return factory.createNeg(negType);
	}

	public JAXBElement<?> createExists(JAXBElement<?>[] content) {
		ExistsType existsType = factory.createExistsType();
		for (JAXBElement<?> jaxbElement : content) {
			if (jaxbElement.getValue() instanceof AndInnerType) {
				existsType.setAnd((AndInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof OrInnerType) {
				existsType.setOr((OrInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof AtomType) {
				existsType.setAtom((AtomType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof NegType) {
				existsType.setNeg((NegType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof EqualType) {
				existsType.setEqual((EqualType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof ExistsType) {
				existsType.setExists((ExistsType) jaxbElement.getValue());
			}
		}
		return factory.createExists(existsType);
	}

	public RelType createRel(String content) {
		// transform the name of the relation: to lower case
		content = content.substring(0,1).toLowerCase() + content.substring(1);
		
		RelType relType = factory.createRelType();
		relType.getContent().add(content);
		return relType;
	}

	public JAXBElement<OidType> createOid(String identifier) {
		OidType oidType = factory.createOidType();
		oidType.setVar(createVar(identifier).getValue());
		return factory.createOid(oidType);
	}

	public JAXBElement<OpAtomType> createOp(RelType relType) {
		OpAtomType opAtomType = factory.createOpAtomType();
		opAtomType.setRel(relType);
		return factory.createAtomTypeOp(opAtomType);
	}

	public JAXBElement<IfType> createIf(JAXBElement<?>[] content) {
		IfType ifType = factory.createIfType();

		for (JAXBElement<?> jaxbElement : content) {
			if (jaxbElement.getValue() instanceof AndInnerType) {
				ifType.setAnd((AndInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof OrInnerType) {
				ifType.setOr((OrInnerType) jaxbElement.getValue());
			} else if (jaxbElement.getValue() instanceof AtomType) {
				ifType.setAtom((AtomType) jaxbElement.getValue());
			}
		}

		return factory.createIf(ifType);
	}

	public JAXBElement<DoType> createDo(JAXBElement<?>[] content) {
		DoType doType = factory.createDoType();
		doType.getUpdatePrimitivesContent().addAll(convertJAXBArray(content));
		return factory.createDo(doType);
	}

	public JAXBElement<ThenType> createThen(JAXBElement<?>[] content) {
		ThenType thenType = factory.createThenType();

		for (JAXBElement<?> element : content) {
			if (element.getValue() instanceof AtomType) {
				thenType.setAtom((AtomType) element.getValue());
			} else if (element.getValue() instanceof AssertType) {
				thenType.setAssert((AssertType) element.getValue());
			} else if (element.getValue() instanceof RetractType) {
				thenType.setRetract((RetractType) element.getValue());
			} else if (element.getValue() instanceof UpdateType) {
				thenType.setUpdate((UpdateType) element.getValue());
			} else if (element.getValue() instanceof EqualType) {
				thenType.setEqual((EqualType) element.getValue());
			}
		}

		return factory.createThen(thenType);
	}

	public JAXBElement<AssertType> createAssert(JAXBElement<?>[] content) {
		AssertType assertType = factory.createAssertType();
		assertType.getFormulaOrRulebaseOrAtom().addAll(
				convertJAXBArray(content));
		return factory.createAssert(assertType);
	}

	public JAXBElement<RetractType> createRetract(JAXBElement<?>[] content) {
		RetractType retractType = factory.createRetractType();
		retractType.getFormulaOrRulebaseOrAtom().addAll(
				convertJAXBArray(content));
		return factory.createRetract(retractType);
	}

	public JAXBElement<QueryType> createQuery(JAXBElement<?>[] content) {
		QueryType queryType = factory.createQueryType();
		queryType.getFormulaOrRulebaseOrAtom()
				.addAll(convertJAXBArray(content));
		return factory.createQuery(queryType);
	}

	public JAXBElement<RuleType> createRule(JAXBElement<?>[] content) {
		RuleType ruleType = factory.createRuleType();
		ruleType.getContent().addAll(Arrays.asList(content));
		return factory.createRule(ruleType);
	}

	public JAXBElement<RuleMLType> createRuleML(JAXBElement<?>[] content) {
		RuleMLType ruleMLType = factory.createRuleMLType();
		ruleMLType.getAssertOrRetractOrQuery()
				.addAll(convertJAXBArray(content));
		return factory.createRuleML(ruleMLType);
	}

	public JAXBElement<ImpliesType> createImplies(JAXBElement<?>[] content) {
		ImpliesType impliesType = factory.createImpliesType();
		impliesType.getContent().addAll(Arrays.asList(content));
		return factory.createImplies(impliesType);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<?> convertJAXBArray(JAXBElement<?>[] content) {
		List result = new ArrayList();

		for (JAXBElement<?> jaxbElement : content) {
			if (jaxbElement != null) {
				result.add(jaxbElement.getValue());
			}
		}

		return result;
	}

	public String createUniqueVar() {
		// Random random = new Random();
		// int unique;
		// do {
		// unique = random.nextInt(1000);
		// } while (this.uniqueVars.contains(unique));
		//
		// uniqueVars.add(new Integer(unique));

		return "VAR" + uniqueVarNum++;
	}

	public ObjectFactory getFactory() {
		return factory;
	}

	public String marshal(JAXBElement<?> ruleML) {
		return marshal(ruleML, true);
	}

	public String marshal(JAXBElement<?> ruleML, boolean formatted) {
		try {
			JAXBContext jContext = JAXBContext.newInstance("reactionruleml");

			Marshaller marshaller = jContext.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, formatted);

			StringWriter writer = new StringWriter();
			marshaller.marshal(ruleML.getValue(), writer);
			return writer.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
}
