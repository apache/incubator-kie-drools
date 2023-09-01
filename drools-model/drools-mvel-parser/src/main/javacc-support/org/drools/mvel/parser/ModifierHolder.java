package org.drools.mvel.parser;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.JavaToken;


import static com.github.javaparser.utils.Utils.assertNotNull;

/**
 * Helper class for {@link GeneratedJavaParser}
 */
class ModifierHolder {
    final NodeList<Modifier> modifiers;
    final NodeList<AnnotationExpr> annotations;
    final JavaToken begin;

    ModifierHolder(JavaToken begin, NodeList<Modifier> modifiers, NodeList<AnnotationExpr> annotations) {
        this.begin = begin;
        this.modifiers = assertNotNull(modifiers);
        this.annotations = annotations;
    }
}
