package org.kie.internal.executor.api;

public interface ExecutorStoreService {

	void persistRequest(RequestInfo request);
	
	void updateRequest(RequestInfo request);
	
	RequestInfo removeRequest(Long requestId);
	
	RequestInfo findRequest(Long id);	
	
	void persistError(ErrorInfo error);
	
	void updateError(ErrorInfo error);
	
	ErrorInfo removeError(Long errorId);
	
	ErrorInfo findError(Long id);
	
	Runnable buildExecutorRunnable();
	
}
