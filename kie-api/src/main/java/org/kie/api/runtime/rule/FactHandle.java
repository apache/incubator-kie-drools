package org.kie.api.runtime.rule;

/**
 * An handle to a fact inserted into the working memory
 */
public interface FactHandle {
    Object getObject();

    boolean isNegated();

    boolean isEvent();

    long getId();

    long getRecency();

     <K> K as(Class<K> klass) throws ClassCastException;

    boolean isValid();

    /**
     * The way how the fact to which this FactHandle was assigned
     * has been inserted into the working memory
     */
    enum State {
        ALL,

        /**
         * A fact that has been explicitly stated into the working memory
         */
        STATED,

        /**
         * A fact that has been logically inserted into the working memory
         */
        LOGICAL;

        public boolean isStated() {
            return this != LOGICAL;
        }

        public boolean isLogical() {
            return this != STATED;
        }
    }

    String toExternalForm();
}
