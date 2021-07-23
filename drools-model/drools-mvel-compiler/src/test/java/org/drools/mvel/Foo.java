package org.drools.mvel;

import java.util.Collection;

public class Foo {
  private Bar bar = new Bar();
  public String register;
  public String aValue = "";
  public String bValue = "";
  private String name = "dog";
  private int countTest = 0;
  private boolean boolTest = true;
  private char charTest;
  public char charTestFld;
  private Collection collectionTest;
  private SampleBean sampleBean = new SampleBean();
  public Bar publicBar = new Bar();
  public static final Bar STATIC_BAR = new Bar();
  
  private char[] charArray;

  private char[][] charArrayMulti;

  public void abc() {
  }

  public Bar getBar() {
    return bar;
  }

  public void setBar(Bar bar) {
    this.bar = bar;
  }

  public String happy() {
    return "happyBar";
  }

  public String toUC(String s) {
    register = s;
    return s.toUpperCase();
  }

  public int getNumber() {
    return 4;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Collection getCollectionTest() {
    return collectionTest;
  }

  public void setCollectionTest(Collection collectionTest) {
    this.collectionTest = collectionTest;
  }

  public SampleBean getSampleBean() {
    return sampleBean;
  }

  public void setSampleBean(SampleBean sampleBean) {
    this.sampleBean = sampleBean;
  }

  public int getCountTest() {
    return countTest;
  }

  public void setCountTest(int countTest) {
    this.countTest = countTest;
  }

  public boolean isBoolTest() {
    return boolTest;
  }

  public void setBoolTest(boolean boolTest) {
    this.boolTest = boolTest;
  }

  public char getCharTest() {
    return charTest;
  }

  public void setCharTest(char charTest) {
    this.charTest = charTest;
  }

  public char[] getCharArray() {
    return charArray;
  }

  public void setCharArray(char[] charArray) {
    this.charArray = charArray;
  }

  public char[][] getCharArrayMulti() {
    return charArrayMulti;
  }

  public void setCharArrayMulti(char[][] charArrayMulti) {
    this.charArrayMulti = charArrayMulti;
  }

  public boolean equals(Object o) {
    return o instanceof Foo;
  }
}
