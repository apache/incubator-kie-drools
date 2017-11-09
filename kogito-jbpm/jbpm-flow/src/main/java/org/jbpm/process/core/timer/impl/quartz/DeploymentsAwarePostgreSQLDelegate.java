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

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import org.quartz.impl.jdbcjobstore.PostgreSQLDelegate;
import org.quartz.utils.Key;
import org.slf4j.Logger;

public class DeploymentsAwarePostgreSQLDelegate extends PostgreSQLDelegate {

    private QuartzUtils quartzUtils = new QuartzUtils();

    public DeploymentsAwarePostgreSQLDelegate(Logger log, String tablePrefix, String instanceId, Boolean useProperties) {
        super(log, tablePrefix, instanceId, useProperties);
    }

    public DeploymentsAwarePostgreSQLDelegate(Logger log, String tablePrefix, String instanceId) {
        super(log, tablePrefix, instanceId);
    }

    @Override
    public List selectTriggerToAcquire(Connection conn, long noLaterThan, long noEarlierThan) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        List nextTriggers = new LinkedList();
        try {
            List<String> deploymentIds = quartzUtils.getDeployments();
            ps = conn.prepareStatement(rtp(quartzUtils.nextTriggerQuery(deploymentIds)));

            // Try to give jdbc driver a hint to hopefully not pull over 
            // more than the few rows we actually need.
            ps.setFetchSize(5);
            ps.setMaxRows(5);

            ps.setString(1, STATE_WAITING);
            ps.setBigDecimal(2, new BigDecimal(String.valueOf(noLaterThan)));
            ps.setBigDecimal(3, new BigDecimal(String.valueOf(noEarlierThan)));
            int index = 4;
            for (String deployment : deploymentIds) {
                ps.setString(index++, deployment);
            }

            rs = ps.executeQuery();

            while (rs.next() && nextTriggers.size() < 5) {
                nextTriggers.add(new Key(
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
    public int countMisfiredTriggersInStates(Connection conn, String state1, String state2, long ts) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            List<String> deploymentIds = quartzUtils.getDeployments();

            ps = conn.prepareStatement(rtp(quartzUtils.countMisfiredTriggersQuery(deploymentIds)));
            ps.setBigDecimal(1, new BigDecimal(String.valueOf(ts)));
            ps.setString(2, state1);
            ps.setString(3, state2);
            int index = 4;
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
    public boolean selectMisfiredTriggersInStates(Connection conn,
                                                  String state1,
                                                  String state2,
                                                  long ts,
                                                  int count,
                                                  List resultList) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            List<String> deploymentIds = quartzUtils.getDeployments();

            ps = conn.prepareStatement(rtp(quartzUtils.misfiredTriggersQuery(deploymentIds)));
            ps.setBigDecimal(1, new BigDecimal(String.valueOf(ts)));
            ps.setString(2, state1);
            ps.setString(3, state2);
            int index = 4;
            for (String deployment : deploymentIds) {
                ps.setString(index++, deployment);
            }
            rs = ps.executeQuery();

            boolean hasReachedLimit = false;
            while (rs.next() && (hasReachedLimit == false)) {
                if (resultList.size() == count) {
                    hasReachedLimit = true;
                } else {
                    String triggerName = rs.getString(COL_TRIGGER_NAME);
                    String groupName = rs.getString(COL_TRIGGER_GROUP);
                    resultList.add(new Key(triggerName, groupName));
                }
            }

            return hasReachedLimit;
        } finally {
            closeResultSet(rs);
            closeStatement(ps);
        }
    }
}
