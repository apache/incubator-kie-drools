package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.DisabledPropertyReactiveWarning;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.TypeDeclarationError;
import org.drools.compiler.lang.descr.AbstractClassTypeDeclarationDescr;
import org.drools.compiler.lang.descr.AnnotationDescr;
import org.drools.compiler.lang.descr.EnumDeclarationDescr;
import org.drools.compiler.lang.descr.EnumLiteralDescr;
import org.drools.compiler.lang.descr.ImportDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.QualifiedName;
import org.drools.compiler.lang.descr.TypeDeclarationDescr;
import org.drools.compiler.lang.descr.TypeFieldDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.base.ClassFieldAccessor;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.TypeResolver;
import org.drools.core.base.evaluators.TimeIntervalParser;
import org.drools.core.base.mvel.MVELCompileable;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.AnnotationDefinition;
import org.drools.core.factmodel.BuildUtils;
import org.drools.core.factmodel.ClassBuilder;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.factmodel.EnumClassDefinition;
import org.drools.core.factmodel.EnumLiteralDefinition;
import org.drools.core.factmodel.FieldDefinition;
import org.drools.core.factmodel.GeneratedFact;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.Trait;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.HierarchySorter;
import org.drools.core.util.asm.ClassFieldInspector;
import org.kie.api.definition.type.ClassReactive;
import org.kie.api.definition.type.FactField;
import org.kie.api.definition.type.Key;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.definition.type.Role;
import org.kie.api.io.Resource;
import org.kie.api.runtime.rule.Match;
import org.kie.internal.builder.conf.PropertySpecificOption;

import java.beans.IntrospectionException;
import java.io.Externalizable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import static org.drools.core.util.BitMaskUtil.isSet;
import static org.drools.core.util.ClassUtils.convertClassToResourcePath;
import static org.drools.core.util.StringUtils.isEmpty;

public class TypeDeclarationBuilder {

    private final KnowledgeBuilderImpl kbuilder;

    private final Map<String, TypeDeclaration> builtinTypes = new HashMap<String, TypeDeclaration>();
    private Map<String, TypeDeclaration> cacheTypes;

    private final Set<String> generatedTypes                = new HashSet<String>();

    private TimeIntervalParser timeParser;

    TypeDeclarationBuilder(KnowledgeBuilderImpl kbuilder) {
        this.kbuilder = kbuilder;
        initBuiltinTypeDeclarations();
    }

    private void initBuiltinTypeDeclarations() {
        TypeDeclaration colType = new TypeDeclaration("Collection");
        colType.setTypesafe(false);
        colType.setTypeClass(Collection.class);
        builtinTypes.put("java.util.Collection",
                         colType);

        TypeDeclaration mapType = new TypeDeclaration("Map");
        mapType.setTypesafe(false);
        mapType.setTypeClass(Map.class);
        builtinTypes.put("java.util.Map",
                         mapType);

        TypeDeclaration activationType = new TypeDeclaration("Match");
        activationType.setTypesafe(false);
        activationType.setTypeClass(Match.class);
        builtinTypes.put(Match.class.getCanonicalName(),
                         activationType);

        TypeDeclaration thingType = new TypeDeclaration(Thing.class.getSimpleName());
        thingType.setKind(TypeDeclaration.Kind.TRAIT);
        thingType.setTypeClass(Thing.class);
        builtinTypes.put(Thing.class.getCanonicalName(),
                         thingType);
    }

    public TypeDeclaration getAndRegisterTypeDeclaration(Class<?> cls, String packageName) {
        if (cls.isPrimitive() || cls.isArray()) {
            return null;
        }
        TypeDeclaration typeDeclaration = getCachedTypeDeclaration(cls);
        if (typeDeclaration != null) {
            registerTypeDeclaration(packageName, typeDeclaration);
            return typeDeclaration;
        }
        typeDeclaration = getExistingTypeDeclaration(cls);
        if (typeDeclaration != null) {
            initTypeDeclaration(cls, typeDeclaration);
            return typeDeclaration;
        }

        typeDeclaration = createTypeDeclarationForBean(cls);
        initTypeDeclaration(cls, typeDeclaration);
        registerTypeDeclaration(packageName, typeDeclaration);
        return typeDeclaration;
    }

    private void registerTypeDeclaration(String packageName,
                                         TypeDeclaration typeDeclaration) {
        if (typeDeclaration.getNature() == TypeDeclaration.Nature.DECLARATION || packageName.equals(typeDeclaration.getTypeClass().getPackage().getName())) {
            PackageRegistry packageRegistry = kbuilder.getPackageRegistry(packageName);
            if (packageRegistry != null) {
                packageRegistry.getPackage().addTypeDeclaration(typeDeclaration);
            } else {
                kbuilder.newPackage(new PackageDescr(packageName, ""));
                kbuilder.getPackageRegistry(packageName).getPackage().addTypeDeclaration(typeDeclaration);
            }
        }
    }

    TypeDeclaration getTypeDeclaration(Class<?> cls) {
        if (cls.isPrimitive() || cls.isArray())
            return null;

        // If this class has already been accessed, it'll be in the cache
        TypeDeclaration tdecl = getCachedTypeDeclaration(cls);
        return tdecl != null ? tdecl : createTypeDeclaration(cls);
    }

    private TypeDeclaration createTypeDeclaration(Class<?> cls) {
        TypeDeclaration typeDeclaration = getExistingTypeDeclaration(cls);

        if (typeDeclaration == null) {
            typeDeclaration = createTypeDeclarationForBean(cls);
        }

        initTypeDeclaration(cls, typeDeclaration);
        return typeDeclaration;
    }

    private TypeDeclaration getCachedTypeDeclaration(Class<?> cls) {
        if (this.cacheTypes == null) {
            this.cacheTypes = new HashMap<String, TypeDeclaration>();
            return null;
        } else {
            return cacheTypes.get(cls.getName());
        }
    }

    private TypeDeclaration getExistingTypeDeclaration(Class<?> cls) {
        // Check if we are in the built-ins
        TypeDeclaration typeDeclaration = this.builtinTypes.get((cls.getName()));
        if (typeDeclaration == null) {
            // No built-in
            // Check if there is a user specified typedeclr
            PackageRegistry pkgReg = kbuilder.getPackageRegistry(ClassUtils.getPackage(cls));
            if (pkgReg != null) {
                String className = cls.getName();
                String typeName = className.substring(className.lastIndexOf(".") + 1);
                typeDeclaration = pkgReg.getPackage().getTypeDeclaration(typeName);
            }
        }
        return typeDeclaration;
    }

    private void initTypeDeclaration(Class<?> cls,
                                     TypeDeclaration typeDeclaration) {
        ClassDefinition clsDef = typeDeclaration.getTypeClassDef();
        if (clsDef == null) {
            clsDef = new ClassDefinition();
            typeDeclaration.setTypeClassDef(clsDef);
        }

        if (typeDeclaration.isPropertyReactive()) {
            processModifiedProps(cls, clsDef);
        }
        processFieldsPosition(cls, clsDef, typeDeclaration);

        // build up a set of all the super classes and interfaces
        Set<TypeDeclaration> tdecls = new LinkedHashSet<TypeDeclaration>();

        tdecls.add(typeDeclaration);
        buildTypeDeclarations(cls,
                              tdecls);

        // Iterate and for each typedeclr assign it's value if it's not already set
        // We start from the rear as those are the furthest away classes and interfaces
        TypeDeclaration[] tarray = tdecls.toArray(new TypeDeclaration[tdecls.size()]);
        for (int i = tarray.length - 1; i >= 0; i--) {
            TypeDeclaration currentTDecl = tarray[i];
            if (!isSet(typeDeclaration.getSetMask(),
                       TypeDeclaration.ROLE_BIT) && isSet(currentTDecl.getSetMask(),
                                                          TypeDeclaration.ROLE_BIT)) {
                typeDeclaration.setRole(currentTDecl.getRole());
            }
            if (!isSet(typeDeclaration.getSetMask(),
                       TypeDeclaration.FORMAT_BIT) && isSet(currentTDecl.getSetMask(),
                                                            TypeDeclaration.FORMAT_BIT)) {
                typeDeclaration.setFormat(currentTDecl.getFormat());
            }
            if (!isSet(typeDeclaration.getSetMask(),
                       TypeDeclaration.TYPESAFE_BIT) && isSet(currentTDecl.getSetMask(),
                                                              TypeDeclaration.TYPESAFE_BIT)) {
                typeDeclaration.setTypesafe(currentTDecl.isTypesafe());
            }
        }

        this.cacheTypes.put(cls.getName(),
                            typeDeclaration);
    }

