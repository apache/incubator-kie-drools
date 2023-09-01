package org.kie.utll.xml;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.security.TypePermission;

/**
 * Permission for any type which is annotated with an XStream annotation.
 * This presumes that because the class has an XStream annotation, it was designed with XStream in mind,
 * and therefore it is not vulnerable. Jackson and JAXB follow this philosophy too.
 */
// TODO Replace with upstream one when upgrading to XStream 1.5.0
// See https://github.com/x-stream/xstream/pull/99)
public class AnyAnnotationTypePermission implements TypePermission {

    @Override
    public boolean allows(final Class type) {
        if (type == null) {
            return false;
        }
        return type.isAnnotationPresent(XStreamAlias.class)
                || type.isAnnotationPresent(XStreamAliasType.class)
                || type.isAnnotationPresent(XStreamInclude.class);
    }

}
