/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formbuilder.server.task;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.BPMN2ProcessProvider;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.impl.ByteArrayResource;
import org.jbpm.formbuilder.server.GuvnorHelper;
import org.jbpm.formbuilder.server.xml.AssetDTO;
import org.jbpm.formbuilder.server.xml.PackageDTO;
import org.jbpm.formbuilder.server.xml.PackageListDTO;
import org.jbpm.formbuilder.shared.task.TaskDefinitionService;
import org.jbpm.formbuilder.shared.task.TaskRef;
import org.jbpm.formbuilder.shared.task.TaskServiceException;
import org.springframework.beans.factory.InitializingBean;

public class GuvnorTaskDefinitionService implements TaskDefinitionService, InitializingBean {
    
    private final TaskRepoHelper repo = new TaskRepoHelper();
    private final TaskDefinitionsSemanticModule module = new TaskDefinitionsSemanticModule(repo);
    private final BPMN2ProcessProvider provider = new BPMN2ProcessProvider() {
        @Override
        public void configurePackageBuilder(PackageBuilder packageBuilder) {
            PackageBuilderConfiguration conf = packageBuilder.getPackageBuilderConfiguration();
            if (conf.getSemanticModules().getSemanticModule(TaskDefinitionsSemanticModule.URI) == null) {
                conf.addSemanticModule(module);
            }
        }
    };

    private GuvnorHelper helper;
    private String baseUrl;
    private String user;
    private String password;
    
    public GuvnorTaskDefinitionService() {
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.helper = new GuvnorHelper(baseUrl, user, password);;
    }
    
    public void setHelper(GuvnorHelper helper) {
        this.helper = helper;
    }
    
    public GuvnorHelper getHelper() {
        return helper;
    }
    
