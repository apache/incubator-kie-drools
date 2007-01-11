package org.mvel.conversion;

import org.mvel.ConversionException;
import org.mvel.ConversionHandler;

import java.util.HashMap;
import java.util.Map;
import static java.lang.String.valueOf;
import java.math.BigDecimal;
import java.math.BigInteger;

public class ShortCH implements ConversionHandler {
    private static final Map<Class, Converter> CNV =
            new HashMap<Class, Converter>();


    public Object convertFrom(Object in) {
        if (!CNV.containsKey(in.getClass())) throw new ConversionException("cannot convert type: "
                + in.getClass().getName() + " to: " + Short.class.getName());
        return CNV.get(in.getClass()).convert(in);
    }


    public boolean canConvertFrom(Class cls) {
        return CNV.containsKey(cls);
    }


    static {
        CNV.put(String.class,
                new Converter() {
                    public Short convert(Object o) {
                        return Short.parseShort(((String) o));
                    }
                }
        );

        CNV.put(Object.class,
                new Converter() {
                    public Object convert(Object o) {
                        return CNV.get(String.class).convert(valueOf(o));
                    }
                }
        );

        CNV.put(BigDecimal.class,
                new Converter() {
                    public Short convert(Object o) {
                        return ((BigDecimal) o).shortValue();
                    }
                }
        );


        CNV.put(BigInteger.class,
                new Converter() {
                    public Short convert(Object o) {
                        return ((BigInteger) o).shortValue();
                    }
                }
        );


        CNV.put(Short.class,
                new Converter() {
                    public Object convert(Object o) {
                        return o;
                    }
                }
        );

        CNV.put(Integer.class,
                new Converter() {
                    public Short convert(Object o) {
                        if (((Integer) o) > Short.MAX_VALUE) {
                            throw new ConversionException("cannot coerce Integer to Short since the value ("
                                    + valueOf(o) + ") exceeds that maximum precision of Integer.");
                        }
                        else {
                            return ((Integer) o).shortValue();
                        }
                    }
                }
        );

        CNV.put(Float.class,
                new Converter() {
                    public Short convert(Object o) {
                        if (((Float) o) > Short.MAX_VALUE) {
                            throw new ConversionException("cannot coerce Float to Short since the value ("
                                    + valueOf(o) + ") exceeds that maximum precision of Integer.");
                        }
                        else {
                            return ((Float) o).shortValue();
                        }
                    }
                }
        );

        CNV.put(Double.class,
                new Converter() {
                    public Short convert(Object o) {
                        if (((Double) o) > Short.MAX_VALUE) {
                            throw new ConversionException("cannot coerce Double to Short since the value ("
                                    + valueOf(o) + ") exceeds that maximum precision of Integer.");
                        }
                        else {
                            return ((Double) o).shortValue();
                        }
                    }
                }
        );

        CNV.put(Long.class,
                new Converter() {
                    public Short convert(Object o) {
                        if (((Long) o) > Short.MAX_VALUE) {
                            throw new ConversionException("cannot coerce Integer to Short since the value ("
                                    + valueOf(o) + ") exceeds that maximum precision of Integer.");
                        }
                        else {
                            return ((Long) o).shortValue();
                        }
                    }
                }
        );

        CNV.put(Boolean.class,
                new Converter() {
                    public Short convert(Object o) {
                        return ((Boolean) o).booleanValue() ? (short)1: (short)0;
                    }
                }
        );

    }
}
