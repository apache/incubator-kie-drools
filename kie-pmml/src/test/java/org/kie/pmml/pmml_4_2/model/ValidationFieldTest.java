package org.kie.pmml.pmml_4_2.model;

import org.dmg.pmml.pmml_4_2.descr.DATATYPE;
import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.INVALIDVALUETREATMENTMETHOD;
import org.dmg.pmml.pmml_4_2.descr.Interval;
import org.dmg.pmml.pmml_4_2.descr.OPTYPE;
import org.dmg.pmml.pmml_4_2.descr.Value;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class ValidationFieldTest {

    @Test
    public void testValidationFieldDataFieldINVALIDVALUETREATMENTMETHOD() {
        DataField df = new DataField();
        df.setName("testField");
        df.setDataType(DATATYPE.STRING);
        df.setOptype(OPTYPE.CATEGORICAL);
        Value a = new Value();
        a.setValue("a");
        Value b = new Value();
        b.setValue("b");
        df.getValues().add(a);
        df.getValues().add(b);
        ValidationField vf = new ValidationField(df, INVALIDVALUETREATMENTMETHOD.AS_MISSING);
        assertNotNull(vf);
    }

    @Test
    public void testBuildValidationSingleIntervalLeftOnly() {
        DataField df = new DataField();
        df.setName("testFieldLO");
        df.setDataType(DATATYPE.STRING);
        df.setOptype(OPTYPE.CONTINUOUS);
        Interval i = new Interval();
        i.setClosure("openOpen");
        i.setLeftMargin(0.0);
        df.getIntervals().add(i);
        ValidationField vf = new ValidationField(df, INVALIDVALUETREATMENTMETHOD.AS_MISSING);
        System.out.println(vf.getValidationString());
    }

    @Test
    public void testBuildValidationSingleIntervalRightOnly() {
        DataField df = new DataField();
        df.setName("testFieldRO");
        df.setDataType(DATATYPE.STRING);
        df.setOptype(OPTYPE.CONTINUOUS);
        Interval i = new Interval();
        i.setClosure("openOpen");
        i.setRightMargin(100.0);
        df.getIntervals().add(i);
        ValidationField vf = new ValidationField(df, INVALIDVALUETREATMENTMETHOD.AS_MISSING);
        System.out.println(vf.getValidationString());
    }

    @Test
    public void testBuildValidationSingleIntervalBothEnds() {
        DataField df = new DataField();
        df.setName("testFieldB");
        df.setDataType(DATATYPE.STRING);
        df.setOptype(OPTYPE.CONTINUOUS);
        Interval i = new Interval();
        i.setClosure("closedOpen");
        i.setLeftMargin(0.0);
        i.setRightMargin(100.0);
        df.getIntervals().add(i);
        ValidationField vf = new ValidationField(df, INVALIDVALUETREATMENTMETHOD.AS_MISSING);
        System.out.println(vf.getValidationString());
    }

    @Test
    public void testBuildValidationMultiInterval() {
        DataField df = new DataField();
        df.setName("testFieldMI");
        df.setDataType(DATATYPE.STRING);
        df.setOptype(OPTYPE.CONTINUOUS);
        Interval i = new Interval();
        i.setClosure("openOpen");
        i.setLeftMargin(50.0);
        Interval j = new Interval();
        j.setClosure("openClosed");
        j.setLeftMargin(10.0);
        j.setRightMargin(12.5);
        df.getIntervals().add(i);
        df.getIntervals().add(j);
        ValidationField vf = new ValidationField(df, INVALIDVALUETREATMENTMETHOD.AS_MISSING);
        System.out.println(vf.getValidationString());
    }

    @Test
    public void testBuildValidation() {
        DataField df = new DataField();
        df.setName("testFieldC");
        df.setDataType(DATATYPE.STRING);
        df.setOptype(OPTYPE.CATEGORICAL);
        Value a = new Value();
        a.setValue("a");
        Value b = new Value();
        b.setValue("b");
        df.getValues().add(a);
        df.getValues().add(b);
        ValidationField vf = new ValidationField(df, INVALIDVALUETREATMENTMETHOD.AS_MISSING);
        System.out.println(vf.getValidationString());
    }

}
