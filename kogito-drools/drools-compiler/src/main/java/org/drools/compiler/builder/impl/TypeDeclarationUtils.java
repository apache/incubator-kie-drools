package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.base.TypeResolver;
import org.drools.core.factmodel.BuildUtils;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.util.StringUtils;
import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.Role;

import java.io.IOException;

import static org.drools.core.util.StringUtils.isEmpty;

public class TypeDeclarationUtils {

    public static String lookupSimpleNameByImports( String name, AbstractClassTypeDeclarationDescr typeDescr, PackageDescr packageDescr, ClassLoader loader ) {
        Class<?> typeClass = null;
        if ( isQualified( name ) ) {
            typeClass = getClassForType( name, loader );
        }
        if ( typeClass == null ) {
            for ( ImportDescr id : packageDescr.getImports() ) {
                String imp = id.getTarget();
                int separator = imp.lastIndexOf( '.' );
                String tail = imp.substring( separator + 1 );
                if ( tail.equals( name ) ) {
                    typeClass = getClassForType( imp, loader );
                    if ( typeClass != null ) {
                        return typeClass.getCanonicalName();
                    } else {
                        return imp;
                    }
                } else if ( tail.equals( "*" ) ) {
                    typeClass = getClassForType( imp.substring( 0, imp.length() - 1 ) + name, loader );
                    if ( typeClass != null ) {
                        String resolvedNamespace = imp.substring( 0, separator );
                        if ( resolvedNamespace.equals( typeDescr.getNamespace() ) ) {
                            // the class was found in the declared namespace, so stop here
                            break;
                            // here, the class was found in a different namespace. It means that the class was declared
                            // with no namespace and the initial guess was wrong, or that there is an ambiguity.
                            // So, we need to check that the resolved class is compatible with the declaration.
                        } else if ( name.equals( typeDescr.getType().getName() )
                                    && ! isCompatible( typeClass, typeDescr ) ) {
                            typeClass = null;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        String className = typeClass != null ? typeClass.getName() : name;
        return className;
    }

    public static Class<?> getExistingDeclarationClass( AbstractClassTypeDeclarationDescr typeDescr, PackageRegistry reg ) {
        if (reg == null) {
            return null;
        }
        String availableName = typeDescr.getType().getFullName();
        try {
            return reg.getTypeResolver().resolveType(availableName);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * Tries to determine the namespace (package) of a simple type chosen to be
     * the superclass of a declared bean. Looks among imports, local
     * declarations and previous declarations. Means that a class can't extend
     * another class declared in package that has not been loaded yet.
     *
     * @param klass
     *            the simple name of the class
     * @param packageDescr
     *            the descriptor of the package the base class is declared in
     * @param pkgRegistry
     *            the current package registry
     * @return the fully qualified name of the superclass
     */
    public static String resolveType( String klass,
                                      PackageDescr packageDescr,
                                      PackageRegistry pkgRegistry ) {

        String arraySuffix = "";
        int arrayIndex = klass.indexOf("[");
        if ( arrayIndex >= 0 ) {
            arraySuffix = klass.substring(arrayIndex);
            klass = klass.substring( 0, arrayIndex );
        }

        //look among imports
        for (ImportDescr id : packageDescr.getImports()) {
            String fqKlass = id.getTarget();
            if ( fqKlass.endsWith( "." + klass ) ) {
                //logger.info("Replace supertype " + sup + " with full name " + id.getTarget());
                return arrayIndex < 0 ? fqKlass : fqKlass + arraySuffix;
            }
        }

        //look among local declarations
        if (pkgRegistry != null) {
            for (String declaredName : pkgRegistry.getPackage().getTypeDeclarations().keySet()) {
                if (declaredName.equals(klass)) {
                    TypeDeclaration typeDeclaration = pkgRegistry.getPackage().getTypeDeclaration(declaredName);
                    if ( typeDeclaration.getTypeClass() != null ) {
                        klass = typeDeclaration.getTypeClass().getName();
                    }
                }
            }
        }

        if ((klass != null) && (!klass.contains(".")) && (packageDescr.getNamespace() != null && !packageDescr.getNamespace().isEmpty())) {
            for (AbstractClassTypeDeclarationDescr td : packageDescr.getClassAndEnumDeclarationDescrs()) {
                if ( klass.equals( td.getTypeName() ) ) {
                    if ( td.getType().getFullName().contains( "." ) ) {
                        klass = td.getType().getFullName();
                    }
                }
            }
        }

        return arrayIndex < 0 ? klass : klass + arraySuffix;
    }


    public static String typeName2ClassName( String type, ClassLoader loader ) {
        Class<?> cls = getClassForType( type, loader );
        return cls != null ? cls.getName() : type;
    }


    public static Class<?> getClassForType( String type, ClassLoader loader ) {
        Class<?> cls = null;
        if ( ! isQualified( type ) ) {
            return null;
        }
        String className = type;
        while (true) {
            try {
                cls = Class.forName( className, true, loader );
                break;
            } catch (ClassNotFoundException e) { }
            int separator = className.lastIndexOf('.');
            if (separator < 0) {
                break;
            }
            className = className.substring(0, separator) + "$" + className.substring(separator + 1);
        }
        return cls;
    }

    public static boolean isCompatible( Class<?> typeClass, AbstractClassTypeDeclarationDescr typeDescr ) {
        try {
            if ( typeDescr.getFields().isEmpty() ) {
                return true;
            }
            Class<?> sup = typeClass.getSuperclass();
            if ( sup == null ) {
                return true;
            }
            if ( ! sup.getName().equals( typeDescr.getSupertTypeFullName() ) ) {
                return false;
            }
            ClassFieldInspector cfi = new ClassFieldInspector( typeClass, false );
            if ( cfi.getGetterMethods().size() != typeDescr.getFields().size() ) {
                return false;
            }
            for ( String fieldName : cfi.getFieldTypes().keySet() ) {
                if ( ! typeDescr.getFields().containsKey( fieldName ) ) {
                    return false;
                }
                String fieldTypeName = typeDescr.getFields().get( fieldName ).getPattern().getObjectType();
                Class fieldType = cfi.getFieldTypes().get( fieldName );
                if ( ! fieldTypeName.equals( fieldType.getName() ) || ! fieldTypeName.equals( fieldType.getSimpleName() ) ) {
                    return false;
                }
            }

        } catch ( IOException e ) {
            return false;
        }
        return true;
    }


    /**
     * Tries to determine whether a given annotation is properly defined using a
     * java.lang.Annotation and can be resolved
     *
     * Proper annotations will be wired to dynamically generated beans
     */
    public static Class resolveAnnotation( String annotation,
                                           TypeResolver resolver ) {

        // do not waste time with @format
        if ( TypeDeclaration.Format.ID.equals(annotation)) {
            return null;
        }
        // known conflicting annotation
        if (TypeDeclaration.ATTR_CLASS.equals(annotation)) {
            return null;
        }

        try {
            return resolver.resolveType(annotation.indexOf('.') < 0 ?
                                        annotation.substring(0, 1).toUpperCase() + annotation.substring(1) :
                                        annotation);
        } catch (ClassNotFoundException e) {
            // internal annotation, or annotation which can't be resolved.
            if (TypeDeclaration.Role.ID.equals(annotation)) {
                return Role.class;
            }
            if ("key".equals(annotation)) {
                return Key.class;
            }
            if ("position".equals(annotation)) {
                return Position.class;
            }
            return null;
        }
    }




    public static boolean isQualified( String name ) {
        return !StringUtils.isEmpty( name ) && name.indexOf( '.' ) >= 0;
    }

    public static boolean isNovelClass( AbstractClassTypeDeclarationDescr typeDescr, PackageRegistry reg ) {
        return getExistingDeclarationClass( typeDescr, reg ) == null;
    }

    /*
    public static String lookupSimpleNameByImportStar( AbstractClassTypeDeclarationDescr typeDescr, TypeResolver resolver ) {
        if ( isEmpty(typeDescr.getNamespace()) && typeDescr.getFields().isEmpty() ) {
            // might be referencing a class imported with a package import (.*)

            if (resolver != null) {
                try {
                    Class<?> clz = resolver.resolveType( typeDescr.getTypeName() );
                    return clz.getName();
                } catch (Exception e) {
                    // intentionally eating the exception as we will fallback to default namespace
                }
            }
        }
        return null;
    }
    */


    public static String rewriteInitExprWithImports( String expr,
                                                     TypeResolver typeResolver ) {
        if (expr == null) {
            return null;
        }
        if ( typeResolver == null ) {
            return expr;
        }
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        boolean inTypeName = false;
        boolean afterDot = false;
        int typeStart = 0;
        for (int i = 0; i < expr.length(); i++) {
            char ch = expr.charAt(i);
            if (Character.isJavaIdentifierStart(ch)) {
                if (!inTypeName && !inQuotes && !afterDot) {
                    typeStart = i;
                    inTypeName = true;
                }
            } else if (!Character.isJavaIdentifierPart(ch)) {
                if (ch == '"') {
                    inQuotes = !inQuotes;
                } else if (ch == '.' && !inQuotes) {
                    afterDot = true;
                } else if (!Character.isSpaceChar(ch)) {
                    afterDot = false;
                }
                if (inTypeName) {
                    inTypeName = false;
                    String type = expr.substring(typeStart, i);
                    sb.append( getFullTypeName( type, typeResolver ) );
                }
            }
            if (!inTypeName) {
                sb.append(ch);
            }
        }
        if (inTypeName) {
            String type = expr.substring(typeStart);
            sb.append( getFullTypeName( type, typeResolver ) );
        }
        return sb.toString();
    }

    private static String getFullTypeName( String type,
                                          TypeResolver typeResolver) {
        if ( isLiteralOrKeyword( type ) ) {
            return type;
        }
        try {
            return typeResolver.getFullTypeName(type);
        } catch (ClassNotFoundException e) {
            return type;
        }
    }

    private static boolean isLiteralOrKeyword( String type ) {
        return "true".equals( type )
               || "false".equals( type )
               || "null".equals( type )
               || "new".equals( type );
    }


    // not the cleanest logic, but this is what the builders expect downstream
    public static String toBuildableType( String className, ClassLoader loader ) {
        int arrayDim = BuildUtils.externalArrayDimSize( className );
        String prefix = "";

        String coreType = arrayDim == 0 ? className : className.substring( 0, className.indexOf( "[" ) );
        coreType = typeName2ClassName( coreType, loader );

        if ( arrayDim > 0 ) {
            coreType = BuildUtils.getTypeDescriptor( coreType );
            for ( int j = 0; j < arrayDim; j++ ) {
                prefix = "[" + prefix;
            }
        } else {
            return coreType;
        }

        return prefix + coreType;
    }
}




