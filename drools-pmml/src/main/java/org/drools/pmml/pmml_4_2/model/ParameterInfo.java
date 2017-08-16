package org.drools.pmml.pmml_4_2.model;

public class ParameterInfo<T> {
    private String name;
    private Class<T> type;
    private T value;

    public ParameterInfo(String name, Class<T> type, T value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getCapitalizedName() {
        return name.substring(0,1).toUpperCase()+name.substring(1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<T> getType() {
        return type;
    }

    public void setType(Class<T> type) {
        this.type = type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("ParameterInfo( ");
        stringBuilder.append("name='").append(name).append("', ");
        stringBuilder.append("type='").append(type.getName()).append("', ");
        if (type.getName().equals(String.class.getName())) {
            stringBuilder.append("value='").append(value).append("' )");
        } else {
            stringBuilder.append("value=").append(value).append(" )");
        }
        return stringBuilder.toString();
    }
}
