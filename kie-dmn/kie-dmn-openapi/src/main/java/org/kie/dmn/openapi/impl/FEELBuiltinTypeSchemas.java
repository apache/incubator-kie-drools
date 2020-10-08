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

import org.eclipse.microprofile.openapi.OASFactory;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.media.Schema.SchemaType;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.SimpleType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.typesafe.DMNTypeUtils;

public class FEELBuiltinTypeSchemas {

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
                return OASFactory.createObject(Schema.class).description("FEEL:years and months duration").type(SchemaType.STRING).format("years and months duration").example("P1Y2M");
            case SimpleType.DAYS_AND_TIME_DURATION:
            case "dayTimeDuration":
                return OASFactory.createObject(Schema.class).description("FEEL:days and time duration").type(SchemaType.STRING).format("days and time duration").example("P1D");
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Schema convertBuiltInToJavaClass(BuiltInType builtin) {
        switch (builtin) {
            case UNKNOWN:
                return OASFactory.createObject(Schema.class).type(SchemaType.OBJECT).description("FEEL:Any");
            case DATE:
                return OASFactory.createObject(Schema.class).type(SchemaType.STRING).format("date").description("FEEL:date");
            case TIME:
                return OASFactory.createObject(Schema.class).type(SchemaType.STRING).format("time").description("FEEL:time");
            case DATE_TIME:
                return OASFactory.createObject(Schema.class).type(SchemaType.OBJECT).format("date-time").description("FEEL:date and time");
            case BOOLEAN:
                return OASFactory.createObject(Schema.class).type(SchemaType.BOOLEAN).description("FEEL:boolean");
            case NUMBER:
                return OASFactory.createObject(Schema.class).type(SchemaType.NUMBER).description("FEEL:number");
            case STRING:
                return OASFactory.createObject(Schema.class).type(SchemaType.STRING).description("FEEL:string");
            case DURATION:
            default:
                throw new IllegalArgumentException();
        }
    }

    private FEELBuiltinTypeSchemas() {
        // deliberate intention not to allow instantiation of this class.
    }
}
