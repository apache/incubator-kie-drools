package org.drools.natural.ruledoc.dumper;

import java.io.StringWriter;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.drools.natural.NaturalLanguageException;
import org.drools.natural.ruledoc.DictionaryHelper;

/**
 * This utility generates stuff - either HTML for reviewing rulesets, or
 * rulesource itself.
 * 
 * It makes heavy use of the apache Velocity templating engine.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RuleDumper {

    private static VelocityEngine velocity;
    
    /** Lazy init the velocity engine.
     * Yes yes, double checked locking and all that... blah blah blah..
     * It wouldn't matter to much if a race happened anyway.
     */
    private static VelocityEngine getVelocity() throws Exception {
        if (velocity == null) {
            synchronized(RuleDumper.class) {
                //tell velocity to load templates from the classpath
                VelocityEngine e = new VelocityEngine();
                e.setProperty("resource.loader", "class");
                e.setProperty("class.resource.loader.class", 
                                          "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
                e.init();
                velocity = e;
            }            
        }   
        return velocity;
    }
    private Template template;
    
    
    public RuleDumper(String templateName){
        try {
                        
            template = getVelocity().getTemplate( templateName );
        }
        catch ( ResourceNotFoundException e ) {
            throw new NaturalLanguageException( "Unable to find output template.",
                                                e );
        }
        catch ( ParseErrorException e ) {
            throw new NaturalLanguageException( "Parse error on output template.",
                                                e );
        }
        catch ( Exception e ) {
            throw new NaturalLanguageException( "Error loading output template.",
                                                e );
        }
    }

    public void dump(StringWriter writer,
                     List ruleFragments,
                     DictionaryHelper dicHelper){

        VelocityContext ctx = new VelocityContext();
        ctx.put("functions", dicHelper.getFunctions());
        ctx.put("rule-list",
                 ruleFragments );
        ctx.put("application-data", dicHelper.getApplicationData());
        ctx.put("ruleset-name", dicHelper.getRulesetName());
        ctx.put("imports", dicHelper.getImports());        

        try {
            
            template.merge( ctx,
                            writer );
        }
        catch ( Exception e ) {
            throw new NaturalLanguageException( "Error merging into output template.",
                                                e );
        }

    }

}
