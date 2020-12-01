/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.openapi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.openapi.NamingPolicy;
import org.kie.dmn.openapi.model.DMNModelIOSets;
import org.kie.dmn.openapi.model.DMNModelIOSets.DSIOSets;
import org.kie.dmn.typesafe.DMNTypeUtils;

public class DMNTypeSchemas {

    private final List<DMNModelIOSets> ioSets;
    private final Set<DMNType> typesIndex;
    private final NamingPolicy namingPolicy;

    public DMNTypeSchemas(List<DMNModelIOSets> ioSets, Set<DMNType> typesIndex, NamingPolicy namingPolicy) {
        this.ioSets = Collections.unmodifiableList(ioSets);
        this.typesIndex = Collections.unmodifiableSet(typesIndex);
        this.namingPolicy = namingPolicy;
    }

    public Map<DMNType, Schema> generateSchemas() {
        Map<DMNType, Schema> schemas = new HashMap<>();
        for (DMNType t : typesIndex) {
            Schema schema = schemaFromType(t);
            schemas.put(t, schema);
        }
        return schemas;
    }

    private Schema refOrBuiltinSchema(DMNType t) {
        if (DMNTypeUtils.isFEELBuiltInType(t)) {
            return FEELBuiltinTypeSchemas.from(t);
        }
        if (typesIndex.contains(t)) {
            Schema schema = OASFactory.createObject(Schema.class).ref(namingPolicy.getRef(t));
            return schema;
        }
        throw new UnsupportedOperationException();
    }

    private boolean isIOSet(DMNType t) {
        for (DMNModelIOSets ios : ioSets) {
            if (ios.getInputSet().equals(t)) {
                return true;
            }
            for (DSIOSets ds : ios.getDSIOSets()) {
                if (ds.getDSInputSet().equals(t)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Schema schemaFromType(DMNType t) {
        if (t instanceof CompositeTypeImpl) {
            return schemaFromCompositeType((CompositeTypeImpl) t);
        }
        if (t instanceof SimpleTypeImpl) {
            return schemaFromSimpleType((SimpleTypeImpl) t);
        }
        throw new UnsupportedOperationException();
    }

    private Schema schemaFromSimpleType(SimpleTypeImpl t) {
        DMNType baseType = t.getBaseType();
        if (baseType == null) {
            throw new IllegalStateException();
        }
        Schema schema = refOrBuiltinSchema(baseType);
        if (t.getAllowedValues() != null && !t.getAllowedValues().isEmpty()) {
            FEELSchemaEnum.parseAllowedValuesIntoSchema(schema, t.getAllowedValues());
        }
        schema = nestAsItemIfCollection(schema, t);
        schema.addExtension(DMNOASConstants.X_DMN_TYPE, getDMNTypeSchemaXDMNTYPEdescr(t));
        return schema;
    }

    private Schema schemaFromCompositeType(CompositeTypeImpl ct) {
        Schema schema = OASFactory.createObject(Schema.class).type(SchemaType.OBJECT);
        if (ct.getBaseType() == null) { // main case
            for (Entry<String, DMNType> fkv : ct.getFields().entrySet()) {
                schema.addProperty(fkv.getKey(), refOrBuiltinSchema(fkv.getValue()));
            }
            if (isIOSet(ct) && ct.getFields().size() > 0) {
                schema.required(new ArrayList<>(ct.getFields().keySet()));
            }
        } else if (ct.isCollection()) {
            schema = refOrBuiltinSchema(ct.getBaseType());
        } else {
            throw new IllegalStateException();
        }
        schema = nestAsItemIfCollection(schema, ct);
        schema.addExtension(DMNOASConstants.X_DMN_TYPE, getDMNTypeSchemaXDMNTYPEdescr(ct));
        return schema;
    }

    private Schema nestAsItemIfCollection(Schema original, DMNType t) {
        if (t.isCollection()) {
            return OASFactory.createObject(Schema.class).type(SchemaType.ARRAY).items(original);
        } else {
            return original;
        }
    }

    private String getDMNTypeSchemaXDMNTYPEdescr(DMNType t) {
        if (((BaseDMNTypeImpl) t).getBelongingType() == null) { // internals for anonymous inner types.
            return t.toString();
        } else {
            return null;
        }
    }
}
