package org.drools.core.reteoo;

public class ObjectTypeNodeId {

    public static final ObjectTypeNodeId DEFAULT_ID = new ObjectTypeNodeId(-1, 0);

    private final int otnId;
    private final int id;

    public ObjectTypeNodeId(int otnId, int id) {
        this.otnId = otnId;
        this.id    = id;
    }

    @Override
    public String toString() {
        return "ObjectTypeNode.Id[" + otnId + "#" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ObjectTypeNodeId)) {
            return false;
        }

        ObjectTypeNodeId otherId = (ObjectTypeNodeId) o;
        return id == otherId.id && otnId == otherId.otnId;
    }

    @Override
    public int hashCode() {
        return 31 * otnId + 37 * id;
    }

    public boolean before(ObjectTypeNodeId otherId) {
        return otherId != null && (otnId < otherId.otnId || (otnId == otherId.otnId && id < otherId.id));
    }

    public int getId() {
        return id;
    }
}