    @Override
    public List<TaskRef> query(String pkgName, String filter) throws TaskServiceException {
        HttpClient client = helper.getHttpClient();
        GetMethod method = null;
        try {
            method = helper.createGetMethod(helper.getApiSearchUrl(pkgName));
            helper.setAuth(client, method);
            client.executeMethod(method);
            Properties props = new Properties();
            props.load(method.getResponseBodyAsStream());
            List<TaskRef> tasks = new ArrayList<TaskRef>();
            for (Object key : props.keySet()) {
                String assetId = key.toString();
                if (assetId.endsWith(ResourceType.BPMN2.getDefaultExtension()) || 
                        assetId.endsWith(ResourceType.DRF.getDefaultExtension()) ||
                        assetId.endsWith("bpmn2")) {
                    String content = getTaskDefinitionContent(pkgName, assetId);
                    if (content != null && !"".equals(content)) {
                        List<TaskRef> processTasks = getProcessTasks(content, assetId);
                        if (processTasks != null) {
                            for (TaskRef ref : processTasks) {
                                if (filter == null || "".equals(filter)) {
                                    tasks.add(ref);
                                } else if (ref.getProcessId().contains(assetId) || ref.getTaskName().contains(filter)) {
                                    tasks.add(ref);
                                }
                            }
                        }
                    }
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new TaskServiceException("Couldn't read task definitions", e);
        } catch (Exception e) {
            throw new TaskServiceException("Unexpected error", e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }
    }
    
    @Override
    public List<TaskRef> getTasksByName(String pkgName, String processId, String taskId) throws TaskServiceException {
        HttpClient client = helper.getHttpClient();
        List<TaskRef> retval = new ArrayList<TaskRef>();
        if (pkgName != null) {
            GetMethod call = helper.createGetMethod(helper.getRestBaseUrl());
            try {
                helper.setAuth(client, call);
                call.addRequestHeader("Accept", "application/xml");
                client.executeMethod(call);
                PackageListDTO dto = helper.jaxbTransformation(PackageListDTO.class, 
                        call.getResponseBodyAsStream(), 
                        PackageListDTO.RELATED_CLASSES);
                PackageDTO pkg = dto.getSelectedPackage(pkgName);
                List<String> urls = new ArrayList<String>();
                for (String url : pkg.getAssets()) {
                    GetMethod subCall = helper.createGetMethod(url);
                    try {
                        helper.setAuth(client, call);
                        subCall.addRequestHeader("Accept", "application/xml");
                        client.executeMethod(subCall);
                        AssetDTO subDto = helper.jaxbTransformation(AssetDTO.class, 
                                subCall.getResponseBodyAsStream(), 
                                AssetDTO.RELATED_CLASSES);
                        if (subDto.getMetadata().getFormat().equals("bpmn2")) {
                            urls.add(subDto.getSourceLink());
                        }
                    } finally {
                        subCall.releaseConnection();
                    }
                }
                for (String url : urls) {
                    //download the process in processUrl and get the right task
                    GetMethod processCall = helper.createGetMethod(url);
                    try {
                        helper.setAuth(client, processCall);
                        client.executeMethod(processCall);
                        String processContent = processCall.getResponseBodyAsString();
                        List<TaskRef> tasks = getProcessTasks(processContent, "any.bpmn2");
                        for (TaskRef task : tasks) {
                            if (task.getProcessId() != null && task.getProcessId().equals(processId)) {
                                if (task.getTaskId() != null && task.getTaskId().equals(taskId)) {
                                    retval.add(task);
                                }
                            }
                        }
                    } finally {
                        processCall.releaseConnection();
                    }
                }
            } catch (JAXBException e) {
                throw new TaskServiceException("Couldn't read task definition for package:" + pkgName +
                        ", process: " + processId + ", task: " + taskId, e);
            } catch (IOException e) {
                throw new TaskServiceException("Couldn't read task definition for package:" + pkgName +
                        ", process: " + processId + ", task: " + taskId, e);
            } catch (Exception e) {
                throw new TaskServiceException("Unexpected error", e);
            } finally {
                call.releaseConnection();
            }
        }
        return retval;
    }
    
    @Override
    public String getContainingPackage(final String uuid) throws TaskServiceException {
        try {
            return helper.getPackageNameByContentUUID(uuid);
        } catch (JAXBException e) {
            throw new TaskServiceException("problem querying package", e);
        } catch (IOException e) {
            throw new TaskServiceException("problem querying package", e);
        } catch (Exception e) {
            throw new TaskServiceException("Unexpected error", e);
        }
    }

    @Override
    public TaskRef getTaskByUUID(final String packageName, final String userTask, final String uuid) throws TaskServiceException {
        HttpClient client = helper.getHttpClient();
        if (packageName != null) {
            GetMethod call = helper.createGetMethod(helper.getRestBaseUrl());
            try {
                helper.setAuth(client, call);
                call.addRequestHeader("Accept", "application/xml");
                client.executeMethod(call);
                PackageListDTO dto = helper.jaxbTransformation(PackageListDTO.class,
                        call.getResponseBodyAsStream(), 
                        PackageListDTO.RELATED_CLASSES);
                String processUrl = null;
                String format = null;
                PackageDTO pkg = dto.getSelectedPackage(packageName);
                for (String url : pkg.getAssets()) {
                    GetMethod subCall = helper.createGetMethod(url);
                    try {
                        helper.setAuth(client, subCall);
                        subCall.addRequestHeader("Accept", "application/xml");
                        client.executeMethod(subCall);
                        AssetDTO subDto = helper.jaxbTransformation(AssetDTO.class, 
                                subCall.getResponseBodyAsStream(), 
                                AssetDTO.RELATED_CLASSES);
                        if (subDto.getMetadata().getUuid().equals(uuid)) {
                            processUrl = subDto.getSourceLink();
                            format = subDto.getMetadata().getFormat();
                            break;
                        }
                    } finally {
                        subCall.releaseConnection();
                    }
                }
                if (format != null && "bpmn2".equals(format)) {
                    //download the process in processUrl and get the right task
                    GetMethod processCall = helper.createGetMethod(processUrl);
                    try {
                        helper.setAuth(client, processCall);
                        client.executeMethod(processCall);
                        String processContent = processCall.getResponseBodyAsString();
                        List<TaskRef> tasks = getProcessTasks(processContent, "any." + format);
                        for (TaskRef task : tasks) {
                            if (isReferencedTask(userTask, task)) {
                                return task;
                            }
                        }
                    } finally {
                        processCall.releaseConnection();
                    }
                }
            } catch (JAXBException e) {
                throw new TaskServiceException("Couldn't read task definition" + uuid + " : " + userTask, e);
            } catch (IOException e) {
                throw new TaskServiceException("Couldn't read task definition " + uuid + " : " + userTask, e);
            } catch (Exception e) {
                throw new TaskServiceException("Unexpected error", e);
            } finally {
                call.releaseConnection();
            }
        }
        return null;
    }

    @Override
    public TaskRef getBPMN2Task(String bpmn2ProcessContent, String processName, String userTask)
            throws TaskServiceException {
        TaskRef retval = null;
        List<TaskRef> tasks = getProcessTasks(bpmn2ProcessContent, processName);
        if (tasks != null) {
            for (TaskRef task : tasks) {
                if (task.getTaskName().equals(userTask)) {
                    retval = task;
                    break;
                }
            }
        }
        return retval;
    }
    
    private boolean isReferencedTask(String userTask, TaskRef task) {
        boolean emptyUserTask = userTask == null || "".equals(userTask);
        boolean taskIsStartProcess = task.getTaskId().equals(ProcessGetInputHandler.PROCESS_INPUT_NAME);
        boolean taskIsSearchedTask = userTask != null && task.getTaskId().equals(userTask);
        
        return (emptyUserTask && taskIsStartProcess) || taskIsSearchedTask;
    }
    
    private String getTaskDefinitionContent(String pkgName, String itemName) throws IOException {
        HttpClient client = helper.getHttpClient();
        if (itemName != null && !"".equals(itemName)) {
            
            String getUrl = helper.getApiSearchUrl(pkgName) + 
                    URLEncoder.encode(itemName, GuvnorHelper.ENCODING);
            GetMethod method = helper.createGetMethod(getUrl);
            try {
                helper.setAuth(client, method);
                client.executeMethod(method);
                return method.getResponseBodyAsString();
            } finally {
                method.releaseConnection();
            }
        }
        return "";
    }
    
    protected List<TaskRef> getProcessTasks(String bpmn2Content, String processName) {
        if (bpmn2Content == null || "".equals(bpmn2Content)) {
            return new ArrayList<TaskRef>();
        }
        if (BPMN2ProcessFactory.getBPMN2ProcessProvider() != provider) {
            BPMN2ProcessFactory.setBPMN2ProcessProvider(provider);
        }
        repo.clear();
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        boolean isBPMN = processName.toLowerCase().endsWith("bpmn");
        boolean isBPMN2 = processName.toLowerCase().endsWith("bpmn2");
        ResourceType type = (isBPMN || isBPMN2) ? ResourceType.BPMN2 : ResourceType.DRF;
        kbuilder.add(new ByteArrayResource(bpmn2Content.getBytes()), type);
        if (!kbuilder.hasErrors()) {
            return new ArrayList<TaskRef>(repo.getTasks());
        } else {
            return new ArrayList<TaskRef>();
        }
    }
}
