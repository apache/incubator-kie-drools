package org.drools.lang.descr;

/**
 * This represents a literal node in the rule language. This is
 * a constraint on a single field of a column. 
 * The "text" contains the content, which may also be an enumeration. 
 */
public class LiteralDescr extends PatternDescr {
    private String fieldName;
    private String evaluator;
    private String text;
    private boolean staticFieldValue;

    public LiteralDescr(String fieldName,
                        String evaluator,
                        String text) {
        this(fieldName, evaluator, text, false);
    }
    
    public LiteralDescr(String fieldName,
                        String evaluator,
                        String text, boolean staticFieldValue) {
        this.fieldName = fieldName;
        this.text = text;
        this.evaluator = evaluator;
        this.staticFieldValue = staticFieldValue;
    }
    
    public boolean isStaticFieldValue() {
        return staticFieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getEvaluator() {
        return evaluator;
    }

    public String getText() {
        return this.text;
    }
}
