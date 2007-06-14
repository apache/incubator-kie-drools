/*
 * Copyright 2006 JBoss Inc
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
 *
 * Created on Jun 13, 2007
 */
package org.drools.tools.update.drl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.lang.DrlDumper;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.tools.update.drl.actions.MemoryActionsFix;

/**
 * A class to update DRL source code from version
 * 3.0 to version 4.0
 * 
 * @author etirelli
 */
public class DRLUpdate {
    
    ActionsRegistry actionsRegistry = new ActionsRegistry();
    
    public DRLUpdate() {
        this.actionsRegistry.addAction( RuleDescr.class, new MemoryActionsFix() );
    }
    
    public void updateDrl( Reader source, Writer target ) throws DroolsParserException, IOException {
        DrlParser parser = new DrlParser();
        PackageDescr pkg = parser.parse( source );
        DescriptorsVisitor visitor = new DescriptorsVisitor( this.actionsRegistry );
        
        visitor.visit( pkg );
        
        DrlDumper dumper = new DrlDumper();
        String drl = dumper.dump( pkg );
        target.write( drl );
        
    }

}
