package org.drools.compiler.lang;


import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.OperatorDescr;

public interface ExpressionRewriter {

    public String dump( BaseDescr base );

    public String dump( BaseDescr base,
                        MVELDumper.MVELDumperContext context );

    public String dump( BaseDescr base,
                        int parentPrecedence );

    public StringBuilder dump( StringBuilder sbuilder,
                               BaseDescr base,
                               int parentPriority,
                               boolean isInsideRelCons,
                               MVELDumper.MVELDumperContext context );

    public void processRestriction( MVELDumper.MVELDumperContext context,
                                    StringBuilder sbuilder,
                                    String left,
                                    OperatorDescr operator,
                                    String right );

    public Class<?> getEvaluatorWrapperClass();
}
