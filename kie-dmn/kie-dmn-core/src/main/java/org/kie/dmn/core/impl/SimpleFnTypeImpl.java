package org.kie.dmn.core.impl;

import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.model.api.FunctionItem;

/**
 * @see DMNType
 */
public class SimpleFnTypeImpl extends SimpleTypeImpl {

    private final Map<String, DMNType> params;
    private final DMNType returnType;
    private final FunctionItem fi;

    public SimpleFnTypeImpl(String namespace, String name, String id, Type feelType, Map<String, DMNType> params, DMNType returnType, FunctionItem fi) {
        super(namespace, name, id, false, null, null, feelType);
        this.params = new HashMap<>(params);
        this.returnType = returnType;
        this.fi = fi;
    }

    public Map<String, DMNType> getParams() {
        return params;
    }

    public DMNType getReturnType() {
        return returnType;
    }

    public FunctionItem getFunctionItem() {
        return fi;
    }

    public BaseDMNTypeImpl clone() {
        throw new UnsupportedOperationException();
    }
}
