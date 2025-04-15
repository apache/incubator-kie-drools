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

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.typesafe.DMNTypeUtils;

import java.util.List;

public class FEELBuiltinTypeSchemaMapper {

    public static Schema from(DMNType t) {
        BuiltInType builtin = DMNTypeUtils.getFEELBuiltInType(t);
        if (builtin == BuiltInType.DURATION) {
            return convertDurationToSchema(t);
        } else {
            return convertBuiltInToJavaClass(builtin);
        }
    }

    private static Schema convertDurationToSchema(DMNType t) {
        switch (t.getName()) {
            case SimpleType.YEARS_AND_MONTHS_DURATION:
            case "yearMonthDuration":
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:years and months duration").addType(SchemaType.STRING).format("years and months duration").examples(List.of("P1Y2M"));
            case SimpleType.DAYS_AND_TIME_DURATION:
            case "dayTimeDuration":
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:days and time duration").addType(SchemaType.STRING).format("days and time duration").examples(List.of("P1D"));
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Schema convertBuiltInToJavaClass(BuiltInType builtin) {
        switch (builtin) {
            case UNKNOWN:
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:Any"); // intentional, do NOT add .type(SchemaType.OBJECT), the JSONSchema to represent FEEL:Any is {}
            case DATE:
                return OASFactory.createObject(Schema.class).addType(SchemaType.STRING).format("date").addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:date");
            case TIME:
                return OASFactory.createObject(Schema.class).addType(SchemaType.STRING).format("time").addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:time");
            case DATE_TIME:
                return OASFactory.createObject(Schema.class).addType(SchemaType.STRING).format("date-time").addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:date and time");
            case BOOLEAN:
                return OASFactory.createObject(Schema.class).addType(SchemaType.BOOLEAN).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:boolean");
            case NUMBER:
                return OASFactory.createObject(Schema.class).addType(SchemaType.NUMBER).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:number");
            case STRING:
                return OASFactory.createObject(Schema.class).addType(SchemaType.STRING).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:string");
            case CONTEXT:
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:context"); // intentional, do NOT add .type(SchemaType.OBJECT), the JSONSchema to represent FEEL:context is {}
            case DURATION:
            default:
                throw new IllegalArgumentException("Unsupported built-in type: " + builtin);
        }
    }

    private FEELBuiltinTypeSchemaMapper() {
        // deliberate intention not to allow instantiation of this class.
    }
}
