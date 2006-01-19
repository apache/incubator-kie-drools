package org.drools.smf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.readers.ResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;
import org.drools.IntegrationException;
import org.drools.rule.Rule;
import org.drools.rule.RuleSet;
import org.drools.spi.Condition;
import org.drools.spi.Functions;

public class RuleSetCompiler
{
    private final RuleSet        ruleSet;

    private final String         ruleSetValidFileName;

    private final String         packageName;

    private final String         knowledgeHelper;

    private final MemoryResourceReader src;
    private final MemoryResourceStore  dst;

    //    private File          srcJar;
    //    private File          binJar;

    public RuleSetCompiler(RuleSet ruleSet,
                           String packageName,
                           String knowledgeHelper) throws IntegrationException,
                                                  IOException
    {

        //        srcJar = null;
        //        binJar = null;                       

        this.ruleSet = ruleSet;
        this.ruleSetValidFileName = this.ruleSet.getName().replaceAll( "(^[0-9]|[^\\w$])",
                                                                       "_" ).toLowerCase();

        this.packageName = packageName;
        this.knowledgeHelper = knowledgeHelper;

        this.src = new MemoryResourceReader();
        this.dst = new MemoryResourceStore();

        compile();

        //        //srcJar may be null so check before we do toURL
        //        URL srcJarUrl = null;
        //        if ( ( this.srcJar != null ) )
        //        {
        //            srcJarUrl = this.srcJar.toURL();
        //        }
        //        
        //       
        //        return new RuleSetPackage( ruleSet, this.binJar.toURL(), srcJarUrl  );
    }

    public RuleSet getRuleSet()
    {
        return ruleSet;
    }

    public byte[] getSourceDeploymentJar() throws IOException
    {
        String[] files = this.src.list();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Jarer jarer = new Jarer( output );
        for ( int i = 0; i < files.length; i++ )
        {
            jarer.addCharArray( this.src.getContent( files[i] ),
                                files[i] );
        }
        jarer.close();
        return output.toByteArray();
    }

    public byte[] getBinaryDeploymentJar() throws IOException
    {
        String[] files = this.dst.list();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Jarer jarer = new Jarer( output );
        for ( int i = 0; i < files.length; i++ )
        {
            jarer.addByteArray( this.dst.read( files[i] ),
                                files[i].replace('.', '/') + ".class" );
        }

        jarer.addObject( this.ruleSetValidFileName,
                         this.ruleSet );
        
        Properties prop = new Properties();
        prop.setProperty( "name",
                          this.ruleSetValidFileName );        
        ByteArrayOutputStream propStream = new ByteArrayOutputStream();
        prop.store(propStream, null);                          
        jarer.addByteArray(propStream.toByteArray(), "rule-set.conf");

        jarer.close();
        return output.toByteArray();
    }

    private void compile() throws IOException,
                          IntegrationException
    {

        Map functionMap = this.ruleSet.getFunctions();
        Iterator it = functionMap.values().iterator();
        SemanticFunctions functions = null;
        SemanticFunctionsCompiler compiler = null;
        String functionClassName = null;
        String name = null;
        String semanticPackageName = null;

        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
        if ( parentClassLoader == null )
        {
            parentClassLoader = this.getClass().getClassLoader();
        }

        ResourceStoreClassLoader classLoader = new ResourceStoreClassLoader( parentClassLoader,
                                                                             new ResourceStore[]{dst} );

        Map parents = new HashMap();
        Map files = new HashMap();
        Object object = null;
        while ( it.hasNext() )
        {
            object = it.next();
            if ( !(object instanceof SemanticFunctions) )
            {
                continue;
            }

            functions = (SemanticFunctions) object;
            name = functions.getName();

            // Make a copy of the imports
            // Cannot use the original as it will be updated by the compiler
            Set imports = new HashSet();
            imports.addAll( this.ruleSet.getImporter().getImports() );

            compiler = functions.getSemanticFunctionsCompiler();

            //Use the regex ruleset name + timestamp to hopefully create a unique namespace 
            //This is further backed by the derived class name from the rulename            
            semanticPackageName = this.packageName + "." + this.ruleSetValidFileName + "_" + System.currentTimeMillis() + "." + compiler.getSemanticType();
            functionClassName = generateUniqueLegalName( semanticPackageName,
                                                         src,
                                                         name.toUpperCase().charAt( 0 ) + name.substring( 1 ),
                                                         "." + compiler.getSemanticType() );

            compiler.generate( (Functions) functions,
                               imports,
                               semanticPackageName,
                               functionClassName,
                               null,
                               src,
                               files );

            parents.put( compiler.getSemanticType(),
                         semanticPackageName + "." + functionClassName );

        }

        it = files.keySet().iterator();
        object = null;
        List list = null;
        while ( it.hasNext() )
        {
            object = it.next();
            if ( object instanceof SemanticFunctionsCompiler )
            {
                compiler = (SemanticFunctionsCompiler) object;
                list = (List) files.get( compiler );
                compiler.compile( (String[]) list.toArray( new String[list.size()] ),
                                  src,
                                  dst,
                                  classLoader );
            }
        }

        Rule[] rules = this.ruleSet.getRules();

        //Use the regex ruleset name + timestamp to hopefully create a unique namespace 
        //This is further backed by the derived class name from the rulename            
        String rulePackageName = this.packageName + "." + this.ruleSetValidFileName + "_" + System.currentTimeMillis();
        // use a HashMap to map the rules to their new class names, used for wiring        
        Map ruleNameMap = new HashMap();
        for ( int i = 0; i < rules.length; i++ )
        {
            compileRule( rules[i],
                         rulePackageName,
                         parents,
                         ruleNameMap,
                         this.knowledgeHelper,
                         src,
                         dst,
                         classLoader );

        }

        //        File conf = new File( this.temp,
        //                              "rule-set.conf" );
        //
        //        Properties prop = new Properties();
        //        prop.setProperty( "name",
        //                          this.ruleSetName );
        //        FileOutputStream fos = new FileOutputStream( conf );
        //        prop.store( fos ,
        //                    null );
        //        fos.close();

        setInvokers( this.ruleSet,
                     rulePackageName,
                     ruleNameMap,
                     classLoader );

        //        createBinJar();
        //        createSrcJar();
        //        conf.delete();
    }

