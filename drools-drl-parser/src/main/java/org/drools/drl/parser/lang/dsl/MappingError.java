package org.drools.drl.parser.lang.dsl;

import org.drools.drl.parser.DroolsError;

/**
 * MappingError
 * A class to represent errors found in a DSL mapping 
 *
 *
 * Created: 11/04/2006
 */
public class MappingError extends DroolsError {
    public static final int TEMPLATE_UNKNOWN       = 0;
    public static final int TEMPLATE_NATURAL       = 1;
    public static final int TEMPLATE_TARGET        = 2;

    public static final int ERROR_UNUSED_TOKEN     = 21;
    public static final int ERROR_UNDECLARED_TOKEN = 22;
    public static final int ERROR_INVALID_TOKEN    = 23;
    public static final int ERROR_UNMATCHED_BRACES = 24;

    private final int       errorCode;
    private final int       template;
    private final int       offset;
    private final String    token;
    private String          templateText;
    private final int[]     line;

    public MappingError(final int errorCode,
                        final int template,
                        final int offset,
                        final String token,
                        final String templateText,
                        final int line ) {
        this.errorCode = errorCode;
        this.template = template;
        this.token = token;
        this.offset = offset;
        this.templateText = templateText;
        this.line = new int[] { line };
    }

    /**
     * Returns this error code
     * @return
     */
    public int getErrorCode() {
        return this.errorCode;
    }
    
    public int[] getLines() {
        return this.line;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * @return the template
     */
    public int getTemplate() {
        return this.template;
    }

    /**
     * @return the original content.
     */
    public String getTemplateText() {
        return this.templateText;
    }

    /**
     * @inheritDoc 
     *
     * @see org.kie.compiler.DroolsError#getMessage()
     */
    public String getMessage() {
        switch ( this.errorCode ) {
            case ERROR_UNUSED_TOKEN :
                return "Warning, the token " + this.token + " not used in the mapping.";
            case ERROR_UNDECLARED_TOKEN :
                return "Warning, the token " + this.token + " not found in the expression. (May not be a problem).";
            case ERROR_INVALID_TOKEN :
                return "Invalid token declaration at offset " + this.offset + ": " + this.token;
            case ERROR_UNMATCHED_BRACES :
                return "Unexpected } found at offset " + this.offset;
            default :
                return "Unkown error at offset: " + this.offset;
        }
    }
}
