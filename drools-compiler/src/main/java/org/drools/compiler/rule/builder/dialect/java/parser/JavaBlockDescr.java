/*
 * Copyright 2015 JBoss Inc
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

import java.util.List;
import java.util.Map;

public interface JavaBlockDescr {
    
    public static enum BlockType {
        MODIFY, UPDATE, INSERT, RETRACT, DELETE, ENTRY, EXIT, CHANNEL, TRY, CATCH, FINAL, IF, ELSE, FOR, SWITCH, WHILE, THROW
    }

    public BlockType getType();

    public int getStart();

    public int getEnd();
    
    public String getTargetExpression();
    public void setTargetExpression(String str);

    public Map<String, Class<?>> getInputs();

    public void setInputs(Map<String, Class< ? >> variables);

    /**
     * Returns the list of in-code, declared variables that are available
     * in the scope of this block
     * @return
     */
    public List<JavaLocalDeclarationDescr> getInScopeLocalVars();

    /**
     * Sets the list of in-code, declared variables that are available
     * in the scope of this block
     */
    public void setInScopeLocalVars( List<JavaLocalDeclarationDescr> inScopeLocalVars );
}
