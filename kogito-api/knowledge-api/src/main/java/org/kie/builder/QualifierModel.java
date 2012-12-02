package org.kie.builder;

import java.util.Map;

public interface QualifierModel {

    String getType();

    String getValue();

    void setValue(String value);

    QualifierModel addArgument(String key, String value);

    Map<String, String> getArguments();
}
