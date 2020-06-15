/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.pmml_4_2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.dmg.pmml.pmml_4_2.descr.ClusteringModel;
import org.dmg.pmml.pmml_4_2.descr.DataDictionary;
import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.NaiveBayesModel;
import org.dmg.pmml.pmml_4_2.descr.NeuralNetwork;
import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.dmg.pmml.pmml_4_2.descr.RegressionModel;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.dmg.pmml.pmml_4_2.descr.SupportVectorMachineModel;
import org.dmg.pmml.pmml_4_2.descr.TreeModel;
import org.drools.core.io.impl.ByteArrayResource;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.core.util.IoUtils;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.io.ResourceFactory;
import org.kie.pmml.pmml_4_2.model.Miningmodel;
import org.kie.pmml.pmml_4_2.model.PMML4ModelType;
import org.kie.pmml.pmml_4_2.model.PMML4UnitImpl;
import org.kie.pmml.pmml_4_2.model.PMMLMiningField;
import org.kie.pmml.pmml_4_2.model.PMMLOutputField;
import org.kie.pmml.pmml_4_2.model.Treemodel;
import org.kie.pmml.pmml_4_2.model.mining.MiningSegment;
import org.kie.pmml.pmml_4_2.model.mining.MiningSegmentation;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.xml.sax.SAXException;

import static org.drools.core.command.runtime.pmml.PmmlConstants.DEFAULT_ROOT_PACKAGE;

public class PMML4Compiler {

    public static final String PMML_NAMESPACE = DEFAULT_ROOT_PACKAGE;
    public static final String PMML_DROOLS = DEFAULT_ROOT_PACKAGE;
    public static final String PMML = PMML_NAMESPACE + ".descr";
    public static final String SCHEMA_PATH = "xsd/org/dmg/pmml/pmml_4_2/pmml-4-2.xsd";
    public static final String BASE_PACK = PMML_DROOLS.replace('.', '/');

    protected static boolean globalLoaded = false;
    protected static final String[] GLOBAL_TEMPLATES = new String[]{
            "global/pmml_header.drlt",
            "global/pmml_import.drlt",
            "global/rule_meta.drlt",
            "global/modelMark.drlt",

            "global/dataDefinition/common.drlt",
            "global/dataDefinition/rootDataField.drlt",
            "global/dataDefinition/inputBinding.drlt",
            "global/dataDefinition/outputBinding.drlt",
            "global/dataDefinition/ioTypeDeclare.drlt",
            "global/dataDefinition/updateIOField.drlt",
            "global/dataDefinition/inputFromEP.drlt",
            "global/dataDefinition/inputBean.drlt",
            "global/dataDefinition/outputBean.drlt",

            "global/manipulation/confirm.drlt",
            "global/manipulation/mapMissingValues.drlt",
            "global/manipulation/propagateMissingValues.drlt",

            "global/validation/intervalsOnDomainRestriction.drlt",
            "global/validation/valuesNoRestriction.drlt",
            "global/validation/valuesOnDomainRestriction.drlt",
            "global/validation/valuesOnDomainRestrictionMissing.drlt",
            "global/validation/valuesOnDomainRestrictionInvalid.drlt",
    };

    protected static boolean transformationLoaded = false;
    protected static final String[] TRANSFORMATION_TEMPLATES = new String[]{
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
            "transformations/functions/function.drlt"
    };

    protected static boolean miningLoaded = false;
    protected static final String[] MINING_TEMPLATES = new String[]{
            "models/common/mining/miningField.drlt",
            "models/common/mining/miningFieldInvalid.drlt",
            "models/common/mining/miningFieldMissing.drlt",
            "models/common/mining/miningFieldOutlierAsMissing.drlt",
            "models/common/mining/miningFieldOutlierAsExtremeLow.drlt",
            "models/common/mining/miningFieldOutlierAsExtremeUpp.drlt",

            "models/common/targets/targetReshape.drlt",
            "models/common/targets/aliasedOutput.drlt",
            "models/common/targets/addOutputFeature.drlt",
            "models/common/targets/addRelOutputFeature.drlt",
            "models/common/targets/outputQuery.drlt",
            "models/common/targets/outputQueryPredicate.drlt"
    };

