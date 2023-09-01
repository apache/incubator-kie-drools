package org.drools.mvel.parser.ast.expr;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;

public abstract class RuleItem extends Node {

    public RuleItem( TokenRange range ) {
        super( range );
    }
}
