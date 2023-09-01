package org.drools.model.codegen.project.template;

import java.text.MessageFormat;

public class TemplateInstantiationException extends RuntimeException {

    public TemplateInstantiationException(String classType, String templateName, String errorMessage) {
        super(MessageFormat.format(
                "Cannot instantiate template for ''{0}'', file ''{1}'': {2}", classType, templateName, errorMessage));
    }

    public TemplateInstantiationException(String classType, String templateName, Throwable cause) {
        super(MessageFormat.format(
                "Cannot instantiate template for ''{0}'', file ''{1}''. An exception was caught.", classType, templateName), cause);
    }
}
