/*
 * Copyright 2010 JBoss Inc
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

package org.drools.reteoo.test.dsl;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Description;


/**
 * A class to describe a single Node test
 * 
 * @author etirelli
 */
public class NodeTestDef {
    
    private String name;
    private int line;
    private List<DslStep> steps;
    private Description description;

    public NodeTestDef() {
        this( "", -1 );
    }
    
    public NodeTestDef(String name,
                    int line) {
        super();
        this.name = name;
        this.line = line;
        this.steps = new ArrayList<DslStep>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public List<DslStep> getSteps() {
        return steps;
    }

    public void addStep(DslStep step) {
        this.steps.add( step );
    }

    public Description getDescription() {
        return description;
    }

    public void setDescription(Description description) {
        this.description = description;
    }

}
