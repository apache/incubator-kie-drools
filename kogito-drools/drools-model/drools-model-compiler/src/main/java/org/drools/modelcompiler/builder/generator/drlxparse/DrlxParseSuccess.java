package org.drools.modelcompiler.builder.generator.drlxparse;

import java.util.Collection;

public interface DrlxParseSuccess extends DrlxParseResult {

    boolean isValidExpression();

    String getExprBinding();

    Expression getExpr();

    boolean isRequiresSplit();

    boolean isTemporal();

    DrlxParseSuccess addAllWatchedProperties( Collection<String> watchedProperties);
}
