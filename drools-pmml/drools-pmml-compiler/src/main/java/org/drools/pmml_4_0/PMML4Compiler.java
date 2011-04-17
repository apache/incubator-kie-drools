/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.pmml_4_0;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.*;
import org.drools.conf.EventProcessingOption;
import org.drools.io.ResourceFactory;
import org.drools.pmml_4_0.descr.PMML;
import org.drools.runtime.StatefulKnowledgeSession;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class PMML4Compiler implements org.drools.compiler.PMMLCompiler {


    public static final String PMML = "org.drools.pmml_4_0.descr";
    public static final String BASE_PACK = PMML4Compiler.class.getPackage().getName().replace('.','/');


    public static final String[] PMML_VISIT_RULES = new String[] {
            (BASE_PACK+"/pmml_visitor.drl"),
            (BASE_PACK+"/pmml_compiler.drl"),
            (BASE_PACK+"/pmml_informer.drl")
    };



    protected static final String[] NAMED_TEMPLATES = new String[] {

            "global/pmml_header.drlt",


            "global/dataDefinition/rootDataField.drlt",
            "global/dataDefinition/ioTypeDeclare.drlt",
            "global/dataDefinition/updateIOField.drlt",
            "global/dataDefinition/inputFromEP.drlt",

            "global/manipulation/confirm.drlt",
            "global/manipulation/mapMissingValues.drlt",
            "global/manipulation/propagateMissingValues.drlt",

            "global/validation/intervalsOnDomainRestriction.drlt",
            "global/validation/valuesOnDomainRestriction.drlt",
            "global/validation/valuesOnDomainRestrictionMissing.drlt",
            "global/validation/valuesOnDomainRestrictionInvalid.drlt",

            "transformations/normContinuous/boundedLowerOutliers.drlt",
            "transformations/normContinuous/boundedUpperOutliers.drlt",
            "transformations/normContinuous/normContOutliersAsMissing.drlt",
            "transformations/normContinuous/linearTractNormalization.drlt",
            "transformations/normContinuous/lowerExtrapolateLinearTractNormalization.drlt",
            "transformations/normContinuous/upperExtrapolateLinearTractNormalization.drlt",


            "transformations/aggregate/aggregate.drlt",
            "transformations/aggregate/collect.drlt",


            "transformations/simple/constantField.drlt",
            "transformations/simple/aliasedField.drlt",



            "transformations/normDiscrete/indicatorFieldYes.drlt",
            "transformations/normDiscrete/indicatorFieldNo.drlt",
            "transformations/normDiscrete/predicateField.drlt",

            "transformations/discretize/intervalBinning.drlt",
            "transformations/discretize/outOfBinningDefault.drlt",
            "transformations/discretize/outOfBinningMissing.drlt",


            "transformations/mapping/mapping.drlt",


            "transformations/functions/apply.drlt",
            "transformations/functions/function.drlt",

            "models/common/mining/miningField.drlt",
            "models/common/mining/miningFieldInvalid.drlt",
            "models/common/mining/miningFieldMissing.drlt",
            "models/common/mining/miningFieldOutlierAsMissing.drlt",
            "models/common/mining/miningFieldOutlierAsExtremeLow.drlt",
            "models/common/mining/miningFieldOutlierAsExtremeUpp.drlt",

            "models/common/target/targetReshape.drlt",
            "models/common/target/aliasedOutput.drlt",
            "models/common/target/addOutputFeature.drlt",

            "models/neural/neuralBeans.drlt",
            "models/neural/neuralWireInput.drlt",
            "models/neural/neuralBuildSynapses.drlt",
            "models/neural/neuralLinkSynapses.drlt",
            "models/neural/neuralFire.drlt",
            "models/neural/neuralLayerMaxNormalization.drlt",
            "models/neural/neuralLayerSoftMaxNormalization.drlt",
            "models/neural/neuralOutputQuery.drlt",
            "models/neural/neuralOutputQueryPredicate.drlt",
            "models/neural/neuralOutputField.drlt",
            "models/neural/neuralClean.drlt",



            "informer/modelQuestionnaire.drlt",
            "informer/modelQuestion.drlt",
            "informer/modelMultiQuestion.drlt",
            "informer/modelQuestionBinding.drlt",
            "informer/modelQuestionRebinding.drlt" ,
            "informer/modelCreateByBinding.drlt",
            "informer/modelInvalidAnswer.drlt",
            "informer/modelOutputBinding.drlt",
            "informer/modelRevalidate.drlt"

    };



    protected static final String RESOURCE_PATH = BASE_PACK;
    protected static final String TEMPLATE_PATH = "/" + RESOURCE_PATH + "/templates/";

    private static KnowledgeBase visitor;


    private StatefulKnowledgeSession kSession;
    private KnowledgeBase kbase;


    static {
        try {
            List<InputStream> theories = new LinkedList<InputStream>();
            for (String t : PMML_VISIT_RULES)
                theories.add(ResourceFactory.newClassPathResource(t,PMML4Compiler.class).getInputStream());
            visitor = readKnowledgeBase( theories );
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }


    public PMML4Compiler() {
        super();


    }


     protected StatefulKnowledgeSession getSession(String theory) {
        KnowledgeBase kbase = readKnowledgeBase(new ByteArrayInputStream(theory.getBytes()));
        return kbase != null ? kbase.newStatefulKnowledgeSession() : null;
     }


    private static KnowledgeBase readKnowledgeBase(InputStream theory) {
        return readKnowledgeBase(Arrays.asList(theory));
    }

    private static KnowledgeBase readKnowledgeBase(List<InputStream> theory) {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (InputStream is : theory)
            kbuilder.add(ResourceFactory.newInputStreamResource(is), ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
            conf.setEventProcessingMode(EventProcessingOption.STREAM);
            //conf.setConflictResolver(LifoConflictResolver.getInstance());
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}



	protected String generateTheory(PMML pmml) {
        StringBuilder sb = new StringBuilder();

        TemplateRegistry registry = new SimpleTemplateRegistry();
        buildRegistry(registry);

        StatefulKnowledgeSession visitorSession = visitor.newStatefulKnowledgeSession();

        PMML4Wrapper wrapper = new PMML4Wrapper();
            wrapper.setPack("org.drools.pmml_4_0.test");

        visitorSession.setGlobal("theory",sb);
            visitorSession.setGlobal("registry",registry);
            visitorSession.setGlobal("fld2var",new HashMap());
            visitorSession.setGlobal("utils",wrapper);


            visitorSession.insert(pmml);
            visitorSession.fireAllRules();


        String ans = sb.toString();
        visitorSession.dispose();

        return ans;
	}



    private void buildRegistry(TemplateRegistry registry) {
        for (String ntempl : NAMED_TEMPLATES) {
            try {
                String path = TEMPLATE_PATH+ntempl;
                InputStream stream = ResourceFactory.newClassPathResource(path, this.getClass()).getInputStream();

                registry.addNamedTemplate( path.substring(path.lastIndexOf('/') + 1),
                                           TemplateCompiler.compileTemplate(stream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    public String compile(String fileName) {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(RESOURCE_PATH+"/"+fileName);
        return compile(stream);
    }


    public String compile(InputStream source) {
        PMML pmml = loadModel(PMML,source);
        return generateTheory(pmml);
    }


	public void dump(String s, OutputStream ostream) {
		// write to outstream
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(ostream, "UTF-8");
			writer.write(s);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
                if (writer != null) {
                    writer.flush();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}




	/**
	 * Imports a PMML source file, returning a Java descriptor
	 * @param model			the PMML package name (classes derived from a specific schema)
	 * @param source		the name of the PMML resource storing the predictive model
	 * @return				the Java Descriptor of the PMML resource
	 */
	protected PMML  loadModel(String model, InputStream source) {
		try {
			JAXBContext jc = JAXBContext.newInstance(model);
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			return (PMML) unmarshaller.unmarshal(source);
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}

	}








}
