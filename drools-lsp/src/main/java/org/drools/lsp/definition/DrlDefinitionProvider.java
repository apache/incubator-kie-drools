/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.lsp.definition;

import java.util.ArrayList;
import java.util.List;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.TypeDeclarationDescr;
import org.drools.lsp.model.DrlDocumentModel;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

public class DrlDefinitionProvider {

    public List<Location> getDefinition(DrlDocumentModel model, Position position) {
        List<Location> locations = new ArrayList<>();
        PackageDescr pkg = model.getPackageDescr();
        if (pkg == null) {
            return locations;
        }

        String word = model.getWordAt(position.getLine(), position.getCharacter());
        if (word.isEmpty()) {
            return locations;
        }

        findRuleDefinition(pkg, word, model.getUri(), locations);
        findTypeDeclarationDefinition(pkg, word, model.getUri(), locations);
        findGlobalDefinition(pkg, word, model.getUri(), locations);
        findFunctionDefinition(pkg, word, model.getUri(), locations);
        findImportDefinition(pkg, word, model.getUri(), locations);

        return locations;
    }

    private void findRuleDefinition(PackageDescr pkg, String word, String uri, List<Location> locations) {
        for (RuleDescr rule : pkg.getRules()) {
            if (word.equals(rule.getName())) {
                locations.add(toLocation(uri, rule));
                return;
            }
        }
    }

    private void findTypeDeclarationDefinition(PackageDescr pkg, String word, String uri, List<Location> locations) {
        for (TypeDeclarationDescr typeDecl : pkg.getTypeDeclarations()) {
            if (word.equals(typeDecl.getTypeName())) {
                locations.add(toLocation(uri, typeDecl));
                return;
            }
        }
    }

    private void findGlobalDefinition(PackageDescr pkg, String word, String uri, List<Location> locations) {
        for (GlobalDescr global : pkg.getGlobals()) {
            if (word.equals(global.getIdentifier())) {
                locations.add(toLocation(uri, global));
                return;
            }
        }
    }

    private void findFunctionDefinition(PackageDescr pkg, String word, String uri, List<Location> locations) {
        for (FunctionDescr func : pkg.getFunctions()) {
            if (word.equals(func.getName())) {
                locations.add(toLocation(uri, func));
                return;
            }
        }
    }

    private void findImportDefinition(PackageDescr pkg, String word, String uri, List<Location> locations) {
        for (ImportDescr imp : pkg.getImports()) {
            String target = imp.getTarget();
            String simpleName = target.contains(".") ? target.substring(target.lastIndexOf('.') + 1) : target;
            if (matchesName(word, simpleName) || matchesName(word, target)) {
                locations.add(toLocation(uri, imp));
                return;
            }
        }
    }

    private static boolean matchesName(String word, String name) {
        if (word.equals(name)) {
            return true;
        }
        String wordSimple = word.contains(".") ? word.substring(word.lastIndexOf('.') + 1) : word;
        return wordSimple.equals(name);
    }

    private Location toLocation(String uri, BaseDescr descr) {
        int startLine = Math.max(0, descr.getLine() - 1);
        int startCol = Math.max(0, descr.getColumn());
        int endLine = descr.getEndLine() > 0 ? descr.getEndLine() - 1 : startLine;
        int endCol = descr.getEndColumn() > 0 ? descr.getEndColumn() : startCol;

        Position start = new Position(startLine, startCol);
        Position end = new Position(endLine, endCol);
        return new Location(uri, new Range(start, end));
    }
}
