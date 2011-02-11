/**
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

package org.drools.verifier.visitor;

import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.jarloader.PackageHeaderLoader;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarInputStream;

/**
 * @author Toni Rikkola
 */
public class PackageDescrVisitor {

    private final VerifierData data;
    private List<JarInputStream> jars = null;

    private RulePackage rulePackage;

    public PackageDescrVisitor(VerifierData data,
                               List<JarInputStream> jars) {
        this.data = data;
        this.jars = jars;
    }

    public void visitPackageDescr(PackageDescr descr) throws UnknownDescriptionException, ClassNotFoundException, IOException {
        rulePackage = data.getPackageByName(descr.getName());

        if (rulePackage == null) {
            rulePackage = new RulePackage();

            rulePackage.setName(descr.getName());
            data.add(rulePackage);
        }

        visitImports(descr.getImports());

        TypeDeclarationDescrVisitor typeDeclarationDescrVisitor = new TypeDeclarationDescrVisitor(data);
        typeDeclarationDescrVisitor.visit(descr.getTypeDeclarations());

        visitRules(descr.getRules());
    }

    private void visitImports(List<ImportDescr> importDescrs) throws IOException, ClassNotFoundException {

        HashSet<String> imports = new HashSet<String>();
        for (ImportDescr i : importDescrs) {
            String fullName = i.getTarget();
            String name = fullName.substring(fullName.lastIndexOf(".") + 1);

            imports.add(fullName);

            Import objectImport = new Import(rulePackage);
            objectImport.setName(fullName);
            objectImport.setShortName(name);
            data.add(objectImport);

            ObjectType objectType = this.data.getObjectTypeByFullName(fullName);

            if (objectType == null) {
                objectType = new ObjectType();
            }

            objectType.setName(name);
            objectType.setFullName(fullName);
            data.add(objectType);
        }

        PackageHeaderLoader packageHeaderLoader = new PackageHeaderLoader(imports, jars);

        for (String factTypeName : packageHeaderLoader.getClassNames()) {
            String name = factTypeName.substring(factTypeName.lastIndexOf(".") + 1);
            Collection<String> fieldNames = packageHeaderLoader.getFieldNames(factTypeName);
            for (String fieldName : fieldNames) {
                ObjectType objectType = this.data.getObjectTypeByObjectTypeNameAndPackageName(name, rulePackage.getName());

                Field field = data.getFieldByObjectTypeAndFieldName(objectType.getFullName(), fieldName);
                if (field == null) {
                    field = ObjectTypeFactory.createField(fieldName, objectType);
                    field.setFieldType(packageHeaderLoader.getFieldType(objectType.getName(), fieldName));
                    data.add(field);
                }
            }
        }
    }

    private void visitRules(List<RuleDescr> rules) throws UnknownDescriptionException {
        for (RuleDescr ruleDescr : rules) {
            visitRuleDescr(ruleDescr);
        }
    }

    private void visitRuleDescr(RuleDescr descr) throws UnknownDescriptionException {
        RuleDescrVisitor visitor = new RuleDescrVisitor(data,
                rulePackage);
        visitor.visitRuleDescr(descr);
    }

}
