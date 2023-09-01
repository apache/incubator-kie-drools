package org.drools.model.codegen.execmodel.generator.visitor.accumulate;

public class InvalidInlineTemplateException extends RuntimeException {

    InvalidInlineTemplateException() {
    }

    InvalidInlineTemplateException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getMessage() {
        return "Inline accumulate template is not valid";
    }
}
