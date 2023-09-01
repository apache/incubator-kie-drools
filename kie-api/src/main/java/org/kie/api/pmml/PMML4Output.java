package org.kie.api.pmml;

public interface PMML4Output<T> {
    public String getCorrelationId();
    public void setCorrelationId(String correlationId);
    public String getName();
    public void setName(String name);
    public String getDisplayValue();
    public void setDisplayValue(String displayValue);
    public T getValue();
    public void setValue(T value);
    public Double getWeight();
    public void setWeight(Double weight);
    public String getSegmentationId();
    public void setSegmentationId(String segmentationId);
    public String getSegmentId();
    public void setSegmentId(String segmentId);
}
