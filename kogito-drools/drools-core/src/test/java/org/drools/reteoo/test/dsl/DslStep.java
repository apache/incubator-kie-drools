/**
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
import java.util.Arrays;
import java.util.List;

public class DslStep {
    private int          line;

    private String       name;
    private List<String[]> commands;

    public DslStep(int line,
                   String name ) {
        this( line, name, new ArrayList<String[]>() );
    }

    public DslStep(int line,
                   String name,
                   List<String[]> commands) {
        this.line = line;
        this.name = name;
        this.commands = commands;
    }

    public String getName() {
        return name;
    }

    public int getLine() {
        return line;
    }

    public List<String[]> getCommands() {
        return commands;
    }
    
    public void addCommand( String[] command ) {
        this.commands.add( command );
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append( line );
        builder.append( " : " );
        builder.append( name );
        builder.append( " : " );
        for( String[] command : commands ) {
            builder.append( Arrays.toString( command ) );
            builder.append( "\n" );
        }
        return  builder.toString();
    }

}