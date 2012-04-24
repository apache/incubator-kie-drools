package org.drools.lang;


import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.OperatorDescr;

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
