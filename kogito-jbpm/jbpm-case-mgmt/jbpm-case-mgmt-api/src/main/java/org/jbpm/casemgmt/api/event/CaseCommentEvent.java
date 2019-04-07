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

package org.jbpm.casemgmt.api.event;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;

/**
 * Represents occurrence of case comment related operation
 */
public class CaseCommentEvent extends CaseEvent {

    private CommentInstance comment;
    
    public CaseCommentEvent(String user, String caseId, CaseFileInstance caseFile, CommentInstance comment) {
        super(user, caseId, caseFile);
        this.comment = comment;
    }
    
    /**
     * Returns actual CommentInstance that is added or removed
     */
    public CommentInstance getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "CaseCommentEvent [comment=" + comment + ", caseId=" + getCaseId() + "]";
    }

}
