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
package org.drools.compiler.rule.builder.dialect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELAnalysisResult;
import org.drools.core.base.TypeResolver;
import org.kie.api.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDialect implements Dialect {

    private static final Logger log = LoggerFactory.getLogger(KnowledgeBuilderConfigurationImpl.class);

    protected PackageRegistry                packageRegistry;

    @Override
    public TypeResolver getTypeResolver() {
        return this.packageRegistry.getTypeResolver();
    }

    @Override
    public PackageRegistry getPackageRegistry() {
        return this.packageRegistry;
    }

    private void debug(Collection<String> refTypes) {
       StackTraceElement [] stack = Thread.currentThread().getStackTrace();
       for( StackTraceElement ste : stack ) {
          if( ste.getClassName().toLowerCase().endsWith("test") ) {
              log.warn(" --> " + ste.getClassName());
              break;
          }
       }
       for( String type : refTypes ) {
           log.warn(" >>- " + type );
       }
    }

    protected void addAnalysisResultReferences( String pkgName,  BaseDescr descr,  AnalysisResult analysisResult) {

        new Throwable("addAnalysisResultReferences").printStackTrace();

        // TODO: if local class, change name..
        // TODO: checks for weird classes? (anonymous?, array?, primitive?)
        List<String> referencedTypes = null;

        if( analysisResult instanceof MVELAnalysisResult ) {
            MVELAnalysisResult mvelAnalysis = (MVELAnalysisResult) analysisResult;

            for( Class<?> varClass : mvelAnalysis.getMvelVariables().values() ) {
                referencedTypes = addClassName(varClass, referencedTypes);
            }
            Class<?> returnType = mvelAnalysis.getReturnType();
            if( returnType != null ) {
                referencedTypes = addClassName(returnType, referencedTypes);
            }
        }

        BoundIdentifiers boundIds = analysisResult.getBoundIdentifiers();
        Class<?> analysisClass =  boundIds.getThisClass();
        if( analysisClass != null ) {
            referencedTypes = addClassName(analysisClass, referencedTypes);
        }
        Map<String, Class<?>> decls = boundIds.getDeclrClasses();
        if( decls != null ) {
           for( Class<?> cls  : decls.values() ) {
               referencedTypes = addClassName(cls, referencedTypes);
           }
        }
        Map<String, Class<?>> globals = boundIds.getGlobals();
        if( globals != null ) {
           for( Class<?> cls : globals.values() ) {
               referencedTypes = addClassName(cls, referencedTypes);
           }
        }

        Resource resource = descr.getResource();
        assert resource != null : descr.getClass().getSimpleName() + " instance has a null .resource field!";
        if( resource == null ) {
            log.error("Null resource for when collecting type references from " + descr.getClass().getSimpleName() );
        } else if( referencedTypes != null && ! referencedTypes.isEmpty() ) {
            this.packageRegistry.addTypeReferences(pkgName, resource, new HashSet<>(referencedTypes));
            debug(referencedTypes);
        }
    }

    private static List<String> addClassName(Class<?> cls, List<String> referencedTypes) {
       if( cls == null || cls.isAnonymousClass() || cls.isLocalClass() || cls.isPrimitive() ) {
           return referencedTypes;
       }
       if( cls.getProtectionDomain().getCodeSource() == null ) {
           return referencedTypes;
       }

       while( cls.isArray() ) {
           cls = cls.getComponentType();
       }

       if( referencedTypes == null ) {
           referencedTypes = new ArrayList<>(4);
       }
       if( cls.isMemberClass() ) {
           referencedTypes.add(cls.getEnclosingClass().getCanonicalName());
       }
       referencedTypes.add(cls.getCanonicalName());

       return referencedTypes;
    }

}
