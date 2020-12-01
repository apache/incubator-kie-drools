/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.evaluator.assembler.factories;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.io.internal.InternalResource;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.pmml.api.PMMLRuntimeFactory;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.io.FileUtils.getFile;

public class PMMLRuntimeFactoryImpl implements PMMLRuntimeFactory {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeFactoryImpl.class);
    private static  final KieServices KIE_SERVICES = KieServices.get();

    @Override
    public PMMLRuntime getPMMLRuntimeFromFile(File pmmlFile) {
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile);
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromClasspath(String pmmlFileName) {
        File pmmlFile = getPMMLFileFromClasspath(pmmlFileName);
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile);
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromKieContainerByKieBase( String kieBase, String pmmlFileName, String gav) {
        ReleaseId releaseId = new ReleaseIdImpl(gav);
        File pmmlFile = getPMMLFileFromKieContainerByKieBase(pmmlFileName, kieBase, releaseId);
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile, releaseId);
    }

    @Override
    public PMMLRuntime getPMMLRuntimeFromKieContainerByDefaultKieBase(String pmmlFileName, String gav) {
        ReleaseId releaseId = new ReleaseIdImpl(gav);
        File pmmlFile = getPMMLFileFromKieContainerByDefaultKieBase(pmmlFileName, releaseId);
        return PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile, releaseId);
    }

    /**
     * Load a <code>File</code> with the given <b>pmmlFileName</b> from the
     * current <code>Classloader</code>
     *
     * @param pmmlFileName
     * @return
     */
    private File getPMMLFileFromClasspath(final String pmmlFileName) {
        return getFile(pmmlFileName);
    }

    /**
     * Load a <code>File</code> with the given <b>pmmlFileName</b> from the <code>kjar</code> contained in the
     * <code>KieContainer</code> with the given <code>ReleaseId</code>
     *
     * @param pmmlFileName
     * @param kieBase the name of the Kiebase configured inside the <b>kmodule.xml</b> of the loaded <b>kjar</b>
     * @param releaseId
     * @return
     */
    private File getPMMLFileFromKieContainerByKieBase(final String pmmlFileName, final String kieBase, final ReleaseId releaseId) {
        KieContainerImpl kieContainer = (KieContainerImpl) KIE_SERVICES.newKieContainer(releaseId);
        InternalResource internalResource = ((InternalKieModule) (kieContainer)
                .getKieModuleForKBase(kieBase))
                .getResource(pmmlFileName);
        try(InputStream inputStream = internalResource.getInputStream()) {
            return getPMMLFile(pmmlFileName, inputStream);
        } catch(Exception e) {
            throw new ExternalException(e);
        }
    }

    /**
     * Load a <code>File</code> with the given <b>pmmlFileName</b> from the <code>kjar</code> contained in the
     * <code>KieContainer</code> with the given <code>ReleaseId</code>
     * It will use the <b>default</b> Kiebase defined inside the <b>kmodule.xml</b> of the loaded <b>kjar</b>
     *
     * @param pmmlFileName
     * @param releaseId
     * @return
     */
    private File getPMMLFileFromKieContainerByDefaultKieBase(final String pmmlFileName, final ReleaseId releaseId) {
        KieContainerImpl kieContainer = (KieContainerImpl) KIE_SERVICES.newKieContainer(releaseId);
        String defaultKieBase = kieContainer.getKieProject().getDefaultKieBaseModel().getName();
        return getPMMLFileFromKieContainerByKieBase(pmmlFileName, defaultKieBase, releaseId);
    }

    /**
     * Load a <code>File</code> with the given <b>fullFileName</b> from the given
     * <code>InputStream</code>
     *
     * @param fileName <b>full path</b> of file to load
     * @param inputStream
     * @return
     */
    private File getPMMLFile(String fileName, InputStream inputStream) {
        FileOutputStream outputStream = null;
        try  {
            File toReturn = File.createTempFile(fileName, null);
            outputStream = new FileOutputStream(toReturn);
            byte[] byteArray = new byte[1024];
            int i;
            while ((i = inputStream.read(byteArray)) > 0) {
                outputStream.write(byteArray, 0, i);
            }
            return toReturn;
        } catch(Exception e) {
            throw new ExternalException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                logger.warn("Failed to close outputStream", e);
            }
        }

    }
}
