package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;

final class FieldCloningUtils {

    static void copyBoolean(Field field, Object original, Object clone) {
        boolean originalValue = getBooleanFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static boolean getBooleanFieldValue(Object bean, Field field) {
        try {
            return field.getBoolean(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, boolean value) {
        try {
            field.setBoolean(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyByte(Field field, Object original, Object clone) {
        byte originalValue = getByteFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static byte getByteFieldValue(Object bean, Field field) {
        try {
            return field.getByte(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, byte value) {
        try {
            field.setByte(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyChar(Field field, Object original, Object clone) {
        char originalValue = getCharFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static char getCharFieldValue(Object bean, Field field) {
        try {
            return field.getChar(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, char value) {
        try {
            field.setChar(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyShort(Field field, Object original, Object clone) {
        short originalValue = getShortFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static short getShortFieldValue(Object bean, Field field) {
        try {
            return field.getShort(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, short value) {
        try {
            field.setShort(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyInt(Field field, Object original, Object clone) {
        int originalValue = getIntFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static int getIntFieldValue(Object bean, Field field) {
        try {
            return field.getInt(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, int value) {
        try {
            field.setInt(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyLong(Field field, Object original, Object clone) {
        long originalValue = getLongFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static long getLongFieldValue(Object bean, Field field) {
        try {
            return field.getLong(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, long value) {
        try {
            field.setLong(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyFloat(Field field, Object original, Object clone) {
        float originalValue = getFloatFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static float getFloatFieldValue(Object bean, Field field) {
        try {
            return field.getFloat(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, float value) {
        try {
            field.setFloat(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyDouble(Field field, Object original, Object clone) {
        double originalValue = getDoubleFieldValue(original, field);
        setFieldValue(clone, field, originalValue);
    }

    private static double getDoubleFieldValue(Object bean, Field field) {
        try {
            return field.getDouble(bean);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnRead(bean, field, e);
        }
    }

    private static void setFieldValue(Object bean, Field field, double value) {
        try {
            field.setDouble(bean, value);
        } catch (IllegalAccessException e) {
            throw FieldCloningUtils.createExceptionOnWrite(bean, field, value, e);
        }
    }

    static void copyObject(Field field, Object original, Object clone) {
        Object originalValue = FieldCloningUtils.getObjectFieldValue(original, field);
        FieldCloningUtils.setObjectFieldValue(clone, field, originalValue);
    }

    static Object getObjectFieldValue(Object bean, Field field) {
        try {
            return field.get(bean);
        } catch (IllegalAccessException e) {
            throw createExceptionOnRead(bean, field, e);
        }
    }

    private static RuntimeException createExceptionOnRead(Object bean, Field field, Exception rootCause) {
        return new IllegalStateException("The class (" + bean.getClass() + ") has a field (" + field
                + ") which cannot be read to create a planning clone.", rootCause);
    }

    static void setObjectFieldValue(Object bean, Field field, Object value) {
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw createExceptionOnWrite(bean, field, value, e);
        }
    }

    private static RuntimeException createExceptionOnWrite(Object bean, Field field, Object value, Exception rootCause) {
        return new IllegalStateException("The class (" + bean.getClass() + ") has a field (" + field
                + ") which cannot be written with the value (" + value + ") to create a planning clone.", rootCause);
    }

    private FieldCloningUtils() {
        // No external instances.
    }

}
