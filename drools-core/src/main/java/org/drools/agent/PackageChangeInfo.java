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

package org.drools.agent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.drools.rule.Package;

/**
 * Contains the changes to packages.
 * 
 * @author Toni Rikkola
 * 
 */
class PackageChangeInfo {

    private Collection<Package> changedPackages;
    private Collection<String> removedPackages;

    void addPackage(Package p) {

        if (changedPackages == null) {
            changedPackages = new ArrayList<Package>();
        }

        changedPackages.add(p);
    }

    void addRemovedPackages(Collection<String> removedPackages) {

        for (String name : removedPackages) {
            addRemovedPackage(name);
        }

    }

    Collection<Package> getChangedPackages() {
        if (changedPackages != null) {
            return changedPackages;
        } else {
            return Collections.emptyList();
        }
    }

    Collection<String> getRemovedPackages() {
        if (removedPackages != null) {
            return removedPackages;
        } else {
            return Collections.emptyList();
        }
    }

    void addRemovedPackage(String name) {

        if (removedPackages == null) {
            removedPackages = new ArrayList<String>();
        }

        removedPackages.add(name);
    }
}
