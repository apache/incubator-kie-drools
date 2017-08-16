package org.drools.pmml.pmml_4_2.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.dmg.pmml.pmml_4_2.descr.FIELDUSAGETYPE;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.dmg.pmml.pmml_4_2.descr.Scorecard;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Unit;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class ScorecardModel extends AbstractModel implements PMML4Model {
    private static String SCORECARD_DATA_POJO_TEMPLATE = "/org/drools/pmml/pmml_4_2/templates/mvel/scorecard/scorecardDataClass.mvel";
    private static String SCORECARD_OUTPUT_POJO_TEMPLATE = "/org/drools/pmml/pmml_4_2/templates/mvel/scorecard/scorecardOutputClass.mvel";
    private Scorecard rawModel;
    private String miningPojoClassName;
    private String outputPojoClassName;

    public Function<ScorecardModel,String> miningPojoClassname = (model) -> {
        return helper.compactUpperCase(getModelId()).concat("ScoreCardData");
    };
    public Function<ScorecardModel,String> outputPojoClassname = (model) -> {
        return "OverallScore";
    };

    public ScorecardModel(String modelId, Scorecard rawModel, PMML4Unit owner) {
        super(modelId, PMML4ModelType.SCORECARD, owner);
        this.rawModel = rawModel;
        initMiningFieldMap();
        initOutputFieldMap();
    }

    @Override
    public MiningSchema getMiningSchema() {
        return rawModel.getExtensionsAndCharacteristicsAndMiningSchemas().stream()
                .filter(serializable -> serializable instanceof MiningSchema)
                .map(serializable -> (MiningSchema)serializable)
                .findFirst().orElse(null);
    }

    @Override
    public Output getOutput() {
        return rawModel.getExtensionsAndCharacteristicsAndMiningSchemas().stream()
                .filter(serializable -> serializable instanceof Output)
                .map(serializable -> (Output)serializable)
                .findFirst().orElse(null);
    }

    @Override
    public List<MiningField> getRawMiningFields() {
        List<MiningField> miningFields = null;
        if (miningFieldMap == null || miningFieldMap.isEmpty()) {
            initMiningFieldMap();
            miningFields = (miningFieldMap != null && !miningFieldMap.isEmpty()) ?
                    new ArrayList<>(miningFieldMap.values()) : new ArrayList<>();
        } else {
            miningFields = new ArrayList<>(miningFieldMap.values());
        }
        return miningFields;
    }

    @Override
    public List<OutputField> getRawOutputFields() {
        List<OutputField> outputFields = null;
        if (outputFieldMap == null || outputFieldMap.isEmpty()) {
            initOutputFieldMap();
            outputFields = (outputFieldMap != null && !outputFieldMap.isEmpty()) ?
                    new ArrayList<>(outputFieldMap.values()) : new ArrayList<>();
        } else {
            outputFields = new ArrayList<>(outputFieldMap.values());
        }
        return outputFields;
    }

    private boolean isValidMiningField(PMMLDataField field) {
        if (miningFieldMap.containsKey(field.getName())) {
            MiningField miningField = miningFieldMap.get(field.getName());
            if (miningField != null && miningField.getUsageType() != FIELDUSAGETYPE.PREDICTED)
                return true;
        }
        return false;
    }

    @Override
    public List<PMMLDataField> getMiningFields() {
        List<PMMLDataField> dataDictionaryFields = getOwner().getDataDictionaryFields();
        List<PMMLDataField> miningFieldList = dataDictionaryFields.stream()
                .filter(ddf -> isValidMiningField(ddf))
                .collect(Collectors.toList());

        return (miningFieldList != null && !miningFieldList.isEmpty()) ? miningFieldList : new ArrayList<>();
    }

    @Override
    public List<PMMLDataField> getOutputFields() {
        List<PMMLDataField> outputFields = outputFieldMap.values().stream()
                .map(outputField -> new PMMLDataField(outputField))
                .collect(Collectors.toList());
        return (outputFields != null && !outputFields.isEmpty()) ? outputFields : new ArrayList<>();
    }

    public String getMiningPojo() {
        TemplateRegistry registry = getTemplateRegistry();
        List<PMMLDataField> dataFields = getMiningFields();
        Map<String, Object> vars = new HashMap<>();
        String className = helper.compactAsJavaId(getModelId(),true);
        vars.put("pmmlPackageName",PMML_JAVA_PACKAGE_NAME);
        vars.put("className",className);
        vars.put("imports",new ArrayList<>());
        vars.put("dataFields",dataFields);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TemplateRuntime.execute( registry.getNamedTemplate("ScoreCardDataTemplate"),
                                 null,
                                 new MapVariableResolverFactory(vars),
                                 baos );
        String returnVal = new String(baos.toByteArray());
        System.out.println("**************** START ScoreCardData Java ****************");
        System.out.println(returnVal);
        System.out.println("**************** END ScoreCardData Java ****************");
        return returnVal;
    }

    public String getOutputPojo() {
        TemplateRegistry registry = getTemplateRegistry();
        List<PMMLDataField> dataFields = getOutputFields();
        Map<String, Object> vars = new HashMap<>();
        String className = "OverallScore";
        vars.put("pmmlPackageName","org.drools.pmml.pmml_4_2.model");
        vars.put("className",className);
        vars.put("imports",new ArrayList<>());
        vars.put("dataFields",dataFields);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TemplateRuntime.execute( registry.getNamedTemplate("ScoreCardOutputTemplate"),
                                 null,
                                 new MapVariableResolverFactory(vars),
                                 baos );
        return new String(baos.toByteArray());
    }

    private TemplateRegistry getTemplateRegistry() {
        TemplateRegistry registry = new SimpleTemplateRegistry();
        InputStream inputStream = Scorecard.class.getResourceAsStream(SCORECARD_DATA_POJO_TEMPLATE);
        CompiledTemplate ct = TemplateCompiler.compileTemplate(inputStream);
        registry.addNamedTemplate("ScoreCardDataTemplate",ct);
        inputStream = Scorecard.class.getResourceAsStream(SCORECARD_OUTPUT_POJO_TEMPLATE);
        ct = TemplateCompiler.compileTemplate(inputStream);
        registry.addNamedTemplate("ScoreCardOutputTemplate",ct);
        return registry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ScorecardModel model = (ScorecardModel) o;

        return rawModel != null ? rawModel.equals(model.rawModel) : model.rawModel == null;
    }

    @Override
    public int hashCode() {
        return rawModel != null ? rawModel.hashCode() : 0;
    }

}
