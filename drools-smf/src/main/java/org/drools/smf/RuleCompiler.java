package org.drools.smf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.ResourceStore;
import org.drools.rule.Declaration;
import org.drools.rule.Rule;
import org.drools.spi.Consequence;
import org.drools.spi.RuleComponent;

public class RuleCompiler
{

    private static final RuleCompiler INSTANCE = new RuleCompiler();

    public static RuleCompiler getInstance()
    {
        return RuleCompiler.INSTANCE;
    }

    private RuleCompiler()
    {

    }

    public void compile(Rule rule,
                        String packageName,
                        Map parents,
                        Map ruleNameMap,
                        String knowledgeHelper,
                        ResourceReader src,
                        ResourceStore dst,
                        ClassLoader classLoader) throws IOException
    {
        Map map = new HashMap();
        Map files = new HashMap();
        Map invokers = new HashMap();
        files.put( "invokers",
                   invokers );

        // First gather up all the conditions for the different SMF compiler lists
        List conditions = rule.getConditions();
        Iterator it = conditions.iterator();
        SemanticRule condition = null;
        SemanticRuleCompiler compiler = null;
        Object object = null;
        while ( it.hasNext() )
        {
            object = it.next();
            //only process this component if it implements the SemanticRule interface
            if ( ! ( object instanceof SemanticRule ) ) 
            {
                continue;
            }
            condition = (SemanticRule) object;
            compiler = condition.getSemanticRuleCompiler();
            List list = (List) map.get( compiler );
            if ( list == null )
            {
                list = new ArrayList();
            }

            list.add( condition );

            map.put( compiler,
                     list );
        }

        List list = null;
        // Add the consequence for to its smf compiler list
        Consequence consequence = rule.getConsequence();
        //only process this component if it implements the SemanticRule interface
        if ( consequence instanceof SemanticRule ) 
        {        
            compiler = ((SemanticRule) consequence).getSemanticRuleCompiler();
            list = (List) map.get( compiler );
            if ( list == null )
            {
                list = new ArrayList();
            }
    
            list.add( consequence );
            map.put( compiler,
                     list );
        }

        it = map.keySet().iterator();

        RuleComponent[] components = null;
        String semanticPackageName = null;
        String name = null;
        String className = null;
        List declarations = null;
        while ( it.hasNext() )
        {
            // Make a copy of the imports
            // Cannot use the original as it will be updated by the compiler
            Set imports = new HashSet();
            imports.addAll( rule.getImporter().getImports() );

            compiler = (SemanticRuleCompiler) it.next();
            list = (List) map.get( compiler );
            components = (RuleComponent[]) list.toArray( new RuleComponent[list.size()] );

            name = rule.getName();

            className = (String) ruleNameMap.get( rule.getName() );

            if ( className == null )
            {
                semanticPackageName = packageName + "." + compiler.getSemanticType();

                className = generateUniqueLegalName( semanticPackageName,
                                                     src,
                                                     name.toUpperCase().charAt( 0 ) + name.substring( 1 ),
                                                     "." + compiler.getSemanticType() );
                ruleNameMap.put( rule,
                                 className );
            }

            declarations = rule.getParameterDeclarations();
            compiler.generate( components,
                               (Declaration[]) declarations.toArray( new Declaration[declarations.size()] ),
                               imports,
                               rule.getApplicationData(),
                               semanticPackageName,
                               className,
                               (String) parents.get( compiler.getSemanticType() ),
                               knowledgeHelper,
                               src,
                               files );
        }

        compile( files, src, dst, classLoader );
        compile( invokers, src, dst, classLoader );
        
        
//        it = files.keySet().iterator();
//        Object object = null;
//        list = null;
//        while ( it.hasNext() )
//        {
//            object = it.next();
//            if ( object instanceof SemanticRuleCompiler ) {
//                compiler = (SemanticRuleCompiler) object;
//                list = ( List ) files.get( compiler ); 
//                compiler.compile( ( String[] ) list.toArray( new String[ list.size() ] ),
//                                  src,
//                                  dst );
//            }
//        }
//        
//        
//        
//        it = invokers.keySet().iterator();
//        object = null;
//        list = null;
//        while ( it.hasNext() )
//        {
//            object = it.next();
//            if ( object instanceof SemanticRuleCompiler ) {
//                compiler = (SemanticRuleCompiler) object;
//                list = ( List ) files.get( compiler ); 
//                compiler.compile( ( String[] ) list.toArray( new String[ list.size() ] ),
//                                  src,
//                                  dst );
//            }
//        }
        

    }
    
    private void compile( Map files,
                          ResourceReader src,
                          ResourceStore dst,
                          ClassLoader classLoader)
    {
        Iterator it = files.keySet().iterator();
        Object object = null;
        List list = null;
        SemanticRuleCompiler compiler = null;
        while ( it.hasNext() )
        {
            object = it.next();
            if ( object instanceof SemanticRuleCompiler ) {
                compiler = (SemanticRuleCompiler) object;
                list = ( List ) files.get( compiler ); 
                compiler.compile( ( String[] ) list.toArray( new String[ list.size() ] ),
                                  src,
                                  dst,
                                  classLoader);
            }
        }        
    }

    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * 
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    private String generateUniqueLegalName(String packageName,
                                           ResourceReader src,
                                           String name,
                                           String ext)
    {
        // replaces the first char if its a number and after that all non
        // alphanumeric or $ chars with _
        String newName = name.replaceAll( "(^[0-9]|[^\\w$])",
                                          "_" );

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        boolean exists = true;
        while ( exists )
        {
            counter++;
            String fileName = packageName.replaceAll( "\\.",
                                                      "/" ) + newName + "_" + counter + ext;
            
            exists = src.isAvailable(fileName);
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 )
        {
            newName = newName + "_" + counter;
        }

        return newName;
    }

}
