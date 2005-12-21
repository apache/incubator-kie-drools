package org.drools.natural.ruledoc;

public interface RuleDocumentListener
{

    /**
     * Process a line of text.
     */
    public abstract void handleText(String text);

    public abstract void startTable();

    public abstract void startColumn();

    public abstract void startRow();

    public abstract void endTable();

    public abstract void endColumn();

    public abstract void endRow();

    public abstract void startComment();

    public abstract void endComment();

}