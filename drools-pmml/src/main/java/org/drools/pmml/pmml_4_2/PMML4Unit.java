package org.drools.pmml.pmml_4_2;

import java.util.List;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.drools.pmml.pmml_4_2.model.PMMLDataField;

public interface PMML4Unit {
    public PMML getRawPMML();
    public List<PMML4Model> getModels();
    public List<PMMLDataField> getDataDictionaryFields();
}
