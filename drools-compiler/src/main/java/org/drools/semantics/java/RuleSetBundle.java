package org.drools.semantics.java;

import java.util.Collections;
import java.util.List;

import org.apache.commons.jci.readers.MemoryResourceReader;
import org.apache.commons.jci.stores.MemoryResourceStore;
import org.apache.commons.jci.stores.ResourceStore;
import org.apache.commons.jci.stores.ResourceStoreClassLoader;
import org.drools.rule.RuleSet;

public class RuleSetBundle {
    private final MemoryResourceReader     src;
    private final MemoryResourceStore      dst;
    private int                            counter;
    
    private final RuleSet                  ruleSet;
    
    private final String                   packageName; 
    
    private final ResourceStoreClassLoader classLoader;
    
    public RuleSetBundle(RuleSet ruleSet) {
        this( ruleSet, "org.drools.generated", null );
    }
    
    public RuleSetBundle(RuleSet ruleSet, String packageName, ClassLoader parentClassLoader) {
        this.src = new MemoryResourceReader();
        this.dst = new MemoryResourceStore();       
                 
        this.ruleSet = ruleSet;
        
        this.packageName = packageName + "." + ruleSet.getName().replaceAll( "(^[0-9]|[^\\w$])", "_" ) + "_" + System.currentTimeMillis();
        
        if ( parentClassLoader == null ) {
            parentClassLoader = Thread.currentThread().getContextClassLoader();
            if ( parentClassLoader == null )
            {
                parentClassLoader = this.getClass().getClassLoader();
            }            
        }
        
        classLoader = new ResourceStoreClassLoader( parentClassLoader,
                                                    new ResourceStore[]{dst} );
    }
    
    public RuleSet getRuleSet() {
        return this.ruleSet;
    }
    
    public String getPackageName() {
        return this.packageName;
    }
    
    public MemoryResourceReader getMemoryResourceReader() {
        return this.src;
    }

    public MemoryResourceStore getMemoryResourceStore() {
        return this.dst;
    }
    
    public ResourceStoreClassLoader getResourcStoreClassLoader() {
        return this.classLoader;
    }
    
    public byte[] getSrcJar() {
        return null;
    }
    
    public byte[] getBinJar() {
        return getBinJar(Collections.EMPTY_LIST);
    }
    
    public byte[] getBinJar(List objects) {
        return null;
    }    
    
    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * 
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    public String generateUniqueLegalName(String packageName,
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

            exists = this.src.isAvailable( fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 )
        {
            newName = newName + "_" + counter;
        }

        return newName;
    }
    
    
}
