package org.mvel.conversion;

import org.mvel.ConversionHandler;
import org.mvel.ConversionException;

import java.util.Map;
import java.util.HashMap;
import static java.lang.String.valueOf;
import java.math.BigDecimal;
import java.math.BigInteger;

public class DoubleCH implements ConversionHandler {
    private static final Map<Class, Converter> CNV =
            new HashMap<Class, Converter>();


    public Object convertFrom(Object in) {
        if (!CNV.containsKey(in.getClass())) throw new ConversionException("cannot convert type: "
                + in.getClass().getName() + " to: " + Integer.class.getName());
        return CNV.get(in.getClass()).convert(in);
    }


    public boolean canConvertFrom(Class cls) {
        return CNV.containsKey(cls);
    }

    static {
        CNV.put(String.class,
                new Converter() {
                    public Object convert(Object o) {
                        return Double.parseDouble(((String) o));
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
                    public Double convert(Object o) {
                        return ((BigDecimal) o).doubleValue();
                    }
                }
        );


        CNV.put(BigInteger.class,
                new Converter() {
                    public Double convert(Object o) {
                        return ((BigInteger) o).doubleValue();
                    }
                }
        );


        CNV.put(Double.class,
                new Converter() {
                    public Object convert(Object o) {
                        return o;
                    }
                }
        );

        CNV.put(Float.class,
                new Converter() {
                    public Double convert(Object o) {
                        if (((Float) o) > Double.MAX_VALUE) {
                            throw new ConversionException("cannot coerce Float to Double since the value ("
                                    + valueOf(o) + ") exceeds that maximum precision of Double.");

                        }

                        return ((Float) o).doubleValue();
                    }
                });

        CNV.put(Integer.class,
                new Converter() {
                    public Double convert(Object o) {
                        //noinspection UnnecessaryBoxing
                        return ((Integer) o).doubleValue();
                    }
                }
        );

        CNV.put(Short.class,
                new Converter() {
                    public Double convert(Object o) {
                        //noinspection UnnecessaryBoxing
                        return ((Short) o).doubleValue();
                    }
                }
        );

        CNV.put(Long.class,
                new Converter() {
                    public Double convert(Object o) {
                        //noinspection UnnecessaryBoxing
                        return ((Long) o).doubleValue();
                    }
                }
        );


        CNV.put(Boolean.class,
                new Converter() {
                    public Double convert(Object o) {
                        if ((Boolean) o) return 1d;
                        else return 0d;
                    }
                }
        );

    }
}
