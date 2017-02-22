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

package org.drools.compiler.lang.descr;

import org.drools.core.rule.Namespaceable;
import org.kie.api.io.Resource;
import org.kie.internal.definition.KnowledgeDescr;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class PackageDescr extends BaseDescr
        implements
        Namespaceable,
        KnowledgeDescr {

    private static final long               serialVersionUID       = 530l;
    private String                          documentation;

    private UnitDescr                       unit;

    private List<ImportDescr>               imports                = Collections.emptyList();
    private List<FunctionImportDescr>       functionImports        = Collections.emptyList();
    private List<AccumulateImportDescr>     accumulateImports      = Collections.emptyList();
    private List<AttributeDescr>            attributes             = Collections.emptyList();
    private List<GlobalDescr>               globals                = Collections.emptyList();
    private List<FunctionDescr>             functions              = Collections.emptyList();
    private List<RuleDescr>                 rules                  = Collections.emptyList();
    private List<TypeDeclarationDescr>      typeDeclarations       = Collections.emptyList();
    private Set<EntryPointDeclarationDescr> entryPointDeclarations = Collections.emptySet();
    private Set<WindowDeclarationDescr>     windowDeclarations     = Collections.emptySet();
    private List<EnumDeclarationDescr>      enumDeclarations       = Collections.emptyList();

    public PackageDescr() {
        this("",
                "");
    }

    public PackageDescr(final String namespace) {
        this(namespace,
                "");
    }

    public PackageDescr(final String namespace,
            final String documentation) {
        setNamespace(namespace);
        this.documentation = documentation;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        documentation = in.readUTF();
        unit = (UnitDescr) in.readObject();
        imports = (List<ImportDescr>) in.readObject();
        functionImports = (List<FunctionImportDescr>) in.readObject();
        attributes = (List<AttributeDescr>) in.readObject();
        globals = (List<GlobalDescr>) in.readObject();
        functions = (List<FunctionDescr>) in.readObject();
        windowDeclarations = (Set<WindowDeclarationDescr>) in.readObject();
        rules = (List<RuleDescr>) in.readObject();
        entryPointDeclarations = (Set<EntryPointDeclarationDescr>) in.readObject();
        typeDeclarations = (List<TypeDeclarationDescr>) in.readObject();
        enumDeclarations = (List<EnumDeclarationDescr>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeUTF(documentation);
        out.writeObject(unit);
        out.writeObject(imports);
        out.writeObject(functionImports);
        out.writeObject(attributes);
        out.writeObject(globals);
        out.writeObject(functions);
        out.writeObject(windowDeclarations);
        out.writeObject(rules);
        out.writeObject(entryPointDeclarations);
        out.writeObject(typeDeclarations);
        out.writeObject(enumDeclarations);
    }

    public String getName() {
        return getNamespace() == null ? "" : getNamespace();
    }

    public void setName(String name) {
        setNamespace(name);
    }

    public String getDocumentation() {
        return this.documentation;
    }

    public void setUnit( UnitDescr unit ) {
        this.unit = unit;
    }

    public UnitDescr getUnit() {
        return unit;
    }

    public void addAllImports( final Collection<ImportDescr> importEntries ) {
        if (this.imports == Collections.EMPTY_LIST) {
            this.imports = new ArrayList<ImportDescr>();
        }
        this.imports.addAll(importEntries);
    }

    public void addImport(final ImportDescr importEntry) {
        if (this.imports == Collections.EMPTY_LIST) {
            this.imports = new ArrayList<ImportDescr>();
        }
        this.imports.add(importEntry);
    }

    public List<ImportDescr> getImports() {
        return this.imports;
    }

    public void addFunctionImport(final FunctionImportDescr importFunction) {
        if (this.functionImports == Collections.EMPTY_LIST) {
            this.functionImports = new ArrayList<FunctionImportDescr>();
        }
        this.functionImports.add(importFunction);
    }

    public List<FunctionImportDescr> getFunctionImports() {
        return this.functionImports;
    }

    public void addAccumulateImport(final AccumulateImportDescr importAccumulate) {
        if (this.accumulateImports == Collections.EMPTY_LIST) {
            this.accumulateImports = new ArrayList<AccumulateImportDescr>();
        }
        this.accumulateImports.add(importAccumulate);
    }

    public List<AccumulateImportDescr> getAccumulateImports() {
        return this.accumulateImports;
    }

    public void addGlobal(final GlobalDescr global) {
        if (this.globals == Collections.EMPTY_LIST) {
            this.globals = new ArrayList<GlobalDescr>();
        }
        this.globals.add(global);
    }

    public List<GlobalDescr> getGlobals() {
        return this.globals;
    }

    public void addAttribute(final AttributeDescr attribute) {
        if (this.attributes == Collections.EMPTY_LIST) {
            this.attributes = new ArrayList<AttributeDescr>();
        }
        this.attributes.add(attribute);
    }

    public List<AttributeDescr> getAttributes() {
        return this.attributes;
    }

    public AttributeDescr getAttribute(String name) {
        if (name != null) {
            for (AttributeDescr attr : this.attributes) {
                if (name.equals(attr.getName())) {
                    return attr;
                }
            }
        }
        return null;
    }

    public void addFunction(final FunctionDescr function) {
        if (this.functions == Collections.EMPTY_LIST) {
            this.functions = new ArrayList<FunctionDescr>(1);
        }
        this.functions.add(function);
    }

    public List<FunctionDescr> getFunctions() {
        return this.functions;
    }

    public void addRule(final RuleDescr rule) {
        if (this.rules == Collections.EMPTY_LIST) {
            this.rules = new ArrayList<RuleDescr>(1);
        }
        rule.setLoadOrder(rules.size());
        this.rules.add(rule);
    }

    public void afterRuleAdded(RuleDescr rule) {
        for (final AttributeDescr at : attributes) {
            // check if rule overrides the attribute
            if (!rule.getAttributes().containsKey(at.getName())) {
                // if not, use default value
                rule.addAttribute(at);
            }
        }
    }

    public List<RuleDescr> getRules() {
        return this.rules;
    }

    public void addTypeDeclaration(TypeDeclarationDescr declaration) {
        if (this.typeDeclarations == Collections.EMPTY_LIST) {
            this.typeDeclarations = new ArrayList<TypeDeclarationDescr>();
        }
        this.typeDeclarations.add(declaration);
    }

    public List<TypeDeclarationDescr> getTypeDeclarations() {
        return this.typeDeclarations;
    }

    public void addEntryPointDeclaration(EntryPointDeclarationDescr epDescr) {
        if (this.entryPointDeclarations == Collections.EMPTY_SET) {
            this.entryPointDeclarations = new HashSet<EntryPointDeclarationDescr>();
        }
        this.entryPointDeclarations.add(epDescr);
    }

    public Set<EntryPointDeclarationDescr> getEntryPointDeclarations() {
        return this.entryPointDeclarations;
    }

    public Set<WindowDeclarationDescr> getWindowDeclarations() {
        return this.windowDeclarations;
    }

    public void addWindowDeclaration(WindowDeclarationDescr window) {
        if (this.windowDeclarations == Collections.EMPTY_SET) {
            this.windowDeclarations = new HashSet<WindowDeclarationDescr>();
        }
        this.windowDeclarations.add(window);
    }

    public void addEnumDeclaration(EnumDeclarationDescr declaration) {
        if (this.enumDeclarations == Collections.EMPTY_LIST) {
            this.enumDeclarations = new ArrayList<EnumDeclarationDescr>();
        }
        this.enumDeclarations.add(declaration);
    }

    public List<EnumDeclarationDescr> getEnumDeclarations() {
        return this.enumDeclarations;
    }

    public List<AbstractClassTypeDeclarationDescr> getClassAndEnumDeclarationDescrs() {
        List<AbstractClassTypeDeclarationDescr> list = new ArrayList<AbstractClassTypeDeclarationDescr>(getEnumDeclarations());
        list.addAll(getTypeDeclarations());
        return Collections.unmodifiableList(list);
    }

    public void removeObjectsGeneratedFromResource(Resource resource) {
        removeObjectsGeneratedFromResource(resource, imports);
        removeObjectsGeneratedFromResource(resource, functionImports);
        removeObjectsGeneratedFromResource(resource, attributes);
        removeObjectsGeneratedFromResource(resource, globals);
        removeObjectsGeneratedFromResource(resource, functions);
        removeObjectsGeneratedFromResource(resource, rules);
        removeObjectsGeneratedFromResource(resource, typeDeclarations);
        removeObjectsGeneratedFromResource(resource, entryPointDeclarations);
        removeObjectsGeneratedFromResource(resource, windowDeclarations);
        removeObjectsGeneratedFromResource(resource, enumDeclarations);
    }

    private <T extends BaseDescr> void removeObjectsGeneratedFromResource(Resource resource, Collection<T> descrs) {
        Iterator<T> i = descrs.iterator();
        while (i.hasNext()) {
            if (resource.equals(i.next().getResource())) {
                i.remove();
            }
        }
    }
}
