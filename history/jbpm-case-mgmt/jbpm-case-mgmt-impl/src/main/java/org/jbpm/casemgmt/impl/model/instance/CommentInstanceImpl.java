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

package org.jbpm.casemgmt.impl.model.instance;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.jbpm.casemgmt.api.model.instance.CommentInstance;


public class CommentInstanceImpl implements CommentInstance {

    private static final long serialVersionUID = 3910618838737687280L;
    
    private String id;
    private Date createdAt;    
    private String author;    
    private String comment;
    
    private List<String> restrictedTo;
    
    public CommentInstanceImpl(String author, String comment, List<String> restrictedTo) {
        this.id = UUID.randomUUID().toString();
        this.createdAt = new Date();
        this.author = author;
        this.comment = comment;
        this.restrictedTo = restrictedTo;
    }

    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    @Override
    public List<String> getRestrictedTo() {
        return restrictedTo;
    }
    
    public void setRestrictedTo(List<String> restrictedTo) {
        this.restrictedTo = restrictedTo;
    }

    @Override
    public String toString() {
        return "CommentInstanceImpl [createdAt=" + createdAt + ", author=" + author + ", comment='" + comment + "']";
    }


}
