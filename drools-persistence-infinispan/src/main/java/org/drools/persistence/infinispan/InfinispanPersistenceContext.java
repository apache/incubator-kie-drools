package org.drools.persistence.infinispan;

import org.drools.persistence.PersistenceContext;
import org.drools.persistence.info.EntityHolder;
import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.infinispan.Cache;

public class InfinispanPersistenceContext implements PersistenceContext {
	
	private static int SESSIONINFO_KEY = 1;
	private static long WORKITEMINFO_KEY = 1;
	private static final Object syncObject = new Object();
	
    private Cache<String, Object> cache;
    private boolean isJTA;
    
    public InfinispanPersistenceContext(Cache<String, Object> cache) {
        this(cache, true);
    }
    
    public InfinispanPersistenceContext(Cache<String, Object> cache, boolean isJTA) {
        this.cache = cache;
        this.isJTA = isJTA;
    }

    public void persist(SessionInfo entity) {
    	if (entity.getId() == null) {
    		entity.setId(generateSessionInfoId());
    	}
    	String key = createSessionKey(entity.getId());
    	entity.update();
        this.cache.put(key , new EntityHolder(key, entity) );
    }
    
    private Integer generateSessionInfoId() {
    	synchronized (syncObject) {
    		while (cache.containsKey("sessionInfo" + SESSIONINFO_KEY)) {
    			SESSIONINFO_KEY++;
    		}
    	}
    	return SESSIONINFO_KEY;
    }
    
    private Long generateWorkItemInfoId() {
    	synchronized (syncObject) {
    		while (cache.containsKey("workItem" + WORKITEMINFO_KEY)) {
    			WORKITEMINFO_KEY++;
    		}
    	}
    	return WORKITEMINFO_KEY;
    }

	private String createSessionKey(Integer id) {
		return "sessionInfo" + safeId(id);
	}
	
	private String createWorkItemKey(Long id) {
		return "workItem" + safeId(id);
	}
	
	private String safeId(Number id) {
		return String.valueOf(id); //TODO
	}

    public SessionInfo findSessionInfo(Integer id) {
    	EntityHolder holder = (EntityHolder) this.cache.get( createSessionKey(id) );
    	if (holder == null) {
    		return null;
    	}
        return holder.getSessionInfo();
    }

    @Override
    public void remove(SessionInfo sessionInfo) {
        cache.remove( createSessionKey(sessionInfo.getId()) );
        cache.evict( createSessionKey(sessionInfo.getId()) );
    }

    public boolean isOpen() {
    	//cache doesn't close
        return true;
    }

    public void joinTransaction() {
    	if (isJTA) {
    		//cache.getAdvancedCache().getTransactionManager().getTransaction().???? TODO
    	}
    }

    public void close() {
        //cache doesn't close
    }

    public void persist(WorkItemInfo workItemInfo) {
    	if (workItemInfo.getId() == null) {
    		workItemInfo.setId(generateWorkItemInfoId());
    	}
    	String key = createWorkItemKey(workItemInfo.getId());
    	workItemInfo.update();
    	cache.put(key, new EntityHolder(key, workItemInfo));
    }

    public WorkItemInfo findWorkItemInfo(Long id) {
    	EntityHolder holder = (EntityHolder) cache.get(createWorkItemKey(id));
    	if (holder == null) {
    		return null;
    	}
    	return holder.getWorkItemInfo();
    }

    public void remove(WorkItemInfo workItemInfo) {
        cache.remove( createWorkItemKey(workItemInfo.getId()) );
        cache.evict( createWorkItemKey(workItemInfo.getId()) );
    }

    public WorkItemInfo merge(WorkItemInfo workItemInfo) {
    	String key = createWorkItemKey(workItemInfo.getId());
    	workItemInfo.update();
    	return ((EntityHolder) cache.put(key, new EntityHolder(key, workItemInfo))).getWorkItemInfo();
    }
    
    public Cache<String, Object> getCache() {
		return cache;
	}
}
