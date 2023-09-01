package org.kie.efesto.common.api.model;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * package private Singleton repository to cache generated classes. Accessed by EfestoContext
 */
enum GeneratedClassesRepository {

    INSTANCE;

    private Map<ModelLocalUriId, Map<String, byte[]>> generatedClassesMap = new ConcurrentHashMap<>();

    public void addGeneratedClasses(ModelLocalUriId modelLocalUriId, Map<String, byte[]> generatedClasses) {
        generatedClassesMap.put(modelLocalUriId, generatedClasses);
    }

    public Map<String, byte[]> getGeneratedClasses(ModelLocalUriId modelLocalUriId) {
        return generatedClassesMap.get(modelLocalUriId);
    }

    public Map<String, byte[]> removeGeneratedClasses(ModelLocalUriId modelLocalUriId) {
        return generatedClassesMap.remove(modelLocalUriId);
    }

    public boolean containsKey(ModelLocalUriId modelLocalUriId) {
        return generatedClassesMap.containsKey(modelLocalUriId);
    }

    public Set<ModelLocalUriId> localUriIdKeySet() {
        return generatedClassesMap.keySet();
    }

    public void clear() {
        generatedClassesMap.clear();
    }
}
