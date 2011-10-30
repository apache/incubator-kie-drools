package ruleml.translator.ruleml2drl;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.drools.io.ResourceFactory;
import org.xml.sax.InputSource;

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
import reactionruleml.RuleMLType;
import reactionruleml.RuleType;
import reactionruleml.SlotType;
import reactionruleml.ThenType;
import reactionruleml.VarType;
import ruleml.translator.ruleml2drl.DroolsBuilder.Drl;

/**
 * Translator for RuleML intput to Drools DLR-source.
 * 
 * @author Jabarski
 */
public class RuleML2DroolsTranslator  {

	private Drl drl = new Drl();

	private RuleMLGenericProcessor currentProcessor = new RuleMLGenericProcessor();

	private List<DrlPattern> whenPatterns = new ArrayList<DrlPattern>();
	private List<DrlPattern> thenPatterns = new ArrayList<DrlPattern>();
	private PartType currentContext = PartType.NONE;

	public String translate(Object o) {
		if (!(o instanceof String)) {
			throw new IllegalArgumentException(
					"The type of the object to translate is not String");
		}
		String input = (String) o;
		RuleMLType ruleML = createRuleMLFromXML(input);

		currentProcessor.setTranslator(this);

		dispatchType(ruleML);

		getDrl().setPackage_("org.ruleml.translator");
		getDrl().setImports(
				new String[] { "org.ruleml.translator.TestDataModel.*" });

		return getDrl().toString();
	}

	public PartType getCurrentContext() {
		return currentContext;
	}

	public void setCurrentContext(PartType currentContext) {
		this.currentContext = currentContext;
	}

	public List<DrlPattern> getWhenPatterns() {
		return whenPatterns;
	}

	public void setWhenPatterns(List<DrlPattern> whenPatterns) {
		this.whenPatterns = whenPatterns;
	}

	public List<DrlPattern> getThenPatterns() {
		return thenPatterns;
	}

	public void setThenPatterns(List<DrlPattern> thenPatterns) {
		this.thenPatterns = thenPatterns;
	}

	public Drl getDrl() {
		return drl;
	}

	// contains the both context state alternatives for Drools source
	// (when,then)
	enum PartType {
		NONE, WHEN, THEN
	}

	// Representation for the drools pattern
	public static class DrlPattern {
		enum RelComponentType {
			IND, VAR, DATA
		}

		private String className;
		private String variable;
		private List<String> constraints = new ArrayList<String>();
		private String prefix;

		public String getVariable() {
			return variable;
		}

		public void setVariable(String variable) {
			this.variable = variable;
		}

		public void setRelName(String relName) {
			// format the name with capital letter
			this.className = relName.substring(0, 1).toUpperCase()
					+ relName.substring(1);
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public String getPrefix() {
			return prefix;
		}

		public void addConstraint(String constraint) {
			this.getConstraints().add(constraint);
		}

		public List<String> getConstraints() {
			return constraints;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((className == null) ? 0 : className.hashCode());
			result = prime * result
					+ ((constraints == null) ? 0 : constraints.hashCode());
			result = prime * result
					+ ((prefix == null) ? 0 : prefix.hashCode());
			result = prime * result
					+ ((variable == null) ? 0 : variable.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DrlPattern other = (DrlPattern) obj;
			if (className == null) {
				if (other.className != null)
					return false;
			} else if (!className.equals(other.className))
				return false;
			if (constraints == null) {
				if (other.constraints != null)
					return false;
			} else if (!constraints.equals(other.constraints))
				return false;
			if (prefix == null) {
				if (other.prefix != null)
					return false;
			} else if (!prefix.equals(other.prefix))
				return false;
			if (variable == null) {
				if (other.variable != null)
					return false;
			} else if (!variable.equals(other.variable))
				return false;
			return true;
		}

		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer();

			if (getPrefix() != null) {
				sb.append(prefix + "( new ");
			}

			if (getVariable() != null) {
				sb.append(variable + ": ");
			}

			sb.append(className).append("(");
			for (String relComponent : constraints) {
				sb.append(relComponent);
				sb.append(",");
			}

			if (sb.charAt(sb.length() - 1) == ',') {
				sb.replace(sb.length() - 1, sb.length(), "");
			}
			sb.append(")");

			if (getPrefix() != null) {
				sb.append(");");
			}

			return sb.toString();
		}
	}

	/**
	 * Method to read ruleml 1.0 from xml input.
	 * 
	 * @param input The name of the resource.
	 * @return The JAXB parent type for the ruleml 1.0 object model.
	 */
	private static RuleMLType createRuleMLFromXML(String input) {
		try {
			JAXBContext jContext = JAXBContext.newInstance("reactionruleml");
			Unmarshaller unmarshaller = jContext.createUnmarshaller();
			JAXBElement<?> unmarshal = (JAXBElement<?>) unmarshaller
					.unmarshal(new ByteArrayInputStream(input.getBytes()));
			RuleMLType ruleMLType = (RuleMLType) unmarshal.getValue();
			return ruleMLType;
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * The main method for the dispatching of ruleml types within a translation.
	 * 
	 * @param value
	 *            The RuleML type to be transformed: in the most cases this will
	 *            be RuleMLType, but also other subtypes can be transformed.
	 */
	public void dispatchType(Object value) {
		if (value instanceof SlotType) {
			currentProcessor.processSlot((SlotType) value);
		} else if (value instanceof IndType) {
			currentProcessor.processInd((IndType) value);
		} else if (value instanceof VarType) {
			currentProcessor.processVar((VarType) value);
		} else if (value instanceof OpAtomType) {
			currentProcessor.processOpAtom((OpAtomType) value);
		} else if (value instanceof RelType) {
			currentProcessor.processRel((RelType) value);
		} else if (value instanceof IfType) {
			currentProcessor.processIf((IfType) value);
		} else if (value instanceof ThenType) {
			currentProcessor.processThen((ThenType) value);
		} else if (value instanceof DoType) {
			currentProcessor.processDo((DoType) value);
		} else if (value instanceof AssertType) {
			currentProcessor.processAssert((AssertType) value);
		} else if (value instanceof RetractType) {
			currentProcessor.processRetract((RetractType) value);
		} else if (value instanceof QueryType) {
			// if (queryProcessor == null) {
			// queryProcessor = new QueryProcessor(this);
			// }
			// currentProcessor = queryProcessor;
			currentProcessor.processQuery((QueryType) value);
		} else if (value instanceof ImpliesType) {
			currentProcessor.processImplies((ImpliesType) value);
		} else if (value instanceof RuleType) {
			currentProcessor.processRule((RuleType) value);
		} else if (value instanceof AtomType) {
			currentProcessor.processAtom((AtomType) value);
		} else if (value instanceof AndInnerType) {
			currentProcessor.processAnd((AndInnerType) value);
		} else if (value instanceof AndQueryType) {
			currentProcessor.processAnd((AndQueryType) value);
		} else if (value instanceof OrInnerType) {
			currentProcessor.processOr((OrInnerType) value);
		} else if (value instanceof RuleMLType) {
			dispatchType(((RuleMLType) value).getAssertOrRetractOrQuery());
		} else if (value instanceof List) {
			for (Object o : (List) value) {
				dispatchType(o);
			}
		} else if (value instanceof JAXBElement<?>) {
			dispatchType(((JAXBElement<?>) value).getValue());
		}
	}

}
