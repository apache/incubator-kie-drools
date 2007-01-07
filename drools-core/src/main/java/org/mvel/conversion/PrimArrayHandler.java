package org.mvel.conversion;

import static java.lang.reflect.Array.set;
import static java.lang.reflect.Array.newInstance;

import static org.mvel.DataConversion.convert;
import static org.mvel.DataConversion.canConvert;
import org.mvel.ConversionHandler;
import org.mvel.ConversionException;

import java.util.Map;
import java.util.HashMap;

public class PrimArrayHandler implements ConversionHandler {
    private final Map<Class, Converter> CNV = new HashMap<Class, Converter>();

    private final Class primitiveType;

    public PrimArrayHandler(Class type) {
        this.primitiveType = type;

        CNV.put(Object[].class, new Converter() {
            public Object convert(Object o) {
                return handleLooseTypeConversion(o.getClass(), (Object[]) o, primitiveType);
            }
        });

        CNV.put(String[].class, new Converter() {
            public Object convert(Object o) {
                return handleLooseTypeConversion(o.getClass(), (String[]) o, primitiveType);
            }
        });
    }

    public Object convertFrom(Object in) {
        if (!CNV.containsKey(in.getClass())) throw new ConversionException("cannot convert type: "
                + in.getClass().getName() + " to: " + primitiveType.getName());

        return CNV.get(in.getClass()).convert(in);
    }

    public boolean canConvertFrom(Class cls) {
        return CNV.containsKey(cls);
    }


    /**
     * Messy method to handle primitive boxing for conversion. If someone can re-write this more
     * elegantly, be my guest.
     *
     * @param sourceType
     * @param input
     * @param targetType
     * @return
     */
    private static Object handleLooseTypeConversion(Class sourceType, Object[] input, Class targetType) {
        Class targType = targetType.getComponentType();

        Object target = newInstance(targType, input.length);

        if (input.length > 0
                && canConvert(targetType.getComponentType(), sourceType.getComponentType())) {
            for (int i = 0; i < input.length; i++) {
                set(target, i, convert(input[i], targType));
            }
        }
        else {
            throw new ConversionException("cannot convert to type: "
                    + targetType.getComponentType().getName() + "[] from " + sourceType.getComponentType().getName());
        }

        return target;
    }
}
