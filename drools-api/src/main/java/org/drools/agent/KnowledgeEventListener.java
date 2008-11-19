package org.drools.agent;

public interface KnowledgeEventListener {
    /**
     * For general info messages
     */
    public void info(String message);

    /**
     * For a warning (useful when tracking down problems).
     */
    public void warning(String message);

    /**
     * An exception occurred.
     */
    public void exception(Exception e);

    /**
     * These should not be logged, just shown if needed.
     */
    public void debug(String message);
}
