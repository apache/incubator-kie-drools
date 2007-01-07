package org.mvel.conversion;

import org.mvel.ConversionHandler;
import org.mvel.ConversionException;

import java.util.Map;
import java.util.HashMap;

public class IntArrayCH implements ConversionHandler {
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
        CNV.put(String[].class,
                new Converter() {
                    public Object convert(Object o) {
                        String[] old = (String[]) o;
                        Integer[] n = new Integer[old.length];
                        for (int i = 0; i < old.length; i++) {
                            n[i] = Integer.parseInt(old[i]);
                        }

                        return n;
                    }
                }
        );

        CNV.put(Object[].class,
                new Converter() {
                    public Object convert(Object o) {
                        Object[] old = (Object[]) o;
                        Integer[] n = new Integer[old.length];
                        for (int i = 0; i < old.length; i++) {
                            n[i] = Integer.parseInt(String.valueOf(old[i]));
                        }

                        return n;
                    }
                }
        );

    }
}
