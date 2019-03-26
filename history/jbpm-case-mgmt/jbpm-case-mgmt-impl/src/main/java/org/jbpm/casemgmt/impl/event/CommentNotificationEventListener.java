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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jbpm.casemgmt.api.event.CaseCommentEvent;
import org.jbpm.casemgmt.api.event.CaseEventListener;
import org.jbpm.casemgmt.api.model.instance.CommentInstance;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.utils.NotificationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Event listener that allows to fire notification based on mentions in the case comments
 * that refer to case roles - meaning all role assignments (users or groups) will be notified
 * whenever mentioned.
 *
 */
public class CommentNotificationEventListener implements CaseEventListener, Cacheable {

    private static final Logger logger = LoggerFactory.getLogger(CommentNotificationEventListener.class);
    
    public static final String DEFAULT_SENDER = "cases@jbpm.org";
    public static final String DEFAULT_TEMPLATE = "mentioned-in-comment";
    public static final String DEFAULT_SUBJECT = "You have been mentioned in case ({0}) comment";
    
    public static final String CASE_ID_PARAM = "_CASE_ID_";
    public static final String AUTHOR_PARAM = "_AUTHOR_";
    public static final String COMMENT_PARAM = "_COMMENT_";
    public static final String COMMENT_ID_PARAM = "_COMMENT_ID_";
    public static final String CREATED_AT_PARAM = "_CREATED_AT_";
    
    private final Pattern mentionPattern = Pattern.compile("(?<=^|(?<=[^a-zA-Z0-9-_\\\\.]))@([A-Za-z][A-Za-z0-9_]+)");
    
    private final ServiceLoader<NotificationPublisher> discoveredPublishers = ServiceLoader.load(NotificationPublisher.class);
    
    private final String sender;
    private final String template;
    private final String subject;
    
    private final List<NotificationPublisher> publishers = new ArrayList<>();
    
    public CommentNotificationEventListener() {  
        this(DEFAULT_SENDER, DEFAULT_TEMPLATE, DEFAULT_SUBJECT);
    }
    
    public CommentNotificationEventListener(String sender, String template, String subject) {   
        this.sender = sender;
        this.template = template;
        this.subject = subject;
        logger.debug("Comment notification will use {} as template and {} as subject", template, subject);
        
        for (NotificationPublisher publisher : discoveredPublishers) {
            publishers.add(publisher);
        }
        logger.debug("Discovered notification publishers {}", publishers);
    }
    
    @Override
    public void afterCaseCommentAdded(CaseCommentEvent event) { 
        buildAndPublishNotification(event);
    }

    @Override
    public void afterCaseCommentUpdated(CaseCommentEvent event) {
        buildAndPublishNotification(event);
    }

    
    protected void buildAndPublishNotification(CaseCommentEvent event) {
        if (publishers.isEmpty()) {
            logger.debug("No publishers available, skipping comment notifications");
            return;
        }
        
        CommentInstance comment = event.getComment();
        List<String> mentionedRoles = extractMentionedRoles(comment.getComment());
        if (mentionedRoles.isEmpty()) {
            logger.debug("No one has been mentioned in the comment, skipping comment notification");
            return;
        }
        logger.debug("Found mentions {} in comment {}", mentionedRoles, comment.getId());
        StringBuilder commentContent = new StringBuilder(comment.getComment());
        Set<OrganizationalEntity> recipients = collectOrgEntitiesByRole(mentionedRoles, event, commentContent);
        
        String notificationSubject = MessageFormat.format(subject, event.getCaseId());
        Map<String, Object> parameters = buildParams(event, commentContent);

        for (NotificationPublisher publisher : publishers) {
            if (!publisher.isActive()) {
                logger.debug("Publisher {} is not active, skipping it", publisher);
                continue;
            }
            
            try {
                publisher.publish(sender, notificationSubject, recipients, template, parameters);
            } catch (IllegalArgumentException e) {
                publisher.publish(sender, notificationSubject, recipients, commentContent.toString());
            }
        }
    }
    
    protected List<String> extractMentionedRoles(String commentText) {
        List<String> foundMentions = new ArrayList<>();
        Matcher matcher = mentionPattern.matcher(commentText);
        while (matcher.find()) {
            String mention = matcher.group(0).substring(1);
            foundMentions.add(mention);
        }
        
        return foundMentions;
    }
    
    protected Set<OrganizationalEntity> collectOrgEntitiesByRole(List<String> mentionedRoles, CaseCommentEvent event, StringBuilder commentContent) {
        Set<OrganizationalEntity> recipients = new HashSet<>();
        CommentInstance comment = event.getComment();
        
        for (String roleName : mentionedRoles) {
            if (comment.getRestrictedTo() != null && !comment.getRestrictedTo().isEmpty() && !comment.getRestrictedTo().contains(roleName)) {
                // mentioned role is not allowed to see this comment so remove it from the list
                continue;
            }
            try {
                Collection<OrganizationalEntity> assignments = ((CaseAssignment) event.getCaseFile()).getAssignments(roleName);
                recipients.addAll(assignments);
                
                String assignmnetsFlatten = assignments.stream().map(oe -> oe.getId()).collect(Collectors.joining(","));
                String updatedCommentContent = commentContent.toString().replaceAll("@" + roleName, assignmnetsFlatten);
                commentContent.setLength(0);
                commentContent.append(updatedCommentContent);
            } catch (IllegalArgumentException e) {
                logger.debug("Role {} does not exist in case {}", roleName, event.getCaseId());
            }
        }
        
        return recipients;
    }
    
    protected Map<String, Object> buildParams(CaseCommentEvent event, StringBuilder commentContent) {
        Map<String, Object> parameters = new HashMap<>();
        
        parameters.put(CASE_ID_PARAM, event.getCaseId());
        parameters.put(AUTHOR_PARAM, event.getComment().getAuthor());
        parameters.put(COMMENT_ID_PARAM, event.getComment().getId());
        parameters.put(COMMENT_PARAM, commentContent.toString());
        parameters.put(CREATED_AT_PARAM, event.getComment().getCreatedAt());        
        
        return parameters;
    }

    public void addPublisher(NotificationPublisher publisher) {
        this.publishers.add(publisher);
    }
    
    @Override
    public void close() {  
        
        this.publishers.clear();
    }
}
