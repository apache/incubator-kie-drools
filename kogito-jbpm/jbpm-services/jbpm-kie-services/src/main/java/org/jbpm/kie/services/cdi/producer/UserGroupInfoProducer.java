package org.jbpm.kie.services.cdi.producer;

import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;

public interface UserGroupInfoProducer {

	UserGroupCallback produceCallback();
	
	UserInfo produceUserInfo();
}
