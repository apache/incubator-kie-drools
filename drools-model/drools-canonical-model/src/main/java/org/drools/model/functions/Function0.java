package org.drools.model.functions;

public interface Function0<R> {
    R apply();

    public static final class Null implements Function0 {

        public static final Null INSTANCE = new Null();

        private Null() { }

        @Override
        public Object apply() {
            return null;
        }
    }
}
