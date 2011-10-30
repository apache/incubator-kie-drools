package ruleml.translator.drl2ruleml;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.definition.KnowledgePackage;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;

import reactionruleml.DoType;
import reactionruleml.IfType;
import reactionruleml.RuleMLType;
import reactionruleml.RuleType;

/**
 * Translator for Drools rules to RuleML
 * 
 * @author Jabarski
 */
public class Drools2RuleMLTranslator {
	public static RuleMLBuilder builder = new RuleMLBuilder();

	public String translate(Object src) {
		// check the type of the object to transform
		if (!(src instanceof String)) {
			throw new IllegalStateException("The object to transform is not of the correct type String, "
					+ src);
		}
		String ruleBase = (String) src;
		// initiate
		KnowledgeBase kbase = readKnowledgeBase(ruleBase);
		PackageDescr pkgDescr = getPkgDescription(ruleBase);
		List<JAXBElement<?>> content = new ArrayList<JAXBElement<?>>();
		// get the packages from the knowledge base
		Collection<KnowledgePackage> knowledgePackages = kbase.getKnowledgePackages();
		// iterate over the packages
		for (KnowledgePackage knowledgePackage : knowledgePackages) {
			// get the rules from the package
			Collection<org.drools.definition.rule.Rule> rules = knowledgePackage.getRules();
			int i = 0;
			// iterate over the rules in the package
			for (org.drools.definition.rule.Rule rule : rules) {
				// get the rule
				Rule rule_ = (Rule) kbase.getRule(rule.getPackageName(), rule.getName());
				// get the root group element
				GroupElement[] transformedLhs = rule_.getTransformedLhs();
				// process the LHS (WHEN part)
				WhenPartAnalyzer whenPartAnalyzer = new WhenPartAnalyzer();
				JAXBElement<?> whenPart = whenPartAnalyzer.processGroupElement(transformedLhs[0]);
				// process the RHS(Then part), and set the type
				
				ThenPartAnalyzer thenPartAnalyzer = new ThenPartAnalyzer(whenPartAnalyzer);
				JAXBElement<?>[] thenPart = thenPartAnalyzer.processThenPart(pkgDescr.getRules().get(i)
						.getConsequence().toString());
				
				JAXBElement<?>[] ruleType = wrapRule(whenPart, thenPart);
				content.addAll(Arrays.asList(ruleType));
				i++;
			}
		}
		JAXBElement<RuleMLType> ruleML = builder.createRuleML(content
				.toArray(new JAXBElement<?>[content.size()]));
		// serialize and return
		return builder.marshal(ruleML, true);
	}

	/**
	 * Creates the wrapper Rule element
	 * 
	 * @param whenPart
	 *          The when part of the drools rule (LHS)
	 * @param thenPart
	 *          The then part of the drools rule (RHS)
	 * @return The RuleType element object
	 */
	private JAXBElement<?>[] wrapRule(JAXBElement<?> whenPart, JAXBElement<?>[] thenPart) {
		if (whenPart == null) {
			return thenPart;
		} else {
			// -> Rule -> If , Do)
			JAXBElement<IfType> ifType = builder.createIf(new JAXBElement<?>[] { whenPart });
			JAXBElement<DoType> doType = builder.createDo(thenPart);
			JAXBElement<RuleType> ruleType = builder.createRule(new JAXBElement<?>[] { ifType, doType });
			JAXBElement<?> assertContent = builder.createAssert(new JAXBElement<?>[] { ruleType });
			return new JAXBElement<?>[] { assertContent };
		}
	}

	/**
	 * Helper method for reading the knowledge base
	 * 
	 * @return The knowledge base red from file
	 */
	private KnowledgeBase readKnowledgeBase(String ruleBase) {
		final StringReader sr = new StringReader(ruleBase);
		final Resource resource = ResourceFactory.newReaderResource(sr);
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		kbuilder.add(resource, ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error : errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

	/**
	 * Helper method for reading the package description from resource
	 * 
	 * @param ruleBase
	 *          The drools ruleBase as a string
	 * @return The packageDescription
	 */
	private PackageDescr getPkgDescription(String ruleBase) {
		final StringReader sr = new StringReader(ruleBase);
		final Resource resource = ResourceFactory.newReaderResource(sr);
		final DrlParser parser = new DrlParser();
		PackageDescr pkgDescr;
		try {
			pkgDescr = parser.parse(resource.getInputStream());
			return pkgDescr;
		} catch (DroolsParserException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not get the Drools pkgDescription from ruleBase "
					+ ruleBase, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Could not get the Drools pkgDescription from ruleBase "
					+ ruleBase, e);
		}
	}

	/**
	 * Gets all the properties of a data class (relation) to translate them in ruleml.
	 * 
	 * @param pattern
	 *          The Drl Pattern for which the relation should be created.
	 * @return List of all properties of the class represented from the pattern.
	 */
	public static List<String> getPropertiesFromClass(Class<?> clazz) {
		List<String> propertiesList = new ArrayList<String>();
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(clazz, Object.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				propertiesList.add(propertyDescriptor.getDisplayName());
			}
			return propertiesList;
		} catch (IntrospectionException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
