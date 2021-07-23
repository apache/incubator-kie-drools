package org.drools.mvel;


public enum MyEnum {
  ALTERNATIVE("Alternative"),
  FULL_DOCUMENTATION("FullDocumentation");

  private final String value;

  MyEnum(String v) {
    value = v;
  }

  public String value() {
    return value;
  }

  public static MyEnum fromValue(String v) {
    for (MyEnum c : MyEnum.values()) {
      if (c.value.equals(v)) {
        return c;
      }
    }
    throw new IllegalArgumentException(v);
  }
}
