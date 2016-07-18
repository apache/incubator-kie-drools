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

package org.drools.core.base;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static org.drools.core.util.ClassUtils.safeLoadClass;

public class ClassTypeResolver
    implements
    TypeResolver {
    private String                           defaultPackagName;

    private Set<String>                      imports          = Collections.emptySet();

    private Set<String>                      implicitImports  = Collections.emptySet();

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
            throw new RuntimeException( "ClassTypeResolver cannot have a null parent ClassLoader" );
        }

        this.classLoader = classLoader;
    }

    public ClassTypeResolver(Set<String> imports,
                             ClassLoader rootClassLoader,
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
     * @see org.kie.semantics.base.Importer#getImports( Class clazz )
     */
    /* (non-Javadoc)
     * @see org.kie.semantics.java.TypeResolver#getImports()
     */
    public Set<String> getImports() {
        return this.imports;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.semantics.base.Importer#addImports(org.kie.spi.ImportEntry)
     */
    /* (non-Javadoc)
     * @see org.kie.semantics.java.TypeResolver#addImport(java.lang.String)
     */
    public void addImport( final String importEntry ) {
        if ( this.imports == Collections.EMPTY_SET ) {
            this.imports = new HashSet<String>();
        }
        this.imports.add( importEntry );
    }

    public void addImplicitImport( final String importEntry ) {
        if ( this.implicitImports == Collections.EMPTY_SET ) {
            this.implicitImports = new HashSet<String>();
        }
        this.implicitImports.add(importEntry);
    }

    private Class<?> lookupFromCache( final String className ) throws ClassNotFoundException {
        Class<?> clazz = this.cachedImports.get( className );
        if (clazz == Void.class) {
            throw new ClassNotFoundException( "Unable to find class '" + className + "'" );
        }
        return clazz;
    }

    public void registerClass( String className, Class<?> clazz ) {
        this.cachedImports.put( className, clazz );
    }

    public Class<?> resolveType(String className) throws ClassNotFoundException {
        return resolveType(className, ACCEPT_ALL_CLASS_FILTER);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.kie.semantics.base.Importer#importClass(java.lang.ClassLoader,
     *      java.lang.String)
     */
    /* (non-Javadoc)
     * @see org.kie.semantics.java.TypeResolver#resolveType(java.lang.String)
     */
    public Class< ? > resolveType( String className, ClassFilter classFilter ) throws ClassNotFoundException {
        Class< ? > clazz = lookupFromCache( className );

        if (clazz != null && !classFilter.accept(clazz)) {
            clazz = null;
        }

        boolean isArray = false;
        StringBuilder arrayClassName = null;

        if ( clazz == null && className.indexOf( '[' ) > 0 ) {
            arrayClassName = new StringBuilder();
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

        boolean isPrimitive = false;

        //is the class a primitive type ?
        if ( clazz == null && internalNamesMap.containsKey( className ) ) {
            clazz = Class.forName( "[" + internalNamesMap.get( className ),
                                   true,
                                   this.classLoader ).getComponentType();
            isPrimitive = true;
        }

        // try loading className
        if ( clazz == null ) {
            clazz = safeLoadClass(this.classLoader, className);
            if (clazz != null && !classFilter.accept(clazz)) {
                clazz = null;
            }
        }

        // try as a nested class
        if ( clazz == null ) {
            clazz = importClass( className,
                                 className );
            if (clazz != null && !classFilter.accept(clazz)) {
                clazz = null;
            }
        }

        // Now try the className with each of the given explicit imports
        if ( clazz == null ) {
            clazz = getClassFromImports(className, classFilter, imports);
        }

        // Now try the className with each of the given implicit imports
        if ( clazz == null ) {
            clazz = getClassFromImports(className, classFilter, implicitImports);
        }

        // Now try the java.lang package
        if ( clazz == null ) {
            clazz = defaultClass( className );
            if (clazz != null && !classFilter.accept(clazz)) {
                clazz = null;
            }
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
            this.cachedImports.put( className, Void.class );
            throw new ClassNotFoundException( "Unable to find class '" + className + "'" );
        }

        this.cachedImports.put( clazz.getSimpleName(),
                                clazz );

        return clazz;
    }

    private Class<?> getClassFromImports(String className, ClassFilter classFilter, Collection<String> usedImports) {
        final Set<Class<?>> validClazzCandidates = new HashSet<Class<?>>();

        for (String i : usedImports) {
            Class<?> clazz = importClass( i, className );
            if ( clazz != null && classFilter.accept(clazz) ) {
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
            for ( Class<?> validClazzCandidate : validClazzCandidates ) {
                if ( 0 != sb.length() ) {
                    sb.append( ", " );
                }
                sb.append( validClazzCandidate.getName() );
            }
            throw new Error( "Unable to find ambiguously defined class '" + className + "', candidates are: [" + sb.toString() + "]" );
        }

        return validClazzCandidates.size() == 1 ? validClazzCandidates.iterator().next() : null;
    }

    private Class<?> importClass( String importText, String className ) {
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
            clazz = safeLoadClass(this.classLoader, qualifiedClass);

            // maybe its a nested class?
            int lastIndex;
            while ( clazz == null && (lastIndex = qualifiedClass.lastIndexOf( '.' )) != -1 ) {
                qualifiedClass = qualifiedClass.substring( 0, lastIndex ) + "$" + qualifiedClass.substring( lastIndex + 1 );
                clazz = safeLoadClass(this.classLoader, qualifiedClass);
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
     * @see org.drools.core.base.TypeResolver#getFullTypeName(java.lang.String)
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

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }
}
