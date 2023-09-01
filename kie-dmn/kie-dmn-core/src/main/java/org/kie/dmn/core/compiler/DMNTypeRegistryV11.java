package org.kie.dmn.core.compiler;

import java.util.Map;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;

public class DMNTypeRegistryV11 extends DMNTypeRegistryAbstract {

    public DMNTypeRegistryV11(Map<String, QName> aliases) {
        super(aliases);
    }

    @Override
    protected String feelNS() {
        return KieDMNModelInstrumentedBase.URI_FEEL;
    }

    private static final DMNType UNKNOWN = new SimpleTypeImpl(KieDMNModelInstrumentedBase.URI_FEEL,
                                                              BuiltInType.UNKNOWN.getName(),
                                                              null, true, null, null,
                                                              BuiltInType.UNKNOWN );

    @Override
    public DMNType unknown() {
        return UNKNOWN;
    }


}
