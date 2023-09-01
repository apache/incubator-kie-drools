package org.kie.dmn.feel.lang.types;

import java.util.List;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;

/**
 * Used for:
 * instance of itemDef
 * itemDef resolutions.
 */
public interface FEELTypeRegistry {

    Scope getItemDefScope(Scope parent);

    Type resolveFEELType(List<String> qns);

}
