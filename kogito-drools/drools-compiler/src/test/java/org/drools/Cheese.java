package org.drools;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



public class Cheese {
    private String type;
    private int    price;

    public Cheese(String type,
                  int price) {
        super();
        this.type = type;
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }
    
    public void setPrice(int price) {
        this.price = price;
    }

}