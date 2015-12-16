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

package org.drools.compiler.rule.builder.dialect.java.parser;


/**
 * A helper class used during java code parsing to identify
 * and handle exitPoints calls
 */
public class JavaInterfacePointsDescr extends AbstractJavaBlockDescr implements JavaBlockDescr {
    
    private int start;
    private int end;
    private String id;
    private String targetExpression;
    private BlockType type;
    
    public JavaInterfacePointsDescr( String id ) {
        this.id = id;
    }
    
    /* (non-Javadoc)
     * @see org.kie.rule.builder.dialect.java.parser.JavaBlockDescr#getStart()
     */
    public int getStart() {
        return start;
    }
    public void setStart(int start) {
        this.start = start;
    }
    /* (non-Javadoc)
     * @see org.kie.rule.builder.dialect.java.parser.JavaBlockDescr#getEnd()
     */
    public int getEnd() {
        return end;
    }
    public void setEnd(int end) {
        this.end = end;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String toString() {
        return type+"( start="+start+" end="+end+" id="+id+" )";
    }

    public BlockType getType() {
        return type;
    }
    
    public String getTargetExpression() {
        return targetExpression;
    }
    
    public void setTargetExpression(String targetExpression) {
        this.targetExpression = targetExpression;
    }

    public void setType(BlockType type) {
        this.type = type;
    }

}
