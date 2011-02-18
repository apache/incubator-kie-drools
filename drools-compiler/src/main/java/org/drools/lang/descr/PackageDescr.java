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

package org.drools.lang.descr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.rule.Namespaceable;

public class PackageDescr extends BaseDescr
    implements
    Namespaceable {
    /**
     *
     */
    private static final long          serialVersionUID = 510l;
    private String                     namespace;
    private String                     documentation;

    private List<ImportDescr>          imports          = Collections.emptyList();
    private List<FunctionImportDescr>  functionImports  = Collections.emptyList();
    private List<AttributeDescr>       attributes       = Collections.emptyList();
    private List<GlobalDescr>          globals          = Collections.emptyList();
    private List<FactTemplateDescr>    factTemplates    = Collections.emptyList();
    private List<FunctionDescr>        functions        = Collections.emptyList();
    private List<RuleDescr>            rules            = Collections.emptyList();
    private List<TypeDeclarationDescr> typeDeclarations = Collections.emptyList();

    public PackageDescr() {
    }

    public PackageDescr(final String namespace) {
        this( namespace,
              "" );
    }

    public PackageDescr(final String namespace,
                        final String documentation) {
        this.namespace = namespace;
        this.documentation = documentation;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        namespace = (String) in.readUTF();
        documentation = (String) in.readUTF();
        imports = (List<ImportDescr>) in.readObject();
        functionImports = (List<FunctionImportDescr>) in.readObject();
        attributes = (List<AttributeDescr>) in.readObject();
        globals = (List<GlobalDescr>) in.readObject();
        factTemplates = (List<FactTemplateDescr>) in.readObject();
        functions = (List<FunctionDescr>) in.readObject();
        rules = (List<RuleDescr>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeUTF( namespace );
        out.writeUTF( documentation );
        out.writeObject( imports );
        out.writeObject( functionImports );
        out.writeObject( attributes );
        out.writeObject( globals );
        out.writeObject( factTemplates );
        out.writeObject( functions );
        out.writeObject( rules );
    }

    public String getNamespace() {
        return this.namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return this.namespace == null ? "" : this.namespace;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public void addImport(final ImportDescr importEntry) {
        if ( this.imports == Collections.EMPTY_LIST ) {
            this.imports = new ArrayList<ImportDescr>();
        }
        this.imports.add( importEntry );
    }

    public List<ImportDescr> getImports() {
        return this.imports;
    }

    public void addFunctionImport(final FunctionImportDescr importFunction) {
        if ( this.functionImports == Collections.EMPTY_LIST ) {
            this.functionImports = new ArrayList<FunctionImportDescr>();
        }
        this.functionImports.add( importFunction );
    }

    public List<FunctionImportDescr> getFunctionImports() {
        return this.functionImports;
    }

    public void addGlobal(final GlobalDescr global) {
        if ( this.globals == Collections.EMPTY_LIST ) {
            this.globals = new ArrayList<GlobalDescr>();
        }
        this.globals.add( global );
    }

    public List<GlobalDescr> getGlobals() {
        return this.globals;
    }

    public void addAttribute(final AttributeDescr attribute) {
        if ( this.attributes == Collections.EMPTY_LIST ) {
            this.attributes = new ArrayList<AttributeDescr>();
        }
        this.attributes.add( attribute );
    }

    public List<AttributeDescr> getAttributes() {
        return this.attributes;
    }
    
    public AttributeDescr getAttribute( String name ) {
        if( name != null ) {
            for( AttributeDescr attr : this.attributes ) {
                if( name.equals( attr.getName() ) ) {
                    return attr;
                }
            }
        }
        return null;
    }

    public void addFactTemplate(final FactTemplateDescr factTemplate) {
        if ( this.factTemplates == Collections.EMPTY_LIST ) {
            this.factTemplates = new ArrayList<FactTemplateDescr>( 1 );
        }
        this.factTemplates.add( factTemplate );
    }

    public List<FactTemplateDescr> getFactTemplates() {
        return this.factTemplates;
    }

    public void addFunction(final FunctionDescr function) {
        if ( this.functions == Collections.EMPTY_LIST ) {
            this.functions = new ArrayList<FunctionDescr>( 1 );
        }
        this.functions.add( function );
    }

    public List<FunctionDescr> getFunctions() {
        return this.functions;
    }

    public void addRule(final RuleDescr rule) {
        if ( this.rules == Collections.EMPTY_LIST ) {
            this.rules = new ArrayList<RuleDescr>( 1 );
        }
        for ( final AttributeDescr at : attributes ) {
            // check if rule overrides the attribute
            if ( !rule.getAttributes().containsKey( at.getName() ) ) {
                // if not, use default value
                rule.addAttribute( at );
            }
        }
        this.rules.add( rule );
    }

    public List<RuleDescr> getRules() {
        return this.rules;
    }

    public void addTypeDeclaration(TypeDeclarationDescr declaration) {
        if ( this.typeDeclarations == Collections.EMPTY_LIST ) {
            this.typeDeclarations = new ArrayList<TypeDeclarationDescr>();
        }
        this.typeDeclarations.add( declaration );
    }

    public List<TypeDeclarationDescr> getTypeDeclarations() {
        return this.typeDeclarations;
    }
}