    private static void compileRule(Rule rule,
                                    String packageName,
                                    Map parents,
                                    Map ruleMap,
                                    String knowledgeHelper,
                                    ResourceReader src,
                                    ResourceStore dst,
                                    ClassLoader classLoader) throws IOException
    {
        RuleCompiler compiler = RuleCompiler.getInstance();
        compiler.compile( rule,
                          packageName,
                          parents,
                          ruleMap,
                          knowledgeHelper,
                          src,
                          dst,
                          classLoader );
    }

    private void setInvokers(RuleSet ruleSet,
                             String packageName,
                             Map ruleMap,
                             ClassLoader classLoader) throws IntegrationException
    {
        Rule[] rules = ruleSet.getRules();
        Rule rule = null;
        SemanticInvokeable component = null;
        String name = null;
        String semanticPackageName = null;
        try
        {
            for ( int i = 0; i < rules.length; i++ )
            {
                rule = rules[i];
                Condition[] conditions = (Condition[]) rule.getConditions().toArray( new Condition[rule.getConditions().size()] );
                for ( int j = 0; j < conditions.length; j++ )
                {
                    //only wire up this condition if it implements SemanticInvokeble
                    if ( !(conditions[j] instanceof SemanticInvokeable) )
                    {
                        continue;
                    }
                    component = (SemanticInvokeable) conditions[j];
                    name = component.getName();
                    semanticPackageName = packageName + "." + component.getSemanticType();
                    component.setInvoker( (Invoker) classLoader.loadClass( semanticPackageName + "." + ruleMap.get( rule ) + "Invoker$" + name.toUpperCase().charAt( 0 ) + name.substring( 1 ) + "Invoker" ).newInstance() );

                }

                //only wire up this consequenceif it implements SemanticInvokeble
                if ( rule.getConsequence() instanceof SemanticInvokeable )
                {
                    component = (SemanticInvokeable) rule.getConsequence();
                    name = component.getName();
                    semanticPackageName = packageName + "." + component.getSemanticType();
                    component.setInvoker( (Invoker) classLoader.loadClass( semanticPackageName + "." + ruleMap.get( rule ) + "Invoker$" + name.toUpperCase().charAt( 0 ) + name.substring( 1 ) + "Invoker" ).newInstance() );
                }
            }
        }
        catch ( InstantiationException e )
        {
            throw new IntegrationException( "Unable to bind RuleSet '" + ruleSet.getName() + "' component to Class Method: " + e.getMessage(),
                                            e );
        }
        catch ( IllegalAccessException e )
        {
            throw new IntegrationException( "Unable to bind RuleSet '" + ruleSet.getName() + "'  component to Class Method: " + e.getMessage(),
                                            e );
        }
        catch ( ClassNotFoundException e )
        {
            throw new IntegrationException( "Unable to bind RuleSet '" + ruleSet.getName() + "'  component to Class Method: " + e.getMessage(),
                                            e );
        }
    }

    //    private void createBinJar() throws FileNotFoundException,
    //                               IOException
    //    {
    //        String jarName = this.ruleSetName + ".jar";
    //        this.binJar = new File( this.temp,
    //                                jarName );
    //        Jarer jarer = new Jarer( this.binJar );
    //
    //        jarer.addDirectory( this.dst );
    //        jarer.addObject( this.ruleSetName,
    //                         this.ruleSet );
    //
    //        File conf = new File( this.temp,
    //                              "rule-set.conf" );        
    //
    //        jarer.addFile( conf,
    //                       "rule-set.conf" );
    //       
    //        jarer.close();
    //        conf.delete();
    //        this.binJar.deleteOnExit();
    //
    //    }
    //
    //    /**
    //     * Only create a src jar is the src directory has entries
    //     * 
    //     * @throws FileNotFoundException
    //     * @throws IOException
    //     */
    //    private void createSrcJar() throws FileNotFoundException,
    //                               IOException
    //    {
    //        // Only create a src jar if the src directory has entries 
    //        if ( this.src.list().length != 0 )
    //        {
    //            String jarName = this.ruleSet.getName().replaceAll( "(^[0-9]|[^\\w$])",
    //                                                                "_" ) + "-src.jar";
    //            this.srcJar = new File( this.temp,
    //                                    jarName );
    //            Jarer jarer = new Jarer( this.srcJar );
    //            jarer.addDirectory( this.src );
    //            jarer.close();
    //            this.srcJar.deleteOnExit();
    //        }
    //    }

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

            exists = src.isAvailable( fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 )
        {
            newName = newName + "_" + counter;
        }

        return newName;
    }

}
