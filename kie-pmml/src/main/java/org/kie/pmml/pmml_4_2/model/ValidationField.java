package org.kie.pmml.pmml_4_2.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.INVALIDVALUETREATMENTMETHOD;
import org.dmg.pmml.pmml_4_2.descr.Interval;
import org.dmg.pmml.pmml_4_2.descr.OPTYPE;
import org.dmg.pmml.pmml_4_2.descr.Value;
import org.kie.pmml.pmml_4_2.PMML4Helper;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class ValidationField {

    private String fieldName;
    private INVALIDVALUETREATMENTMETHOD invalidValueTreatmentMethod;
    private String validationString;
    private String fieldType;
    private Value missingValue;
    private static TemplateRegistry templateRegistry;

    private static final String VALUES_TEMPLATE = "" +
                                                  "public boolean @{fieldName}IsValid(String input) {\n" +
                                                  "   List<String> validValues = Arrays.asList(" +
                                                  "@foreach{value: values}\"@{value.value}\"" +
                                                  "@end{\", \"});\n" +
                                                  "@if{validValuesOnly == true}" +
                                                  "// testing here @{validValuesOnly}\n" +
                                                  "   return validValues.contains(input);\n@else{}return true;\n@end{}" +
                                                  "}\n";
    private static final String OPENOPEN_TEMPLATE = "@code{ String leftOper; String rightOper; }" +
                                                    "public boolean @{fieldName}IsValid(Number input) {\n" +
                                                    "   boolean valid = false;\n" +
                                                    "   valid = ( " +
                                                    "   @foreach{interval: intervals}" +
                                                    "@if{interval.closure == \"openOpen\" || interval.closure == \"openClosed\"} @code{ leftOper = \">\"; } @else{} @code{ leftOper = \">=\"; }@end{}" +
                                                    "@if{interval.closure == \"openOpen\" || interval.closure == \"closedOpen\"} @code{ rightOper = \"<\"; } @else{} @code{ rightOper = \"<=\"; }@end{}" +
                                                    "@if{interval.leftMargin != null && interval.rightMargin != null}" +
                                                    "   (input.doubleValue() @{leftOper} @{interval.leftMargin} && input.doubleValue() @{rightOper} @{interval.rightMargin})\n" +
                                                    "@elseif{interval.leftMargin != null}" +
                                                    "   input.doubleValue() @{leftOper} @{interval.leftMargin} \n" +
                                                    "@elseif{interval.rightMargin != null}" +
                                                    "   input.doubleValue() @{rightOper} @{interval.rightMargin} \n" +
                                                    "   @end{}" +
                                                    "   @end{\" || \"});\n" +
                                                    "   return valid;\n" +
                                                    "}\n";
    private static final String ALWAYSTRUE_TEMPLATE = "" +
                                                      "public boolean @{fieldName}IsValid(Object o) {\n" +
                                                      "   return true;\n" +
                                                      "}\n";

    static {
        templateRegistry = new SimpleTemplateRegistry();
        InputStream is = new ByteArrayInputStream(VALUES_TEMPLATE.getBytes());
        templateRegistry.addNamedTemplate("ValuesTemplate", TemplateCompiler.compileTemplate(is));
        is = new ByteArrayInputStream(OPENOPEN_TEMPLATE.getBytes());
        templateRegistry.addNamedTemplate("openOpenTemplate", TemplateCompiler.compileTemplate(is));
        is = new ByteArrayInputStream(ALWAYSTRUE_TEMPLATE.getBytes());
        templateRegistry.addNamedTemplate("alwaysTrueTemplate", TemplateCompiler.compileTemplate(is));
    }

    public ValidationField() {
        // empty
    }

    public ValidationField(DataField df, INVALIDVALUETREATMENTMETHOD ivtm) {
        init(df, ivtm);
    }

    public ValidationField(PMMLMiningField miningField) {
        if (miningField.isInDictionary()) {
            init(miningField.getRawDataField(), miningField.getInvalidValueTreatment());
        } else {
            this.validationString = getAlwaysTrue(miningField.getName());
        }
    }

    private void init(DataField df, INVALIDVALUETREATMENTMETHOD ivtm) {
        this.fieldName = df.getName();
        this.invalidValueTreatmentMethod = ivtm;
        buildValidationString(df);
        PMML4Helper helper = new PMML4Helper();
        fieldType = helper.mapDatatype(df.getDataType());
        missingValue = df.getValues().stream().filter(v -> "missing".equals(v.getProperty())).findFirst().orElse(null);
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public INVALIDVALUETREATMENTMETHOD getInvalidValueTreatmentMethod() {
        return invalidValueTreatmentMethod;
    }

    public void setInvalidValueTreatmentMethod(INVALIDVALUETREATMENTMETHOD invalidValueTreatmentMethod) {
        this.invalidValueTreatmentMethod = invalidValueTreatmentMethod;
    }

    public String getValidationString() {
        return validationString;
    }

    public void setValidationString(String validationString) {
        this.validationString = validationString;
    }

    public String getFieldType() {
        return fieldType;
    }

    public Value getMissingValue() {
        return missingValue;
    }

    private void buildValidationString(DataField df) {
        List<Value> values = df.getValues();
        OPTYPE opType = df.getOptype();
        Boolean containsValidValues = values.stream().anyMatch(v -> "valid".equals(v.getProperty()));
        List<Interval> intervals = df.getIntervals();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (values != null && !values.isEmpty() && opType == OPTYPE.CATEGORICAL) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("values", values);
            vars.put("fieldName", df.getName());
            vars.put("validValuesOnly", containsValidValues);
            TemplateRuntime.execute(templateRegistry.getNamedTemplate("ValuesTemplate"),
                                    null,
                                    new MapVariableResolverFactory(vars),
                                    baos);
            this.validationString = baos.toString();
        } else if (intervals != null && !intervals.isEmpty() && opType == OPTYPE.CONTINUOUS) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("intervals", intervals);
            vars.put("fieldName", df.getName());
            TemplateRuntime.execute(templateRegistry.getNamedTemplate("openOpenTemplate"),
                                    null,
                                    new MapVariableResolverFactory(vars),
                                    baos);
            this.validationString = baos.toString();
        } else {
            this.validationString = getAlwaysTrue(df.getName());
        }
    }

    private String getAlwaysTrue(String fieldName) {
        Map<String, Object> vars = new HashMap<>();
        vars.put("fieldName", fieldName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TemplateRuntime.execute(templateRegistry.getNamedTemplate("alwaysTrueTemplate"),
                                null,
                                new MapVariableResolverFactory(vars),
                                baos);
        return baos.toString();
    }
}
