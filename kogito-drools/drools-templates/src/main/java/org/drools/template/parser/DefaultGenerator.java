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

package org.drools.template.parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * Generate the rules for a decision table row from a rule template.
 */
public class DefaultGenerator
    implements
    Generator {

    private Map<String, RuleTemplate> ruleTemplates;

    private TemplateRegistry          registry = new SimpleTemplateRegistry();

    private List<String>              rules    = new ArrayList<String>();

    public DefaultGenerator(final Map<String, RuleTemplate> t) {
        ruleTemplates = t;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.decisiontable.parser.Generator#generate(java.lang.String,
     *      org.drools.decisiontable.parser.Row)
     */
    public void generate(String templateName,
                         Row row) {
        try {
            CompiledTemplate template = getTemplate( templateName );
            Map<String, Object> vars = new HashMap<String, Object>();
            vars.put( "row",
                      row );

            for ( Cell cell : row.getCells() ) {
                cell.addValue( vars );
            }

            String drl = String.valueOf( TemplateRuntime.execute( template,
                                                                  vars,
                                                                  registry ) );

            rules.add( drl );
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    private CompiledTemplate getTemplate(String templateName) throws IOException {
        CompiledTemplate contents;
        if ( !registry.contains( templateName ) ) {
            RuleTemplate template = ruleTemplates.get( templateName );
            contents = TemplateCompiler.compileTemplate( template.getContents() );
            registry.addNamedTemplate( templateName,
                                       contents );
        } else {
            contents = registry.getNamedTemplate( templateName );
        }
        return contents;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.decisiontable.parser.Generator#getDrl()
     */
    public String getDrl() {
        StringBuffer sb = new StringBuffer();
        for ( String rule : rules ) {
            sb.append( rule ).append( "\n" );
        }
        return sb.toString();
    }

}
