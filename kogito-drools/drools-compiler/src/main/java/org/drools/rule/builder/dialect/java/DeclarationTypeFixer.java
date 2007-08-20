/**
 * 
 */
package org.drools.rule.builder.dialect.java;

import org.drools.rule.Declaration;
import org.drools.util.ClassUtils;

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
