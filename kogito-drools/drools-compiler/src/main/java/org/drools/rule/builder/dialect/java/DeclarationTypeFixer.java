/**
 * 
 */
package org.drools.rule.builder.dialect.java;

import org.drools.core.util.ClassUtils;
import org.drools.rule.Declaration;

/**
 * @author fburlet
 * @author gdupriez
 *
 */
public class DeclarationTypeFixer {

    public String fix(final Declaration declaration) {
        final Class classType = declaration.getExtractor().getExtractToClass();
        return ClassUtils.canonicalName( classType );
    }
}
