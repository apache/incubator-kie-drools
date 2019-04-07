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

package org.jbpm.casemgmt.impl.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.casemgmt.impl.event.CommentNotificationEventListener.AUTHOR_PARAM;
import static org.jbpm.casemgmt.impl.event.CommentNotificationEventListener.CASE_ID_PARAM;
import static org.jbpm.casemgmt.impl.event.CommentNotificationEventListener.COMMENT_ID_PARAM;
import static org.jbpm.casemgmt.impl.event.CommentNotificationEventListener.COMMENT_PARAM;
import static org.jbpm.casemgmt.impl.event.CommentNotificationEventListener.CREATED_AT_PARAM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jbpm.casemgmt.api.event.CaseCommentEvent;
import org.jbpm.casemgmt.api.model.CaseRole;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.impl.model.CaseRoleImpl;
import org.jbpm.casemgmt.impl.model.instance.CaseFileInstanceImpl;
import org.jbpm.casemgmt.impl.model.instance.CommentInstanceImpl;
import org.jbpm.casemgmt.impl.util.TestNotificationPublisher;
import org.junit.Test;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.User;

public class CommentNotificationEventListenerTest {

    @Test
    public void testFindMentionsNoMentionsInComment() {
        String commentText = "just a plain text without mentioning any role";
        CommentNotificationEventListener listener = new CommentNotificationEventListener();
        
        List<String> found = listener.extractMentionedRoles(commentText);
        assertThat(found).isEmpty();
    }
    
    @Test
    public void testFindMentionsSingleMentionsInComment() {
        String commentText = "just a plain text mentioning @owner only";
        CommentNotificationEventListener listener = new CommentNotificationEventListener();
        
        List<String> found = listener.extractMentionedRoles(commentText);
        assertThat(found).hasSize(1);
        assertThat(found).contains("owner");
    }
    
    @Test
    public void testFindMentionsMultipleMentionsInComment() {
        String commentText = "@owner could you please get in touch with @manager regarding your vacation?";
        CommentNotificationEventListener listener = new CommentNotificationEventListener();
        
        List<String> found = listener.extractMentionedRoles(commentText);
        assertThat(found).hasSize(2);
        assertThat(found).contains("owner");
        assertThat(found).contains("manager");
    }
    
    @Test
    public void testCollectOrgEntitiesByRole() {
        CommentNotificationEventListener listener = new CommentNotificationEventListener();
        
        List<String> mentionedRoles = new ArrayList<>();
        mentionedRoles.add("owner");
        mentionedRoles.add("manager");
        
        CommentInstanceImpl comment = new CommentInstanceImpl("john", "simple comment for @owner and @manager", new ArrayList<>());
        CaseFileInstance caseFile = buildCaseFile(mentionedRoles);
        
        CaseCommentEvent event = new CaseCommentEvent("john", caseFile.getCaseId() , caseFile, comment);
        
        StringBuilder commentContent = new StringBuilder(comment.getComment());
        Set<OrganizationalEntity> collected = listener.collectOrgEntitiesByRole(mentionedRoles, event, commentContent);
        
        assertThat(collected).hasSize(2);
        assertThat(collected).allMatch(item -> item instanceof User);
        assertThat(commentContent.toString()).isEqualTo("simple comment for john and mary");
    }
    
    @Test
    public void testCollectOrgEntitiesByRoleNotExistingRole() {
        CommentNotificationEventListener listener = new CommentNotificationEventListener();
        
        List<String> mentionedRoles = new ArrayList<>();
        mentionedRoles.add("owner");
        mentionedRoles.add("manager");
        
        CommentInstanceImpl comment = new CommentInstanceImpl("john", "simple comment for @owner and @manager", new ArrayList<>());
        CaseFileInstance caseFile = buildCaseFile(mentionedRoles);
        
        CaseCommentEvent event = new CaseCommentEvent("john", caseFile.getCaseId() , caseFile, comment);
        // add additional role that is not in case file
        mentionedRoles.add("notexisting");
        StringBuilder commentContent = new StringBuilder(comment.getComment());
        Set<OrganizationalEntity> collected = listener.collectOrgEntitiesByRole(mentionedRoles, event, commentContent);
        
        assertThat(collected).hasSize(2);
        assertThat(collected).allMatch(item -> item instanceof User);
        
        assertThat(commentContent.toString()).isEqualTo("simple comment for john and mary");
    }
    
