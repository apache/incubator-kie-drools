/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence.mongodb.index;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.persistence.api.schema.EntityIndexDescriptor;
import org.kie.kogito.persistence.api.schema.IndexDescriptor;
import org.kie.kogito.persistence.api.schema.SchemaRegisteredEvent;
import org.kie.kogito.persistence.api.schema.SchemaRegistrationException;
import org.kie.kogito.persistence.mongodb.client.MongoClientManager;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimaps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.IndexModel;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@ApplicationScoped
public class IndexManager {

    private static final long MAX_INDEX_NUMBER = 63L;

    static final String INDEX_NAME_FIELD = "name";

    static final String DEFAULT_INDEX = "_id_";

    Map<String, EntityIndexDescriptor> indexes = new ConcurrentHashMap<>();

    Map<String, String> collectionIndexMapping = new ConcurrentHashMap<>();

    @Inject
    IndexSchemaAcceptor schemaAcceptor;

    @Inject
    Event<ProcessIndexEvent> processIndexEvent;

    @Inject
    Instance<MongoClientManager> mongoClientManager;

    @ConfigProperty(name = "kogito.apps.persistence.indexing", defaultValue = "true")
    Boolean indexEnabled;

    public void onSchemaRegisteredEvent(@Observes SchemaRegisteredEvent event) {
        if (schemaAcceptor.accept(event.getSchemaType())) {
            indexes.putAll(event.getSchemaDescriptor().getEntityIndexDescriptors());
            if (indexEnabled) {
                updateIndexes(event.getSchemaDescriptor().getEntityIndexDescriptors().values());
            }

            event.getSchemaDescriptor().getProcessDescriptor().ifPresent(processDescriptor -> processIndexEvent.fire(new ProcessIndexEvent(processDescriptor)));
        }
    }

    public void onIndexCreateOrUpdateEvent(@Observes IndexCreateOrUpdateEvent event) {
        if (!indexEnabled) {
            return;
        }
        String indexType = collectionIndexMapping.put(event.getCollection(), event.getIndex());
        if (!event.getIndex().equals(indexType)) {
            MongoCollection<Document> collection = this.getCollection(event.getCollection());
            EntityIndexDescriptor index = indexes.get(event.getIndex());
            updateCollection(collection, index);
        }
    }

    void updateIndexes(Collection<EntityIndexDescriptor> entityIndexDescriptorList) {
        entityIndexDescriptorList.forEach(entityIndexDescriptor -> this.getCollectionsWithIndex(entityIndexDescriptor.getName())
                .forEach(col -> this.updateCollection(this.getCollection(col), entityIndexDescriptor)));
    }

    void updateCollection(MongoCollection<Document> collection, EntityIndexDescriptor index) {
        if (index == null) {
            return;
        }

        List<IndexModel> parsedIndexes = createIndexForEntity("", index);
        Map<String, IndexModel> indexNameMap = parsedIndexes.stream().collect(toMap(ind -> ind.getOptions().getName(), identity()));

        List<String> indexesExists = StreamSupport.stream(collection.listIndexes().spliterator(), false)
                .map(document -> document.getString(INDEX_NAME_FIELD)).filter(name -> !DEFAULT_INDEX.equals(name)).collect(toList());

        indexesExists.forEach(ind -> {
            if (!indexNameMap.containsKey(ind)) {
                collection.dropIndex(ind);
            }
        });

        if (MAX_INDEX_NUMBER < indexNameMap.size()) {
            throw new SchemaRegistrationException("A single MongoDB collection can have no more than 64 indexes");
        }

        List<IndexModel> indexesToAdd = indexNameMap.entrySet().stream()
                .filter(entry -> !indexesExists.contains(entry.getKey())).map(Map.Entry::getValue).collect(toList());
        if (!indexesToAdd.isEmpty()) {
            collection.createIndexes(indexesToAdd);
        }
    }

    List<IndexModel> createIndexForEntity(String parentField, EntityIndexDescriptor entityIndexDescriptor) {
        String pkg = entityIndexDescriptor.getName().substring(0, entityIndexDescriptor.getName().lastIndexOf(".") + 1);
        String prefixUUID = UUID.nameUUIDFromBytes(parentField.getBytes()).toString() + ".";

        List<IndexModel> indexesToCreate = entityIndexDescriptor.getIndexDescriptors().parallelStream()
                .flatMap(indexDescriptor -> createIndex(indexDescriptor, parentField, prefixUUID).stream()).collect(toList());

        List<IndexModel> subIndexesToCreate = entityIndexDescriptor.getAttributeDescriptors().parallelStream()
                .filter(attributeDescriptor -> !attributeDescriptor.isPrimitiveType())
                .flatMap(attributeDescriptor -> {
                    String fieldName = parentField.isEmpty() ? attributeDescriptor.getName() : (parentField + "." + attributeDescriptor.getName());
                    if (indexes.containsKey(attributeDescriptor.getTypeName())) {
                        return createIndexForEntity(fieldName, indexes.get(attributeDescriptor.getTypeName())).stream();
                    } else if (indexes.containsKey(pkg + attributeDescriptor.getTypeName())) {
                        return createIndexForEntity(fieldName, indexes.get(pkg + attributeDescriptor.getTypeName())).stream();
                    }
                    return Stream.empty();
                }).collect(toList());

        return Stream.concat(indexesToCreate.stream(), subIndexesToCreate.stream()).collect(toList());
    }

    Optional<IndexModel> createIndex(IndexDescriptor indexDescriptor, String parentField, String prefixUUID) {
        String indexName = prefixUUID + indexDescriptor.getName();

        List<String> fieldNames = indexDescriptor.getIndexAttributes().stream()
                .map(attributeName -> parentField.isEmpty() ? attributeName : (parentField + "." + attributeName))
                .collect(toList());

        if (!fieldNames.isEmpty()) {
            return Optional.of(new IndexModel(Indexes.ascending(fieldNames), new IndexOptions().name(indexName).sparse(true)));
        }

        return Optional.empty();
    }

    List<String> getCollectionsWithIndex(String index) {
        return Multimaps.invertFrom(Multimaps.forMap(collectionIndexMapping), ArrayListMultimap.create()).get(index);
    }

    MongoCollection<Document> getCollection(String collection) {
        return mongoClientManager.get().getCollection(collection);
    }

    Map<String, EntityIndexDescriptor> getIndexes() {
        return indexes;
    }

    Map<String, String> getCollectionIndexMapping() {
        return collectionIndexMapping;
    }
}
