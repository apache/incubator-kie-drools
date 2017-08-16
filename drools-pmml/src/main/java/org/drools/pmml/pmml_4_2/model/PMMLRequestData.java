package org.drools.pmml.pmml_4_2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PMMLRequestData {
    private String modelName;
    private List<ParameterInfo> requestParams;

    public PMMLRequestData() {
        this.requestParams = new ArrayList<>();
    }

    public PMMLRequestData(String modelName) {
        this.modelName = modelName;
        this.requestParams = new ArrayList<>();
    }

    private <T> T getValue(String parameterName) {
        ParameterInfo pinfo = requestParams.stream().filter(pi -> pi.getName().equals(parameterName)).findFirst().orElse(null);
        return (pinfo != null) ? (T)pinfo.getValue() : null;
    }

    public String getModelName() {
        return this.modelName;
    }

    public String getCompactCapitalizedModelName() {
        String[] tokens = modelName.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String part: tokens) {
            builder.append(part.substring(0,1).toUpperCase()).append(part.substring(1));
        }
        return builder.toString();
    }

    public synchronized Map<String, ParameterInfo> getMappedRequestParams() {
        return requestParams.stream().collect(Collectors.toMap(pi -> pi.getName(),pi -> pi));
    }

    public synchronized Collection<ParameterInfo> getRequestParams() {
        return new ArrayList<>(this.requestParams);
    }

    public synchronized boolean addRequestParam(ParameterInfo parameter) {
        return this.requestParams.add(parameter);
    }

    public synchronized boolean removeRequestParam(ParameterInfo parameter) {
        return this.requestParams.remove(parameter);
    }

    public synchronized boolean addRequestParam(String paramName, Object value) {
        Class<?> clazz = value.getClass();
        ParameterInfo parameter = new ParameterInfo(paramName, clazz, value);
        return this.addRequestParam(parameter);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder("PMMLRequestData( ");
        stringBuilder.append("modelName='").append(modelName).append("', requestParams=[");
        Iterator<ParameterInfo> iter = requestParams.iterator();
        boolean firstParam = true;
        while (iter.hasNext()) {
            if (!firstParam) {
                stringBuilder.append(", ");
            } else {
                firstParam = false;
            }
            ParameterInfo pi = iter.next();
            stringBuilder.append(pi.toString());
        }
        stringBuilder.append("] )");
        return stringBuilder.toString();
    }
}
