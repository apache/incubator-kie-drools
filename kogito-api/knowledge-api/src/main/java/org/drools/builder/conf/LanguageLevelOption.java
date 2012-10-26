package org.drools.builder.conf;

/**
 * A class for the language level configuration.
 */
public class LanguageLevelOption implements SingleValueKnowledgeBuilderOption {

    private static final long serialVersionUID = 510l;

    /**
     * The property name for the language level
     */
    public static final String PROPERTY_NAME = "drools.lang.level";

    private final int languageLevel;

    /**
     * Private constructor to enforce the use of the factory method
     * @param languageLevel
     */
    private LanguageLevelOption( int languageLevel ) {
        this.languageLevel = languageLevel;
    }

    /**
     * This is a factory method for this LanguageLevel configuration.
     * The factory method is a best practice for the case where the
     * actual object construction is changed in the future.
     *
     * @param languageLevel the level of the language to be used
     *
     * @return the actual type safe LanguageLevel configuration.
     */
    public static LanguageLevelOption get( int languageLevel ) {
        return new LanguageLevelOption( languageLevel );
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    /**
     * Returns the level of the language to be used
     *
     * @return
     */
    public int getLanguageLevel() {
        return languageLevel;
    }

    @Override
    public String toString() {
        return "LanguageLevelOption( languageLevel="+languageLevel+" )";
    }

    @Override
    public int hashCode() {
        return languageLevel;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        LanguageLevelOption other = (LanguageLevelOption) obj;
        return languageLevel == other.languageLevel;
    }
}
