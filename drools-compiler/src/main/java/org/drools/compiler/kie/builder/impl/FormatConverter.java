package org.drools.compiler.kie.builder.impl;

public interface FormatConverter {

    FormatConversionResult convert(String name, byte[] input);

    static class DummyConverter implements FormatConverter {

        public static final FormatConverter INSTANCE = new DummyConverter();

        private DummyConverter() { }

        @Override
        public FormatConversionResult convert(String name, byte[] input) {
            return new FormatConversionResult(name, input);
        }
    }
}
