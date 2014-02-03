package org.jbpm.kie.services.cdi.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.jbpm.services.task.identity.DefaultUserInfo;
import org.jbpm.services.task.identity.MvelUserGroupCallbackImpl;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;

@ApplicationScoped
@Alternative
@Selectable
public class DefaultUserGroupInfoProducer implements UserGroupInfoProducer {

	private UserGroupCallback callback = new MvelUserGroupCallbackImpl(true);
	private UserInfo userInfo = new DefaultUserInfo(true);
	
	@Override
	@Produces
	public UserGroupCallback produceCallback() {
		return callback;
	}

	@Override
	@Produces
	public UserInfo produceUserInfo() {
		return userInfo;
	}

}
