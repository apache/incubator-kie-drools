package org.kie.dmn.core.compiler;

import java.util.Map;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.v1_4.KieDMNModelInstrumentedBase;

public class DMNTypeRegistryV14 extends DMNTypeRegistryAbstract {

    private static final DMNType UNKNOWN = new SimpleTypeImpl(KieDMNModelInstrumentedBase.URI_FEEL,
                                                              BuiltInType.UNKNOWN.getName(),
                                                              null, true, null, null,
                                                              BuiltInType.UNKNOWN );

    public DMNTypeRegistryV14(Map<String, QName> aliases) {
        super(aliases);
    }

    @Override
    public DMNType unknown() {
        return UNKNOWN;
    }

    @Override
    protected String feelNS() {
        return KieDMNModelInstrumentedBase.URI_FEEL;
    }
}
