package org.mvel.conversion;

import org.mvel.ConversionHandler;
import org.mvel.ConversionException;

import java.util.Map;
import java.util.HashMap;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

public class BooleanCH implements ConversionHandler {
    private static final Map<Class, Converter> CNV =
            new HashMap<Class, Converter>();


    public Object convertFrom(Object in) {
        if (!CNV.containsKey(in.getClass())) throw new ConversionException("cannot convert type: "
                + in.getClass().getName() + " to: " + Boolean.class.getName());
        return CNV.get(in.getClass()).convert(in);
    }


    public boolean canConvertFrom(Class cls) {
        return CNV.containsKey(cls);
    }

    static {
        CNV.put(String.class,
                new Converter() {
                    public Object convert(Object o) {
                        return !(((String) o).equalsIgnoreCase("false"))
                                || (((String) o).equalsIgnoreCase("no"))
                                || (((String) o).equalsIgnoreCase("off"))
                                || (o.equals("0"));
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

        CNV.put(Boolean.class,
                new Converter() {
                    public Object convert(Object o) {
                        return o;
                    }
                }
        );

        CNV.put(Integer.class,
                new Converter() {
                    public Boolean convert(Object o) {
                        return (((Integer) o) > 0);
                    }
                }
        );

        CNV.put(Float.class,
                new Converter() {
                    public Boolean convert(Object o) {
                        return (((Float) o) > 0);
                    }
                }
        );

        CNV.put(Double.class,
                new Converter() {
                    public Boolean convert(Object o) {
                        return (((Double) o) > 0);
                    }
                }
        );

        CNV.put(Short.class,
                new Converter() {
                    public Boolean convert(Object o) {
                        return (((Short) o) > 0);
                    }
                }
        );

        CNV.put(Long.class,
                new Converter() {
                    public Boolean convert(Object o) {
                        return (((Long) o) > 0);
                    }
                }
        );

    }
}
