package org.drools.modelcompiler.builder.generator.declaredtype;

class PojoGenerationError {

    private final String errorMessage;

    public PojoGenerationError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
