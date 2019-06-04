/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.pmml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.dmg.pmml.Extension;
import org.dmg.pmml.MiningField.UsageType;
import org.dmg.pmml.MiningSchema;
import org.dmg.pmml.Model;
import org.dmg.pmml.PMML;
import org.xml.sax.SAXException;

public class PMMLInfo<M extends PMMLModelInfo> {

    protected final Collection<M> models;
    protected final PMMLHeaderInfo header;

    public PMMLInfo(Collection<M> models, PMMLHeaderInfo header) {
        this.models = Collections.unmodifiableList(new ArrayList<>(models));
        this.header = header;
    }

    public static PMMLInfo<PMMLModelInfo> from(InputStream is) throws SAXException, JAXBException {
        PMML pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
        List<PMMLModelInfo> models = new ArrayList<>();
        for (Model pm : pmml.getModels()) {
            MiningSchema miningSchema = pm.getMiningSchema();
            Collection<String> inputFields = new ArrayList<>();
            miningSchema.getMiningFields()
                        .stream()
                        .filter(mf -> mf.getUsageType() == UsageType.ACTIVE)
                        .forEach(fn -> inputFields.add(fn.getName().getValue()));
            Collection<String> outputFields = new ArrayList<>();
            pm.getOutput().getOutputFields().forEach(of -> outputFields.add(of.getName().getValue()));
            models.add(new PMMLModelInfo(pm.getModelName(), inputFields, outputFields));
        }
        Map<String, String> headerExtensions = new HashMap<>();
        for (Extension ex : pmml.getHeader().getExtensions()) {
            headerExtensions.put(ex.getName(), ex.getValue());
        }
        PMMLInfo<PMMLModelInfo> info = new PMMLInfo<>(models, new PMMLHeaderInfo("http://www.dmg.org/PMML-" + pmml.getBaseVersion().replace(".", "_"), headerExtensions));
        return info;
    }

    public Collection<M> getModels() {
        return models;
    }

    public PMMLHeaderInfo getHeader() {
        return header;
    }

    public static class PMMLHeaderInfo {

        protected final Map<String, String> headerExtensions;
        protected final String pmmlNSURI;

        public PMMLHeaderInfo(String pmmlNSURI, Map<String, String> headerExtensions) {
            this.pmmlNSURI = pmmlNSURI;
            this.headerExtensions = Collections.unmodifiableMap(new HashMap<>(headerExtensions));
        }

        public Map<String, String> getHeaderExtensions() {
            return headerExtensions;
        }

        public String getPmmlNSURI() {
            return pmmlNSURI;
        }

    }
}
