package org.kie.dmn.core.impl;

import java.util.Map;

import org.kie.dmn.feel.util.EvalHelper;

public interface FEELPropertyAccessible {

    EvalHelper.PropertyValueResult getFEELProperty(String property);

    Map<String, Object> allFEELProperties();
}
