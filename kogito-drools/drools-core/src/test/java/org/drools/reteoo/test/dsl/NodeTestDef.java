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
