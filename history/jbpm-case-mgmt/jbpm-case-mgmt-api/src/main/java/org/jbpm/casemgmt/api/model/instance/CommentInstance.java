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

package org.jbpm.casemgmt.api.model.instance;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Represents single case comment instance that can be attached 
 * to a case at any time by case participants
 *
 */
public interface CommentInstance extends Serializable {

    /**
     * Returns unique id assigned to this comment
     * @return id of the comment
     */
    String getId();
    /**
     * Exact date at which comment was added.
     * @return created at date
     */
    Date getCreatedAt();
    
    /**
     * Author of the comment
     * @return id of the author
     */
    String getAuthor();
    
    /**
     * Actual comment test
     * @return the comment
     */
    String getComment();
    
    /**
     * List of case roles given comment is restricted to
     * @return restrictions
     */
    List<String> getRestrictedTo();
}
