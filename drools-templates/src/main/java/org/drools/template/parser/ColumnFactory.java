package org.drools.template.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory to produce a column of the correct type based on its declaration.
 * [] indicates a column that represents an array (comma-delimited) of values.
 */
public class ColumnFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ColumnFactory.class);
    private final static Pattern PATTERN = Pattern.compile("((\\$)*([a-zA-Z0-9_]*))(\\[\\])?(:\\s*([a-zA-Z]*)(\\[\\])?)?");

    public Column getColumn(String value) {
        Matcher m = PATTERN.matcher(value);
        if (!m.matches()) {
            throw new IllegalArgumentException("value " + value + " is not a valid column definition");
        }

        String name = m.group(1);
        String type = m.group(6);
        type = type == null ? "String" : type;
        boolean array = (m.group(4) != null) || (m.group(7) != null);
        if (array) {
            return new ArrayColumn(name,
                                   createColumn(name,
                                                type));
        }
        return createColumn(name,
                            type);
    }

    @SuppressWarnings("unchecked")
    private Column createColumn(String name,
                                String type) {
        try {
            Class<Column> klass = (Class<Column>) Class.forName(this.getClass().getPackage().getName() + "." + type + "Column");
            Constructor<Column> constructor = klass.getConstructor(String.class);
            return constructor.newInstance(name);
        } catch (SecurityException e) {
            LOG.error("Exception", e);
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            LOG.error("Exception", e);
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            LOG.error("Exception", e);
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            LOG.error("Exception", e);
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            LOG.error("Exception", e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            LOG.error("Exception", e);
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            LOG.error("Exception", e);
            throw new RuntimeException(e);
        }
    }
}
