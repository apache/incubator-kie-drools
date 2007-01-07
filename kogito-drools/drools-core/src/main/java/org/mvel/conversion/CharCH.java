package org.mvel.conversion;

import org.mvel.ConversionHandler;
import org.mvel.ConversionException;

import java.util.Map;
import java.util.HashMap;
import static java.lang.String.valueOf;

public class CharCH implements ConversionHandler {
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
                        if ((((String) o).length()) > 0)
                            throw new ConversionException("cannot convert a string with a length greater than 1 to java.lang.Character");

                        return (((String) o)).charAt(0);
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

        CNV.put(Character.class,
                new Converter() {
                    public Object convert(Object o) {
                        //noinspection UnnecessaryBoxing
                        return new Character(((Character) o));
                    }
                }
        );


    }
}
