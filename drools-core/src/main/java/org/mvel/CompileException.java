package org.mvel;

import static java.lang.String.copyValueOf;

public class CompileException extends RuntimeException {
    private char[] expr;
    private int cursor;

    public CompileException() {
        super();
    }

    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, char[] expr, int cursor, Exception e) {
        super("Failed to compile:\n[Error: " + message + "]\n[Near: \"" + showCodeNearError(expr, cursor) + "\"]", e);
    }

    public CompileException(String message, char[] expr, int cursor) {
         super("Failed to compile:\n[Error: " + message + "]\n[Near: \"" + showCodeNearError(expr, cursor) + "\"]");
     }


    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompileException(Throwable cause) {
        super(cause);
    }

    private static CharSequence showCodeNearError(char[] expr, int cursor) {
        int start = cursor - 10;
        int end = (cursor + 20);

        if (start < 0) {
            start = 0;
        }
        if (end > expr.length) {
            end = expr.length - 1;
        }
        return "'" + copyValueOf(expr, start, end - start) + "'";
    }
}