    @Test
    public void testBuildParametersMap() {
        CommentNotificationEventListener listener = new CommentNotificationEventListener();

        CommentInstanceImpl comment = new CommentInstanceImpl("john", "simple comment for @owner and @manager", new ArrayList<>());        
        CaseCommentEvent event = new CaseCommentEvent("john", "CASE-00001" , null, comment);
       
        StringBuilder commentContent = new StringBuilder(comment.getComment());
        Map<String, Object> parameters = listener.buildParams(event, commentContent);
        
        assertThat(parameters).hasSize(5);
        assertThat(parameters)
        .containsEntry(AUTHOR_PARAM, comment.getAuthor())
        .containsEntry(CASE_ID_PARAM, "CASE-00001")
        .containsEntry(COMMENT_ID_PARAM, comment.getId())
        .containsEntry(COMMENT_PARAM, commentContent.toString())
        .containsEntry(CREATED_AT_PARAM, comment.getCreatedAt());
    }
    
    @Test
    public void testNotificationOnCommentAdded() {
        CommentNotificationEventListener listener = new CommentNotificationEventListener();
        
        List<String> mentionedRoles = new ArrayList<>();
        mentionedRoles.add("owner");
        mentionedRoles.add("manager");
        CaseFileInstance caseFile = buildCaseFile(mentionedRoles);
        
        CommentInstanceImpl comment = new CommentInstanceImpl("john", "simple comment for @owner and @manager", new ArrayList<>());        
        CaseCommentEvent event = new CaseCommentEvent("john", caseFile.getCaseId() , caseFile, comment);
        
        TestNotificationPublisher publisher = new TestNotificationPublisher(false);
        listener.addPublisher(publisher);
        
        listener.afterCaseCommentAdded(event);
        
        String expectedNotification = "Publishing notification from cases@jbpm.org, with subject You have been mentioned in case (CASE-00001) comment to [[UserImpl:'mary'], [UserImpl:'john']] with template mentioned-in-comment";
        
        List<String> published = publisher.get();
        assertThat(published).hasSize(1);
        assertThat(published.get(0)).isEqualTo(expectedNotification);
    }
    
    @Test
    public void testNotificationOnCommentAddedWithRawBody() {
        CommentNotificationEventListener listener = new CommentNotificationEventListener();
        
        List<String> mentionedRoles = new ArrayList<>();
        mentionedRoles.add("owner");
        mentionedRoles.add("manager");
        CaseFileInstance caseFile = buildCaseFile(mentionedRoles);
        
        CommentInstanceImpl comment = new CommentInstanceImpl("john", "simple comment for @owner and @manager", new ArrayList<>());        
        CaseCommentEvent event = new CaseCommentEvent("john", caseFile.getCaseId() , caseFile, comment);
        
        TestNotificationPublisher publisher = new TestNotificationPublisher(true);
        listener.addPublisher(publisher);
        
        listener.afterCaseCommentAdded(event);
        
        String expectedNotification = "Publishing notification from cases@jbpm.org, with subject You have been mentioned in case (CASE-00001) comment to [[UserImpl:'mary'], [UserImpl:'john']] with body simple comment for john and mary";
        
        List<String> published = publisher.get();
        assertThat(published).hasSize(1);
        assertThat(published.get(0)).isEqualTo(expectedNotification);
    }
    
    protected CaseFileInstance buildCaseFile(List<String> mentionedRoles) {
        CaseFileInstanceImpl caseFile = new CaseFileInstanceImpl("CASE-00001", "dummy", new HashMap<>());
        
        List<CaseRole> roles = mentionedRoles.stream().map(roleName -> new CaseRoleImpl(roleName)).collect(Collectors.toList());
        caseFile.setupRoles(roles);
        
        caseFile.assignUser("owner", "john");
        caseFile.assignUser("manager", "mary");  
        
        return caseFile;
    }
   
}
