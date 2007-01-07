package org.mvel.conversion;

import org.mvel.ConversionHandler;
import org.mvel.ConversionException;

import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalCH implements ConversionHandler {
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
        CNV.put(Object.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal(String.valueOf(o));
                    }
                }
        );

        CNV.put(BigDecimal.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return (BigDecimal) o;
                    }
                }
        );


        CNV.put(BigInteger.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((BigInteger) o);
                    }
                }
        );

        CNV.put(String.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((String) o);
                    }
                }
        );

        CNV.put(Double.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((Double) o);
                    }
                }
        );

        CNV.put(Float.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((Float) o);
                    }
                }
        );


        CNV.put(Short.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((Short) o);
                    }
                }
        );

        CNV.put(Long.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((Long) o);
                    }
                }
        );

        CNV.put(Integer.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((Integer) o);
                    }
                }
        );

        CNV.put(String.class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((String) o);
                    }
                }
        );

        CNV.put(char[].class,
                new Converter() {
                    public BigDecimal convert(Object o) {
                        return new BigDecimal((char[]) o);
                    }
                }

        );
    }
}
