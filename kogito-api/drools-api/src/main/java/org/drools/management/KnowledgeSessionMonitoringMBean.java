package org.drools.management;

import java.util.Date;
import java.util.Map;

import javax.management.ObjectName;

/**
 * An MBean interface for Knowledge Session monitoring
 * 
 * @author etirelli
 */
public interface KnowledgeSessionMonitoringMBean {

    /**
     * Resets all stats
     */
    public void reset();

    /**
     * Returns this MBean name
     * 
     * @return
     */
    public ObjectName getName();

    /**
     * Returns the associated knowledge base ID
     * 
     * @return
     */
    public String getKnowledgeBaseId();

    /**
     * Returns the associated knowledge session ID
     * 
     * @return
     */
    public int getKnowledgeSessionId();

    /**
     * Returns the total fact count current loaded into this session
     * 
     * @return
     */
    public long getTotalFactCount();

    /**
     * Returns the total number of activations fired in this session since last 
     * reset.
     * 
     * @return
     */
    public long getTotalActivationsFired();

    /**
     * Returns the total number of activations cancelled in this session since 
     * last reset.
     *  
     * @return
     */
    public long getTotalActivationsCancelled();

    /**
     * Returns the total number of activations created in this session since 
     * last reset.
     * 
     * @return
     */
    public long getTotalActivationsCreated();

    /**
     * Returns the total milliseconds spent firing rules in this session since last reset.
     * 
     * @return
     */
    public long getTotalFiringTime();

    /**
     * Returns the average firing time in milliseconds for rules in this session
     * since last reset.
     * 
     * @return
     */
    public double getAverageFiringTime();

    /**
     * Returns a formatted String with statistics for a single rule in this session,
     * like number of activations created, cancelled and fired as well as firing time.
     *  
     * @param ruleName the name of the rule for which statistics are requested.
     * 
     * @return
     */
    public String getStatsForRule(String ruleName);

    /**
     * Returns the timestamp of the last stats reset
     * 
     * @return
     */
    public Date getLastReset();
    
    public Map<String,String> getStatsByRule();    

    public long getTotalProcessInstancesStarted();
    
    public long getTotalProcessInstancesCompleted();
    
    public String getStatsForProcess(String processId);
    
    public Map<String,String> getStatsByProcess();
    
    public String getStatsForProcessInstance(long processInstanceId);
    
    public Map<Long,String> getStatsByProcessInstance();
}