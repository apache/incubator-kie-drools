/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.drools.compiler.builder.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.drools.compiler.compiler.PackageCompilationResult;
import org.drools.core.io.impl.ByteArrayResource;
import org.kie.api.io.Resource;
import org.kie.internal.builder.CompilationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to carry the results of compilation from the inner compiler classes to the "outer" API implemenation classes
 * In this case the results are the type references resolved during compilation and used for impact analysis.
 */
public class ProjectCompilationResult implements CompilationResult {

    private static final Logger log = LoggerFactory.getLogger(ProjectCompilationResult.class);

    private Map<String, Set<String>> resourceTypeReferencesMap;
    private volatile boolean added = false;

    public void addResourceTypeReferenceMapping( String pkgName, Resource resource, Collection<String> typeRefList ) {
        if( resource == null ) {
            // The TypeResolutionTest.noNullResources() test is basically to make sure that this never happens
            log.error("Null resource found in package " + pkgName);
        } else {
            String uniqueResourcePath = null;
            if( resource.getSourcePath() != null ) {
                uniqueResourcePath = resource.getSourcePath();
            } else {
                if( resource instanceof ByteArrayResource ) {
                    uniqueResourcePath = "ByteArray@" + System.identityHashCode(resource);
                }
                assert resource instanceof ByteArrayResource : "Unexpected resource type without a source path: " + resource.getClass().getName();
            }
            assert uniqueResourcePath != null : "Unable to create unique resource path for resource!";
            addResourceTypeReferenceMapping(uniqueResourcePath, typeRefList);
        }
    }

    public void addResourceTypeReferenceMapping( String uniqueResourcePath, Collection<String> typeRefList ) {
        Set<String> typeRefSet = internalGetResourceTypeReferencesMap().get(uniqueResourcePath);
        if( typeRefSet == null ) {
            typeRefSet = new HashSet<String>(typeRefList);
            resourceTypeReferencesMap.put(uniqueResourcePath, typeRefSet);
        } else {
            typeRefSet.addAll(typeRefList);
        }
    }

    private Map<String, Set<String>> internalGetResourceTypeReferencesMap() {
        if( this.resourceTypeReferencesMap == null ) {
            this.resourceTypeReferencesMap = new HashMap<String, Set<String>>(4);
        }
        return resourceTypeReferencesMap;
    }

    @Override
    public Map<String, Set<String>> getTypeReferences() {
        if( this.resourceTypeReferencesMap == null ) {
            return Collections.emptyMap();
        }

        return this.resourceTypeReferencesMap;
    }

    public void addTypeReferences( PackageCompilationResult compResult ) {
        ProjectCompilationResult result = (ProjectCompilationResult) compResult;
        if( ! result.added ) {
            result.added = true;
            if( result.resourceTypeReferencesMap == null ) {
                return;
            }

            for( Entry<String, Set<String>> entry : result.resourceTypeReferencesMap.entrySet() ) {
                internalGetResourceTypeReferencesMap().put(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public String toString() {
        if( resourceTypeReferencesMap == null || resourceTypeReferencesMap.isEmpty() ) {
            return "(" + this.added + ")";
        }
        StringBuilder out = new StringBuilder();
        for( Entry<String, Set<String>> entry : resourceTypeReferencesMap.entrySet() ) {
            out.append(entry.getKey() + " [");
            Iterator<String> iter = entry.getValue().iterator();
            if( ! entry.getValue().isEmpty() ) {
               out.append(iter.next());
            }
            while( iter.hasNext() ) {
               out.append(", " + iter.next());
            }
            out.append("] ");
        }
        out.append("(").append(this.added).append(")");
        return out.toString();
    }

}
