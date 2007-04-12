/**
 * 
 */
package org.drools.rule.builder.dialect.java;

import org.drools.rule.Declaration;

/**
 * @author fburlet
 * @author gdupriez
 *
 */
public class DeclarationTypeFixer {

    public String fix(final Declaration declaration) {
        final Class classType = declaration.getExtractor().getExtractToClass();
        if ( classType.isArray() ) {
            return classType.getComponentType().getName().concat( "[]" );
        } else {
            // Does this replacement usefull in the declaration type ??
            return classType.getName().replace( '$',
                                                '.' );
        }
    }
}
