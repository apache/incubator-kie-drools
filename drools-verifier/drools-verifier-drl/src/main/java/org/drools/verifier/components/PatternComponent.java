package org.drools.verifier.components;

public abstract class PatternComponent extends RuleComponent {

  private String patternName;
  private int patternOrderNumber;

  public PatternComponent(Pattern pattern) {
    super(pattern.getDescr(), pattern.getPackageName(), pattern.getRuleName());
    this.patternName = pattern.getName();
    this.patternOrderNumber = pattern.getOrderNumber();

  }

  PatternComponent(String packageName, String ruleName, String patternName, int patternOrderNumber) {
    super(null, packageName, ruleName);

    this.patternName = patternName;
    this.patternOrderNumber = patternOrderNumber;
  }

  public String getPatternPath() {
    return String.format("%s/ruleComponent[@type=%s @orderNumber=%s]", getRulePath(), VerifierComponentType.PATTERN.getType(),
        patternOrderNumber);
  }

  @Override
  public String getPath() {
    return String.format("%s/patternComponent[%s]", getPatternPath(), getOrderNumber());
  }

  public String getPatternName() {
    return patternName;
  }

  public int getPatternOrderNumber() {
    return patternOrderNumber;
  }

}
