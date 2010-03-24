package org.drools.verifier.components;

public abstract class PatternComponent extends RuleComponent {

    private String patternName;
    private int    patternOrderNumber;

    public PatternComponent(Pattern pattern) {
        this( pattern.getPackageName(),
              pattern.getRuleName(),
              pattern.getName(),
              pattern.getOrderNumber() );

    }

    PatternComponent(String packageName,
                     String ruleName,
                     String patternName,
                     int patternOrderNumber) {
        super( packageName,
               ruleName );

        this.patternName = patternName;
        this.patternOrderNumber = patternOrderNumber;
    }

    public String getPatternPath() {
        return String.format( "%s.pattern[%s]",
                              getRulePath(),
                              patternOrderNumber );
    }

    @Override
    public String getPath() {
        return String.format( "%s.patternComponent[%s]",
                              getPatternPath(),
                              getOrderNumber() );
    }

    public String getPatternName() {
        return patternName;
    }

}
