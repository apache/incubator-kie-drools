package org.drools.base;

import org.drools.runtime.rule.FactHandle;
import org.drools.WorkingMemory;
import org.drools.spi.JavaFact;

public class DelegateJavaFactHandler
    implements
    JavaFact {
    private JavaFactRegistryEntry[] entries;

    public JavaFactRegistryEntry[] listWorkingMemories() {
        return this.entries;
    }

    public boolean register(final WorkingMemory workingMemory) {
        if ( workingMemory == null ) {
            return false;
        }

        if ( isRegistered( workingMemory ) ) {
            return false;
        }

        final JavaFactRegistryEntry[] newEntries;
        int position;
        if ( this.entries == null ) {
            newEntries = new JavaFactRegistryEntry[1];
            position = 0;
        } else {
            final int newLength = this.entries.length + 1;
            newEntries = new JavaFactRegistryEntry[newLength];
            System.arraycopy( this.entries,
                              0,
                              newEntries,
                              0,
                              newLength - 1 );
            position = this.entries.length;
        }

        final FactHandle handle = workingMemory.insert( this );

        newEntries[position] = new JavaFactRegistryEntry( workingMemory,
                                                          handle );

        this.entries = newEntries;
        return true;
    }

    public void unregisterAll() {
        for ( int i = 0, length = this.entries.length; i < length; i++ ) {
            final WorkingMemory workingMemory = this.entries[i].getWorkingMemory();
            final FactHandle handle = this.entries[i].getFactHandle();
            workingMemory.retract( handle );
        }

    }

    public boolean unregister(final WorkingMemory workingMemory) {
        if ( this.entries == null ) {
            return false;
        }

        //  If there is only one entry, see if it matched and if so null
        if ( this.entries.length == 1 && this.entries[0].getWorkingMemory() == workingMemory ) {
            this.entries = null;
            return true;
        }

        //  try the first
        if ( this.entries[0].getWorkingMemory() == workingMemory ) {
            final JavaFactRegistryEntry[] newEntries = new JavaFactRegistryEntry[this.entries.length - 1];
            System.arraycopy( this.entries,
                              1,
                              newEntries,
                              0,
                              newEntries.length );
            this.entries = newEntries;
            return true;
        }

        // try the last
        if ( this.entries[this.entries.length - 1].getWorkingMemory() == workingMemory ) {
            final JavaFactRegistryEntry[] newEntries = new JavaFactRegistryEntry[this.entries.length - 1];
            System.arraycopy( this.entries,
                              0,
                              newEntries,
                              0,
                              newEntries.length );
            this.entries = newEntries;
            return true;
        }

        // try middle
        for ( int i = 0, length = this.entries.length; i < length; i++ ) {
            if ( this.entries[i].getWorkingMemory() == workingMemory ) {
                final JavaFactRegistryEntry[] newEntries = new JavaFactRegistryEntry[this.entries.length - 1];
                System.arraycopy( this.entries,
                                  0,
                                  newEntries,
                                  0,
                                  i );
                System.arraycopy( this.entries,
                                  i + 1,
                                  newEntries,
                                  i,
                                  newEntries.length - 1 );
                this.entries = newEntries;
                return true;
            }
        }
        return false;
    }

    public boolean isRegistered(final WorkingMemory workingMemory) {
        if ( this.entries == null ) {
            return false;
        }

        for ( int i = 0, length = this.entries.length; i < length; i++ ) {
            if ( this.entries[i].getWorkingMemory() == workingMemory ) {
                return true;
            }
        }
        return false;
    }

    public int[] getChanges() {
        // TODO Auto-generated method stub
        return null;
    }

}
