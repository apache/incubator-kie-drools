package org.drools.persistence.map;

import org.drools.persistence.EntityInfo;

public interface AbstractStorage {

    EntityInfo find(Long id);

    void saveOrUpdate(EntityInfo storedObject);

}
