/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
