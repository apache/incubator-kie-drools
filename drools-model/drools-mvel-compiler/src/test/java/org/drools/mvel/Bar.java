/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel;

import java.util.ArrayList;
import java.util.List;

public class Bar {
  private String name = "dog";
  private boolean woof = true;
  private int age = 14;
  private String assignTest = "";
  private List<Integer> testList = new ArrayList<Integer>();
  private Integer[] intarray = new Integer[1];

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isWoof() {
    return woof;
  }

  public void setWoof(boolean woof) {
    this.woof = woof;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public boolean isFoo(Object obj) {
    return obj instanceof Foo;
  }

  public String getAssignTest() {
    return assignTest;
  }

  public void setAssignTest(String assignTest) {
    this.assignTest = assignTest;
  }


  public List<Integer> getTestList() {
    return testList;
  }

  public void setTestList(List<Integer> testList) {
    this.testList = testList;
  }

  public String happy() {
    return "happyBar";
  }

  public static int staticMethod() {
    return 1;
  }

  public Integer[] getIntarray() {
    return intarray;
  }

  public void setIntarray(Integer[] intarray) {
    this.intarray = intarray;
  }

  public boolean equals(Object o) {
    return o instanceof Bar;
  }
}
