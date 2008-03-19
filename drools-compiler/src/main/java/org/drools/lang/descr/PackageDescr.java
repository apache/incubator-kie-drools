package org.drools.lang.descr;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;

public class PackageDescr extends BaseDescr {
    /**
     *
     */
    private static final long serialVersionUID = 400L;
    private String      name;
    private String      documentation;

    private List              imports          = Collections.EMPTY_LIST;
    private List              functionImports  = Collections.EMPTY_LIST;
    private List              attributes       = Collections.EMPTY_LIST;
    private List              globals          = Collections.EMPTY_LIST;
    private List              factTemplates    = Collections.EMPTY_LIST;
    private List              functions        = Collections.EMPTY_LIST;
    private List              rules            = Collections.EMPTY_LIST;
    private List<TypeDeclarationDescr> typeDeclarations     = Collections.emptyList();

    public PackageDescr() {
    }

    public PackageDescr(final String name) {
        this( name,
              "" );
    }

    public PackageDescr(final String name,
                        final String documentation) {
        this.name = name;
        this.documentation = documentation;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        name    = (String)in.readObject();
        documentation   = (String)in.readObject();
        imports    = (List)in.readObject();
        functionImports    = (List)in.readObject();
        attributes    = (List)in.readObject();
        globals    = (List)in.readObject();
        factTemplates    = (List)in.readObject();
        functions    = (List)in.readObject();
        rules    = (List)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(name);
        out.writeObject(documentation);
        out.writeObject(imports);
        out.writeObject(functionImports);
        out.writeObject(attributes);
        out.writeObject(globals);
        out.writeObject(factTemplates);
        out.writeObject(functions);
        out.writeObject(rules);
    }

    public String getName() {
        return this.name;
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public void addImport(final ImportDescr importEntry) {
        if ( this.imports == Collections.EMPTY_LIST ) {
            this.imports = new ArrayList();
        }
        this.imports.add( importEntry );
    }

    public List getImports() {
        return this.imports;
    }

    public void addFunctionImport(final FunctionImportDescr importFunction) {
        if ( this.functionImports == Collections.EMPTY_LIST ) {
            this.functionImports = new ArrayList();
        }
        this.functionImports.add( importFunction );
    }

    public List getFunctionImports() {
        return this.functionImports;
    }

    public void addGlobal(final GlobalDescr global) {
        if ( this.globals == Collections.EMPTY_LIST ) {
            this.globals = new ArrayList();
        }
        this.globals.add( global );
    }

    public List getGlobals() {
        return this.globals;
    }

    public void addAttribute(final AttributeDescr attribute) {
        if ( this.attributes == Collections.EMPTY_LIST ) {
            this.attributes = new ArrayList();
        }
        this.attributes.add( attribute );
    }

    public List getAttributes() {
        return this.attributes;
    }

    public void addFactTemplate(final FactTemplateDescr factTemplate) {
        if ( this.factTemplates == Collections.EMPTY_LIST ) {
            this.factTemplates = new ArrayList( 1 );
        }
        this.factTemplates.add( factTemplate );
    }

    public List getFactTemplates() {
        return this.factTemplates;
    }

    public void addFunction(final FunctionDescr function) {
        if ( this.functions == Collections.EMPTY_LIST ) {
            this.functions = new ArrayList( 1 );
        }
        this.functions.add( function );
    }

    public List getFunctions() {
        return this.functions;
    }

    public void addRule(final RuleDescr rule) {
        if ( this.rules == Collections.EMPTY_LIST ) {
            this.rules = new ArrayList( 1 );
        }
        for ( Iterator iter = attributes.iterator(); iter.hasNext(); ) {
            AttributeDescr at = (AttributeDescr) iter.next();
            boolean overridden = false;
            //check for attr in rule
            for ( Iterator iterator = rule.getAttributes().iterator(); iterator.hasNext(); ) {
                AttributeDescr ruleAt = (AttributeDescr) iterator.next();
                if (ruleAt.getName().equals( at.getName() )) {
                    overridden = true;
                }
            }
            if (!overridden) {
                rule.addAttribute( at );
            }
        }
        this.rules.add( rule );
    }

    public List getRules() {
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