    protected static boolean neuralLoaded = false;
    protected static final String[] NEURAL_TEMPLATES = new String[]{
            "models/neural/neuralBeans.drlt",
            "models/neural/neuralWireInput.drlt",
            "models/neural/neuralBuildSynapses.drlt",
            "models/neural/neuralBuildNeurons.drlt",
            "models/neural/neuralLinkSynapses.drlt",
            "models/neural/neuralFire.drlt",
            "models/neural/neuralLayerMaxNormalization.drlt",
            "models/neural/neuralLayerSoftMaxNormalization.drlt",
            "models/neural/neuralOutputField.drlt",
            "models/neural/neuralClean.drlt"
    };

    protected static boolean svmLoaded = false;
    protected static final String[] SVM_TEMPLATES = new String[]{
            "models/svm/svmParams.drlt",
            "models/svm/svmDeclare.drlt",
            "models/svm/svmFunctions.drlt",
            "models/svm/svmBuild.drlt",
            "models/svm/svmInitSupportVector.drlt",
            "models/svm/svmInitInputVector.drlt",
            "models/svm/svmKernelEval.drlt",
            "models/svm/svmOutputGeneration.drlt",
            "models/svm/svmOutputVoteDeclare.drlt",
            "models/svm/svmOutputVote1vN.drlt",
            "models/svm/svmOutputVote1v1.drlt",
    };

    protected static boolean naiveBayesLoaded = false;
    protected static final String[] NAIVE_BAYES_TEMPLATES = new String[]{
            "models/bayes/naiveBayesDeclare.drlt",
            "models/bayes/naiveBayesEvalDiscrete.drlt",
            "models/bayes/naiveBayesEvalContinuous.drlt",
            "models/bayes/naiveBayesBuildCounts.drlt",
            "models/bayes/naiveBayesBuildDistrs.drlt",
            "models/bayes/naiveBayesBuildOuts.drlt",
    };

    protected static boolean simpleRegLoaded = false;
    protected static final String[] SIMPLEREG_TEMPLATES = new String[]{
            "models/regression/regDeclare.drlt",
            "models/regression/regCommon.drlt",
            "models/regression/regParams.drlt",
            "models/regression/regEval.drlt",
            "models/regression/regClaxOutput.drlt",
            "models/regression/regNormalization.drlt",
            "models/regression/regDecumulation.drlt",
    };

    protected static boolean clusteringLoaded = false;
    protected static final String[] CLUSTERING_TEMPLATES = new String[]{
            "models/clustering/clusteringDeclare.drlt",
            "models/clustering/clusteringInit.drlt",
            "models/clustering/clusteringEvalDistance.drlt",
            "models/clustering/clusteringEvalSimilarity.drlt",
            "models/clustering/clusteringMatrixCompare.drlt"
    };

    protected static boolean treeLoaded = false;
    protected static final String[] TREE_TEMPLATES = new String[]{
            "models/tree/treeDeclare.drlt",
            "models/tree/treeCommon.drlt",
            "models/tree/treeInputDeclare.drlt",
            "models/tree/treeInit.drlt",
            "models/tree/treeAggregateEval.drlt",
            "models/tree/treeDefaultEval.drlt",
            "models/tree/treeEval.drlt",
            "models/tree/treeIOBinding.drlt",
            "models/tree/treeMissHandleAggregate.drlt",
            "models/tree/treeMissHandleWeighted.drlt",
            "models/tree/treeMissHandleLast.drlt",
            "models/tree/treeMissHandleNull.drlt",
            "models/tree/treeMissHandleNone.drlt"
    };

