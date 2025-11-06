package org.kie.dmn.feel.lang.ast.dialectHandlers;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;

import java.util.Map;

public class DialectHandlerFactory {
    /*public static DialectHandler getHandler(FEELDialect dialect) {
        //final static map <feeldialect, feel/bfeel>
        return switch (dialect) {
            case BFEEL -> new BFEELDialectHandler();
            case FEEL -> new FEELDialectHandler();
        };
    }*/

    private static final Map<FEELDialect, DialectHandler> DIALECT_HANDLERS = Map.of(
            FEELDialect.FEEL, new FEELDialectHandler(),
            FEELDialect.BFEEL, new BFEELDialectHandler()
    );

    public static DialectHandler getHandler(EvaluationContext ctx) {
        if(ctx == null) {
            return new FEELDialectHandler();
        }
        FEELDialect dialect = ctx.getFEELDialect();
        return DIALECT_HANDLERS.getOrDefault(dialect, new FEELDialectHandler());
    }

}
