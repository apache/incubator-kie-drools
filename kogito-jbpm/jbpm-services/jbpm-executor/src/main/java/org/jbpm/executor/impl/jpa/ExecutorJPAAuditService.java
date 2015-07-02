/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.jbpm.executor.impl.jpa;

import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_STATUS_ID;
import static org.kie.internal.query.QueryParameterIdentifiers.EXECUTOR_TIME_ID;

import java.util.Date;

import javax.persistence.EntityManagerFactory;

import org.jbpm.services.task.audit.service.TaskJPAAuditService;
import org.kie.api.executor.STATUS;
import org.kie.internal.runtime.manager.audit.query.ErrorInfoLogDeleteBuilder;
import org.kie.internal.runtime.manager.audit.query.RequestInfoLogDeleteBuilder;

public class ExecutorJPAAuditService extends TaskJPAAuditService {
	
	static { 

        addCriteria(EXECUTOR_TIME_ID, "l.time", Date.class);
        addCriteria(EXECUTOR_STATUS_ID, "l.status", STATUS.class);
	}

	public ExecutorJPAAuditService(EntityManagerFactory emf) {
		super(emf);
	}
	
	public ErrorInfoLogDeleteBuilder errorInfoLogDeleteBuilder() {
		return new ErrorInfoLogDeleteBuilderImpl(this);
	}

	public RequestInfoLogDeleteBuilder requestInfoLogDeleteBuilder() {
		return new RequestInfoLogDeleteBuilderImpl(this);
	}
}
