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

package org.drools.compiler.rule.builder.dialect.asm;

import org.drools.core.rule.Declaration;
import org.drools.core.rule.builder.dialect.asm.InvokerDataProvider;

import java.util.Map;

public class InvokerContext implements InvokerDataProvider {

    private Map<String, Object> invokerContext;

    public InvokerContext(Map<String, Object> invokerContext) {
        this.invokerContext = invokerContext;
    }

    @Override
    public int hashCode() {
        return (Integer) invokerContext.get("hashCode");
    }

    public String getPackageName() {
        return (String) invokerContext.get("package");
    }

    public String getRuleClassName() {
        return (String) invokerContext.get("ruleClassName");
    }

    public String getInternalRuleClassName() {
        return (getPackageName() + "." + getRuleClassName()).replace(".", "/");
    }

    public String getInvokerClassName() {
        return (String) invokerContext.get("invokerClassName");
    }

    public String getMethodName() {
        return (String) invokerContext.get("methodName");
    }

    public String[] getGlobals() {
        return (String[]) invokerContext.get("globals");
    }

    public String[] getGlobalTypes() {
        return (String[]) invokerContext.get("globalTypes");
    }

    public Boolean[] getNotPatterns() {
        return (Boolean[]) invokerContext.get("notPatterns");
    }

    public Declaration[] getDeclarations() {
        return (Declaration[]) invokerContext.get( "declarations" );
    }
}
