package org.kie.dmn.feel.lang.ast;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.Type;


/**
 * A super interface for all AST nodes
 */
public interface ASTNode {
    int getStartChar();

    int getEndChar();

    int getStartLine();

    int getStartColumn();

    int getEndLine();

    int getEndColumn();

    String getText();

    Type getResultType();

    Object evaluate(EvaluationContext ctx);

    ASTNode[] getChildrenNode();

    <T> T accept(Visitor<T> v);

}
