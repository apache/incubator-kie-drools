package org.drools.pmml.pmml_4_2;

import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.DataField;
import org.dmg.pmml.pmml_4_2.descr.MiningField;
import org.dmg.pmml.pmml_4_2.descr.MiningSchema;
import org.dmg.pmml.pmml_4_2.descr.Output;
import org.dmg.pmml.pmml_4_2.descr.OutputField;
import org.drools.pmml.pmml_4_2.model.PMML4ModelType;
import org.drools.pmml.pmml_4_2.model.PMMLDataField;

public interface PMML4Model {
    public String getModelId();
    public PMML4ModelType getModelType();
    public List<MiningField> getRawMiningFields();
    public List<OutputField> getRawOutputFields();
    public List<PMMLDataField> getMiningFields();
    public List<PMMLDataField> getOutputFields();
    public PMML4Unit getOwner();
    public MiningSchema getMiningSchema();
    public Output getOutput();
}
