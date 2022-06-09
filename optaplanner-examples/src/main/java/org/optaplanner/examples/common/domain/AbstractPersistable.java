package org.optaplanner.examples.common.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public abstract class AbstractPersistable {

    protected Long id;

    protected AbstractPersistable() {
    }

    protected AbstractPersistable(long id) {
        this.id = id;
    }

    @PlanningId
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // This part is currently commented out because it's probably a bad thing to mix identification with equality

    //    public boolean equals(Object o) {
    //        if (this == o) {
    //            return true;
    //        }
    //        if (id == null || !(o instanceof AbstractPersistable)) {
    //            return false;
    //        } else {
    //            AbstractPersistable other = (AbstractPersistable) o;
    //            return getClass().equals(other.getClass()) && id.equals(other.id);
    //        }
    //    }
    //
    //    public int hashCode() {
    //        if (id == null) {
    //            return super.hashCode();
    //        } else {
    //            // A direct implementation (instead of HashCodeBuilder) to avoid dependencies
    //            return (((17 * 37)
    //                    + getClass().hashCode())) * 37
    //                    + id.hashCode();
    //        }
    //    }

    @Override
    public String toString() {
        return getClass().getName().replaceAll(".*\\.", "") + "-" + id;
    }

}