    protected static boolean scorecardLoaded = false;
    protected static final String[] SCORECARD_TEMPLATES = new String[]{
            "models/scorecard/scorecardInit.drlt",
            "models/scorecard/scorecardParamsInit.drlt",
            "models/scorecard/scorecardDeclare.drlt",
            "models/scorecard/scorecardDataDeclare.drlt",
            "models/scorecard/scorecardPartialScore.drlt",
            "models/scorecard/scorecardScoring.drlt",
            "models/scorecard/scorecardOutputGeneration.drlt",
            "models/scorecard/scorecardOutputRankCode.drlt",
            "mvel/scorecard/complexPartialScore.mvel"
    };

    protected static final String RESOURCE_PATH = BASE_PACK;
    protected static final String TEMPLATE_PATH = "/" + RESOURCE_PATH + "/templates/";

    private static TemplateRegistry registry;

    private static List<KnowledgeBuilderResult> visitorBuildResults = new ArrayList<KnowledgeBuilderResult>();
    private List<KnowledgeBuilderResult> results;
    private Schema schema;

    private PMML4Helper helper;

    public PMML4Compiler() {
        super();
        this.results = new ArrayList<KnowledgeBuilderResult>();
        helper = new PMML4Helper();
        helper.setPack("org.kie.pmml.pmml_4_2.test");

        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        try {
            schema = sf.newSchema(PMML4Compiler.class.getClassLoader().getResource(SCHEMA_PATH));
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    public PMML4Helper getHelper() {
        return helper;
    }

    private String getRuleUnitClass(PMML4Unit unit) {
        PMML4Model root = unit.getRootModel();
        return root.getRuleUnitClassName();
    }

    public String generateTheory(PMML pmml) {
        StringBuilder sb = new StringBuilder();
        PMML4Unit unit = new PMML4UnitImpl(pmml);

        KieBase visitor;
        try {
            visitor = checkBuildingResources(pmml);
        } catch (IOException e) {
            this.results.add(new PMMLError(e.getMessage()));
            return null;
        }

        KieSession visitorSession = visitor.newKieSession();

        helper.reset();
        visitorSession.setGlobal("registry", registry);
        visitorSession.setGlobal("fld2var", new HashMap());
        visitorSession.setGlobal("utils", helper);
        visitorSession.setGlobal("unitPackageName", helper.getPack());
        visitorSession.setGlobal("ruleUnitClassName", getRuleUnitClass(unit));

        visitorSession.setGlobal("theory", sb);

        visitorSession.insert(pmml);

        visitorSession.fireAllRules();

        String modelEvaluatingRules = sb.toString();

        visitorSession.dispose();

        return modelEvaluatingRules;
    }

    private static void initRegistry() {
        if (registry == null) {
            registry = new SimpleTemplateRegistry();
        }

        if (!globalLoaded) {
            for (String ntempl : GLOBAL_TEMPLATES) {
                prepareTemplate(ntempl);
            }
            globalLoaded = true;
        }

        if (!transformationLoaded) {
            for (String ntempl : TRANSFORMATION_TEMPLATES) {
                prepareTemplate(ntempl);
            }
            transformationLoaded = true;
        }

        if (!miningLoaded) {
            for (String ntempl : MINING_TEMPLATES) {
                prepareTemplate(ntempl);
            }
            miningLoaded = true;
        }
    }

    private static KieBase checkBuildingResources(PMML pmml) throws IOException {
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = ks.getKieClasspathContainer(PMML4Compiler.class.getClassLoader());

        if (registry == null) {
            initRegistry();
        }

        String chosenKieBase = null;

        for (Object o : pmml.getAssociationModelsAndBaselineModelsAndClusteringModels()) {

            if (o instanceof NaiveBayesModel) {
                if (!naiveBayesLoaded) {
                    for (String ntempl : NAIVE_BAYES_TEMPLATES) {
                        prepareTemplate(ntempl);
                    }
                    naiveBayesLoaded = true;
                }
                chosenKieBase = chosenKieBase == null ? "KiePMML-Bayes" : "KiePMML";
            }

            if (o instanceof NeuralNetwork) {
                if (!neuralLoaded) {
                    for (String ntempl : NEURAL_TEMPLATES) {
                        prepareTemplate(ntempl);
                    }
                    neuralLoaded = true;
                }
                chosenKieBase = chosenKieBase == null ? "KiePMML-Neural" : "KiePMML";
            }

            if (o instanceof ClusteringModel) {
                if (!clusteringLoaded) {
                    for (String ntempl : CLUSTERING_TEMPLATES) {
                        prepareTemplate(ntempl);
                    }
                    clusteringLoaded = true;
                }
                chosenKieBase = chosenKieBase == null ? "KiePMML-Cluster" : "KiePMML";
            }

            if (o instanceof SupportVectorMachineModel) {
                if (!svmLoaded) {
                    for (String ntempl : SVM_TEMPLATES) {
                        prepareTemplate(ntempl);
                    }
                    svmLoaded = true;
                }
                chosenKieBase = chosenKieBase == null ? "KiePMML-SVM" : "KiePMML";
            }

            if (o instanceof TreeModel) {
                if (!treeLoaded) {
                    for (String ntempl : TREE_TEMPLATES) {
                        prepareTemplate(ntempl);
                    }
                    treeLoaded = true;
                }
                chosenKieBase = chosenKieBase == null ? "KiePMML-Tree" : "KiePMML";
            }

            if (o instanceof RegressionModel) {
                if (!simpleRegLoaded) {
                    for (String ntempl : SIMPLEREG_TEMPLATES) {
                        prepareTemplate(ntempl);
                    }
                    simpleRegLoaded = true;
                }
                chosenKieBase = chosenKieBase == null ? "KiePMML-Regression" : "KiePMML";
            }

            if (o instanceof Scorecard) {
                if (!scorecardLoaded) {
                    for (String ntempl : SCORECARD_TEMPLATES) {
                        prepareTemplate(ntempl);
                    }
                    scorecardLoaded = true;
                }
                chosenKieBase = chosenKieBase == null ? "KiePMML-Scorecard" : "KiePMML";
            }
        }

        if (chosenKieBase == null) {
            chosenKieBase = "KiePMML-Base";
        }
        return kieContainer.getKieBase(chosenKieBase);
    }

    private static void prepareTemplate(String ntempl) {
        try {
            String path = TEMPLATE_PATH + ntempl;
            Resource res = ResourceFactory.newClassPathResource(path, org.kie.pmml.pmml_4_2.PMML4Compiler.class);
            if (res != null) {
                InputStream stream = res.getInputStream();
                if (stream != null) {
                    registry.addNamedTemplate(path.substring(path.lastIndexOf('/') + 1),
                                              TemplateCompiler.compileTemplate(stream));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String compile(String resource, ClassLoader classLoader) {
        String theory = null;
        Resource cpr = new ClassPathResource(resource);
        try {
            theory = compile(cpr.getInputStream(), classLoader);
        } catch (IOException e) {
            results.add(new PMMLError(e.toString()));
            e.printStackTrace();
        }
        return theory;
    }

    public Resource[] transform(Resource resource, ClassLoader classLoader) {
        String theory = null;
        try {
            theory = compile(resource.getInputStream(), classLoader);
        } catch (IOException e) {
            results.add(new PMMLError(e.toString()));
            e.printStackTrace();
            return new Resource[0];
        }
        return new Resource[]{buildOutputResource(resource, theory)};
    }

    private Resource buildOutputResource(Resource resource, String theory) {
        ByteArrayResource byteArrayResource = new ByteArrayResource(theory.getBytes(IoUtils.UTF8_CHARSET));
        byteArrayResource.setResourceType(ResourceType.PMML);

        if (resource.getSourcePath() != null) {
            String originalPath = resource.getSourcePath();
            int start = originalPath.lastIndexOf(File.separator);
            byteArrayResource.setSourcePath("generated-sources/" + originalPath.substring(start) + ".pmml");
        } else {
            byteArrayResource.setSourcePath("generated-sources/" + helper.getContext() + ".pmml");
        }
        return byteArrayResource;
    }

    private InputStream getInputStreamByFileName(String fileName) {
        InputStream is = null;
        Resource res = ResourceFactory.newClassPathResource(fileName);
        try {
            is = res.getInputStream();
        } catch (Exception e) {
        }
        if (is == null) {
            res = ResourceFactory.newFileResource(fileName);
        }
        try {
            is = res.getInputStream();
        } catch (Exception e) {
            this.results.add(new PMMLError("Unable to retrieve file based resource: " + fileName));
        }
        return is;
    }

    public Map<String, String> getJavaClasses(PMML pmml) throws PMML4Exception {
        Map<String, String> javaClasses = new HashMap<>();
        if (pmml != null && results.isEmpty()) {
            PMML4Unit unit = new PMML4UnitImpl(pmml);
            if (unit != null) {
                List<PMML4Model> models = unit.getModels();
                for (PMML4Model model : models) {
                    Map.Entry<String, String> inputPojo = model.getMappedMiningPojo();
                    Map.Entry<String, String> ruleUnit = model.getMappedRuleUnit();
                    Map<String, String> outputs = model.getOutputTargetPojos();
                    Map.Entry<String, String> outcome = null;
                    if (model.getModelType() == PMML4ModelType.TREE) {
                        outcome = ((Treemodel) model).getTreeNodeJava();
                    }
                    if (inputPojo != null) {
                        javaClasses.put(inputPojo.getKey(), inputPojo.getValue());
                    }
                    if (ruleUnit != null) {
                        javaClasses.put(ruleUnit.getKey(), ruleUnit.getValue());
                    }
                    if (outcome != null) {
                        javaClasses.put(outcome.getKey(), outcome.getValue());
                    }
                    if (outputs != null) {
                        javaClasses.putAll(outputs);
                    }
                }
            }
        }
        return javaClasses;
    }

    public List<PMMLResource> precompile(PMML pmml, ClassLoader classLoader) {
        List<PMMLResource> resources = new ArrayList<>();
        KieServices services = KieServices.Factory.get();
        KieModuleModel module = services.newKieModuleModel();
        this.results = new ArrayList<KnowledgeBuilderResult>();
        helper.setResolver(classLoader);
        PMML4Unit unit = new PMML4UnitImpl(pmml);
        if (unit.containsMiningModel()) {
            Miningmodel rootModel = unit.getRootMiningModel();
            resources = buildResourcesFromModel(pmml, rootModel, null, classLoader, module);
        } else {
            PMML4Model rootModel = unit.getRootModel();
            if (rootModel != null) {
                helper.setPack(rootModel.getModelPackageName());
                KieBaseModel kbm = module.newKieBaseModel(rootModel.getModelId());
                kbm.addPackage(helper.getPack())
                        .setDefault(true)
                        .setEventProcessingMode(EventProcessingOption.CLOUD);
                PMMLResource resource = new PMMLResource(helper.getPack());
                StringBuilder bldr = new StringBuilder(this.compile(pmml, classLoader));
                String extBeanMiningRules = unit.getModelExternalMiningBeansRules(rootModel.getModelId());
                if (extBeanMiningRules != null) {
                    bldr.append(extBeanMiningRules);
                }
                resource.setKieBaseModel(kbm);
                resource.addRules(rootModel.getModelId(), bldr.toString());
                resources.add(resource);
            }
        }
        return resources;
    }

    private void addMissingFieldDefinition(PMML pmml, MiningSegmentation msm, MiningSegment seg) {
        // get the list of models that may contain the field definition
        List<PMML4Model> models = msm.getMiningSegments().stream()
                .filter(s -> s != seg && s.getSegmentIndex() < seg.getSegmentIndex())
                .map(iseg -> {
                    return iseg.getModel();
                })
                .collect(Collectors.toList());
        seg.getModel().getMiningFields().stream().filter(mf -> !mf.isInDictionary()).forEach(pmf -> {
            String fldName = pmf.getName();
            boolean fieldAdded = false;
            for (Iterator<PMML4Model> iter = models.iterator(); iter.hasNext() && !fieldAdded; ) {
                PMML4Model mdl = iter.next();
                PMMLOutputField outfield = mdl.findOutputField(fldName);
                PMMLMiningField target = (outfield != null && outfield.getTargetField() != null) ?
                        mdl.findMiningField(outfield.getTargetField()) : null;
                if (outfield != null) {
                    DataField e = null;
                    if (outfield.getRawDataField() != null && outfield.getRawDataField().getDataType() != null) {
                        e = outfield.getRawDataField();
                    } else if (target != null) {
                        e = copyDataField(target.getRawDataField());
                    }
                    if (e != null) {
                        e.setName(fldName);
                        pmml.getDataDictionary().getDataFields().add(e);
                        BigInteger bi = pmml.getDataDictionary().getNumberOfFields();
                        pmml.getDataDictionary().setNumberOfFields(bi.add(BigInteger.ONE));
                        fieldAdded = true;
                    }
                }
            }
        });
    }

    private DataField copyDataField(DataField df) {
        if (df == null) {
            return null;
        }
        DataField copy = new DataField();
        copy.setDataType(df.getDataType());
        copy.setDisplayName(df.getDisplayName());
        copy.setIsCyclic(df.getIsCyclic());
        copy.setOptype(df.getOptype());
        copy.setName(df.getName());

        return copy;
    }

    protected PMMLResource buildResourceFromSegment(PMML pmml_origin, MiningSegment segment, ClassLoader classLoader, KieModuleModel module) {
        PMML pmml = new PMML();
        DataDictionary dd = pmml_origin.getDataDictionary();
        pmml.setDataDictionary(dd);
        pmml.setHeader(pmml_origin.getHeader());
        pmml.getAssociationModelsAndBaselineModelsAndClusteringModels().add(segment.getModel().getRawModel());
        addMissingFieldDefinition(pmml, segment.getOwner(), segment);
        helper.setPack(segment.getModel().getModelPackageName());//PMML4Helper.pmmlDefaultPackageName()+".mining.segment_"+segment.getSegmentId());

        StringBuilder rules = new StringBuilder(this.compile(pmml, classLoader));
        String extBeanMiningRules = segment.getModel().getExternalBeansMiningRules();
        if (extBeanMiningRules != null) {
            rules.append(extBeanMiningRules);
        }
        KieBaseModel kbModel = module.newKieBaseModel(segment.getOwner().getOwner().getModelId() + "_" + segment.getOwner().getSegmentationId() + "_SEGMENT_" + segment.getSegmentId());
        kbModel.addPackage(helper.getPack())
                .setDefault(false)
                .setEventProcessingMode(EventProcessingOption.CLOUD);
        KieSessionModel ksm = kbModel.newKieSessionModel("SEGMENT_" + segment.getSegmentId());
        ksm.setDefault(true);
        PMMLResource resource = new PMMLResource(helper.getPack());
        resource.setKieBaseModel(kbModel);
        resource.addRules(segment.getModel().getModelId(), rules.toString());
        return resource;
    }

    protected List<PMMLResource> buildResourcesFromModel(PMML pmml, Miningmodel miningModel, List<PMMLResource> resourcesList, ClassLoader classLoader, KieModuleModel module) {
        if (resourcesList == null) {
            resourcesList = new ArrayList<>();
        }
        PMMLResource resource = new PMMLResource(miningModel.getModelPackageName());//new PMMLResource(PMML_DROOLS+".mining.model_"+miningModel.getModelId());
        KieBaseModel rootKieBaseModel = module.newKieBaseModel(resource.getPackageName());
        rootKieBaseModel.addPackage(resource.getPackageName());
        rootKieBaseModel.setDefault(true);
        resource.setKieBaseModel(rootKieBaseModel);
        resource.addRules(miningModel.getModelId(), miningModel.generateRules());
        resourcesList.add(resource);
        getChildResources(pmml, miningModel, resourcesList, classLoader, module);
        return resourcesList;
    }

    protected List<PMMLResource> getChildResources(PMML pmml_origin, Miningmodel parent, List<PMMLResource> resourceList, ClassLoader classLoader, KieModuleModel module) {
        if (parent != null && parent.getSegmentation() != null) {
            MiningSegmentation segmentation = parent.getSegmentation();
            if (segmentation.getMiningSegments() != null) {
                List<MiningSegment> segments = segmentation.getMiningSegments();
                for (MiningSegment segment : segments) {
                    if (segment.getModel() instanceof Miningmodel) {
                        buildResourcesFromModel(pmml_origin, (Miningmodel) segment.getModel(), resourceList, classLoader, module);
                    } else {
                        resourceList.add(buildResourceFromSegment(pmml_origin, segment, classLoader, module));
                    }
                }
            }
        }
        return resourceList;
    }

    public String compile(PMML pmml, ClassLoader classLoader) {
        helper.setResolver(classLoader);

        if (getResults().isEmpty()) {
            return generateTheory(pmml);
        } else {
            return null;
        }
    }

    public String compile(InputStream source, ClassLoader classLoader) {
        this.results = new ArrayList<KnowledgeBuilderResult>();
        PMML pmml = loadModel(PMML, source);
        return compile(pmml, classLoader);
    }

    public List<KnowledgeBuilderResult> getResults() {
        List<KnowledgeBuilderResult> combinedResults = new ArrayList<KnowledgeBuilderResult>(this.results);
        combinedResults.addAll(visitorBuildResults);
        return combinedResults;
    }

    
    public void clearResults() {
        this.results.clear();
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
        } finally {
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
     * @param model the PMML package name (classes derived from a specific schema)
     * @param source the name of the PMML resource storing the predictive model
     * @return the Java Descriptor of the PMML resource
     */
    public PMML loadModel(String model, InputStream source) {
        try {
            if (schema == null) {
                visitorBuildResults.add(new PMMLWarning(ResourceFactory.newInputStreamResource(source), "Could not validate PMML document, schema not available"));
            }
            final JAXBContext jc;
            final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            XMLStreamReader reader = null;
            try {
                Thread.currentThread().setContextClassLoader(PMML4Compiler.class.getClassLoader());

                // Workaround: in Java 9+ Maven does not load the package-info class during plugin execution
                // see https://hibernate.atlassian.net/browse/HHH-12893
                PMML4Compiler.class.getClassLoader().loadClass("org.dmg.pmml.pmml_4_2.descr.package-info");

                Class c = PMML4Compiler.class.getClassLoader().loadClass("org.dmg.pmml.pmml_4_2.descr.PMML");
                jc = JAXBContext.newInstance(c);
                XMLInputFactory xif = XMLInputFactory.newFactory();
                xif.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
                xif.setProperty(XMLInputFactory.SUPPORT_DTD, true);
                reader = xif.createXMLStreamReader(source);
			} finally {
                Thread.currentThread().setContextClassLoader(ccl);
            }
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            if (schema != null) {
                unmarshaller.setSchema(schema);
            }

            if (reader != null) {
                return (PMML) unmarshaller.unmarshal(reader);
            } else {
            	this.results.add(new PMMLError("Unknown error in PMML"));
            	return null;
            }
        } catch (ClassNotFoundException | XMLStreamException | JAXBException e) {
            this.results.add(new PMMLError(e.toString()));
            return null;
        }
    }

    public static void dumpModel(PMML model, OutputStream target) {
        try {
            final JAXBContext jc;
            final ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(PMML4Compiler.class.getClassLoader());
                jc = JAXBContext.newInstance(PMML.class.getPackage().getName(), PMML4Compiler.class.getClassLoader());
            } finally {
                Thread.currentThread().setContextClassLoader(ccl);
            }
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(model, target);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
    
}
