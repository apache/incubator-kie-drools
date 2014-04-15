package org.kie.internal.runtime.manager;

import org.kie.api.runtime.manager.RegisterableItemsFactory;

public interface InternalRegisterableItemsFactory extends RegisterableItemsFactory {

	InternalRuntimeManager getRuntimeManager();
	
	void setRuntimeManager(InternalRuntimeManager manager);
}
