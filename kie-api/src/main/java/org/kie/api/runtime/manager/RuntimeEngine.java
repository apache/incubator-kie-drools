package org.kie.api.runtime.manager;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.task.TaskService;

/**
 * RuntimeEngine is the main entry point to interact with the process engine and task
 * service. It's responsibility is to ensure that process engine and task service
 * are properly configured and know about each other which eliminate the need to
 * manually setup the integration between these two.<br>
 * RuntimeEngines are always produced by <code>RuntimeManager</code> and thus shall never be
 * created manually. <code>RuntimeManager</code> provides all required information to build
 * and bootstrap the <code>RuntimeEngine</code> so it is configured and ready to be used
 * regardless of when it is invoked.
 */
public interface RuntimeEngine {

    /**
     * @return <code>KieSession</code> configured for this <code>RuntimeEngine</code>
     */
    KieSession getKieSession();

    /**
     * @return <code>TaskService</code> configured for this <code>RuntimeEngine</code>
     */
    TaskService getTaskService();

    /**
     * @return <code>AuditService</code> that gives access to underlying runtime data such
     * as process instance, node instance and variable log entries.
     */
    AuditService getAuditService();
}
