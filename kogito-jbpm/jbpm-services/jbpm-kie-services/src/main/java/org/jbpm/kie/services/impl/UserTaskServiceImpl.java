/*
 * Copyright 2014 JBoss by Red Hat.
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

package org.jbpm.kie.services.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.RuntimeDataService;
import org.jbpm.services.api.UserTaskService;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.task.commands.TaskCommand;
import org.jbpm.services.task.impl.TaskContentRegistry;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.Context;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Comment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Task;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.InternalAttachment;
import org.kie.internal.task.api.model.InternalComment;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalI18NText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserTaskServiceImpl implements UserTaskService, VariablesAware {
	
	private static final Logger logger = LoggerFactory.getLogger(UserTaskServiceImpl.class);
	
	private DeploymentService deploymentService;
    
    private RuntimeDataService dataService;
    
    private InternalTaskService nonProcessScopedTaskService;

        
	public void setDeploymentService(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}

	public void setDataService(RuntimeDataService dataService) {
		this.dataService = dataService;
	}
	
	public void setNonProcessScopedTaskService(InternalTaskService nonProcessScopedTaskService) {
		this.nonProcessScopedTaskService = nonProcessScopedTaskService;
	}

	
	// helper methods
	protected RuntimeManager getRuntimeManager(UserTaskInstanceDesc task) {		
		if (task != null && task.getDeploymentId() != null) {			
			return deploymentService.getRuntimeManager(task.getDeploymentId());
		} else if (nonProcessScopedTaskService != null) {
			return new FalbackRuntimeManager(nonProcessScopedTaskService);
		}
		
		return null;
	}
	
	protected RuntimeManager getRuntimeManager(String deploymentId, Command<?> command) {		
		if (deploymentId == null && command instanceof TaskCommand<?>) {
			Long taskId = ((TaskCommand<?>) command).getTaskId();
			if (taskId != null) {
				UserTaskInstanceDesc task = dataService.getTaskById(taskId);
				deploymentId = task.getDeploymentId();
			}
		}
		
		RuntimeManager manager = deploymentService.getRuntimeManager(deploymentId);
		if (manager == null) {
			if (nonProcessScopedTaskService != null) {
				manager = new FalbackRuntimeManager(nonProcessScopedTaskService);
			} else {
				logger.warn("Cannot find runtime manager for deployment {}", deploymentId);
				return null;
			}
		}
		
		return manager;
	}

	@Override
	public void activate(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.activate(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void claim(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.claim(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}

	}

	@Override
	public void complete(Long taskId, String userId, Map<String, Object> params) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		if (manager instanceof InternalRuntimeManager) {
			params = process(params, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.complete(taskId, userId, params);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}

	}

	@Override
	public void delegate(Long taskId, String userId, String targetUserId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.delegate(taskId, userId, targetUserId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void exit(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.exit(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void fail(Long taskId, String userId, Map<String, Object> faultData) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		if (manager instanceof InternalRuntimeManager) {
			faultData = process(faultData, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.fail(taskId, userId, faultData);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void forward(Long taskId, String userId, String targetEntityId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.forward(taskId, userId, targetEntityId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void release(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.release(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void resume(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.resume(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void skip(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.skip(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void start(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.start(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void stop(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.stop(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void suspend(Long taskId, String userId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.suspend(taskId, userId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void nominate(Long taskId, String userId, List<OrganizationalEntity> potentialOwners) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			taskService.nominate(taskId, userId, potentialOwners);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void setPriority(Long taskId, int priority) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).setPriority(taskId, priority);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void setExpirationDate(Long taskId, Date date) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).setExpirationDate(taskId, date);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void setSkipable(Long taskId, boolean skipable) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).setSkipable(taskId, skipable);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}
	
	@Override
	public void setName(Long taskId, String name) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			InternalI18NText text = (InternalI18NText)TaskModelProvider.getFactory().newI18NText();
			text.setLanguage("en-UK");
			text.setText(name);
			
			List<I18NText> names = new ArrayList<I18NText>();
			names.add(text);
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).setTaskNames(taskId, names);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
		
	}

	@Override
	public void setDescription(Long taskId, String description) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			InternalI18NText text = (InternalI18NText)TaskModelProvider.getFactory().newI18NText();
			text.setLanguage("en-UK");
			text.setText(description);
			
			List<I18NText> names = new ArrayList<I18NText>();
			names.add(text);
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).setDescriptions(taskId, names);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public Long saveContent(Long taskId, Map<String, Object> values) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		if (manager instanceof InternalRuntimeManager) {
			values = process(values, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return ((InternalTaskService)taskService).addContent(taskId, (Map<String, Object>)values);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}
	
	@Override
	public void deleteContent(Long taskId, Long contentId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).deleteContent(taskId, contentId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getTaskOutputContentByTaskId(Long taskId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			Task taskInstanceById = taskService.getTaskById(taskId);
	        long documentContentId = taskInstanceById.getTaskData().getOutputContentId();
	        if (documentContentId > 0) {
	            Content contentById = taskService.getContentById(documentContentId);
	            if (contentById == null) {
	                return new HashMap<String, Object>();
	            }
	            
	            ContentMarshallerContext ctx = TaskContentRegistry.get().getMarshallerContext(task.getDeploymentId());
	            Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), ctx.getEnvironment(), ctx.getClassloader());
	            return (Map<String, Object>) unmarshall;
	        }
	        return new HashMap<String, Object>();
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getTaskInputContentByTaskId(Long taskId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			Task taskInstanceById = taskService.getTaskById(taskId);
	        long documentContentId = taskInstanceById.getTaskData().getDocumentContentId();
	        if (documentContentId > 0) {
	            Content contentById = taskService.getContentById(documentContentId);
	            if (contentById == null) {
	                return new HashMap<String, Object>();
	            }
	            
	            ContentMarshallerContext ctx = TaskContentRegistry.get().getMarshallerContext(task.getDeploymentId());
	            Object unmarshall = ContentMarshallerHelper.unmarshall(contentById.getContent(), ctx.getEnvironment(), ctx.getClassloader());
	            return (Map<String, Object>) unmarshall;
	        }
	        return new HashMap<String, Object>();
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public Long addComment(Long taskId, String text, String addedBy, Date addedOn) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			InternalComment comment = (InternalComment)TaskModelProvider.getFactory().newComment();
	        comment.setText(text);
	        comment.setAddedAt(addedOn);
	        comment.setAddedBy(TaskModelProvider.getFactory().newUser(addedBy));
			return ((InternalTaskService)taskService).addComment(taskId, comment);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void deleteComment(Long taskId, Long commentId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).deleteComment(taskId, commentId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public List<Comment> getCommentsByTaskId(Long taskId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return Collections.emptyList();
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return ((InternalTaskService)taskService).getAllCommentsByTaskId(taskId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public Comment getCommentById(Long taskId, Long commentId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return ((InternalTaskService)taskService).getCommentById(commentId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public Long addAttachment(Long taskId, String userId, Object attachment) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		if (manager instanceof InternalRuntimeManager) {
			attachment = process(attachment, ((InternalRuntimeManager) manager).getEnvironment().getClassLoader());
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			InternalAttachment att = (InternalAttachment) TaskModelProvider.getFactory().newAttachment();
			att.setAccessType(AccessType.Inline);
			att.setAttachedAt(new Date());
			att.setAttachedBy(TaskModelProvider.getFactory().newUser(userId));
	        
			Content content = TaskModelProvider.getFactory().newContent();
	        
	        ContentMarshallerContext ctx = TaskContentRegistry.get().getMarshallerContext(task.getDeploymentId());
	        
	        ((InternalContent)content).setContent(ContentMarshallerHelper.marshallContent(attachment, ctx.getEnvironment()));
			return ((InternalTaskService)taskService).addAttachment(taskId, att, content);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public void deleteAttachment(Long taskId, Long attachmentId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			((InternalTaskService)taskService).deleteAttachment(taskId, attachmentId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public Attachment getAttachmentById(Long taskId, Long attachmentId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return ((InternalTaskService)taskService).getAttachmentById(attachmentId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public List<Attachment> getAttachmentsByTaskId(Long taskId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return Collections.emptyList();
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return ((InternalTaskService)taskService).getAllAttachmentsByTaskId(taskId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public <T> T execute(String deploymentId, Command<T> command) {
		Long processInstanceId = CommonUtils.getProcessInstanceId(command);
		
		RuntimeManager manager = getRuntimeManager(deploymentId, command);
		
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstanceId));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return taskService.execute(command);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}
	

	@Override
	public <T> T execute(String deploymentId, Context<?> context, Command<T> command) {
		RuntimeManager manager = getRuntimeManager(deploymentId, command);
		RuntimeEngine engine = manager.getRuntimeEngine(context);
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return taskService.execute(command);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public Task getTask(Long taskId) {
		UserTaskInstanceDesc task = dataService.getTaskById(taskId);

		RuntimeManager manager = getRuntimeManager(task);
		if (manager == null) {
			logger.warn("Cannot find runtime manager for task {}", taskId);
			return null;
		}
		RuntimeEngine engine = manager.getRuntimeEngine(ProcessInstanceIdContext.get(task.getProcessInstanceId()));
		try {
			TaskService taskService = engine.getTaskService();
			// perform actual operation
			return ((InternalTaskService)taskService).getTaskById(taskId);
		} finally {
			disposeRuntimeEngine(manager, engine);
		}
	}

	@Override
	public <T> T process(T variables, ClassLoader cl) {
		// do nothing here as there is no need to process variables
		return variables;
	}
	
	protected void disposeRuntimeEngine(RuntimeManager manager, RuntimeEngine engine) {
		manager.disposeRuntimeEngine(engine);
	}
	
	private class FalbackRuntimeManager implements RuntimeManager {
		
		private InternalTaskService taskService;
		
		public FalbackRuntimeManager(InternalTaskService taskService) {
			this.taskService = taskService;
		}

		@Override
		public RuntimeEngine getRuntimeEngine(Context<?> context) {
			return new RuntimeEngine() {
				
				@Override
				public TaskService getTaskService() {
					return taskService;
				}
				
				@Override
				public KieSession getKieSession() {
					throw new UnsupportedOperationException("Not supported in this impl");
				}
				
				@Override
				public AuditService getAuditLogService() {
					throw new UnsupportedOperationException("Not supported in this impl");
				}
			};
		}

		@Override
		public String getIdentifier() {
			return "fallback task service manager";
		}

		@Override
		public void disposeRuntimeEngine(RuntimeEngine runtime) {
			// do nothing
			
		}

		@Override
		public void close() {
			// do nothing
		}
		
	}

}
