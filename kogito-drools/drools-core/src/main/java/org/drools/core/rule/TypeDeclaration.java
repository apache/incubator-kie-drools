/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.rule;

import org.drools.core.base.ClassObjectType;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateObjectType;
import org.drools.core.spi.AcceptsReadAccessor;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.ObjectType;
import org.drools.core.util.ClassUtils;
import org.kie.api.io.Resource;
import org.kie.internal.definition.KnowledgeDefinition;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The type declaration class stores all type's metadata
 * declared in source files.
 */
public class TypeDeclaration
    implements
    KnowledgeDefinition,
    Externalizable,
    Comparable<TypeDeclaration> {

    public static final int ROLE_BIT                    = 1;
    public static final int TYPESAFE_BIT                = 2;
    public static final int FORMAT_BIT                  = 4;
    public static final int KIND_BIT                    = 8;
    public static final int NATURE_BIT                  = 16;


    public static final String ATTR_CLASS               = "class";
    public static final String ATTR_TYPESAFE            = "typesafe";
    public static final String ATTR_DURATION            = "duration";
    public static final String ATTR_TIMESTAMP           = "timestamp";
    public static final String ATTR_EXPIRE              = "expires";
    public static final String ATTR_KEY                 = "key";
    public static final String ATTR_FIELD_POSITION      = "position";
    public static final String ATTR_PROP_CHANGE_SUPPORT = "propertyChangeSupport";
    public static final String ATTR_PROP_SPECIFIC       = "propertyReactive";
    public static final String ATTR_NOT_PROP_SPECIFIC   = "classReactive";

    public int setMask                                  = 0;

    public static enum Kind {
        CLASS, TRAIT, ENUM;

        public static final String ID = "kind";

        public static Kind parseKind( String kind ) {
            if ( "class".equalsIgnoreCase( kind ) ) {
                return CLASS;
            } else if ( "trait".equalsIgnoreCase( kind ) ) {
                return TRAIT;
            }else if ( "enum".equalsIgnoreCase( kind ) ) {
                return ENUM;
            }
            return null;
        }
    }

    public static enum Role {
        FACT, EVENT;

        public static final String ID = "role";

        public static Role parseRole(String role) {
            if ( "event".equalsIgnoreCase( role ) ) {
                return EVENT;
            } else if ( "fact".equalsIgnoreCase( role ) ) {
                return FACT;
            }
            return null;
        }
    }

    public static enum Format {
        POJO, TEMPLATE;

        public static final String ID = "format";

        public static Format parseFormat(String format) {
            if ( "pojo".equalsIgnoreCase( format ) ) {
                return POJO;
            } else if ( "template".equalsIgnoreCase( format ) ) {
                return TEMPLATE;
            }
            return null;
        }
    }

    public static enum Nature {
        /**
         * A DECLARATION is a Type Declaration that does not contain any
         * field definition and that is just used to add meta-data to an
         * DEFINITION.
         * A DEFINITION of an exiting DEFINITION is also considered a DECLARATION
         */
        DECLARATION,
        /**
         * A DEFINITION is:
         *  1.- Type Declaration containing field definitions.
         *  2.- A DECLARATION with no previous DEFINITION
         */
        DEFINITION;

        public static final String ID = "nature";

        public static Nature parseNature(String nature) {
            if ( "declaration".equalsIgnoreCase( nature ) ) {
                return DECLARATION;
            } else if ( "definition".equalsIgnoreCase( nature ) ) {
                return DEFINITION;
            }
            return null;
        }
    }

    private String                 typeName;
    private Role                   role;
    private Format                 format;
    private Kind                   kind;
    private Nature                 nature;
    private String                 timestampAttribute;
    private String                 durationAttribute;
    private InternalReadAccessor   durationExtractor;
    private InternalReadAccessor   timestampExtractor;
    private transient Class< ? >   typeClass;
    private String                 typeClassName;
    private FactTemplate           typeTemplate;
    private ClassDefinition        typeClassDef;
    private Resource               resource;
    private boolean                dynamic;
    private boolean                typesafe;
    private boolean                novel;
    private boolean                valid;
    private boolean                propertyReactive;
    private boolean javaBased;
    private transient List<String> settableProprties;

    private transient ObjectType   objectType;
    private long                   expirationOffset = -1;

    private int                    order;
    private List<TypeDeclaration>  redeclarations;

    public TypeDeclaration() {

        role = Role.FACT;
        format = Format.POJO;
        kind = Kind.CLASS;
        nature = Nature.DECLARATION;

        this.valid =  true;

        addRedeclaration( this );
    }

    public TypeDeclaration( Class< ? > typeClass ) {
        this(typeClass.getSimpleName());
        setTypeClass(typeClass);
        javaBased = true;
        setTypeClassDef(new ClassDefinition(typeClass));
    }

    public TypeDeclaration( String typeName ) {
        this.typeName = typeName;

        role = Role.FACT;
        format = Format.POJO;
        kind = Kind.CLASS;
        nature = Nature.DECLARATION;

        this.durationAttribute = null;
        this.timestampAttribute = null;
        this.typeTemplate = null;
        this.typesafe =  true;
        this.valid =  true;

        addRedeclaration( this );
    }

    public void readExternal( ObjectInput in ) throws IOException,
                                                      ClassNotFoundException {
        this.typeName = (String) in.readObject();
        this.role = (Role) in.readObject();
        this.format = (Format) in.readObject();
        this.kind = (Kind) in.readObject();
        this.nature = (Nature) in.readObject();
        this.durationAttribute = (String) in.readObject();
        this.timestampAttribute = (String) in.readObject();
        this.typeClassName = (String) in.readObject();
        this.typeTemplate = (FactTemplate) in.readObject();
        this.typeClassDef = (ClassDefinition) in.readObject();
        this.durationExtractor = (InternalReadAccessor) in.readObject();
        this.timestampExtractor = (InternalReadAccessor) in.readObject();
        this.resource = (Resource) in.readObject();
        this.expirationOffset = in.readLong();
        this.dynamic = in.readBoolean();
        this.typesafe = in.readBoolean();
        this.propertyReactive = in.readBoolean();
        this.valid = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( typeName );
        out.writeObject( role );
        out.writeObject( format );
        out.writeObject( kind );
        out.writeObject( nature );
        out.writeObject( durationAttribute );
        out.writeObject( timestampAttribute );
        out.writeObject( typeClassName );
        out.writeObject( typeTemplate );
        out.writeObject( typeClassDef );
        out.writeObject( durationExtractor );
        out.writeObject( timestampExtractor );
        out.writeObject( this.resource );
        out.writeLong(expirationOffset);
        out.writeBoolean(dynamic);
        out.writeBoolean( typesafe );
        out.writeBoolean(propertyReactive);
        out.writeBoolean(valid);
    }

    public int getSetMask() {
        return this.setMask;
    }

    /**
     * @return the type
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the category
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role the category to set
     */
    public void setRole(Role role) {
        this.setMask = this.setMask | ROLE_BIT;
        this.role = role;
    }

    /**
     * @return the format
     */
    public Format getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(Format format) {
        this.setMask = this.setMask | FORMAT_BIT;
        this.format = format;
    }

    /**
     * @return the kind
     */
    public Kind getKind() {
        return kind;
    }

    /**
     * @param kind the kind to set
     */
    public void setKind(Kind kind) {
        this.setMask = this.setMask | KIND_BIT;
        this.kind = kind;
    }

    public Nature getNature() {
        return nature;
    }

    public void setNature( Nature nature ) {
        this.setMask = this.setMask | NATURE_BIT;
        this.nature = nature;
    }

    /**
     * @return the timestampAttribute
     */
    public String getTimestampAttribute() {
        return timestampAttribute;
    }

    /**
     * @param timestampAttribute the timestampAttribute to set
     */
    public void setTimestampAttribute(String timestampAttribute) {
        this.timestampAttribute = timestampAttribute;
    }

    /**
     * @return the durationAttribute
     */
    public String getDurationAttribute() {
        return durationAttribute;
    }

    /**
     * @param durationAttribute the durationAttribute to set
     */
    public void setDurationAttribute(String durationAttribute) {
        this.durationAttribute = durationAttribute;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return the typeClass
     */
    public Class< ? > getTypeClass() {
        return typeClass;
    }

    /**
     * @param typeClass the typeClass to set
     */
    public void setTypeClass(Class< ? > typeClass) {
        this.typeClass = typeClass;
        if ( this.typeClassDef != null ) {
            this.typeClassDef.setDefinedClass( this.typeClass );
        }
        if ( this.typeClass != null ) {
            this.typeClassName = this.typeClass.getName();
        }
    }

    /**
     * @return the typeTemplate
     */
    public FactTemplate getTypeTemplate() {
        return typeTemplate;
    }

    /**
     * @param typeTemplate the typeTemplate to set
     */
    public void setTypeTemplate(FactTemplate typeTemplate) {
        this.typeTemplate = typeTemplate;
    }

    /**
     * Returns true if the given parameter matches this type declaration
     *
     * @param clazz
     * @return
     */
    public boolean matches(Object clazz) {
        boolean matches = false;
        if ( clazz instanceof FactTemplate ) {
            matches = this.typeTemplate.equals( clazz );
        } else {
            matches = this.typeClass.isAssignableFrom( (Class< ? >) clazz );
        }
        return matches;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        TypeDeclaration other = (TypeDeclaration) obj;
        if ( typeName == null ) {
            if ( other.typeName != null ) return false;
        } else if ( !typeName.equals( other.typeName ) ) return false;
        return true;
    }

    public InternalReadAccessor getDurationExtractor() {
        return durationExtractor;
    }

    public void setDurationExtractor(InternalReadAccessor durationExtractor) {
        this.durationExtractor = durationExtractor;
    }

    /**
     * @return the typeClassDef
     */
    public ClassDefinition getTypeClassDef() {
        return typeClassDef;
    }

    /**
     * @param typeClassDef the typeClassDef to set
     */
    public void setTypeClassDef(ClassDefinition typeClassDef) {
        this.typeClassDef = typeClassDef;
    }

    public InternalReadAccessor getTimestampExtractor() {
        return timestampExtractor;
    }

    public void setTimestampExtractor(InternalReadAccessor timestampExtractor) {
        this.timestampExtractor = timestampExtractor;
    }

    public class DurationAccessorSetter
        implements
        AcceptsReadAccessor,
        Serializable {
        private static final long serialVersionUID = 510l;

        public void setReadAccessor(InternalReadAccessor readAccessor) {
            setDurationExtractor( readAccessor );
        }
    }

    public class TimestampAccessorSetter
        implements
        AcceptsReadAccessor,
        Serializable {
        private static final long serialVersionUID = 510l;

        public void setReadAccessor(InternalReadAccessor readAccessor) {
            setTimestampExtractor( readAccessor );
        }
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ObjectType getObjectType() {
        if ( this.objectType == null ) {
            if ( this.getFormat() == Format.POJO ) {
                this.objectType = new ClassObjectType( this.getTypeClass() );
            } else {
                this.objectType = new FactTemplateObjectType( this.getTypeTemplate() );
            }
        }
        return this.objectType;
    }

    public long getExpirationOffset() {
        return this.expirationOffset;
    }

    public void setExpirationOffset(final long expirationOffset) {
        this.expirationOffset = expirationOffset;
    }

    public String getTypeClassName() {
        return typeClassName;
    }

    public void setTypeClassName(String typeClassName) {
        this.typeClassName = typeClassName;
    }

    public boolean isJavaBased() {
        return javaBased;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isTypesafe() {
        return typesafe;
    }

    public void setTypesafe(boolean typesafe) {
        this.setMask = this.setMask | TYPESAFE_BIT;
        this.typesafe = typesafe;
    }

    public boolean isPropertyReactive() {
        return propertyReactive;
    }

    public void setPropertyReactive(boolean propertyReactive) {
        this.propertyReactive = propertyReactive;
    }

    public boolean isNovel() {
        return novel;
    }

    public void setNovel(boolean novel) {
        this.novel = novel;
    }

    public List<String> getSettableProperties() {
        if ( settableProprties == null ) {
            settableProprties = ClassUtils.getSettableProperties( getTypeClass() );
        }
        return settableProprties;
    }

    public void addRedeclaration( TypeDeclaration typeDeclaration ) {
        if ( redeclarations == null ) {
            redeclarations = new ArrayList<TypeDeclaration>();
        }
        redeclarations.add( typeDeclaration );
    }

    private List<TypeDeclaration> getRedeclarations( ) {
        return Collections.unmodifiableList( redeclarations );
    }


    public boolean removeRedeclaration( TypeDeclaration decl ) {
        if ( ! redeclarations.contains( decl ) ) {
            return false;
        } else {
            redeclarations.remove( decl );
            return true;
        }
    }

    public String toString() {
        return "TypeDeclaration{" +
                "typeName='" + typeName + '\'' +
                ", role=" + role +
                ", format=" + format +
                ", kind=" + kind +
                ", nature=" + nature +
                '}';
    }

    public KnowledgeType getKnowledgeType() {
        return KnowledgeType.TYPE;
    }

    public String getNamespace() {
        return this.typeClass != null ? this.typeClass.getPackage().getName() : "";
    }

    public String getFullName() {
        return getNamespace() + "." + getTypeName();
    }

    public String getId() {
        return getTypeName();
    }

    public int getOrder() {
        return order;
    }

    public void setOrder( int order ) {
        this.order = order;
    }

    @Override
    public int compareTo( TypeDeclaration o ) {
        return this.order - o.order;
    }

}
