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

package org.drools.rule.builder.dialect.java;

import java.util.HashMap;
import java.util.Map;

import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

public class KnowledgeHelperFixer {
    private static final Map<String, Macro> macros;
    
    static {
        macros = new HashMap<String, Macro>(5);
        
        macros.put( "insert",
                    new Macro() {
                        public String doMacro() {
                            return "drools.insert";
                        }
                    } );
        
        macros.put( "insertLogical",
                    new Macro() {
                        public String doMacro() {
                            return "drools.insertLogical";
                        }
                    } );
        
        macros.put( "modifyRetract",
                    new Macro() {
                        public String doMacro() {
                            return "drools.modifyRetract";
                        }
                    } );
        
        macros.put( "modifyInsert",
                    new Macro() {
                        public String doMacro() {
                            return "drools.modifyInsert";
                        }
                    } );
        
        macros.put( "update",
                    new Macro() {
                        public String doMacro() {
                            return "drools.update";
                        }
                    } );
        
        macros.put( "retract",
                    new Macro() {
                        public String doMacro() {
                            return "drools.retract";
                        }
                    } );
    }

    public static String fix(final String raw) {
        if  ( raw == null || "".equals( raw.trim() )) {
            return raw;
        }
        
        MacroProcessor macroProcessor = new MacroProcessor();
        macroProcessor.setMacros( macros );
        return macroProcessor.parse( raw );
    }
}
