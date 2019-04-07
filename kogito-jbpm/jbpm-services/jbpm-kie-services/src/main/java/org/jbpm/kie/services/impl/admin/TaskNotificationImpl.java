/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.kie.services.impl.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jbpm.services.api.admin.TaskNotification;
import org.kie.api.task.model.OrganizationalEntity;


public class TaskNotificationImpl implements TaskNotification {

    private static final long serialVersionUID = -3261668409546992835L;
    private Long id;
    private String name;
    private String subject;
    private String content;
    private Date date;
    
    private List<OrganizationalEntity> recipients;
    private List<OrganizationalEntity> businessAdministrators;
    
    private boolean active;
    
    public TaskNotificationImpl(Long id, String name, String subject, String content, Date date, List<OrganizationalEntity> recipients, List<OrganizationalEntity> businessAdministrators, boolean active) {
        super();
        this.id = id;
        this.name = name;
        this.subject = subject;
        this.content = content;
        this.date = date;
        this.recipients = new ArrayList<OrganizationalEntity>(recipients);
        this.businessAdministrators = new ArrayList<OrganizationalEntity>(businessAdministrators);
        this.active = active;
    }

    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    @Override
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public Date getDate() {
        return date;
    }
    
    public void setDate(Date date) {
        this.date = date;
    }
    
    @Override
    public List<OrganizationalEntity> getRecipients() {
        return recipients;
    }
    
    public void setRecipients(List<OrganizationalEntity> recipients) {
        this.recipients = recipients;
    }
    
    @Override
    public List<OrganizationalEntity> getBusinessAdministrators() {
        return businessAdministrators;
    }
    
    public void setBusinessAdministrators(List<OrganizationalEntity> businessAdministrators) {
        this.businessAdministrators = businessAdministrators;
    }

    @Override
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    @Override
    public String toString() {
        return "TaskNotificationImpl [id=" + id + ", name=" + name + ", subject=" + subject + ", content=" + content + 
                ", date=" + date + ", recipients=" + recipients + ", businessAdministrators=" + businessAdministrators + "]";
    }
    
   

}
