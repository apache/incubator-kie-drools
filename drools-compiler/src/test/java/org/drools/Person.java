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



public class Person {
    private final String name;
    private final String likes;
    private final int age;
    
    private char sex;
    
    private boolean alive;
    
    private String status;
    
    public Person( String name  ) {
        this( name, "", 0 );
    }
    
    
    public Person( String name, String likes ) {
        this( name, likes, 0 );
    }
    
    public Person( String name, String likes, int age ) {
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLikes() {
        return likes;
    }

    public String getName() {
        return name;
    }
        
    public int getAge() {
        return this.age;
    }
    
    
    
    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String toString() {
        return "[Person name='" + this.name + "']";
    }
}