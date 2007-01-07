package org.mvel;

public class MVEL {
    static boolean THREAD_SAFE = Boolean.getBoolean("mvflex.expression.threadsafety");

    /**
     * Force MVEL to use thread-safe caching.  This can also be specified enivromentally using the
     * <tt>mvflex.expression.threadsafety</tt> system property.
     *
     * @param threadSafe - true enabled thread-safe caching - false disables thread-safety.
     */
    public static void setThreadSafe(boolean threadSafe) {
        THREAD_SAFE = threadSafe;
        PropertyAccessor.configureFactory();
        Interpreter.configureFactory();
        ExpressionParser.configureFactory();
    }

    public static boolean isThreadSafe() {
        return THREAD_SAFE;
    }

}
