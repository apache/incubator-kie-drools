package org.kie.efesto.runtimemanager.api.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EfestoMapInputDTO implements Serializable {


    private static final long serialVersionUID = 8616386112525557777L;

    private final List<Object> inserts;
    private final Map<String, Object> globals;

    private final Map<String, Object> unwrappedInputParams;
    private final Map<String, EfestoOriginalTypeGeneratedType> fieldTypeMap;

    private final String modelName;
    private final String packageName;

    public EfestoMapInputDTO(final List<Object> inserts,
                          final Map<String, Object> globals,
                          final Map<String, Object> unwrappedInputParams,
                          final Map<String, EfestoOriginalTypeGeneratedType> fieldTypeMap,
                          final String modelName,
                          final String packageName) {
        this.inserts = inserts;
        this.globals = globals;
        this.unwrappedInputParams = unwrappedInputParams;
        this.fieldTypeMap = fieldTypeMap;
        this.modelName = modelName;
        this.packageName = packageName;
    }

    public List<Object> getInserts() {
        return Collections.unmodifiableList(inserts);
    }

    public Map<String, Object> getGlobals() {
        return Collections.unmodifiableMap(globals);
    }

    public Map<String, Object> getUnwrappedInputParams() {
        return Collections.unmodifiableMap(unwrappedInputParams);
    }

    public Map<String, EfestoOriginalTypeGeneratedType> getFieldTypeMap() {
        return Collections.unmodifiableMap(fieldTypeMap);
    }

    public String getModelName() {
        return modelName;
    }

    public String getPackageName() {
        return packageName;
    }
}
