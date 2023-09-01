package org.drools.mvel;

public class Thing {
  private MyEnum myEnum;

  private String name;

  public Thing(String name) {
    this.name = name;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public MyEnum getMyEnum() {
    return myEnum;
  }

  public void setMyEnum(MyEnum myEnum) {
    this.myEnum = myEnum;
  }

}
