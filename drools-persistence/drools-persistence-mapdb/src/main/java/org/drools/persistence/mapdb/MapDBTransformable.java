package org.drools.persistence.mapdb;

import org.drools.persistence.Transformable;
import org.kie.api.persistence.ObjectStoringStrategy;
import org.kie.api.runtime.Environment;
import org.mapdb.DB;

public interface MapDBTransformable extends Transformable {

	String getMapKey();
	
	boolean updateOnMap(DB db, ObjectStoringStrategy[] strategies);

	void setEnvironment(Environment environment);
}
