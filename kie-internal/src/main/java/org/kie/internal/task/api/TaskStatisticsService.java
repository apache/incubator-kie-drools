package org.kie.internal.task.api;

/**
 * The Task Statistics Service provides all
 *  the methods for gathering Task Instance Statistics.
 *  The Task Statistics methods are provided separately from the
 *  Task Query Services, because they can include more complex operations than
 *  simple queries, like aggregations, averages, sums, etc.
 */
public interface TaskStatisticsService {
    public int getCompletedTaskByUserId(String userId);
    public int getPendingTaskByUserId(String userId);

}