    private void processFieldsPosition(Class<?> cls,
                                       ClassDefinition clsDef,
                                       TypeDeclaration typeDeclaration) {
        // it's a new type declaration, so generate the @Position for it
        Collection<Field> fields = new LinkedList<Field>();
        Class<?> tempKlass = cls;
        while (tempKlass != null && tempKlass != Object.class) {
            Collections.addAll(fields, tempKlass.getDeclaredFields());
            tempKlass = tempKlass.getSuperclass();
        }

        FieldDefinition[] orderedFields = new FieldDefinition[fields.size()];

        for (Field fld : fields) {
            Position pos = fld.getAnnotation(Position.class);
            if (pos != null) {
                if (pos.value() < 0 || pos.value() >= fields.size()) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDeclaration,
                                                                       "Out of range position " + pos.value() + " for field '" + fld.getName() + "' on class " + cls.getName()));
                    continue;
                }
                if (orderedFields[pos.value()] != null) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDeclaration,
                                                                       "Duplicated position " + pos.value() + " for field '" + fld.getName() + "' on class " + cls.getName()));
                    continue;
                }
                FieldDefinition fldDef = clsDef.getField(fld.getName());
                if (fldDef == null) {
                    fldDef = new FieldDefinition(fld.getName(), fld.getType().getName());
                }
                fldDef.setIndex(pos.value());
                orderedFields[pos.value()] = fldDef;
            }
        }
        for (FieldDefinition fld : orderedFields) {
            if (fld != null) {
                // it's null if there is no @Position
                clsDef.addField(fld);
            }
        }
    }

    private void processModifiedProps(Class<?> cls,
                                      ClassDefinition clsDef) {
        for (Method method : cls.getDeclaredMethods()) {
            Modifies modifies = method.getAnnotation(Modifies.class);
            if (modifies != null) {
                String[] props = modifies.value();
                List<String> properties = new ArrayList<String>(props.length);
                for (String prop : props) {
                    properties.add(prop.trim());
                }
                clsDef.addModifiedPropsByMethod(method,
                                                properties);
            }
        }
    }

    private TypeDeclaration createTypeDeclarationForBean(Class<?> cls) {
        TypeDeclaration typeDeclaration = new TypeDeclaration(cls);

        PropertySpecificOption propertySpecificOption = kbuilder.getBuilderConfiguration().getOption(PropertySpecificOption.class);
        boolean propertyReactive = propertySpecificOption.isPropSpecific(cls.isAnnotationPresent(PropertyReactive.class),
                                                                         cls.isAnnotationPresent(ClassReactive.class));

        setPropertyReactive(null, typeDeclaration, propertyReactive);

        Role role = cls.getAnnotation(Role.class);
        if (role != null && role.value() == Role.Type.EVENT) {
            typeDeclaration.setRole(TypeDeclaration.Role.EVENT);
        }

        return typeDeclaration;
    }

    private void buildTypeDeclarations(Class<?> cls,
                                       Set<TypeDeclaration> tdecls) {
        // Process current interfaces
        Class<?>[] intfs = cls.getInterfaces();
        for (Class<?> intf : intfs) {
            buildTypeDeclarationInterfaces(intf,
                                           tdecls);
        }

        // Process super classes and their interfaces
        cls = cls.getSuperclass();
        while (cls != null && cls != Object.class) {
            if (!buildTypeDeclarationInterfaces(cls,
                                                tdecls)) {
                break;
            }
            cls = cls.getSuperclass();
        }

    }

    private boolean buildTypeDeclarationInterfaces(Class cls,
                                                   Set<TypeDeclaration> tdecls) {
        PackageRegistry pkgReg;

        TypeDeclaration tdecl = this.builtinTypes.get((cls.getName()));
        if (tdecl == null) {
            pkgReg = kbuilder.getPackageRegistry(ClassUtils.getPackage(cls));
            if (pkgReg != null) {
                tdecl = pkgReg.getPackage().getTypeDeclaration(cls.getSimpleName());
            }
        }
        if (tdecl != null) {
            if (!tdecls.add(tdecl)) {
                return false; // the interface already exists, return to stop recursion
            }
        }

        Class<?>[] intfs = cls.getInterfaces();
        for (Class<?> intf : intfs) {
            pkgReg = kbuilder.getPackageRegistry(ClassUtils.getPackage(intf));
            if (pkgReg != null) {
                tdecl = pkgReg.getPackage().getTypeDeclaration(intf.getSimpleName());
            }
            if (tdecl != null) {
                tdecls.add(tdecl);
            }
        }

        for (Class<?> intf : intfs) {
            if (!buildTypeDeclarationInterfaces(intf,
                                                tdecls)) {
                return false;
            }
        }

        return true;

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
    private String resolveType(String klass,
                               PackageDescr packageDescr,
                               PackageRegistry pkgRegistry) {

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
                if (declaredName.equals(klass))
                    klass = pkgRegistry.getPackage().getTypeDeclaration(declaredName).getTypeClass().getName();
            }
        }

        if ((klass != null) && (!klass.contains(".")) && (packageDescr.getNamespace() != null && !packageDescr.getNamespace().isEmpty())) {
            for (AbstractClassTypeDeclarationDescr td : packageDescr.getClassAndEnumDeclarationDescrs()) {
                if ( klass.equals( td.getTypeName() ) ) {
                    if ( td.getType().getFullName().contains( "." ) ) {
                        klass = td.getType().getFullName();
                    } else {
                        klass = packageDescr.getNamespace() + "." + klass;
                    }
                }
            }

        }

        return arrayIndex < 0 ? klass : klass + arraySuffix;
    }

    /**
     * Resolves and sets the superclass (name and package) for a given type
     * declaration descriptor The declared supertype, if any, may be a simple
     * name or a fully qualified one. In the former case, the simple name could
     * be the local name of some f.q.n. which has to be resolved
     *
     * @param typeDescr
     *            the descriptor of the declared superclass whose superclass
     *            will be identified
     * @param packageDescr
     *            the descriptor of the package the class is declared in
     */
    private void fillSuperType(TypeDeclarationDescr typeDescr,
                               PackageDescr packageDescr) {

        for (QualifiedName qname : typeDescr.getSuperTypes()) {
            String declaredSuperType = qname.getFullName();

            if (declaredSuperType != null) {
                int separator = declaredSuperType.lastIndexOf(".");
                boolean qualified = separator > 0;
                // check if a simple name corresponds to a f.q.n.
                if (!qualified) {
                    declaredSuperType =
                            resolveType(declaredSuperType,
                                        packageDescr,
                                        kbuilder.getPackageRegistry(typeDescr.getNamespace()));

                    declaredSuperType = typeName2ClassName(declaredSuperType);

                    // sets supertype name and supertype package
                    separator = declaredSuperType.lastIndexOf(".");
                    if (separator < 0) {
                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                           "Cannot resolve supertype '" + declaredSuperType + "'"));
                        qname.setName(null);
                        qname.setNamespace(null);
                    } else {
                        qname.setName(declaredSuperType.substring(separator + 1));
                        qname.setNamespace(declaredSuperType.substring(0,
                                                                       separator));
                    }
                }
            }
        }
    }

    private String typeName2ClassName(String type) {
        Class<?> cls = getClassForType(type);
        return cls != null ? cls.getName() : type;
    }

    private Class<?> getClassForType(String type) {
        Class<?> cls = null;
        String superType = type;
        while (true) {
            try {
                cls = Class.forName(superType, true, kbuilder.getRootClassLoader());
                break;
            } catch (ClassNotFoundException e) { }
            int separator = superType.lastIndexOf('.');
            if (separator < 0) {
                break;
            }
            superType = superType.substring(0, separator) + "$" + superType.substring(separator + 1);
        }
        return cls;
    }

    public void fillFieldTypes( AbstractClassTypeDeclarationDescr typeDescr,
                                PackageDescr packageDescr) {

        for (TypeFieldDescr field : typeDescr.getFields().values()) {
            String declaredType = field.getPattern().getObjectType();

            if (declaredType != null) {
                int separator = declaredType.lastIndexOf(".");
                boolean qualified = separator > 0;
                // check if a simple name corresponds to a f.q.n.
                if (!qualified) {
                    declaredType =
                            resolveType(declaredType,
                                        packageDescr,
                                        kbuilder.getPackageRegistry(typeDescr.getNamespace()));

                    field.getPattern().setObjectType(declaredType);
                }
            }
        }
    }

    /**
     * In order to build a declared class, the fields inherited from its
     * superclass(es) are added to its declaration. Inherited descriptors are
     * marked as such to distinguish them from native ones. Various scenarioes
     * are possible. (i) The superclass has been declared in the DRL as well :
     * the fields are cloned as inherited (ii) The superclass is imported
     * (external), but some of its fields have been tagged with metadata (iii)
     * The superclass is imported.
     *
     * The search for field descriptors is carried out in the order. (i) and
     * (ii+iii) are mutually exclusive. The search is as such: (i) The
     * superclass' declared fields are used to build the base class additional
     * fields (iii) The superclass is inspected to discover its (public) fields,
     * from which descriptors are generated (ii) Both (i) and (iii) are applied,
     * but the declared fields override the inspected ones
     *
     *
     *
     *
     * @param typeDescr
     *            The base class descriptor, to be completed with the inherited
     *            fields descriptors
     * @param unprocessableDescrs
     * @param typeResolver
     * @return true if all went well
     */
    private boolean mergeInheritedFields( TypeDeclarationDescr typeDescr, List<TypeDefinition> unresolvedTypes, Map<String, TypeDeclarationDescr> unprocessableDescrs, TypeResolver typeResolver ) {

        if (typeDescr.getSuperTypes().isEmpty())
            return false;
        boolean merge = false;

        for (int j = typeDescr.getSuperTypes().size() - 1; j >= 0; j--) {
            QualifiedName qname = typeDescr.getSuperTypes().get(j);
            String simpleSuperTypeName = qname.getName();
            String superTypePackageName = qname.getNamespace();
            String fullSuper = qname.getFullName();

            merge = mergeFields( simpleSuperTypeName,
                                 superTypePackageName,
                                 fullSuper,
                                 typeDescr,
                                 unresolvedTypes,
                                 unprocessableDescrs,
                                 typeResolver ) || merge;
        }

        return merge;
    }

    private boolean mergeFields( String simpleSuperTypeName,
                                 String superTypePackageName,
                                 String fullSuper,
                                 TypeDeclarationDescr typeDescr,
                                 List<TypeDefinition> unresolvedTypes,
                                 Map<String,TypeDeclarationDescr> unprocessableDescrs,
                                 TypeResolver resolver ) {

        Map<String, TypeFieldDescr> fieldMap = new LinkedHashMap<String, TypeFieldDescr>();
        boolean isNovel = isNovelClass( typeDescr );

        PackageRegistry registry = kbuilder.getPackageRegistry(superTypePackageName);
        InternalKnowledgePackage pack = null;
        if (registry != null) {
            pack = registry.getPackage();
        } else {
            // If there is no regisrty the type isn't a DRL-declared type, which is forbidden.
            // Avoid NPE JIRA-3041 when trying to access the registry. Avoid subsequent problems.
            // DROOLS-536 At this point, the declarations might exist, but the package might not have been processed yet
            if ( isNovel ) {
                unprocessableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
                return false;
            }
        }

        if ( unprocessableDescrs.containsKey( fullSuper ) ) {
            unprocessableDescrs.put( typeDescr.getType().getFullName(), typeDescr );
            return false;
        }

        // if a class is declared in DRL, its package can't be null? The default package is replaced by "defaultpkg"
        boolean isSuperClassTagged = false;
        boolean isSuperClassDeclared = true; //in the same package, or in a previous one

        if (pack != null) {

            // look for the supertype declaration in available packages
            TypeDeclaration superTypeDeclaration = pack.getTypeDeclaration(simpleSuperTypeName);

            if (superTypeDeclaration != null && superTypeDeclaration.getTypeClassDef() != null ) {
                ClassDefinition classDef = superTypeDeclaration.getTypeClassDef();
                // inherit fields
                for (org.kie.api.definition.type.FactField fld : classDef.getFields()) {
                    TypeFieldDescr inheritedFlDescr = buildInheritedFieldDescrFromDefinition(fld, typeDescr);
                    fieldMap.put(inheritedFlDescr.getFieldName(),
                                 inheritedFlDescr);
                }

                // new classes are already distinguished from tagged external classes
                isSuperClassTagged = !superTypeDeclaration.isNovel();
            } else {
                for ( TypeDefinition def : unresolvedTypes ) {
                    if ( def.getTypeClassName().equals( fullSuper ) ) {
                        TypeDeclarationDescr td = (TypeDeclarationDescr) def.typeDescr;
                        for ( TypeFieldDescr tf : td.getFields().values() ) {
                            fieldMap.put( tf.getFieldName(), tf.cloneAsInherited() );
                        }
                        isSuperClassDeclared = def.type.isNovel();
                        break;
                    }
                    isSuperClassDeclared = false;
                }
            }
        } else {
            isSuperClassDeclared = false;
        }

        // look for the class externally
        if ( !isSuperClassDeclared || isSuperClassTagged ) {
            try {
                Class superKlass;
                if ( registry != null ) {
                    superKlass = registry.getTypeResolver().resolveType(fullSuper);
                } else {
                    // if the supertype has not been declared, and we have got so far, it means that this class is not novel
                    superKlass = resolver.resolveType( fullSuper );
                }
                ClassFieldInspector inspector = new ClassFieldInspector(superKlass);
                for (String name : inspector.getGetterMethods().keySet()) {
                    // classFieldAccessor requires both getter and setter
                    if (inspector.getSetterMethods().containsKey(name)) {
                        if (!inspector.isNonGetter(name) && !"class".equals(name)) {
                            TypeFieldDescr inheritedFlDescr = new TypeFieldDescr(
                                    name,
                                    new PatternDescr(
                                            inspector.getFieldTypes().get(name).getName()));
                            inheritedFlDescr.setInherited(!Modifier.isAbstract(inspector.getGetterMethods().get(name).getModifiers()));

                            if (!fieldMap.containsKey(inheritedFlDescr.getFieldName()))
                                fieldMap.put(inheritedFlDescr.getFieldName(),
                                             inheritedFlDescr);
                        }
                    }
                }

            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException("Unable to resolve Type Declaration superclass '" + fullSuper + "'");
            } catch (IOException e) {

            }
        }

        // finally, locally declared fields are merged. The map swap ensures that super-fields are added in order, before the subclass' ones
        // notice that it is not possible to override a field changing its type
        for (String fieldName : typeDescr.getFields().keySet()) {
            if (fieldMap.containsKey(fieldName)) {
                String type1 = fieldMap.get(fieldName).getPattern().getObjectType();
                String type2 = typeDescr.getFields().get(fieldName).getPattern().getObjectType();
                if (type2.lastIndexOf(".") < 0) {
                    try {
                        TypeResolver typeResolver = kbuilder.getPackageRegistry(pack.getName()).getTypeResolver();
                        type1 = typeResolver.resolveType(type1).getName();
                        type2 = typeResolver.resolveType(type2).getName();
                        // now that we are at it... this will be needed later anyway
                        fieldMap.get(fieldName).getPattern().setObjectType(type1);
                        typeDescr.getFields().get(fieldName).getPattern().setObjectType(type2);
                    } catch (ClassNotFoundException cnfe) {
                        // will fail later
                    }
                }

                if (!type1.equals(type2)) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                       "Cannot redeclare field '" + fieldName + " from " + type1 + " to " + type2));
                    typeDescr.setType(null,
                                      null);
                    return false;
                } else {
                    String initVal = fieldMap.get(fieldName).getInitExpr();
                    if (typeDescr.getFields().get(fieldName).getInitExpr() == null) {
                        typeDescr.getFields().get(fieldName).setInitExpr(initVal);
                    }
                    typeDescr.getFields().get(fieldName).setInherited(fieldMap.get(fieldName).isInherited());

                    for (String key : fieldMap.get(fieldName).getAnnotationNames()) {
                        if (typeDescr.getFields().get(fieldName).getAnnotation(key) == null) {
                            typeDescr.getFields().get(fieldName).addAnnotation(fieldMap.get(fieldName).getAnnotation(key));
                        }
                    }

                    if (typeDescr.getFields().get(fieldName).getIndex() < 0) {
                        typeDescr.getFields().get(fieldName).setIndex(fieldMap.get(fieldName).getIndex());
                    }
                }
            }
            fieldMap.put(fieldName,
                         typeDescr.getFields().get(fieldName));
        }

        typeDescr.setFields(fieldMap);

        return true;
    }

    protected TypeFieldDescr buildInheritedFieldDescrFromDefinition(org.kie.api.definition.type.FactField fld, TypeDeclarationDescr typeDescr) {
        PatternDescr fldType = new PatternDescr();
        TypeFieldDescr inheritedFldDescr = new TypeFieldDescr();
        inheritedFldDescr.setFieldName(fld.getName());
        if ( ((FieldDefinition) fld).getFieldAccessor() != null ) {
            // target class may have not been resolved yet
            fldType.setObjectType(((FieldDefinition) fld).getFieldAccessor().getExtractToClassName());
        }
        inheritedFldDescr.setPattern(fldType);
        if (fld.isKey()) {
            inheritedFldDescr.getAnnotations().put(TypeDeclaration.ATTR_KEY,
                                                   new AnnotationDescr(TypeDeclaration.ATTR_KEY));
        }
        inheritedFldDescr.setIndex(((FieldDefinition) fld).getDeclIndex());
        inheritedFldDescr.setInherited(true);

        String initExprOverride = ((FieldDefinition) fld).getInitExpr();
        int overrideCount = 0;
        // only @aliasing local fields may override defaults.
        for (TypeFieldDescr localField : typeDescr.getFields().values()) {
            AnnotationDescr ann = localField.getAnnotation("Alias");
            if (ann != null && fld.getName().equals(ann.getSingleValue().replaceAll("\"", "")) && localField.getInitExpr() != null) {
                overrideCount++;
                initExprOverride = localField.getInitExpr();
            }
        }
        if (overrideCount > 1) {
            // however, only one is allowed
            initExprOverride = null;
        }
        inheritedFldDescr.setInitExpr(initExprOverride);
        return inheritedFldDescr;
    }

    void processTypes(PackageRegistry pkgRegistry, PackageDescr packageDescr, Map<String,TypeDeclarationDescr> unprocessableDescrs) {
        // process types in 2 steps to deal with circular and recursive declarations
        processUnresolvedTypes(pkgRegistry, processTypeDeclarations(pkgRegistry, packageDescr, new ArrayList<TypeDefinition>(), unprocessableDescrs ) );
    }

    void processUnresolvedTypes(PackageRegistry pkgRegistry, List<TypeDefinition> unresolvedTypeDefinitions) {
        if (unresolvedTypeDefinitions != null) {
            for (TypeDefinition typeDef : unresolvedTypeDefinitions) {
                processUnresolvedType(pkgRegistry, typeDef);
            }
        }
    }

    void processUnresolvedType(PackageRegistry pkgRegistry, TypeDefinition unresolvedTypeDefinition) {
        processTypeFields(pkgRegistry, unresolvedTypeDefinition.typeDescr, unresolvedTypeDefinition.type, false);
    }

    private boolean processTypeFields(PackageRegistry pkgRegistry,
                                      AbstractClassTypeDeclarationDescr typeDescr,
                                      TypeDeclaration type,
                                      boolean firstAttempt) {
        if (type.getTypeClassDef() != null) {
            try {
                buildFieldAccessors(type, pkgRegistry);
            } catch (Throwable e) {
                if (!firstAttempt) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                       "Error creating field accessors for TypeDeclaration '" + type.getTypeName() +
                                                                       "' for type '" +
                                                                       type.getTypeName() +
                                                                       " : " + e.getMessage() +
                                                                       "'"));
                }
                return false;
            }
        }

        AnnotationDescr annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_TIMESTAMP);
        String timestamp = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (timestamp != null) {
            type.setTimestampAttribute(timestamp);
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            MVELDialect dialect = (MVELDialect) pkgRegistry.getDialectCompiletimeRegistry().getDialect("mvel");
            PackageBuildContext context = new PackageBuildContext();
            context.init(kbuilder, pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null);
            if (!type.isTypesafe()) {
                context.setTypesafe(false);
            }

            MVELAnalysisResult results = (MVELAnalysisResult)
                    context.getDialect().analyzeExpression(context,
                                                           typeDescr,
                                                           timestamp,
                                                           new BoundIdentifiers(Collections.EMPTY_MAP,
                                                                                Collections.EMPTY_MAP,
                                                                                Collections.EMPTY_MAP,
                                                                                type.getTypeClass()));

            if (results != null) {
                InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader(ClassUtils.getPackage(type.getTypeClass()),
                                                                                             type.getTypeClass().getName(),
                                                                                             timestamp,
                                                                                             type.isTypesafe(),
                                                                                             results.getReturnType());

                MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
                data.addCompileable((MVELCompileable) reader);
                ((MVELCompileable) reader).compile(data);
                type.setTimestampExtractor(reader);
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error creating field accessors for timestamp field '" + timestamp +
                                                                   "' for type '" +
                                                                   type.getTypeName() +
                                                                   "'"));
            }
        }

        annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_DURATION);
        String duration = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (duration != null) {
            type.setDurationAttribute(duration);
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();

            MVELDialect dialect = (MVELDialect) pkgRegistry.getDialectCompiletimeRegistry().getDialect("mvel");
            PackageBuildContext context = new PackageBuildContext();
            context.init(kbuilder, pkg, typeDescr, pkgRegistry.getDialectCompiletimeRegistry(), dialect, null);
            if (!type.isTypesafe()) {
                context.setTypesafe(false);
            }

            MVELAnalysisResult results = (MVELAnalysisResult)
                    context.getDialect().analyzeExpression(context,
                                                           typeDescr,
                                                           duration,
                                                           new BoundIdentifiers(Collections.EMPTY_MAP,
                                                                                Collections.EMPTY_MAP,
                                                                                Collections.EMPTY_MAP,
                                                                                type.getTypeClass()));

            if (results != null) {
                InternalReadAccessor reader = pkg.getClassFieldAccessorStore().getMVELReader(ClassUtils.getPackage(type.getTypeClass()),
                                                                                             type.getTypeClass().getName(),
                                                                                             duration,
                                                                                             type.isTypesafe(),
                                                                                             results.getReturnType());

                MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData("mvel");
                data.addCompileable((MVELCompileable) reader);
                ((MVELCompileable) reader).compile(data);
                type.setDurationExtractor(reader);
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                   "Error processing @duration for TypeDeclaration '" + type.getFullName() +
                                                                   "': cannot access the field '" + duration + "'"));
            }
        }

        annotationDescr = typeDescr.getAnnotation(TypeDeclaration.ATTR_EXPIRE);
        String expiration = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (expiration != null) {
            if (timeParser == null) {
                timeParser = new TimeIntervalParser();
            }
            type.setExpirationOffset(timeParser.parse(expiration)[0]);
        }

        boolean dynamic = typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_PROP_CHANGE_SUPPORT);
        type.setDynamic(dynamic);

        PropertySpecificOption propertySpecificOption = kbuilder.getBuilderConfiguration().getOption(PropertySpecificOption.class);
        boolean propertyReactive = propertySpecificOption.isPropSpecific(typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_PROP_SPECIFIC),
                                                                         typeDescr.getAnnotationNames().contains(TypeDeclaration.ATTR_NOT_PROP_SPECIFIC));

        setPropertyReactive(typeDescr.getResource(), type, propertyReactive);

        if (type.isValid()) {
            // prefer definitions where possible
            if (type.getNature() == TypeDeclaration.Nature.DEFINITION) {
                pkgRegistry.getPackage().addTypeDeclaration(type);
            } else {
                TypeDeclaration oldType = pkgRegistry.getPackage().getTypeDeclaration(type.getTypeName());
                if (oldType == null) {
                    pkgRegistry.getPackage().addTypeDeclaration(type);
                } else {
                    if (type.getRole() == TypeDeclaration.Role.EVENT) {
                        oldType.setRole(TypeDeclaration.Role.EVENT);
                        if ( type.getDurationAttribute() != null ) {
                            oldType.setDurationAttribute( type.getDurationAttribute() );
                            oldType.setDurationExtractor( type.getDurationExtractor() );
                        }
                        if ( type.getTimestampAttribute() != null ) {
                            oldType.setTimestampAttribute( type.getTimestampAttribute() );
                            oldType.setTimestampExtractor( type.getTimestampExtractor() );
                        }
                        if ( type.getExpirationOffset() >= 0 ) {
                            oldType.setExpirationOffset( type.getExpirationOffset() );
                        }
                    }
                    if (type.isPropertyReactive()) {
                        oldType.setPropertyReactive(true);
                    }
                }
            }
        }

        return true;
    }

    private void buildFieldAccessors(final TypeDeclaration type,
                                     final PackageRegistry pkgRegistry) throws SecurityException,
                                                                               IllegalArgumentException,
                                                                               InstantiationException,
                                                                               IllegalAccessException,
                                                                               IOException,
                                                                               IntrospectionException,
                                                                               ClassNotFoundException,
                                                                               NoSuchMethodException,
                                                                               InvocationTargetException,
                                                                               NoSuchFieldException {
        ClassDefinition cd = type.getTypeClassDef();
        ClassFieldAccessorStore store = pkgRegistry.getPackage().getClassFieldAccessorStore();
        for (FieldDefinition attrDef : cd.getFieldsDefinitions()) {
            ClassFieldAccessor accessor = store.getAccessor(cd.getDefinedClass().getName(),
                                                            attrDef.getName());
            attrDef.setReadWriteAccessor(accessor);
        }
    }

    private void setPropertyReactive(Resource resource,
                                     TypeDeclaration type,
                                     boolean propertyReactive) {
        if (propertyReactive && type.getSettableProperties().size() >= 64) {
            kbuilder.addBuilderResult(new DisabledPropertyReactiveWarning(resource, type.getTypeName()));
            type.setPropertyReactive(false);
        } else {
            type.setPropertyReactive(propertyReactive);
        }
    }

    void removeTypesGeneratedFromResource(Resource resource) {
        if (cacheTypes != null) {
            List<String> typesToBeRemoved = new ArrayList<String>();
            for (Map.Entry<String, TypeDeclaration> type : cacheTypes.entrySet()) {
                if (resource.equals(type.getValue().getResource())) {
                    typesToBeRemoved.add(type.getKey());
                }
            }
            for (String type : typesToBeRemoved) {
                cacheTypes.remove(type);
            }
        }
    }

    List<TypeDefinition> processTypeDeclarations( PackageRegistry pkgRegistry, PackageDescr packageDescr, List<TypeDefinition> unresolvedTypes, Map<String, TypeDeclarationDescr> unprocessableDescrs ) {

        Map<String, PackageDescr> foreignPackages = null;

        for (AbstractClassTypeDeclarationDescr typeDescr : packageDescr.getClassAndEnumDeclarationDescrs()) {

            String qName = typeDescr.getType().getFullName();
            Class<?> typeClass = getClassForType(qName);
            if (typeClass == null) {
                typeClass = getClassForType(typeDescr.getTypeName());
            }
            if (typeClass == null) {
                for (ImportDescr id : packageDescr.getImports()) {
                    String imp = id.getTarget();
                    int separator = imp.lastIndexOf('.');
                    String tail = imp.substring(separator + 1);
                    if (tail.equals(typeDescr.getTypeName())) {
                        typeDescr.setNamespace(imp.substring(0, separator));
                        typeClass = getClassForType(typeDescr.getType().getFullName());
                        break;
                    } else if (tail.equals("*")) {
                        typeClass = getClassForType(imp.substring(0, imp.length() - 1) + typeDescr.getType().getName());
                        if (typeClass != null) {
                            String resolvedNamespace = imp.substring(0, separator);
                            if ( resolvedNamespace.equals( typeDescr.getNamespace() ) ) {
                                // the class was found in the declared namespace, so stop here
                                break;
                                // here, the class was found in a different namespace. It means that the class was declared
                                // with no namespace and the initial guess was wrong, or that there is an ambiguity.
                                // So, we need to check that the resolved class is compatible with the declaration.
                            } else if ( isCompatible( typeClass, typeDescr ) ) {
                                typeDescr.setNamespace( resolvedNamespace );
                            } else {
                                typeClass = null;
                            }
                        }
                    }
                }
            }
            String className = typeClass != null ? typeClass.getName() : qName;
            int dotPos = className.lastIndexOf('.');
            if (dotPos >= 0) {
                typeDescr.setNamespace(className.substring(0, dotPos));
                typeDescr.setTypeName(className.substring(dotPos + 1));
            }

            if (isEmpty(typeDescr.getNamespace()) && typeDescr.getFields().isEmpty()) {
                // might be referencing a class imported with a package import (.*)
                PackageRegistry pkgReg = kbuilder.getPackageRegistry(packageDescr.getName());
                if (pkgReg != null) {
                    try {
                        Class<?> clz = pkgReg.getTypeResolver().resolveType(typeDescr.getTypeName());
                        java.lang.Package pkg = clz.getPackage();
                        if (pkg != null) {
                            typeDescr.setNamespace(pkg.getName());
                            int index = typeDescr.getNamespace() != null && !typeDescr.getNamespace().isEmpty() ? typeDescr.getNamespace().length() + 1 : 0;
                            typeDescr.setTypeName(clz.getCanonicalName().substring(index));
                        }
                    } catch (Exception e) {
                        // intentionally eating the exception as we will fallback to default namespace
                    }
                }
            }

            if (isEmpty(typeDescr.getNamespace())) {
                typeDescr.setNamespace(packageDescr.getNamespace()); // set the default namespace
            }

            //identify superclass type and namespace
            if (typeDescr instanceof TypeDeclarationDescr) {
                fillSuperType((TypeDeclarationDescr) typeDescr,
                              packageDescr);
                AnnotationDescr kind = typeDescr.getAnnotation( TypeDeclaration.Kind.ID );
                if ( typeClass != null && kind != null && kind.hasValue() && TypeDeclaration.Kind.TRAIT == TypeDeclaration.Kind.parseKind( kind.getSingleValue() ) ) {
                    fillStaticInterfaces( (TypeDeclarationDescr) typeDescr, typeClass );
                }
            }

            //identify field types as well
            fillFieldTypes(typeDescr,
                           packageDescr);

            if (!typeDescr.getNamespace().equals(packageDescr.getNamespace())) {
                // If the type declaration is for a different namespace, process that separately.
                PackageDescr altDescr;

                if ( foreignPackages == null ) {
                    foreignPackages = new HashMap<String, PackageDescr>(  );
                }

                if ( foreignPackages.containsKey( typeDescr.getNamespace() ) ) {
                    altDescr = foreignPackages.get( typeDescr.getNamespace() );
                } else {
                    altDescr = new PackageDescr(typeDescr.getNamespace());
                    foreignPackages.put( typeDescr.getNamespace(), altDescr );
                }

                if (typeDescr instanceof TypeDeclarationDescr) {
                    altDescr.addTypeDeclaration((TypeDeclarationDescr) typeDescr);
                } else if (typeDescr instanceof EnumDeclarationDescr) {
                    altDescr.addEnumDeclaration((EnumDeclarationDescr) typeDescr);
                }

                for (ImportDescr imp : packageDescr.getImports()) {
                    altDescr.addImport(imp);
                }
                if (!kbuilder.getPackageRegistry().containsKey(altDescr.getNamespace())) {
                    kbuilder.newPackage(altDescr);
                }
            }
        }

        if ( foreignPackages != null ) {
            for ( String ns : foreignPackages.keySet() ) {
                kbuilder.mergePackage( kbuilder.getPackageRegistry(ns), foreignPackages.get( ns ) );
            }
            foreignPackages.clear();
        }

        // sort declarations : superclasses must be generated first
        Collection<AbstractClassTypeDeclarationDescr> sortedTypeDescriptors = sortByHierarchy(kbuilder, packageDescr.getClassAndEnumDeclarationDescrs());

        for (AbstractClassTypeDeclarationDescr typeDescr : sortedTypeDescriptors) {
            registerGeneratedType(typeDescr);
        }

        if (kbuilder.hasErrors()) {
            return Collections.emptyList();
        }

        for (AbstractClassTypeDeclarationDescr typeDescr : sortedTypeDescriptors) {

            if (!typeDescr.getNamespace().equals(packageDescr.getNamespace())) {
                continue;
            }

            processTypeDeclaration( pkgRegistry, typeDescr, sortedTypeDescriptors, unresolvedTypes, unprocessableDescrs );
        }
        return unresolvedTypes;
    }

    private void fillStaticInterfaces( TypeDeclarationDescr typeDescr, Class<?> typeClass ) {
        for ( Class iKlass : ClassUtils.getAllImplementedInterfaceNames( typeClass ) ) {
            typeDescr.addSuperType( iKlass.getName() );
        }

    }

    public void processTypeDeclaration( PackageRegistry pkgRegistry,
                                        AbstractClassTypeDeclarationDescr typeDescr,
                                        Collection<AbstractClassTypeDeclarationDescr> sortedTypeDescriptors,
                                        List<TypeDefinition> unresolvedTypes,
                                        Map<String, TypeDeclarationDescr> unprocessableDescrs ) {
        //descriptor needs fields inherited from superclass
        if (typeDescr instanceof TypeDeclarationDescr) {
            TypeDeclarationDescr tDescr = (TypeDeclarationDescr) typeDescr;
            boolean isNovel = isNovelClass( typeDescr );
            boolean inferFields = ! isNovel && typeDescr.getFields().isEmpty();

            for (QualifiedName qname : tDescr.getSuperTypes()) {
                //descriptor needs fields inherited from superclass
                if (mergeInheritedFields(tDescr, unresolvedTypes, unprocessableDescrs, pkgRegistry.getTypeResolver())) {
                    //descriptor also needs metadata from superclass
                    for (AbstractClassTypeDeclarationDescr descr : sortedTypeDescriptors) {
                        // sortedTypeDescriptors are sorted by inheritance order, so we'll always find the superClass (if any) before the subclass
                        if (qname.equals(descr.getType())) {
                            typeDescr.getAnnotations().putAll(descr.getAnnotations());
                            break;
                        } else if (typeDescr.getType().equals(descr.getType())) {
                            break;
                        }

                    }
                }
            }

            if ( inferFields ) {
                // not novel, but only an empty declaration was provided.
                // after inheriting the fields from supertypes, now we fill in the locally declared fields
                try {
                Class existingClass = getExistingDeclarationClass( typeDescr );
                ClassFieldInspector inspector = new ClassFieldInspector( existingClass );
                    for (String name : inspector.getGetterMethods().keySet()) {
                        // classFieldAccessor requires both getter and setter
                        if (inspector.getSetterMethods().containsKey(name)) {
                            if (!inspector.isNonGetter(name) && !"class".equals(name)) {
                                TypeFieldDescr inheritedFlDescr = new TypeFieldDescr(
                                        name,
                                        new PatternDescr(
                                                inspector.getFieldTypes().get(name).getName()));
                                inheritedFlDescr.setInherited(!Modifier.isAbstract(inspector.getGetterMethods().get(name).getModifiers()));

                                if (!tDescr.getFields().containsKey(inheritedFlDescr.getFieldName()))
                                    tDescr.getFields().put(inheritedFlDescr.getFieldName(),
                                                 inheritedFlDescr);
                            }
                        }
                    }
                } catch ( Exception e ) {
                    // can't happen as we know that the class is not novel - that is, it has been resolved before
                }
            }
        }

        if ( unprocessableDescrs.containsKey( typeDescr.getType().getFullName() ) ) {
            return;
        }

        // Go on with the build
        TypeDeclaration type = new TypeDeclaration(typeDescr.getTypeName());
        if (typeDescr.getResource() == null) {
            typeDescr.setResource(kbuilder.getCurrentResource());
        }
        type.setResource(typeDescr.getResource());

        TypeDeclaration parent = null;
        if (!typeDescr.getSuperTypes().isEmpty()) {
            // parent might have inheritable properties
            PackageRegistry sup = kbuilder.getPackageRegistry(typeDescr.getSuperTypeNamespace());
            if (sup != null) {
                parent = sup.getPackage().getTypeDeclaration(typeDescr.getSuperTypeName());
                if ( parent == null ) {
                    for ( TypeDefinition tdef : unresolvedTypes ) {
                        if ( tdef.getTypeClassName().equals( typeDescr.getSuperTypes().get( 0 ).getFullName() ) ) {
                            parent = tdef.type;
                        }
                    }
                }
                if (parent == null) {
                    // FIXME Does this behavior still make sense? The need to redeclare an existing (java) class in order to be able to extend it...
                    // kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "Declared class " + typeDescr.getTypeName() + " can't extend class " + typeDescr.getSuperTypeName() + ", it should be declared"));
                } else {
                    if (parent.getNature() == TypeDeclaration.Nature.DECLARATION && kbuilder.getKnowledgeBase() != null) {
                        // trying to find a definition
                        parent = kbuilder.getKnowledgeBase().getPackagesMap().get(typeDescr.getSuperTypeNamespace()).getTypeDeclaration(typeDescr.getSuperTypeName());
                    }
                }
            }
        }

        // is it a regular fact or an event?
        AnnotationDescr annotationDescr = getSingleAnnotation(typeDescr, TypeDeclaration.Role.ID);
        String role = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (role != null) {
            type.setRole(TypeDeclaration.Role.parseRole(role));
        } else if (parent != null) {
            // FIXME : Should this be here, since Drools 6 does not namely support annotation inheritance?
            type.setRole(parent.getRole());
        }

        annotationDescr = getSingleAnnotation(typeDescr, TypeDeclaration.ATTR_TYPESAFE);
        String typesafe = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (typesafe != null) {
            type.setTypesafe(Boolean.parseBoolean(typesafe));
        } else if (parent != null && isSet(parent.getSetMask(), TypeDeclaration.TYPESAFE_BIT)) {
            // FIXME : Should this be here, since Drools 6 does not namely support annotation inheritance?
            type.setTypesafe(parent.isTypesafe());
        }

        // is it a pojo or a template?
        annotationDescr = getSingleAnnotation(typeDescr, TypeDeclaration.Format.ID);
        String format = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (format != null) {
            type.setFormat(TypeDeclaration.Format.parseFormat(format));
        }

        // is it a class, a trait or an enum?
        annotationDescr = getSingleAnnotation(typeDescr, TypeDeclaration.Kind.ID);
        String kind = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (kind != null) {
            type.setKind(TypeDeclaration.Kind.parseKind(kind));
        }
        if (typeDescr instanceof EnumDeclarationDescr) {
            type.setKind(TypeDeclaration.Kind.ENUM);
        }

        annotationDescr = getSingleAnnotation(typeDescr, TypeDeclaration.ATTR_CLASS);
        String className = (annotationDescr != null) ? annotationDescr.getSingleValue() : null;
        if (isEmpty(className)) {
            className = type.getTypeName();
        }

        try {
            // the type declaration is generated in any case (to be used by subclasses, if any)
            // the actual class will be generated only if needed
            generateDeclaredBean(typeDescr,
                                 type,
                                 pkgRegistry,
                                 unresolvedTypes);

            Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(typeDescr.getType().getFullName());
            type.setTypeClass(clazz);
        } catch (final ClassNotFoundException e) {
            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                               "Class '" + className +
                                                               "' not found for type declaration of '" +
                                                               type.getTypeName() + "'"));
            return;
        }

        if (!processTypeFields(pkgRegistry, typeDescr, type, true)) {
            unresolvedTypes.add(new TypeDefinition(type, typeDescr));
        }
    }


    private AnnotationDescr getSingleAnnotation(AbstractClassTypeDeclarationDescr typeDescr, String name) {
        AnnotationDescr annotationDescr = typeDescr.getAnnotation(name);
        if (annotationDescr != null && annotationDescr.isDuplicated()) {
            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                               "Duplicated annotation '" + name +
                                                               "' for type declaration of '" +
                                                               typeDescr.getTypeName() + "'"));
            return null;
        }
        return annotationDescr;
    }

    /**
     * Utility method to sort declared beans. Linearizes the hierarchy,
     * i.e.generates a sequence of declaration such that, if Sub is subclass of
     * Sup, then the index of Sub will be > than the index of Sup in the
     * resulting collection. This ensures that superclasses are processed before
     * their subclasses
     */
    public static Collection<AbstractClassTypeDeclarationDescr> sortByHierarchy(KnowledgeBuilderImpl kbuilder, Collection<? extends AbstractClassTypeDeclarationDescr> typeDeclarations) {

        Map<QualifiedName, Collection<QualifiedName>> taxonomy = new HashMap<QualifiedName, Collection<QualifiedName>>();
        Map<QualifiedName, AbstractClassTypeDeclarationDescr> cache = new HashMap<QualifiedName, AbstractClassTypeDeclarationDescr>();

        for (AbstractClassTypeDeclarationDescr tdescr : typeDeclarations) {
            QualifiedName name = tdescr.getType();

            cache.put(name, tdescr);

            if (taxonomy.get(name) == null) {
                taxonomy.put(name, new ArrayList<QualifiedName>());
            } else {
                kbuilder.addBuilderResult(new TypeDeclarationError(tdescr,
                                                                   "Found duplicate declaration for type " + tdescr.getType()));
            }

            Collection<QualifiedName> supers = taxonomy.get(name);

            boolean circular = false;
            for (QualifiedName sup : tdescr.getSuperTypes()) {
                if (!Object.class.getName().equals(name.getFullName())) {
                    if (!hasCircularDependency(tdescr.getType(), sup, taxonomy)) {
                        supers.add(sup);
                    } else {
                        circular = true;
                        kbuilder.addBuilderResult(new TypeDeclarationError(tdescr,
                                                                           "Found circular dependency for type " + tdescr.getTypeName()));
                        break;
                    }
                }
            }
            if (circular) {
                tdescr.getSuperTypes().clear();
            }

            for (TypeFieldDescr field : tdescr.getFields().values()) {
                QualifiedName typeName = new QualifiedName(field.getPattern().getObjectType());
                if (!hasCircularDependency(name, typeName, taxonomy)) {
                    supers.add(typeName);
                }

            }

        }
        List<QualifiedName> sorted = new HierarchySorter<QualifiedName>().sort(taxonomy);
        ArrayList list = new ArrayList(sorted.size());
        for (QualifiedName name : sorted) {
            list.add(cache.get(name));
        }

        return list;
    }

    private static boolean hasCircularDependency(QualifiedName name,
                                                 QualifiedName typeName,
                                                 Map<QualifiedName, Collection<QualifiedName>> taxonomy) {
        if (name.equals(typeName)) {
            return true;
        }
        if (taxonomy.containsKey(typeName)) {
            Collection<QualifiedName> parents = taxonomy.get(typeName);
            if (parents.contains(name)) {
                return true;
            } else {
                for (QualifiedName ancestor : parents) {
                    if (hasCircularDependency(name, ancestor, taxonomy)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Tries to determine whether a given annotation is properly defined using a
     * java.lang.Annotation and can be resolved
     *
     * Proper annotations will be wired to dynamically generated beans
     */
    private Class resolveAnnotation(String annotation,
                                    TypeResolver resolver) {

        // do not waste time with @format
        if (TypeDeclaration.Format.ID.equals(annotation)) {
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

    /**
     * Sorts a bean's fields according to the positional index metadata. The
     * order is as follows (i) as defined using the @position metadata (ii) as
     * resulting from the inspection of an external java superclass, if
     * applicable (iii) in declaration order, superclasses first
     */
    private PriorityQueue<FieldDefinition> sortFields(Map<String, TypeFieldDescr> flds, PackageRegistry pkgRegistry) {
        PriorityQueue<FieldDefinition> queue = new PriorityQueue<FieldDefinition>(flds.size());
        int maxDeclaredPos = 0;
        int curr = 0;

        BitSet occupiedPositions = new BitSet(flds.size());
        for (TypeFieldDescr field : flds.values()) {
            int pos = field.getIndex();
            if (pos >= 0) {
                occupiedPositions.set(pos);
            }
            maxDeclaredPos = Math.max(maxDeclaredPos, pos);
        }

        for (TypeFieldDescr field : flds.values()) {

            try {
                String typeName = field.getPattern().getObjectType();
                String typeNameKey = typeName;

                int arrayIndex = typeName.indexOf( "[" );
                if ( arrayIndex >= 0 ) {
                    typeNameKey = typeName.substring( 0, arrayIndex );
                }

                String fullFieldType = generatedTypes.contains( typeNameKey ) ? BuildUtils.resolveDeclaredType(typeName) : pkgRegistry.getTypeResolver().resolveType(typeName).getName();

                FieldDefinition fieldDef = new FieldDefinition(field.getFieldName(),
                                                               fullFieldType);
                // field is marked as PK
                boolean isKey = field.getAnnotation(TypeDeclaration.ATTR_KEY) != null;
                fieldDef.setKey(isKey);

                fieldDef.setDeclIndex(field.getIndex());
                if (field.getIndex() < 0) {
                    int freePos = occupiedPositions.nextClearBit(0);
                    if (freePos < maxDeclaredPos) {
                        occupiedPositions.set(freePos);
                    } else {
                        freePos = maxDeclaredPos + 1;
                    }
                    fieldDef.setPriority(freePos * 256 + curr++);
                } else {
                    fieldDef.setPriority(field.getIndex() * 256 + curr++);
                }
                fieldDef.setInherited(field.isInherited());
                fieldDef.setInitExpr(field.getInitExpr());

                for (String annotationName : field.getAnnotationNames()) {
                    Class annotation = resolveAnnotation(annotationName,
                                                         pkgRegistry.getTypeResolver());
                    if (annotation != null && annotation.isAnnotation()) {
                        try {
                            AnnotationDefinition annotationDefinition = AnnotationDefinition.build(annotation,
                                                                                                   field.getAnnotations().get(annotationName).getValueMap(),
                                                                                                   pkgRegistry.getTypeResolver());
                            fieldDef.addAnnotation(annotationDefinition);
                        } catch (NoSuchMethodException nsme) {
                            kbuilder.addBuilderResult(new TypeDeclarationError(field,
                                                                              "Annotated field " + field.getFieldName() +
                                                                              "  - undefined property in @annotation " +
                                                                              annotationName + ": " + nsme.getMessage() + ";"));
                        }
                    }
                    if (annotation == null || annotation == Key.class || annotation == Position.class) {
                        fieldDef.addMetaData(annotationName, field.getAnnotation(annotationName).getSingleValue());
                    }
                }

                queue.add(fieldDef);
            } catch (ClassNotFoundException cnfe) {
                kbuilder.addBuilderResult(new TypeDeclarationError(field, cnfe.getMessage()));
            }

        }

        return queue;
    }

    void registerGeneratedType(AbstractClassTypeDeclarationDescr typeDescr) {
        String fullName = typeDescr.getType().getFullName();
        generatedTypes.add(fullName);
    }

    /**
     * Generates a bean, and adds it to the composite class loader that
     * everything is using.
     */
    private void generateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr,
                                      TypeDeclaration type,
                                      PackageRegistry pkgRegistry,
                                      List<TypeDefinition> unresolvedTypeDefinitions) {

        // extracts type, supertype and interfaces
        String fullName = typeDescr.getType().getFullName();

        if (type.getKind().equals(TypeDeclaration.Kind.CLASS)) {
            TypeDeclarationDescr tdescr = (TypeDeclarationDescr) typeDescr;
            if (tdescr.getSuperTypes().size() > 1) {
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "Declared class " + fullName + "  - has more than one supertype;"));
                return;
            } else if (tdescr.getSuperTypes().isEmpty()) {
                tdescr.addSuperType("java.lang.Object");
            }
        }

        AnnotationDescr traitableAnn = typeDescr.getAnnotation(Traitable.class.getSimpleName());
        boolean traitable = traitableAnn != null;

        String[] fullSuperTypes = new String[typeDescr.getSuperTypes().size() + 1];
        int j = 0;
        for (QualifiedName qname : typeDescr.getSuperTypes()) {
            fullSuperTypes[j++] = qname.getFullName();
        }
        fullSuperTypes[j] = Thing.class.getName();

        List<String> interfaceList = new ArrayList<String>();
        interfaceList.add(traitable ? Externalizable.class.getName() : Serializable.class.getName());
        if (traitable) {
            interfaceList.add(TraitableBean.class.getName());
        }
        String[] interfaces = interfaceList.toArray(new String[interfaceList.size()]);

        // prepares a class definition
        ClassDefinition def;
        switch (type.getKind()) {
            case TRAIT:
                def = new ClassDefinition(fullName,
                                          "java.lang.Object",
                                          fullSuperTypes);
                break;
            case ENUM:
                def = new EnumClassDefinition(fullName,
                                              fullSuperTypes[0],
                                              null);
                break;
            case CLASS:
            default:
                def = new ClassDefinition(fullName,
                                          fullSuperTypes[0],
                                          interfaces);
                def.setTraitable(traitable, traitableAnn != null &&
                                            traitableAnn.getValue("logical") != null &&
                                            Boolean.valueOf(traitableAnn.getValue("logical")));
        }

        for (String annotationName : typeDescr.getAnnotationNames()) {
            Class annotation = resolveAnnotation(annotationName,
                                                 pkgRegistry.getTypeResolver());
            if (annotation != null && annotation.isAnnotation()) {
                try {
                    AnnotationDefinition annotationDefinition = AnnotationDefinition.build(annotation,
                                                                                           typeDescr.getAnnotations().get(annotationName).getValueMap(),
                                                                                           pkgRegistry.getTypeResolver());
                    def.addAnnotation(annotationDefinition);
                } catch (NoSuchMethodException nsme) {
                    kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                      "Annotated type " + fullName +
                                                                      "  - undefined property in @annotation " +
                                                                      annotationName + ": " +
                                                                      nsme.getMessage() + ";"));
                }
            }
            if (annotation == null || annotation == Role.class) {
                def.addMetaData(annotationName, typeDescr.getAnnotation(annotationName).getSingleValue());
            }
        }

        // add enum literals, if appropriate
        if (type.getKind() == TypeDeclaration.Kind.ENUM) {
            for (EnumLiteralDescr lit : ((EnumDeclarationDescr) typeDescr).getLiterals()) {
                ((EnumClassDefinition) def).addLiteral(
                        new EnumLiteralDefinition(lit.getName(), lit.getConstructorArgs())
                                                      );
            }
        }

        // fields definitions are created. will be used by subclasses, if any.
        // Fields are SORTED in the process
        if (!typeDescr.getFields().isEmpty()) {
            PriorityQueue<FieldDefinition> fieldDefs = sortFields(typeDescr.getFields(),
                                                                  pkgRegistry);
            int n = fieldDefs.size();
            for (int k = 0; k < n; k++) {
                FieldDefinition fld = fieldDefs.poll();
                if (unresolvedTypeDefinitions != null) {
                    for (TypeDefinition typeDef : unresolvedTypeDefinitions) {
                        if (fld.getTypeName().equals(typeDef.getTypeClassName())) {
                            fld.setRecursive(true);
                            break;
                        }
                    }
                }
                fld.setIndex(k);
                def.addField(fld);
            }
        }

        // check whether it is necessary to build the class or not
        Class<?> existingDeclarationClass = getExistingDeclarationClass(typeDescr);
        type.setNovel(existingDeclarationClass == null);

        // attach the class definition, it will be completed later
        type.setTypeClassDef(def);

        //if is not new, search the already existing declaration and
        //compare them o see if they are at least compatibles
        if (!type.isNovel()) {
            TypeDeclaration previousTypeDeclaration = kbuilder.getPackageRegistry(typeDescr.getNamespace()).getPackage().getTypeDeclaration(typeDescr.getTypeName());

            try {

                if ( type.isNovel() ) {
                    //since the declaration defines one or more fields, it is a DEFINITION
                    type.setNature(TypeDeclaration.Nature.DEFINITION);
                } else {
                    //The declaration doesn't define any field, it is a DECLARATION
                    type.setNature(TypeDeclaration.Nature.DECLARATION);
                }

                //if there is no previous declaration, then the original declaration was a POJO
                //to the behavior previous these changes
                if (previousTypeDeclaration == null) {
                    // new declarations of a POJO can't declare new fields,
                    // except if the POJO was previously generated/compiled and saved into the kjar
                    if ( !kbuilder.getBuilderConfiguration().isPreCompiled() &&
                         !GeneratedFact.class.isAssignableFrom(existingDeclarationClass) &&
                         !type.getTypeClassDef().getFields().isEmpty()
                    ) {
                        try {
                            Class existingClass = pkgRegistry.getPackage().getTypeResolver().resolveType( typeDescr.getType().getFullName() );
                            ClassFieldInspector cfi = new ClassFieldInspector( existingClass );

                            int fieldCount = 0;
                            for ( String existingFieldName : cfi.getFieldTypesField().keySet() ) {
                                if ( ! cfi.isNonGetter( existingFieldName ) && ! "class".equals( existingFieldName ) && cfi.getSetterMethods().containsKey( existingFieldName ) ) {
                                    if ( ! typeDescr.getFields().containsKey( existingFieldName ) ) {
                                        type.setValid(false);
                                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName() +
                                                                                                    " does not include field " + existingFieldName ) );
                                    } else {
                                        String fldType = cfi.getFieldTypes().get( existingFieldName ).getName();
                                        TypeFieldDescr declaredField = typeDescr.getFields().get( existingFieldName );
                                        if ( ! fldType.equals( type.getTypeClassDef().getField( existingFieldName ).getTypeName() ) ) {
                                            type.setValid(false);
                                            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName() +
                                                                                                         " redeclared field " + existingFieldName + " : \n" +
                                                                                                         "existing : " + fldType + " vs declared : " + declaredField.getPattern().getObjectType() ) );
                                        } else {
                                            fieldCount++;
                                        }

                                    }
                                }
                            }

                            if ( fieldCount != typeDescr.getFields().size() ) {
                                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, "New declaration of "+typeDescr.getType().getFullName()
                                                                                             +" can't declaredeclares a different set of fields \n" +
                                                                                             "existing : " + cfi.getFieldTypesField() + "\n" +
                                                                                             "declared : " + typeDescr.getFields() ));

                            }
                        } catch ( IOException e ) {
                            e.printStackTrace();
                            type.setValid(false);
                            kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr, "Unable to redeclare " + typeDescr.getType().getFullName() + " : " + e.getMessage() ) );
                        } catch ( ClassNotFoundException e ) {
                            type.setValid(false);
                            kbuilder.addBuilderResult( new TypeDeclarationError( typeDescr, "Unable to redeclare " + typeDescr.getType().getFullName() + " : " + e.getMessage() ) );
                        }
                    }
                } else {

                    int typeComparisonResult = this.compareTypeDeclarations(previousTypeDeclaration, type);

                    if (typeComparisonResult < 0) {
                        //oldDeclaration is "less" than newDeclaration -> error
                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, typeDescr.getType().getFullName()
                                                                             + " declares more fields than the already existing version"));
                        type.setValid(false);
                    } else if (typeComparisonResult > 0 && !type.getTypeClassDef().getFields().isEmpty()) {
                        //oldDeclaration is "grater" than newDeclaration -> error
                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, typeDescr.getType().getFullName()
                                                                                     + " declares less fields than the already existing version"));
                        type.setValid(false);
                    }

                    //if they are "equal" -> no problem

                    // in the case of a declaration, we need to copy all the
                    // fields present in the previous declaration
                    if (type.getNature() == TypeDeclaration.Nature.DECLARATION) {
                        mergeTypeDeclarations(previousTypeDeclaration, type);
                    }
                }

            } catch (IncompatibleClassChangeError error) {
                //if the types are incompatible -> error
                kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr, error.getMessage()));
            }

        } else {
            //if the declaration is novel, then it is a DEFINITION
            type.setNature(TypeDeclaration.Nature.DEFINITION);
        }

        generateDeclaredBean(typeDescr,
                             type,
                             pkgRegistry,
                             expandImportsInFieldInitExpr(def, pkgRegistry));
    }

    /**
     * Merges all the missing FactFields from oldDefinition into newDeclaration.
     */
    private void mergeTypeDeclarations(TypeDeclaration oldDeclaration,
                                       TypeDeclaration newDeclaration) {
        if (oldDeclaration == null) {
            return;
        }

        //add the missing fields (if any) to newDeclaration
        for (FieldDefinition oldFactField : oldDeclaration.getTypeClassDef().getFieldsDefinitions()) {
            FieldDefinition newFactField = newDeclaration.getTypeClassDef().getField(oldFactField.getName());
            if (newFactField == null) {
                newDeclaration.getTypeClassDef().addField(oldFactField);
            }
        }

        //copy the defined class
        newDeclaration.setTypeClass(oldDeclaration.getTypeClass());
    }

    private int compareTypeDeclarations(TypeDeclaration oldDeclaration,
                                        TypeDeclaration newDeclaration) throws IncompatibleClassChangeError {

        //different formats -> incompatible
        if (!oldDeclaration.getFormat().equals(newDeclaration.getFormat())) {
            throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                                                   + " format that its previous definition: " + newDeclaration.getFormat() + "!=" + oldDeclaration.getFormat());
        }

        //different superclasses -> Incompatible (TODO: check for hierarchy)
        if (!oldDeclaration.getTypeClassDef().getSuperClass().equals(newDeclaration.getTypeClassDef().getSuperClass())) {
            if (oldDeclaration.getNature() == TypeDeclaration.Nature.DEFINITION
                && newDeclaration.getNature() == TypeDeclaration.Nature.DECLARATION
                && Object.class.getName().equals(newDeclaration.getTypeClassDef().getSuperClass())) {
                // actually do nothing. The new declaration just recalls the previous definition, probably to extend it.
            } else {
                throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                                                       + " superclass that its previous definition: " + newDeclaration.getTypeClassDef().getSuperClass()
                                                       + " != " + oldDeclaration.getTypeClassDef().getSuperClass());
            }
        }

        //different duration -> Incompatible
        if (!nullSafeEqualityComparison(oldDeclaration.getDurationAttribute(), newDeclaration.getDurationAttribute())) {
            throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " has a different"
                                                   + " duration: " + newDeclaration.getDurationAttribute()
                                                   + " != " + oldDeclaration.getDurationAttribute());
        }

        //        //different masks -> incompatible
        if (newDeclaration.getNature().equals(TypeDeclaration.Nature.DEFINITION)) {
            if (oldDeclaration.getSetMask() != newDeclaration.getSetMask()) {
                throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + " is incompatible with"
                                                       + " the previous definition: " + newDeclaration
                                                       + " != " + oldDeclaration);
            }
        }

        //TODO: further comparison?

        //Field comparison
        List<FactField> oldFields = oldDeclaration.getTypeClassDef().getFields();
        Map<String, FactField> newFieldsMap = new HashMap<String, FactField>();
        for (FactField factField : newDeclaration.getTypeClassDef().getFields()) {
            newFieldsMap.put(factField.getName(), factField);
        }

        //each of the fields in the old definition that are also present in the
        //new definition must have the same type. If not -> Incompatible
        boolean allFieldsInOldDeclarationAreStillPresent = true;
        for (FactField oldFactField : oldFields) {
            FactField newFactField = newFieldsMap.get(oldFactField.getName());

            if (newFactField != null) {
                //we can't use newFactField.getType() since it throws a NPE at this point.
                String newFactType = ((FieldDefinition) newFactField).getTypeName();

                if (!newFactType.equals( ((FieldDefinition) oldFactField).getTypeName())) {
                    throw new IncompatibleClassChangeError("Type Declaration " + newDeclaration.getTypeName() + "." + newFactField.getName() + " has a different"
                                                           + " type that its previous definition: " + newFactType
                                                           + " != " + oldFactField.getType().getCanonicalName());
                }
            } else {
                allFieldsInOldDeclarationAreStillPresent = false;
            }

        }

        //If the old declaration has less fields than the new declaration, oldDefinition < newDefinition
        if (oldFields.size() < newFieldsMap.size()) {
            return -1;
        }

        //If the old declaration has more fields than the new declaration, oldDefinition > newDefinition
        if (oldFields.size() > newFieldsMap.size()) {
            return 1;
        }

        //If the old declaration has the same fields as the new declaration,
        //and all the fieds present in the old declaration are also present in
        //the new declaration, then they are considered "equal", otherwise
        //they are incompatible
        if (allFieldsInOldDeclarationAreStillPresent) {
            return 0;
        }

        //Both declarations have the same number of fields, but not all the
        //fields in the old declaration are present in the new declaration.
        throw new IncompatibleClassChangeError(newDeclaration.getTypeName() + " introduces"
                                               + " fields that are not present in its previous version.");

    }

    private boolean nullSafeEqualityComparison(Comparable c1,
                                               Comparable c2) {
        if (c1 == null) {
            return c2 == null;
        }
        return c2 != null && c1.compareTo(c2) == 0;
    }

    private ClassDefinition expandImportsInFieldInitExpr(ClassDefinition def,
                                                         PackageRegistry pkgRegistry) {
        TypeResolver typeResolver = pkgRegistry.getPackage().getTypeResolver();
        for (FieldDefinition field : def.getFieldsDefinitions()) {
            field.setInitExpr(rewriteInitExprWithImports(field.getInitExpr(), typeResolver));
        }
        return def;
    }

    private String rewriteInitExprWithImports(String expr,
                                              TypeResolver typeResolver) {
        if (expr == null) {
            return null;
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
                    sb.append(getFullTypeName(type, typeResolver));
                }
            }
            if (!inTypeName) {
                sb.append(ch);
            }
        }
        if (inTypeName) {
            String type = expr.substring(typeStart);
            sb.append(getFullTypeName(type, typeResolver));
        }
        return sb.toString();
    }

    private String getFullTypeName(String type,
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

    private boolean isLiteralOrKeyword( String type ) {
        return "true".equals( type )
               || "false".equals( type )
               || "null".equals( type )
               || "new".equals( type );
    }

    private void generateDeclaredBean(AbstractClassTypeDeclarationDescr typeDescr,
                                      TypeDeclaration type,
                                      PackageRegistry pkgRegistry,
                                      ClassDefinition def) {

        if (typeDescr.getAnnotation(Traitable.class.getSimpleName()) != null
            || (!type.getKind().equals(TypeDeclaration.Kind.TRAIT) &&
                kbuilder.getPackageRegistry().containsKey(def.getSuperClass()) &&
                kbuilder.getPackageRegistry(def.getSuperClass()).getTraitRegistry().getTraitables().containsKey(def.getSuperClass())
        )) {
            if (!isNovelClass(typeDescr)) {
                try {
                    PackageRegistry reg = kbuilder.getPackageRegistry(typeDescr.getNamespace());
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType(availableName);
                    updateTraitDefinition(type,
                                          resolvedType);
                } catch (ClassNotFoundException cnfe) {
                    // we already know the class exists
                }
            }
            pkgRegistry.getTraitRegistry().addTraitable(def);
        } else if (type.getKind().equals(TypeDeclaration.Kind.TRAIT)
                   || typeDescr.getAnnotation(Trait.class.getSimpleName()) != null) {

            if (!type.isNovel()) {
                try {
                    PackageRegistry reg = kbuilder.getPackageRegistry(typeDescr.getNamespace());
                    String availableName = typeDescr.getType().getFullName();
                    Class<?> resolvedType = reg.getTypeResolver().resolveType(availableName);
                    if (!Thing.class.isAssignableFrom(resolvedType)) {
                        updateTraitDefinition(type,
                                              resolvedType);

                        String target = typeDescr.getTypeName() + TraitFactory.SUFFIX;
                        TypeDeclarationDescr tempDescr = new TypeDeclarationDescr();
                        tempDescr.setNamespace(typeDescr.getNamespace());
                        tempDescr.setFields(typeDescr.getFields());
                        tempDescr.setType(target,
                                          typeDescr.getNamespace());
                        tempDescr.addSuperType(typeDescr.getType());
                        TypeDeclaration tempDeclr = new TypeDeclaration(target);
                        tempDeclr.setKind(TypeDeclaration.Kind.TRAIT);
                        tempDeclr.setTypesafe(type.isTypesafe());
                        tempDeclr.setNovel(true);
                        tempDeclr.setTypeClassName(tempDescr.getType().getFullName());
                        tempDeclr.setResource(type.getResource());

                        ClassDefinition tempDef = new ClassDefinition(target);
                        tempDef.setClassName(tempDescr.getType().getFullName());
                        tempDef.setTraitable(false);
                        for (FieldDefinition fld : def.getFieldsDefinitions()) {
                            tempDef.addField(fld);
                        }
                        tempDef.setInterfaces(def.getInterfaces());
                        tempDef.setSuperClass(def.getClassName());
                        tempDef.setDefinedClass(resolvedType);
                        tempDef.setAbstrakt(true);
                        tempDeclr.setTypeClassDef(tempDef);

                        type.setKind(TypeDeclaration.Kind.CLASS);

                        generateDeclaredBean(tempDescr,
                                             tempDeclr,
                                             pkgRegistry,
                                             tempDef);
                        try {
                            Class<?> clazz = pkgRegistry.getTypeResolver().resolveType(tempDescr.getType().getFullName());
                            tempDeclr.setTypeClass(clazz);
                        } catch (ClassNotFoundException cnfe) {
                            kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                               "Internal Trait extension Class '" + target +
                                                                               "' could not be generated correctly'"));
                        } finally {
                            pkgRegistry.getPackage().addTypeDeclaration(tempDeclr);
                        }

                    } else {
                        updateTraitDefinition(type,
                                              resolvedType);
                        pkgRegistry.getTraitRegistry().addTrait(def);
                    }
                } catch (ClassNotFoundException cnfe) {
                    // we already know the class exists
                }
            } else {
                if (def.getClassName().endsWith(TraitFactory.SUFFIX)) {
                    pkgRegistry.getTraitRegistry().addTrait(def.getClassName().replace(TraitFactory.SUFFIX,
                                                                                       ""),
                                                            def);
                } else {
                    pkgRegistry.getTraitRegistry().addTrait(def);
                }
            }

        }

        if (type.isNovel()) {
            String fullName = typeDescr.getType().getFullName();
            JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) pkgRegistry.getDialectRuntimeRegistry().getDialectData("java");
            switch (type.getKind()) {
                case TRAIT:
                    try {
                        buildClass(def, fullName, dialect, kbuilder.getBuilderConfiguration().getClassBuilderFactory().getTraitBuilder());
                    } catch (Exception e) {
                        e.printStackTrace();
                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                           "Unable to compile declared trait " + fullName +
                                                                           ": " + e.getMessage() + ";"));
                    }
                    break;
                case ENUM:
                    try {
                        buildClass(def, fullName, dialect, kbuilder.getBuilderConfiguration().getClassBuilderFactory().getEnumClassBuilder());
                    } catch (Exception e) {
                        e.printStackTrace();
                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                           "Unable to compile declared enum " + fullName +
                                                                           ": " + e.getMessage() + ";"));
                    }
                    break;
                case CLASS:
                default:
                    try {
                        buildClass(def, fullName, dialect, kbuilder.getBuilderConfiguration().getClassBuilderFactory().getBeanClassBuilder());
                    } catch (Exception e) {
                        e.printStackTrace();
                        kbuilder.addBuilderResult(new TypeDeclarationError(typeDescr,
                                                                           "Unable to create a class for declared type " + fullName +
                                                                           ": " + e.getMessage() + ";"));
                    }
                    break;
            }

        }

    }

    /**
     * Checks whether a declaration is novel, or is a retagging of an external one
     */
    private boolean isNovelClass(AbstractClassTypeDeclarationDescr typeDescr) {
        return getExistingDeclarationClass(typeDescr) == null;
    }

    private Class<?> getExistingDeclarationClass(AbstractClassTypeDeclarationDescr typeDescr) {
        PackageRegistry reg = kbuilder.getPackageRegistry(typeDescr.getNamespace());
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

    private void updateTraitDefinition(TypeDeclaration type,
                                       Class concrete) {
        try {

            ClassFieldInspector inspector = new ClassFieldInspector(concrete);
            Map<String, Method> methods = inspector.getGetterMethods();
            Map<String, Method> setters = inspector.getSetterMethods();
            int j = 0;
            for (String fieldName : methods.keySet()) {
                if ("core".equals(fieldName) || "fields".equals(fieldName)) {
                    continue;
                }
                if (!inspector.isNonGetter(fieldName) && setters.keySet().contains(fieldName)) {

                    Class ret = methods.get(fieldName).getReturnType();
                    FieldDefinition field = new FieldDefinition();
                    field.setName(fieldName);
                    field.setTypeName(ret.getName());
                    field.setIndex(j++);
                    type.getTypeClassDef().addField(field);
                }
            }

            Set<String> interfaces = new HashSet<String>();
            Collections.addAll(interfaces, type.getTypeClassDef().getInterfaces());
            for ( Class iKlass : ClassUtils.getAllImplementedInterfaceNames( concrete ) ) {
                interfaces.add(iKlass.getName());
            }
            type.getTypeClassDef().setInterfaces(interfaces.toArray(new String[interfaces.size()]));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void buildClass(ClassDefinition def, String fullName, JavaDialectRuntimeData dialect, ClassBuilder cb) throws Exception {
        byte[] bytecode = cb.buildClass(def, kbuilder.getRootClassLoader());
        String resourceName = convertClassToResourcePath(fullName);
        dialect.putClassDefinition(resourceName, bytecode);
        if (kbuilder.getKnowledgeBase() != null) {
            kbuilder.getKnowledgeBase().registerAndLoadTypeDefinition(fullName, bytecode);
        } else {
            if (kbuilder.getRootClassLoader() instanceof ProjectClassLoader) {
                ((ProjectClassLoader) kbuilder.getRootClassLoader()).defineClass(fullName, resourceName, bytecode);
            } else {
                dialect.write(resourceName, bytecode);
            }
        }
    }

    private boolean isCompatible( Class<?> typeClass, AbstractClassTypeDeclarationDescr typeDescr ) {
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
}
