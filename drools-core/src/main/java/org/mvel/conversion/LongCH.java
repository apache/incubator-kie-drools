package org.mvel.conversion;

import org.mvel.ConversionHandler;
import org.mvel.ConversionException;

import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import java.math.BigInteger;
import static java.lang.String.valueOf;

public class LongCH implements ConversionHandler {
    private static final Map<Class, Converter> CNV =
            new HashMap<Class, Converter>();


    public Object convertFrom(Object in) {
        if (!CNV.containsKey(in.getClass())) throw new ConversionException("cannot convert type: "
                + in.getClass().getName() + " to: " + Long.class.getName());
        return CNV.get(in.getClass()).convert(in);
    }


    public boolean canConvertFrom(Class cls) {
        return CNV.containsKey(cls);
    }

    static {
        CNV.put(String.class,
                new Converter() {
                    public Object convert(Object o) {
                        return Long.parseLong(((String) o));
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
                    public Long convert(Object o) {
                        return ((BigDecimal) o).longValue();
                    }
                }
        );


        CNV.put(BigInteger.class,
                new Converter() {
                    public Long convert(Object o) {
                        return ((BigInteger) o).longValue();
                    }
                }
        );


        CNV.put(Short.class,
                new Converter() {
                    public Object convert(Object o) {
                        //noinspection UnnecessaryBoxing
                        return ((Short) o).longValue();
                    }
                }
        );

        CNV.put(Long.class,
                new Converter() {
                    public Object convert(Object o) {
                        //noinspection UnnecessaryBoxing
                        return new Long(((Long) o));
                    }
                }
        );

        CNV.put(Integer.class,
                new Converter() {
                    public Object convert(Object o) {
                        //noinspection UnnecessaryBoxing
                        return ((Integer) o).longValue();
                    }
                }
        );

        CNV.put(Boolean.class,
                new Converter() {
                    public Long convert(Object o) {
                        if ((Boolean) o) return 1l;
                        else return 0l;
                    }
                }
        );
    }
}
