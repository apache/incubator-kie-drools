package org.kie.dmn.core.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;

public class DMNTypeRegistryV12 extends DMNTypeRegistryAbstract {

    private static final DMNType UNKNOWN = new SimpleTypeImpl(KieDMNModelInstrumentedBase.URI_FEEL,
                                                              BuiltInType.UNKNOWN.getName(),
                                                              null, true, null, null,
                                                              BuiltInType.UNKNOWN );

    public DMNTypeRegistryV12() {
        super(Collections.emptyMap());
    }

    public DMNTypeRegistryV12(Map<String, QName> aliases) {
        super(aliases);
    }

    @Override
    public DMNType unknown() {
        return UNKNOWN;
    }

    /** 
     * DMN v1.2 spec, chapter 7.3.2 ItemDefinition metamodel
     * FEEL built-in data types: number, string, boolean, days and time duration, years and months duration, time, and date and time. Was missing from spec document: date, Any, list, function, context.
     */
    public static final List<BuiltInType> ITEMDEF_TYPEREF_FEEL_BUILTIN = Collections.unmodifiableList(Arrays.asList(BuiltInType.NUMBER,
                                                                                                                    BuiltInType.STRING,
                                                                                                                    BuiltInType.BOOLEAN,
                                                                                                                    BuiltInType.DURATION,
                                                                                                                    BuiltInType.DATE,
                                                                                                                    BuiltInType.TIME,
                                                                                                                    BuiltInType.DATE_TIME,
                                                                                                                    BuiltInType.UNKNOWN,
                                                                                                                    BuiltInType.LIST,
                                                                                                                    BuiltInType.FUNCTION,
                                                                                                                    BuiltInType.CONTEXT));

    @Override
    protected String feelNS() {
        return KieDMNModelInstrumentedBase.URI_FEEL;
    }

}
