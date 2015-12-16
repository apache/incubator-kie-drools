/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler;

public class FirstClass {
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String item5;
    
    public FirstClass() {
    }
    
    public FirstClass(String item1,
                      String item2,
                      String item3,
                      String item4,
                      String item5) {
        super();
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.item4 = item4;
        this.item5 = item5;
    }
    public String getItem1() {
        return item1;
    }
    public void setItem1(String item1) {
        this.item1 = item1;
    }
    public String getItem2() {
        return item2;
    }
    public void setItem2(String item2) {
        this.item2 = item2;
    }
    public String getItem3() {
        return item3;
    }
    public void setItem3(String item3) {
        this.item3 = item3;
    }
    public String getItem4() {
        return item4;
    }
    public void setItem4(String item4) {
        this.item4 = item4;
    }
    public String getItem5() {
        return item5;
    }
    public void setItem5(String item5) {
        this.item5 = item5;
    }

    public static class AlternativeKey {
        
    }

}
