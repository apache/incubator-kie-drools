package org.kie.kogito.codegen;

import java.text.MessageFormat;

public class InvalidTemplateException extends RuntimeException {

    public InvalidTemplateException(String classType, String templateName, String errorMessage) {
        super(MessageFormat.format(
                "Invalid template for ''{0}'', file ''{1}'': {2}", classType, templateName, errorMessage));
    }


}
