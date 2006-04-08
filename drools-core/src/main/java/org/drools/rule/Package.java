package org.drools.rule;

/*
 * $Id: Package.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.spi.Functions;
import org.drools.spi.TypeResolver;

/**
 * Collection of related <code>Rule</code>s.
 * 
 * @see Rule
 * 
 * @author <a href="mail:bob@werken.com">bob mcwhirter </a>
 * 
 * @version $Id: Package.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 */
public class Package
    implements
    Externalizable {
    // ------------------------------------------------------------
    // Constants`
    // ------------------------------------------------------------

    /** Empty <code>Package</code> array. */
    public static final Package[]  EMPTY_ARRAY = new Package[0];

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Name of the pkg. */
    private String                 name;

    /** Set of all rule-names in this <code>Package</code>. */
    private Map                    rules;

    private List                   imports;

    private Map                    globals;

    //private Map                    functions;

    // @todo: add attributes to Package
    //private Map                   attributes;

    private TypeResolver           typeResolver;

    private PackageCompilationData packageCompilationData;

    /** This is to indicate the the package has no errors during the compilation/building phase */
    private boolean                valid = true;
    
    /** This will keep a summary error message as to why this package is not valid */
    private String                 errorSummary;
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * This is a default constructor purely for externalization
     */
    public Package() {
        
    }
    
    /**
     * Construct.
     * 
     * @param name
     *            The name of this <code>Package</code>.
     */
    public Package(String name) {
        this( name, 
              null );
    }    
    
    /**
     * Construct.
     * 
     * @param name
     *            The name of this <code>Package</code>.
     */
    public Package(String name,
                   ClassLoader parentClassLoader) {
        this.name = name;
        this.imports = new ArrayList( 1 );
        this.rules = new LinkedHashMap();
        this.globals = new HashMap();
        //this.functions = new HashMap();
        this.packageCompilationData = new PackageCompilationData( parentClassLoader );
    }    
    

    /**
     * Handles the write serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode. The generated bytecode must be restored before any Rules.
     * 
     */
    public void writeExternal(ObjectOutput stream) throws IOException {
        stream.writeObject( this.packageCompilationData );
        stream.writeObject( name );
        stream.writeObject( imports );
        stream.writeObject( globals );
        //stream.writeObject( functions );
        
        // Rules must be restored by an ObjectInputStream that can resolve using a given ClassLoader to handle seaprately by storing as
        // a byte[]
        ByteArrayOutputStream bos = new ByteArrayOutputStream( );
        ObjectOutput out = new ObjectOutputStream( bos );
        out.writeObject( this.rules );     
        stream.writeObject( bos.toByteArray() );
    }

    /**
     * Handles the read serialization of the Package. Patterns in Rules may reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the generated bytecode; which must be restored before any Rules.
     * A custom ObjectInputStream, able to resolve classes against the bytecode in the PackageCompilationData, is used to restore the Rules.
     * 
     */    
    public void readExternal(ObjectInput stream) throws IOException, ClassNotFoundException {
        // PackageCompilationData must be restored before Rules as it has the ClassLoader needed to resolve the generated code references in Rules
        this.packageCompilationData = (PackageCompilationData) stream.readObject( );
        this.name = (String) stream.readObject(  );
        this.imports = (List) stream.readObject( );
        this.globals = (Map) stream.readObject( );
        //this.functions = (Map) stream.readObject( );
        
                
        // Return the rules stored as a byte[]
        byte[] bytes = (byte[]) stream.readObject();        

        //  Use a custom ObjectInputStream that can resolve against a given classLoader
        ObjectInputStreamWithLoader streamWithLoader = new ObjectInputStreamWithLoader( new ByteArrayInputStream( bytes ),
                                                                                        this.packageCompilationData.getClassLoader() );

        this.rules = (Map) streamWithLoader.readObject();     
        
        System.out.println( "done package" );
    }
    
    private static class ObjectInputStreamWithLoader extends ObjectInputStream {
        private final ClassLoader classLoader;

        public ObjectInputStreamWithLoader(InputStream in,
                                           ClassLoader classLoader) throws IOException {
            super( in );
            this.classLoader = classLoader;
            enableResolveObject( true );
        }

        protected Class resolveClass(ObjectStreamClass desc) throws IOException,
                                                            ClassNotFoundException {
            if ( this.classLoader == null ) {
                return super.resolveClass( desc );
            } else {
                String name = desc.getName();
                return this.classLoader.loadClass( name );
            }
        }
    }    
    
    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the name of this <code>Package</code>.
     * 
     * @return The name of this <code>Package</code>.
     */
    public String getName() {
        return this.name;
    }

    public void addImport(String importEntry) {
        this.imports.add( importEntry );
    }
    
    public void  removeImport(String importEntry) {
        this.imports.remove( importEntry );
    }

    public List getImports() {
        return this.imports;
    }
    

    public void addGlobal(String identifier,
                          Class clazz) {
        this.globals.put( identifier,
                          clazz );
    }
    
    public void removeGlobal( String identifier ) {
        this.globals.remove( identifier );
    }

    public Map getGlobals() {
        return this.globals;
    }    
    
    public void removeFunction(String functionName) {        
        this.packageCompilationData.remove( this.name + "." +  ucFirst ( functionName ) );        
    }

    /**
     * Add a <code>Rule</code> to this <code>Package</code>.
     * 
     * @param rule
     *            The rule to add.
     * 
     * @throws DuplicateRuleNameException
     *             If the <code>Rule</code> attempting to be added has the
     *             same name as another previously added <code>Rule</code>.
     * @throws InvalidRuleException
     *             If the <code>Rule</code> is not valid.
     */
    public void addRule(Rule rule)  {
        String name = rule.getName();

        this.rules.put( name,
                        rule );
        rule.setLoadOrder( this.rules.size() );
    }    
    
    public void removeRule(Rule rule) {
        this.rules.remove( rule.getName() );
        String consequenceName = rule.getConsequence().getClass().getName();
        this.packageCompilationData.remove( consequenceName );
        
        removeClasses( rule.getLhs() );
        
        // Now remove the rule class - the name is a subset of the consequence name
        this.packageCompilationData.remove( consequenceName.substring( 0, consequenceName.indexOf( "ConsequenceInvoker" ) ) ); 
    }
    
    private void removeClasses(ConditionalElement ce) {
        if ( ce instanceof GroupElement ) {
            GroupElement group = ( GroupElement ) ce;
            for ( Iterator it = group.getChildren().iterator(); it.hasNext(); ) {
                Object object = it.next();
                if ( object instanceof ConditionalElement ) {
                    removeClasses( ( ConditionalElement ) object );
                } else if ( object instanceof Column ) {
                    removeClasses( ( Column ) object );
                }
            }
        } else if ( ce instanceof EvalCondition ) { 
            this.packageCompilationData.remove( ( ( EvalCondition ) ce ).getEvalExpression().getClass().getName() );
        } 
    }
    
    private void removeClasses(Column column) {
        for ( Iterator it = column.getConstraints().iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof PredicateConstraint ) {
                this.packageCompilationData.remove( ( ( PredicateConstraint ) object ).getPredicateExpression().getClass().getName() );
            } else if ( object instanceof ReturnValueConstraint ) {
                this.packageCompilationData.remove( ( ( ReturnValueConstraint ) object ).getReturnValueExpression().getClass().getName() );
            }
        }
    }

    /**
     * Retrieve a <code>Rule</code> by name.
     * 
     * @param name
     *            The name of the <code>Rule</code> to retrieve.
     * 
     * @return The named <code>Rule</code>, or <code>null</code> if not
     *         such <code>Rule</code> has been added to this
     *         <code>Package</code>.
     */
    public Rule getRule(String name) {
        return (Rule) this.rules.get( name );
    }

    /**
     * Retrieve all <code>Rules</code> in this <code>Package</code>.
     * 
     * @return An array of all <code>Rules</code> in this <code>Package</code>.
     */
    public Rule[] getRules() {
        return (Rule[]) this.rules.values().toArray( new Rule[this.rules.size()] );
    }

    public void setTypeSolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
    }

    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    public PackageCompilationData getPackageCompilationData() {
        return this.packageCompilationData;
    }   

    public String toString() {
        return "[Package name=" + this.name + "]";
    }
    
    /** Once this is called, the package will be marked as invalid */
    public void setError(String summary) {
        this.errorSummary = summary;
        this.valid = false;
    }
    
    /**
     * @return true (default) if there are no build/structural problems.
     */
    public boolean isValid() {
        return this.valid;
    }
    
    /** This will throw an exception if the package is not valid */
    public void checkValidity() {
        if (!isValid()) 
            throw new InvalidRulePackage(this.getErrorSummary());
    }
    
    /**
     * This will return the error summary (if any) if the package is invalid.
     */
    public String getErrorSummary() {
        return this.errorSummary;
    }

    public boolean equals(Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof Package) ) {
            return false;
        }

        Package other = (Package) object;

        return (this.name.equals( other.name ));
    }

    public int hashCode() {
        return this.name.hashCode();
    }
    
    private String ucFirst(String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }    
    
    public void clear() {
        this.rules.clear();
        this.packageCompilationData.clear();
    }
}
