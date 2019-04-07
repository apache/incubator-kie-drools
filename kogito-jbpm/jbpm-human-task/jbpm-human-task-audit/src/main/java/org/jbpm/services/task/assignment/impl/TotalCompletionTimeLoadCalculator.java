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
package org.jbpm.services.task.assignment.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jbpm.services.task.assignment.UserTaskLoad;
import org.jbpm.services.task.utils.ClassUtil;
import org.kie.api.task.TaskContext;
import org.kie.api.task.model.User;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class TotalCompletionTimeLoadCalculator extends AbstractLoadCalculator {
	private static final Logger logger = LoggerFactory.getLogger(TotalCompletionTimeLoadCalculator.class);
	private static final String IDENTIFIER = "TotalCompletionTime";
	private static Long timeToLive;
	private static Cache<String, Double> taskDurations;
	private static String TASK_LIST_QUERY = ""
			+ "select new org.jbpm.services.task.assignment.impl.TaskInfo(t.taskData.actualOwner.id, t.name, t.taskData.processId, t.taskData.deploymentId, count(t)) "
			+ "from TaskImpl t "
			+ "where t.taskData.actualOwner in (:owners) and t.taskData.status in ('Reserved', 'InProgress', 'Suspended') "
			+ "group by t.taskData.actualOwner, t.name, t.taskData.processId, t.taskData.deploymentId";
	private static String TASK_AVG_DURATION = ""
			+ "select new org.jbpm.services.task.assignment.impl.TaskAverageDuration(avg(bts.duration),t.taskData.deploymentId,t.taskData.processId,bts.taskName) "
			+ "from BAMTaskSummaryImpl bts left join TaskImpl t on (bts.taskId = t.id) "
			+ "where (bts.duration is not null) and t.taskData.processId = :procid and t.taskData.deploymentId = :depid and t.name = :taskname "
			+ "group by t.taskData.deploymentId, t.taskData.processId, t.name";
	
	/**
	 * Initialize the cache
	 */
	static {
		timeToLive = Long.valueOf(System.getProperty("org.jbpm.services.task.assignment.taskduration.timetolive","1800000")); // defaults to 30 minutes
		taskDurations = CacheBuilder.newBuilder().expireAfterWrite(timeToLive, TimeUnit.MILLISECONDS).build();
	}

	public TotalCompletionTimeLoadCalculator() {
		super(IDENTIFIER);
	}

	// Function to get the task key out of a TaskInfo
	private Function<TaskInfo, String> getTaskKey = (ti) -> {
		return ti.getProcessId()+"_"+ti.getDeploymentId()+"_"+ti.getTaskName();
	};
	
	
	/**
	 * Retrieves a list of tasks that the users are currently assigned to, and which
	 * are waiting to be worked on or are being worked on
	 * @param users The list of users that we are interested in
	 * @param context The TaskContext which is associated with the new task
	 * @return A list of TaskInfo objects
	 */
	private synchronized List<TaskInfo> getUserActiveTaskLists(List<User> users, TaskContext context) {
		TaskPersistenceContext taskContext = ((org.jbpm.services.task.commands.TaskContext)context).getPersistenceContext();
		Map<String, Object> params = new HashMap<>();
		params.put("owners", users);
		return taskContext.queryStringWithParametersInTransaction(TASK_LIST_QUERY, params, ClassUtil.<List<TaskInfo>>castClass(List.class));
	}
	
	@Override
	public UserTaskLoad getUserTaskLoad(User user, TaskContext context) {
		UserTaskLoad load = new UserTaskLoad(IDENTIFIER,user);
		List<User> users = Arrays.asList(user);
		List<TaskInfo> userTasks = getUserActiveTaskLists(users,context);
		if (userTasks == null || userTasks.isEmpty()) {
			load.setCalculatedLoad(new Double(0));
		} else {
			Double loadForUser = new Double(0.0);
			for (TaskInfo ti: userTasks) {
				loadForUser += getTaskDuration(ti,context) * ti.getCount();
			}
			load.setCalculatedLoad(loadForUser);
		}
		
		return load;
	}

	@Override
	public Collection<UserTaskLoad> getUserTaskLoads(List<User> users, TaskContext context) {
		Collection<UserTaskLoad> loads = new ArrayList<>();
		List<TaskInfo> usersTasks = getUserActiveTaskLists(users,context);
		// If there are no user tasks then everyone gets a score of 0 (zero)
		if (usersTasks == null || usersTasks.isEmpty()) {
			users.forEach(u -> {
				loads.add(new UserTaskLoad(IDENTIFIER,u,new Double(0)));
			});
		} else {
			users.forEach(u -> {
				Double loadForUser = new Double(0.0);
				//
				// Get the list of tasks for the user
				//
				List<TaskInfo> tasksForUser = usersTasks.stream()
						.filter(ut -> ut.getOwnerId().equals(u.getId()))
						.collect(Collectors.toList());
				//
				// For each task, retrieve the average duration of the task and 
				// multiply that value by the number of instances that the user
				// has assigned, that are not completed
				//
				for (TaskInfo ti: tasksForUser) {
					loadForUser += getTaskDuration(ti,context) * ti.getCount();
				}

				UserTaskLoad load = new UserTaskLoad(IDENTIFIER,u,loadForUser);
				logger.debug("User load: {}",load);
				loads.add(load);
			});
		}
		return loads;
	}
	
	/**
	 * Getter that returns the "time to live" for the cache containing
	 * task's average duration
	 * @return
	 */
	public static Long getTimeToLive() {
		return timeToLive;
	}
	

	/**
	 * Calculates the average duration for a target task, using a query
	 * against the BAMTaskSummary table
	 * @param context Used to retrieve a PersistenceContext
	 * @param processId The identifier for the process definition containing the target task
	 * @param deploymentId The identifier for the deployment which contains the target task
	 * @param name The name of the target task
	 * @return
	 */
	private Double calculateAverageDuration(TaskContext context, String processId, String deploymentId, String name) {
		Double avgDur = new Double(1);
		TaskPersistenceContext taskContext = ((org.jbpm.services.task.commands.TaskContext)context).getPersistenceContext();
		Map<String, Object> params = new HashMap<>();
		params.put("procid",processId);
		params.put("depid", deploymentId);
		params.put("taskname", name);
		List<TaskAverageDuration> durations = taskContext.queryStringWithParametersInTransaction(TASK_AVG_DURATION, params, ClassUtil.<List<TaskAverageDuration>>castClass(List.class));
		if (durations != null && !durations.isEmpty()) {
			avgDur = durations.get(0).getAverageDuration();
			logger.debug("Retrieved duration is {}",avgDur);
		}
		return avgDur;
	}

	/**
	 * Attempts to retrieve the duration of the given task, from the
	 * taskDurations cache. If it is not available from the cache then
	 * it calls the {@code calculateAverageDuration} and puts the result
	 * into the cache
	 * @param taskInfo {@link TaskInfo} containing the processId, deploymentId and name of the task
	 * @param taskContext Context that was passed in from the assignment service
	 * @return The average duration for the task
	 */
	private synchronized Double getTaskDuration(TaskInfo taskInfo, TaskContext taskContext) {
		Double duration = new Double(1);
		String taskKey = getTaskKey.apply(taskInfo);
		duration = taskDurations.getIfPresent(taskKey);
		
		// If the duration isn't available from the cache then we have to get a calculated value
		if (duration == null) {
			duration = calculateAverageDuration(taskContext,taskInfo.getProcessId(),taskInfo.getDeploymentId(),taskInfo.getTaskName());
			taskDurations.put(taskKey, duration);
			logger.debug("Newly calculated duration of {} is {}",taskKey,taskDurations.asMap().get(taskKey));
		}
		return duration;
	}
	
}
