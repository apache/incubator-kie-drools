/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.DMNUnaryTest;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.openapi.NamingPolicy;
import org.kie.dmn.openapi.model.DMNModelIOSets;
import org.kie.dmn.openapi.model.DMNModelIOSets.DSIOSets;
import org.kie.dmn.typesafe.DMNTypeUtils;

import static org.kie.dmn.openapi.impl.DMNOASConstants.X_DMN_DESCRIPTIONS;
import static org.kie.dmn.openapi.impl.DMNOASConstants.X_DMN_TYPE;

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

    static void populateSchemaWithConstraints(Schema toPopulate, SimpleTypeImpl t) {
        if (t.getAllowedValues() != null && !t.getAllowedValues().isEmpty()) {
            parseSimpleType(DMNOASConstants.X_DMN_ALLOWED_VALUES, toPopulate, t.getAllowedValuesFEEL(), t.getAllowedValues());
        }
        if (t.getTypeConstraint() != null && !t.getTypeConstraint().isEmpty()) {
            parseSimpleType(DMNOASConstants.X_DMN_TYPE_CONSTRAINTS, toPopulate, t.getTypeConstraintFEEL(), t.getTypeConstraint());
        }
    }

    static void parseSimpleType(String schemaString, Schema schema, List<UnaryTest> feelUnaryTests, List<DMNUnaryTest> dmnUnaryTests) {
        schema.addExtension(schemaString, feelUnaryTests.stream().map(UnaryTest::toString).collect(Collectors.joining(", ")));
        DMNUnaryTestsMapper.populateSchemaFromUnaryTests(schema, dmnUnaryTests);
    }

    private Schema schemaFromType(DMNType t) {
        if (t instanceof CompositeTypeImpl compositeType) {
            return schemaFromCompositeType(compositeType);
        }
        if (t instanceof SimpleTypeImpl simpleType) {
            return schemaFromSimpleType(simpleType);
        }
        throw new UnsupportedOperationException();
    }

    private Schema schemaFromSimpleType(SimpleTypeImpl t) {
        DMNType baseType = t.getBaseType();
        if (baseType == null) {
            throw new IllegalStateException();
        }
        Schema schema = refOrBuiltinSchema(baseType);
        populateSchemaWithConstraints(schema, t);
        schema = nestAsItemIfCollection(schema, t);
        schema.addExtension(X_DMN_TYPE, getDMNTypeSchemaXDMNTYPEdescr(t));
        processIoSetDoc(schema, t);
        return schema;
    }

    private Schema refOrBuiltinSchema(DMNType t) {
        if (DMNTypeUtils.isFEELBuiltInType(t)) {
            return FEELBuiltinTypeSchemaMapper.from(t);
        }
        if (typesIndex.contains(t)) {
            return OASFactory.createObject(Schema.class).ref(namingPolicy.getRef(t));
        }
        throw new UnsupportedOperationException();
    }

    private boolean isIOSetForInputScope(DMNType t) {
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

    private Optional<Map<String, String>> ioSetDoc(DMNType t) {
        Map<String, String> res = null;
        for (DMNModelIOSets ios : ioSets) {
            if (ios.getInputSet().equals(t)) {
                res = ios.getInputDoc();
            } else if (ios.getOutputSet().equals(t)) {
                res = ios.getOutputDoc();
            }
            for (DSIOSets ds : ios.getDSIOSets()) {
                if (ds.getDSInputSet().equals(t)) {
                    res = ds.getInputDoc();
                } else if (ds.getDSOutputSet().equals(t)) {
                    res = ds.getOutputDoc();
                }
            }
        }
        return Optional.ofNullable(res);
    }

    private Schema schemaFromCompositeType(CompositeTypeImpl ct) {
        Schema schema = OASFactory.createObject(Schema.class).type(SchemaType.OBJECT);
        if (ct.getBaseType() == null) { // main case
            for (Entry<String, DMNType> fkv : ct.getFields().entrySet()) {
                schema.addProperty(fkv.getKey(), refOrBuiltinSchema(fkv.getValue()));
            }
            if (isIOSetForInputScope(ct) && !ct.getFields().isEmpty()) {
                schema.required(new ArrayList<>(ct.getFields().keySet()));
            }
        } else {
            schema = refOrBuiltinSchema(ct.getBaseType());
        }
        schema = nestAsItemIfCollection(schema, ct);
        schema.addExtension(X_DMN_TYPE, getDMNTypeSchemaXDMNTYPEdescr(ct));
        processIoSetDoc(schema, ct);
        return schema;
    }

    private void processIoSetDoc(final Schema schema, final DMNType type) {
        ioSetDoc(type).ifPresent(x -> schema.addExtension(X_DMN_DESCRIPTIONS, x));
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
