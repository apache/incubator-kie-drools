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

package org.drools.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.drools.RuntimeDroolsException;
import org.drools.util.CompositeClassLoader;

public class ClassTypeResolver
    implements
    TypeResolver {
    private String                           defaultPackagName;
    private Set<String>                      imports          = Collections.emptySet();

    private ClassLoader                      classLoader;

    private Map<String, Class< ? >>          cachedImports    = new HashMap<String, Class< ? >>();

    private static final Map<String, String> internalNamesMap = new HashMap<String, String>();
    static {
        internalNamesMap.put( "int",
                              "I" );
        internalNamesMap.put( "boolean",
                              "Z" );
        internalNamesMap.put( "float",
                              "F" );
        internalNamesMap.put( "long",
                              "J" );
        internalNamesMap.put( "short",
                              "S" );
        internalNamesMap.put( "byte",
                              "B" );
        internalNamesMap.put( "double",
                              "D" );
        internalNamesMap.put( "char",
                              "C" );
    }

    public ClassTypeResolver(final Set<String> imports,
                             final ClassLoader classLoader) {
        this.imports = imports;

        if ( classLoader == null ) {
            throw new RuntimeDroolsException( "ClassTypeResolver cannot have a null parent ClassLoader" );
        }

        this.classLoader = classLoader;
    }

    public ClassTypeResolver(Set<String> imports,
                             CompositeClassLoader rootClassLoader,
                             String name) {
        this( imports,
              rootClassLoader );
        this.defaultPackagName = name;
    }

    public void setClassLoader( ClassLoader classLoader ) {
        this.classLoader = classLoader;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.semantics.base.Importer#getImports( Class clazz )
     */
    /* (non-Javadoc)
     * @see org.drools.semantics.java.TypeResolver#getImports()
     */
    public Set<String> getImports() {
        return this.imports;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.semantics.base.Importer#addImports(org.drools.spi.ImportEntry)
     */
    /* (non-Javadoc)
     * @see org.drools.semantics.java.TypeResolver#addImport(java.lang.String)
     */
    public void addImport( final String importEntry ) {
        if ( this.imports == Collections.EMPTY_SET ) {
            this.imports = new HashSet<String>();
        }
        this.imports.add( importEntry );
    }

    public Class<?> lookupFromCache( final String className ) {
        return this.cachedImports.get( className );
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.semantics.base.Importer#importClass(java.lang.ClassLoader,
     *      java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.drools.semantics.java.TypeResolver#resolveType(java.lang.String)
     */
    public Class< ? > resolveType( String className ) throws ClassNotFoundException {
        Class< ? > clazz = null;
        boolean isArray = false;
        boolean isPrimitive = false;
        final StringBuilder arrayClassName = new StringBuilder();

        clazz = lookupFromCache( className );

        if ( clazz == null && className.indexOf( '[' ) > 0 ) {
            // is an array?
            isArray = true;
            int bracketIndex = className.indexOf( '[' );
            final String componentName = className.substring( 0,
                                                              bracketIndex );
            arrayClassName.append( '[' );
            while ( (bracketIndex = className.indexOf( '[',
                                                       bracketIndex + 1 )) > 0 ) {
                arrayClassName.append( '[' );
            }
            className = componentName;
        }

        //is the class a primitive type ?
        if ( clazz == null && internalNamesMap.containsKey( className ) ) {
            clazz = Class.forName( "[" + internalNamesMap.get( className ),
                                   true,
                                   this.classLoader ).getComponentType();
            isPrimitive = true;
        }

        // try loading className
        if ( clazz == null ) {
            try {
                clazz = this.classLoader.loadClass( className );
            } catch ( final ClassNotFoundException e ) {
                clazz = null;
            }
        }

        // try as a nested class
        if ( clazz == null ) {
            clazz = importClass( className,
                                 className );
        }

        // Now try the className with each of the given imports
        if ( clazz == null ) {
            final Set<Class<?>> validClazzCandidates = new HashSet<Class<?>>();

            final Iterator<String> it = this.imports.iterator();
            while ( it.hasNext() ) {
                clazz = importClass( (String) it.next(),
                                     className );
                if ( clazz != null ) {
                    validClazzCandidates.add( clazz );
                }
            }

            if ( validClazzCandidates.size() > 1 ) {
                for ( Iterator<Class<?>> validIt = validClazzCandidates.iterator(); validIt.hasNext(); ) {
                    Class<?> cls = validIt.next();
                    if ( this.defaultPackagName.equals( cls.getPackage().getName() ) ) {
                        validIt.remove();
                    }
                }
            }

            // If there are more than one possible resolutions, complain about
            // the ambiguity
            if ( validClazzCandidates.size() > 1 ) {
                final StringBuilder sb = new StringBuilder();
                final Iterator<Class<?>> clazzCandIter = validClazzCandidates.iterator();
                while ( clazzCandIter.hasNext() ) {
                    if ( 0 != sb.length() ) {
                        sb.append( ", " );
                    }
                    sb.append( clazzCandIter.next().getName() );
                }
                throw new Error( "Unable to find ambiguously defined class '" + className + "', candidates are: [" + sb.toString() + "]" );
            } else if ( validClazzCandidates.size() == 1 ) {
                clazz = (Class<?>) validClazzCandidates.toArray()[0];
            } else {
                clazz = null;
            }

        }

        // Now try the java.lang package
        if ( clazz == null ) {
            clazz = defaultClass( className );
        }

        // If array component class was found, try to resolve the array class of it
        if ( isArray ) {
            if ( isPrimitive ) {
                arrayClassName.append( internalNamesMap.get( className ) );
            } else {
                if ( clazz != null ) {
                    arrayClassName.append( "L" ).append( clazz.getName() ).append( ";" );
                } else {
                    // we know we will probably not be able to resolve this name, but nothing else we can do.
                    arrayClassName.append( "L" ).append( className ).append( ";" );
                }
            }
            try {
                clazz = Class.forName( arrayClassName.toString(),
                                       true,
                                       this.classLoader );
            } catch ( final ClassNotFoundException e ) {
                clazz = null;
            }
        }

        // We still can't find the class so throw an exception
        if ( clazz == null ) {
            throw new ClassNotFoundException( "Unable to find class '" + className + "'" );
        }

        this.cachedImports.put( clazz.getSimpleName(),
                                clazz );

        return clazz;
    }

    private Class<?> importClass( final String importText,
                               final String className ) {
        String qualifiedClass = null;
        Class<?> clazz = null;

        if ( importText.endsWith( "*" ) ) {
            qualifiedClass = importText.substring( 0,
                                                   importText.indexOf( '*' ) ) + className;
        } else if ( importText.endsWith( "." + className ) ) {
            qualifiedClass = importText;
        } else if ( (className.indexOf( '.' ) > 0) && (importText.endsWith( className.split( "\\." )[0] )) ) {
            qualifiedClass = importText + className.substring( className.indexOf( '.' ) );
        } else if ( importText.equals( className ) ) {
            qualifiedClass = importText;
        }

        if ( qualifiedClass != null ) {
            try {
                clazz = this.classLoader.loadClass( qualifiedClass );
            } catch ( final ClassNotFoundException e ) {
                clazz = null;
            }

            // maybe its a nested class?
            int lastIndex;
            while ( clazz == null && (lastIndex = qualifiedClass.lastIndexOf( '.' )) != -1 ) {
                try {

                    qualifiedClass = qualifiedClass.substring( 0,
                                                               lastIndex ) + "$" + qualifiedClass.substring( lastIndex + 1 );
                    clazz = this.classLoader.loadClass( qualifiedClass );
                } catch ( final ClassNotFoundException e ) {
                    clazz = null;
                }
            }

        }

        if ( clazz != null ) {
            if ( this.cachedImports == Collections.EMPTY_MAP ) {
                this.cachedImports = new HashMap<String, Class<?>>();
            }

            this.cachedImports.put( clazz.getSimpleName(),
                                    clazz );
        }

        return clazz;
    }

    private Class<?> defaultClass( final String className ) {
        final String qualifiedClass = "java.lang." + className;
        Class<?> clazz = null;
        try {
            clazz = this.classLoader.loadClass( qualifiedClass );
        } catch ( final ClassNotFoundException e ) {
            // do nothing
        }
        if ( clazz != null ) {
            if ( this.cachedImports == Collections.EMPTY_MAP ) {
                this.cachedImports = new HashMap<String, Class<?>>();
            }
            this.cachedImports.put( className,
                                    clazz );
        }
        return clazz;
    }

    public boolean isEmpty() {
        return this.imports.isEmpty();
    }

    /*
     * (non-Javadoc)
     * @see org.drools.base.TypeResolver#getFullTypeName(java.lang.String)
     */
    public String getFullTypeName( String shortName ) throws ClassNotFoundException {

        Class<?> clz = resolveType( shortName );
        if ( clz == null ) throw new IllegalArgumentException( "Unable to resolve the full type name for " + shortName );
        return clz.getName();

    }

    public void clearImports() {
        if ( this.imports != Collections.EMPTY_SET ) {
            this.imports.clear();
            this.cachedImports.clear();
        }
    }
}
