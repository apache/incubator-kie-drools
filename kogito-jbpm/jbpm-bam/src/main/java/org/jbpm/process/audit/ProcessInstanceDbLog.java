/**
 * Copyright 2010 JBoss Inc
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

package org.jbpm.process.audit;

import java.util.List;

import org.hibernate.Session;

/**
 * This class has been deprecated in favor of {@link JPAProcessInstanceDbLog}.
 * 
 *Please, please use that class instead of this. 
 */
@Deprecated
public class ProcessInstanceDbLog {
    
    @SuppressWarnings("unchecked")
	public static List<ProcessInstanceLog> findProcessInstances() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery("from ProcessInstanceLog").list();
        session.getTransaction().commit();
        return result;
    }

    @SuppressWarnings("unchecked")
	public static List<ProcessInstanceLog> findProcessInstances(String processId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery(
            "from ProcessInstanceLog as log where log.processId = ?")
                .setString(0, processId).list();
        session.getTransaction().commit();
        return result;
    }

	public static List<ProcessInstanceLog> findActiveProcessInstances(String processId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery(
            "from ProcessInstanceLog as log where log.processId = ? AND log.end is null")
                .setString(0, processId).list();
        session.getTransaction().commit();
        return result;
    }

    @SuppressWarnings("unchecked")
	public static ProcessInstanceLog findProcessInstance(long processInstanceId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> result = session.createQuery(
            "from ProcessInstanceLog as log where log.processInstanceId = ?")
                .setLong(0, processInstanceId).list();
        session.getTransaction().commit();
        return result == null || result.size() == 0 ? null : result.get(0);
    }

    @SuppressWarnings("unchecked")
	public static List<NodeInstanceLog> findNodeInstances(long processInstanceId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<NodeInstanceLog> result = session.createQuery(
            "from NodeInstanceLog as log where log.processInstanceId = ?")
                .setLong(0, processInstanceId).list();
        session.getTransaction().commit();
        return result;
    }

    @SuppressWarnings("unchecked")
	public static List<NodeInstanceLog> findNodeInstances(long processInstanceId, String nodeId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<NodeInstanceLog> result = session.createQuery(
            "from NodeInstanceLog as log where log.processInstanceId = ? and log.nodeId = ?")
                .setLong(0, processInstanceId)
                .setString(1, nodeId).list();
        session.getTransaction().commit();
        return result;
    }

	@SuppressWarnings("unchecked")
	public static void clear() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List<ProcessInstanceLog> processInstances =
        	session.createQuery("from ProcessInstanceLog").list();
        for (ProcessInstanceLog processInstance: processInstances) {
        	session.delete(processInstance);
        }
        List<NodeInstanceLog> nodeInstances =
        	session.createQuery("from NodeInstanceLog").list();
        for (NodeInstanceLog nodeInstance: nodeInstances) {
        	session.delete(nodeInstance);
        }
        session.getTransaction().commit();
    }

}
