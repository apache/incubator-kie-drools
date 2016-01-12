/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder.dialect.java;

import org.mvel2.Macro;
import org.mvel2.MacroProcessor;

import java.util.HashMap;
import java.util.Map;

public class KnowledgeHelperFixer {
    private static final Map<String, Macro> macros = new HashMap<String, Macro>();
    
    static {
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

        macros.put( "insertAsync",
                    new Macro() {
                        public String doMacro() {
                            return "drools.insertAsync";
                        }
                    } );

        macros.put( "bolster",
                    new Macro() {
                        public String doMacro() {
                            return "drools.bolster";
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

        macros.put( "delete",
                    new Macro() {
                        public String doMacro() {
                            return "drools.delete";
                        }
                    } );

        macros.put( "don",
                    new Macro() {
                        public String doMacro() {
                            return "drools.don";
                        }
                    } );

        macros.put( "shed",
                    new Macro() {
                        public String doMacro() {
                            return "drools.shed";
                        }
                    } );

        macros.put( "ward",
                    new Macro() {
                        public String doMacro() {
                            return "drools.ward";
                        }
                    } );

        macros.put( "grant",
                    new Macro() {
                        public String doMacro() {
                            return "drools.grant";
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
