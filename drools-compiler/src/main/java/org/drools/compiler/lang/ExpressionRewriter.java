package org.drools.compiler.lang;


import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.ConstraintConnectiveDescr;
import org.drools.drl.ast.descr.OperatorDescr;

public interface ExpressionRewriter {

    String dump( BaseDescr base );

    String dump( BaseDescr base,
                 DumperContext context );

    String dump( BaseDescr base,
                 ConstraintConnectiveDescr parent,
                 DumperContext context );

    String dump( BaseDescr base,
                 int parentPrecedence );

    StringBuilder dump( StringBuilder sbuilder,
                        BaseDescr base,
                        int parentPriority,
                        boolean isInsideRelCons,
                        DumperContext context );

    StringBuilder dump( StringBuilder sbuilder,
                        BaseDescr base,
                        ConstraintConnectiveDescr parent,
                        int parentIndex,
                        int parentPriority,
                        boolean isInsideRelCons,
                        DumperContext context );

    String processRestriction( DumperContext context,
                               String left,
                               OperatorDescr operator,
                               String right );
}