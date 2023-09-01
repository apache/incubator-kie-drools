package org.drools.model.codegen.project.template;

import java.text.MessageFormat;

public class InvalidTemplateException extends RuntimeException {

    public InvalidTemplateException(TemplatedGenerator generator) {
        this(generator, "Wrong template");
    }

    public InvalidTemplateException(TemplatedGenerator generator, String errorMessage) {
        this(generator.targetTypeName(), generator.uncheckedTemplatePath(), errorMessage);
    }

    public InvalidTemplateException(String classType, String templateName, String errorMessage) {
        super(MessageFormat.format(
                "Invalid template for ''{0}'', file ''{1}'': {2}", classType, templateName, errorMessage));
    }

}
