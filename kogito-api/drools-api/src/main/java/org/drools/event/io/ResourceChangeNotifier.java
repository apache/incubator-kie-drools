package org.drools.event.io;

import org.drools.io.Resource;

public interface ResourceChangeNotifier extends ResourceChangeManager {
    public void resourceAdded(Resource resource);
    public void resourceModified(Resource resource);
    public void resourceRemoved(Resource resource);
}
