package org.drools.compiler.lang;

import org.antlr.runtime.IntStream;
import org.antlr.runtime.RecognitionException;

public class DroolsUnexpectedAnnotationException extends RecognitionException {

    private final String annotationName;

    public DroolsUnexpectedAnnotationException(IntStream input, String annotationName) {
        super(input);
        this.annotationName = annotationName;
    }

    @Override
    public String toString() {
        return "DroolsUnexpectedAnnotationException( @"+ annotationName +" )";
    }
}
