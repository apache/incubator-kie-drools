package org.drools.semantics.java;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.spi.TypeResolver;

public class ClassTypeResolver
    implements
    TypeResolver {
    private final List        imports;

    private final ClassLoader classLoader;

    private Map               cachedImports = new HashMap();

    public ClassTypeResolver(final List imports) {
        this( imports,
              null );
    }

    public ClassTypeResolver(final List imports,
                             ClassLoader classLoader) {
        this.imports = imports;

        if ( classLoader == null ) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        if ( classLoader == null ) {
            classLoader = getClass().getClassLoader();
        }

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
    public List getImports() {
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
    public void addImport(final String importEntry) {
        if ( !this.imports.contains( importEntry ) ) {
            this.imports.add( importEntry );
        }
    }

    public Class lookupFromCache(final String className) {
        return (Class) this.cachedImports.get( className );
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
    public Class resolveType(final String className) throws ClassNotFoundException {
        Class clazz = null;

        // Now try the package object type cache         
        clazz = lookupFromCache( className );

        // try loading className
        if ( clazz == null ) {
            try {
                clazz = this.classLoader.loadClass( className );
            } catch ( final ClassNotFoundException e ) {
                clazz = null;
            }
        }

        //is the class a primitive?
        if ( "boolean".equals( className )) {
            return boolean.class;
        } else if ( "byte".equals( className ) ) {
            return byte.class;
        } else if ( "short".equals( className ) ) {
            return short.class;
        } else if ( "char".equals( className ) ) {
            return char.class;
        } else if ( "int".equals( className ) ) {
            return int.class;
        } else if ( "long".equals( className ) ) {
            return long.class;
        } else if ( "float".equals( className ) ) {
            return float.class;
        } else if ( "double".equals( className ) ) {
            return double.class;
        }
        
        // Now try the className with each of the given imports 
        if ( clazz == null ) {
            final Set validClazzCandidates = new HashSet();

            final Iterator it = this.imports.iterator();
            while ( it.hasNext() ) {
                clazz = importClass( (String) it.next(),
                                     className );
                if ( clazz != null ) {
                    validClazzCandidates.add( clazz );
                }
            }

            // If there are more than one possible resolutions, complain about
            // the ambiguity
            if ( validClazzCandidates.size() > 1 ) {
                final StringBuffer sb = new StringBuffer();
                final Iterator clazzCandIter = validClazzCandidates.iterator();
                while ( clazzCandIter.hasNext() ) {
                    if ( 0 != sb.length() ) {
                        sb.append( ", " );
                    }
                    sb.append( ((Class) clazzCandIter.next()).getName() );
                }
                throw new Error( "Unable to find unambiguously defined class '" + className + "', candidates are: [" + sb.toString() + "]" );
            } else if ( validClazzCandidates.size() == 1 ) {
                clazz = (Class) validClazzCandidates.toArray()[0];
            } else {
                clazz = null;
            }

        }

        // Now try the java.lang package
        if (clazz == null) {
        	clazz = defaultClass( className );
        }

        // We still can't find the class so throw an exception 
        if ( clazz == null ) {
            throw new ClassNotFoundException( "Unable to find class '" + className + "'" );
        }

        return clazz;
    }

    private Class importClass(final String importText,
                              final String className) {
        String qualifiedClass = null;
        Class clazz = null;

        // not python
        if ( importText.endsWith( "*" ) ) {
            qualifiedClass = importText.substring( 0,
                                                   importText.indexOf( '*' ) ) + className;
        } else if ( importText.endsWith( "." + className ) ) {
            qualifiedClass = importText;
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
            if ( clazz == null ) {
                try {
                    final int lastIndex = qualifiedClass.lastIndexOf( '.' );
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
                this.cachedImports = new HashMap();
            }

            this.cachedImports.put( className,
                                    clazz );
        }

        return clazz;
    }

    private Class defaultClass(String className) {
		String qualifiedClass = "java.lang." + className;
		Class clazz = null;
		try {
			clazz = this.classLoader.loadClass(qualifiedClass);
		} catch (final ClassNotFoundException e) {
			// do nothing
		}
		if (clazz != null) {
			if (this.cachedImports == Collections.EMPTY_MAP) {
				this.cachedImports = new HashMap();
			}
			this.cachedImports.put(className, clazz);
		}
		return clazz;
	}
    
    public boolean isEmpty() {
        return this.imports.isEmpty();
    }
}