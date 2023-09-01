package org.kie.dmn.openapi.model;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.openapi.NamingPolicy;

public class DMNOASResult {

    private final ObjectNode jsonSchemaNode;
    private final Map<DMNModel, DMNModelIOSets> ioSets = new LinkedHashMap<>();
    private final Map<DMNType, Schema> schemas;
    private final NamingPolicy namingPolicy;

    public DMNOASResult(ObjectNode jsonSchemaNode, Collection<DMNModelIOSets> ioSets, Map<DMNType, Schema> schemas, NamingPolicy namingPolicy) {
        this.jsonSchemaNode = jsonSchemaNode.deepCopy();
        for (DMNModelIOSets ioSet : ioSets) {
            this.ioSets.put(ioSet.getModel(), ioSet);
        }
        this.schemas = Collections.unmodifiableMap(schemas);
        this.namingPolicy = namingPolicy;
    }

    public DMNModelIOSets lookupIOSetsByModel(DMNModel model) {
        return ioSets.get(model);
    }

    public ObjectNode getJsonSchemaNode() {
        return jsonSchemaNode;
    }

    public Collection<DMNModelIOSets> getIoSets() {
        return ioSets.values();
    }

    public Map<DMNType, Schema> getSchemas() {
        return schemas;
    }

    public NamingPolicy getNamingPolicy() {
        return namingPolicy;
    }

}
