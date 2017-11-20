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

package org.drools.model.functions;

public class ScriptBlock implements BlockN {

    private final String script;

    public ScriptBlock(String script) {
        super();
        this.script = script;
    }

    @Override
    public void execute(Object... objs) {
        throw new UnsupportedOperationException("Script Block is not expected to execute out of the box.");
    }

    public String getScript() {
        return script;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( !(o instanceof ScriptBlock) ) return false;

        ScriptBlock that = ( ScriptBlock ) o;

        return script.equals( that.script );
    }

    @Override
    public int hashCode() {
        return script.hashCode();
    }
}
