package org.drools.pmml.pmml_4_2.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.dmg.pmml.pmml_4_2.descr.PMML;
import org.drools.compiler.compiler.io.Resource;
import org.drools.core.io.impl.ClassPathResource;
import org.drools.pmml.pmml_4_2.PMML4Model;
import org.drools.pmml.pmml_4_2.PMML4Unit;

public class PMML4UnitImpl implements PMML4Unit {
    private static PMML4ModelFactory modelFactory = PMML4ModelFactory.getInstance();
    private PMML rawPmml;
    private List<PMML4Model> models;
    private List<PMMLDataField> dataDictionaryFields;

    public PMML4UnitImpl(PMML rawPmml) {
        this.rawPmml = rawPmml;
    }

    public PMML4UnitImpl(String pmmlSourcePath) {
        ClassPathResource resource = new ClassPathResource(pmmlSourcePath);
        try {
            JAXBContext context = JAXBContext.newInstance(PMML.class.getPackage().getName());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            this.rawPmml = (PMML)unmarshaller.unmarshal(resource.getInputStream());
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public PMML getRawPMML() {
        return this.rawPmml;
    }

    @Override
    public List<PMML4Model> getModels() {
        if (models == null || models.isEmpty()) {
            models = modelFactory.getModels(this);
        }
        return (models != null && !models.isEmpty()) ? new ArrayList<>(models) : new ArrayList<>();
    }

    @Override
    public List<PMMLDataField> getDataDictionaryFields() {
        if (dataDictionaryFields == null || dataDictionaryFields.isEmpty()) {
            dataDictionaryFields = new ArrayList<>();
            rawPmml.getDataDictionary().getDataFields().forEach(df -> {
                PMMLDataField dataField = new PMMLDataField(df);
                dataDictionaryFields.add(dataField);
            });
        }
        return (dataDictionaryFields != null && !dataDictionaryFields.isEmpty()) ?
                new ArrayList<>(dataDictionaryFields) : new ArrayList<>();
    }
}
