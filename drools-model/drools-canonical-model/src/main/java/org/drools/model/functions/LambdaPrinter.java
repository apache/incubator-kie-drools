package org.drools.model.functions;

import java.lang.reflect.Field;
import java.util.function.Function;

public interface LambdaPrinter {

    String getLambdaFingerprint(Object lambda);

    static String print(Object lambda) {
        return Factory.get().getLambdaFingerprint(lambda);
    }

    class Factory {
        private static class LazyHolder {
            private static LambdaPrinter INSTANCE = buildPrinter();

            private static LambdaPrinter buildPrinter() {
                try {
                    return new LambdaVisitor( (Function<Object, String>) Class.forName( "org.drools.mvel.asm.LambdaIntrospector" ).newInstance() );
                } catch (Exception e) {
                    return new DummyLambdaPrinter();
                }
            }
        }

        public static LambdaPrinter get() {
            return LazyHolder.INSTANCE;
        }
    }

    class DummyLambdaPrinter implements LambdaPrinter {

        @Override
        public String getLambdaFingerprint( Object lambda ) {
            if (lambda.toString().equals("INSTANCE")) { // Materialized lambda
                return getExpressionHash(lambda);
            }
            return lambda.toString();
        }

        private static String getExpressionHash(Object lambda) {
            Field expressionHash;
            try {
                expressionHash = lambda.getClass().getDeclaredField("EXPRESSION_HASH");
                return (String) expressionHash.get(lambda);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                throw new RuntimeException( e );
            }
        }
    }

    class LambdaVisitor implements LambdaPrinter {

        private final Function<Object, String> introspector;

        public LambdaVisitor( Function<Object, String> introspector ) {
            this.introspector = introspector;
        }

        @Override
        public String getLambdaFingerprint( Object lambda ) {
            return introspector.apply( lambda );
        }
    }

}
