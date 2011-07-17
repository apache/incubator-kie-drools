package org.drools.rule.builder.dialect.java;

import org.drools.core.util.ClassUtils;
import org.drools.rule.Declaration;

public class DeclarationTypeFixer {

    public String fix(final Declaration declaration) {
        if ( declaration.getExtractor() != null ) {
            final Class classType = declaration.getExtractor().getExtractToClass();
            return ClassUtils.canonicalName( classType );
        } else {
            // we assume that null extractor errors are reported else where
            return null;
        }
    }
}
