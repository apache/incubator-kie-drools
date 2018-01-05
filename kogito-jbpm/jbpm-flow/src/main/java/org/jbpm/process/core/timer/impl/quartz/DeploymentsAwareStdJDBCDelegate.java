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

package org.jbpm.process.core.timer.impl.quartz;

import static org.quartz.TriggerKey.triggerKey;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.quartz.TriggerKey;
import org.quartz.impl.jdbcjobstore.StdJDBCDelegate;


public class DeploymentsAwareStdJDBCDelegate extends StdJDBCDelegate {

    private QuartzUtils quartzUtils = new QuartzUtils();

    @Override
    public List<TriggerKey> selectTriggerToAcquire(Connection conn, long noLaterThan, long noEarlierThan, int maxCount) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<TriggerKey> nextTriggers = new LinkedList<TriggerKey>();
        try {
            List<String> deploymentIds = quartzUtils.getDeployments();
            ps = conn.prepareStatement(rtp(quartzUtils.nextTriggerQuery(deploymentIds)));

            // Set max rows to retrieve
            if (maxCount < 1)
                maxCount = 1; // we want at least one trigger back.
            ps.setMaxRows(maxCount);

            // Try to give jdbc driver a hint to hopefully not pull over more than the few rows we actually need.
            // Note: in some jdbc drivers, such as MySQL, you must set maxRows before fetchSize, or you get exception!
            ps.setFetchSize(maxCount);

            ps.setString(1, STATE_WAITING);
            ps.setBigDecimal(2, new BigDecimal(String.valueOf(noLaterThan)));
            ps.setBigDecimal(3, new BigDecimal(String.valueOf(noEarlierThan)));
            
            int index = 4;
            for (String deployment : deploymentIds) {
                ps.setString(index++, deployment);
            }

            rs = ps.executeQuery();
            
            while (rs.next() && nextTriggers.size() <= maxCount) {
                nextTriggers.add(triggerKey(
                                            rs.getString(COL_TRIGGER_NAME),
                                            rs.getString(COL_TRIGGER_GROUP)));
            }

            return nextTriggers;
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
    }
    
    @Override
    public int countMisfiredTriggersInState(Connection conn, String state1, long ts) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            List<String> deploymentIds = quartzUtils.getDeployments();
            
            ps = conn.prepareStatement(rtp(quartzUtils.countMisfiredTriggersQuery(deploymentIds)));
            ps.setBigDecimal(1, new BigDecimal(String.valueOf(ts)));
            ps.setString(2, state1);
            int index = 3;
            for (String deployment : deploymentIds) {
                ps.setString(index++, deployment);
            }
            
            rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new SQLException("No misfired trigger count returned.");
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
    }
    
    @Override
    public List<TriggerKey> selectMisfiredTriggersInState(Connection conn,
                                                          String state,
                                                          long ts) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            List<String> deploymentIds = quartzUtils.getDeployments();

            ps = conn.prepareStatement(rtp(quartzUtils.misfiredTriggersQuery(deploymentIds)));
            ps.setBigDecimal(1, new BigDecimal(String.valueOf(ts)));
            ps.setString(2, state);
            int index = 3;
            for (String deployment : deploymentIds) {
                ps.setString(index++, deployment);
            }
            rs = ps.executeQuery();

            LinkedList<TriggerKey> list = new LinkedList<TriggerKey>();
            while (rs.next()) {
                String triggerName = rs.getString(COL_TRIGGER_NAME);
                String groupName = rs.getString(COL_TRIGGER_GROUP);
                list.add(triggerKey(triggerName, groupName));
            }
            return list;
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
    }

}
