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
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:years and months duration").type(SchemaType.STRING).format("years and months duration").example("P1Y2M");
            case SimpleType.DAYS_AND_TIME_DURATION:
            case "dayTimeDuration":
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:days and time duration").type(SchemaType.STRING).format("days and time duration").example("P1D");
            default:
                throw new IllegalArgumentException();
        }
    }

    private static Schema convertBuiltInToJavaClass(BuiltInType builtin) {
        switch (builtin) {
            case UNKNOWN:
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:Any"); // intentional, do NOT add .type(SchemaType.OBJECT), the JSONSchema to represent FEEL:Any is {}
            case DATE:
                return OASFactory.createObject(Schema.class).type(SchemaType.STRING).format("date").addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:date");
            case TIME:
                return OASFactory.createObject(Schema.class).type(SchemaType.STRING).format("time").addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:time");
            case DATE_TIME:
                return OASFactory.createObject(Schema.class).type(SchemaType.STRING).format("date-time").addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:date and time");
            case BOOLEAN:
                return OASFactory.createObject(Schema.class).type(SchemaType.BOOLEAN).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:boolean");
            case NUMBER:
                return OASFactory.createObject(Schema.class).type(SchemaType.NUMBER).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:number");
            case STRING:
                return OASFactory.createObject(Schema.class).type(SchemaType.STRING).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:string");
            case CONTEXT:
                return OASFactory.createObject(Schema.class).addExtension(DMNOASConstants.X_DMN_TYPE, "FEEL:context"); // intentional, do NOT add .type(SchemaType.OBJECT), the JSONSchema to represent FEEL:context is {}
            case DURATION:
            default:
                throw new IllegalArgumentException();
        }
    }

    private FEELBuiltinTypeSchemas() {
        // deliberate intention not to allow instantiation of this class.
    }
}
