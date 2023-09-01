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
import org.dmg.pmml.Header;
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

    public static PMMLInfo<PMMLModelInfo> from(InputStream is) {
        PMML pmml;
        try {
            pmml = org.jpmml.model.PMMLUtil.unmarshal(is);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<PMMLModelInfo> models = new ArrayList<>();
        for (Model pm : pmml.getModels()) {
            models.add(pmmlToModelInfo(pm));
        }
        PMMLInfo<PMMLModelInfo> info = new PMMLInfo<>(models, pmmlToHeaderInfo(pmml, pmml.getHeader()));
        return info;
    }

    public static PMMLHeaderInfo pmmlToHeaderInfo(PMML pmml, Header h) {
        Map<String, String> headerExtensions = new HashMap<>();
        for (Extension ex : h.getExtensions()) {
            headerExtensions.put(ex.getName(), ex.getValue());
        }
        return new PMMLHeaderInfo("http://www.dmg.org/PMML-" + pmml.getBaseVersion().replace(".", "_"), headerExtensions);
    }

    public static PMMLModelInfo pmmlToModelInfo(Model pm) {
        MiningSchema miningSchema = pm.getMiningSchema();
        Collection<String> inputFields = new ArrayList<>();
        miningSchema.getMiningFields()
                    .stream()
                    .filter(mf -> mf.getUsageType() == UsageType.ACTIVE)
                    .forEach(fn -> inputFields.add(fn.getName().getValue()));
        Collection<String> targetFields = new ArrayList<>();
        miningSchema.getMiningFields()
                    .stream()
                    .filter(mf -> mf.getUsageType() == UsageType.PREDICTED)
                    .forEach(fn -> targetFields.add(fn.getName().getValue()));
        Collection<String> outputFields = new ArrayList<>();
        if (pm.getOutput() != null && pm.getOutput().getOutputFields() != null) {
            pm.getOutput().getOutputFields().forEach(of -> outputFields.add(of.getName().getValue()));
        }
        return new PMMLModelInfo(pm.getModelName(), pm.getClass().getSimpleName(), inputFields, targetFields, outputFields);
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
