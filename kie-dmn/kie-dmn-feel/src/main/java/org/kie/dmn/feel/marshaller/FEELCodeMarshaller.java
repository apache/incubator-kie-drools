package org.kie.dmn.feel.marshaller;

import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.runtime.functions.*;

import java.util.function.Function;

import static org.kie.dmn.feel.lang.types.BuiltInType.justNull;

/**
 * An implementation of the FEEL marshaller interface
 * that converts FEEL objects into it's string representation
 * and vice versa
 */
public class FEELCodeMarshaller
        implements FEELMarshaller<String> {

    public static final FEELCodeMarshaller INSTANCE = new FEELCodeMarshaller();
    private FEEL feel = FEEL.newInstance();

    private FEELCodeMarshaller() {}

    @Override
    public String marshall(Object value) {
        if( value == null ) {
            return "null";
        }
        return BuiltInFunctions.getFunction( CodeFunction.class ).invoke( value ).cata( justNull(), Function.identity());
    }

    @Override
    public Object unmarshall(Type feelType, String value) {
        return feel.evaluate( value );
    }
}
