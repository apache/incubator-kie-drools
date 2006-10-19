/* JUG Java Uuid Generator
 *
 * Copyright (c) 2002- Tatu Saloranta, tatu.saloranta@iki.fi
 *
 * Licensed under the License specified in the file LICENSE which is
 * included with the source code.
 * You may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.util;

import java.io.Serializable;

/**
 * UUID represents Universally Unique Identifiers (aka Global UID in
 * Windows world). UUIDs are usually generated via UUIDGenerator (or in
 * case of 'Null UUID', 16 zero bytes, via static method getNullUUID()),
 * or received from external systems.
 *
 * By default class caches the string presentations of UUIDs so that
 * description is only created the first time it's needed. For memory
 * stingy applications this caching can be turned off (note though
 * that if uuid.toString() is never called, desc is never calculated
 * so only loss is the space allocated for the desc pointer... which
 * can of course be commented out to save memory).
 *
 * Similarly, hash code is calculated when it's needed for the first
 * time, and from thereon that value is just returned. This means
 * that using UUIDs as keys should be reasonably efficient.
 *
 * UUIDs can be compared for equality, serialized, cloned and even sorted.
 * Equality is a simple bit-wise comparison. Ordering (for sorting) is done by
 * first ordering based on type (in the order of numeric values of
 * types), secondarily by time stamp (only for time-based time stamps),
 * and finally by straight numeric byte-by-byte comparison (from
 * most to least significant bytes).
 */

public class UUID
    implements
    Serializable,
    Cloneable,
    Comparable {
    private final static String kHexChars            = "0123456789abcdefABCDEF";

    public final static byte    INDEX_CLOCK_HI       = 6;
    public final static byte    INDEX_CLOCK_MID      = 4;
    public final static byte    INDEX_CLOCK_LO       = 0;

    public final static byte    INDEX_TYPE           = 6;
    // Clock seq. & variant are multiplexed...
    public final static byte    INDEX_CLOCK_SEQUENCE = 8;
    public final static byte    INDEX_VARIATION      = 8;

    public final static byte    TYPE_NULL            = 0;
    public final static byte    TYPE_TIME_BASED      = 1;
    public final static byte    TYPE_DCE             = 2;                                     // Not used
    public final static byte    TYPE_NAME_BASED      = 3;
    public final static byte    TYPE_RANDOM_BASED    = 4;

    /* 'Standard' namespaces defined (suggested) by UUID specs:
     */
    public final static String  NAMESPACE_DNS        = "6ba7b810-9dad-11d1-80b4-00c04fd430c8";
    public final static String  NAMESPACE_URL        = "6ba7b811-9dad-11d1-80b4-00c04fd430c8";
    public final static String  NAMESPACE_OID        = "6ba7b812-9dad-11d1-80b4-00c04fd430c8";
    public final static String  NAMESPACE_X500       = "6ba7b814-9dad-11d1-80b4-00c04fd430c8";

    /* By default let's cache desc, can be turned off. For hash code
     * there's no point in turning it off (since the int is already
     * part of the instance memory allocation); if you want to save
     * those 4 bytes (or possibly bit more if alignment is bad) just
     * comment out hash caching.
     */
    private static boolean      sDescCaching         = true;

    /**
     * The shared null UUID. Would be nice to do lazy instantiation, but
     * if the instance really has to be a singleton, that would mean
     * class-level locking (synchronized getNullUUID()), which would
     * be some overhead... So let's just bite the bullet the first time
     * assuming creation of the null UUID (plus wasted space if it's
     * not needed) can be ignored.
     */
    private final static UUID   sNullUUID            = new UUID();

    private final byte[]        mId                  = new byte[16];
    // Both string presentation and hash value may be cached...
    private transient String    mDesc                = null;
    private transient int       mHashCode            = 0;

    /* *** Object creation: *** */

    /**
     * Default constructor creates a NIL UUID, one that contains all
     * zeroes
     *
     * Note that the clearing of array is actually unnecessary as
     * JVMs are required to clear up the allocated arrays by default.
     */
    public UUID() {
        /*
         for (int i = 0; i < 16; ++i) {
         mId[i] = (byte)0;
         }
         */
    }

    /**
     * Constructor for cases where you already have the 16-byte binary
     * representation of the UUID (for example if you save UUIDs binary
     * takes less than half of space string representation takes).
     *
     * @param data array that contains the binary representation of UUID
     */
    public UUID(final byte[] data) {
        /* Could call the other constructor... and/or use System.arraycopy.
         * However, it's likely that those would make this slower to use,
         * and initialization is really simple as is in any case.
         */
        for ( int i = 0; i < 16; ++i ) {
            this.mId[i] = data[i];
        }
    }

    /**
     * Constructor for cases where you already have the binary
     * representation of the UUID (for example if you save UUIDs binary
     * takes less than half of space string representation takes) in
     * a byte array
     *
     * @param data array that contains the binary representation of UUID
     * @param start byte offset where UUID starts
     */
    public UUID(final byte[] data,
                final int start) {
        for ( int i = 0; i < 16; ++i ) {
            this.mId[i] = data[start + i];
        }
    }

    /**
     * Protected constructor used by UUIDGenerator
     *
     * @param type UUID type
     * @param data 16 byte UUID contents
     */
    UUID(final int type,
         final byte[] data) {
        for ( int i = 0; i < 16; ++i ) {
            this.mId[i] = data[i];
        }
        // Type is multiplexed with time_hi:
        this.mId[UUID.INDEX_TYPE] &= (byte) 0x0F;
        this.mId[UUID.INDEX_TYPE] |= (byte) (type << 4);
        // Variant masks first two bits of the clock_seq_hi:
        this.mId[UUID.INDEX_VARIATION] &= (byte) 0x3F;
        this.mId[UUID.INDEX_VARIATION] |= (byte) 0x80;
    }

    /**
     * Constructor for creating UUIDs from the canonical string
     * representation
     *
     * Note that implementation is optimized for speed, not necessarily
     * code clarity... Also, since what we get might not be 100% canonical
     * (see below), let's not yet populate mDesc here.
     *
     * @param id String that contains the canonical representation of
     *   the UUID to build; 36-char string (see UUID specs for details).
     *   Hex-chars may be in upper-case too; UUID class will always output
     *   them in lowercase.
     */
    public UUID(final String id) throws NumberFormatException {
        if ( id == null ) {
            throw new NullPointerException();
        }
        if ( id.length() != 36 ) {
            throw new NumberFormatException( "UUID has to be represented by the standard 36-char representation" );
        }

        for ( int i = 0, j = 0; i < 36; ++j ) {
            // Need to bypass hyphens:
            switch ( i ) {
                case 8 :
                case 13 :
                case 18 :
                case 23 :
                    if ( id.charAt( i ) != '-' ) {
                        throw new NumberFormatException( "UUID has to be represented by the standard 36-char representation" );
                    }
                    ++i;
            }
            final int index;
            char c = id.charAt( i );

            if ( c >= '0' && c <= '9' ) {
                this.mId[j] = (byte) ((c - '0') << 4);
            } else if ( c >= 'a' && c <= 'f' ) {
                this.mId[j] = (byte) ((c - 'a' + 10) << 4);
            } else if ( c >= 'A' && c <= 'F' ) {
                this.mId[j] = (byte) ((c - 'A' + 10) << 4);
            } else {
                throw new NumberFormatException( "Non-hex character '" + c + "'" );
            }

            c = id.charAt( ++i );

            if ( c >= '0' && c <= '9' ) {
                this.mId[j] |= (byte) (c - '0');
            } else if ( c >= 'a' && c <= 'f' ) {
                this.mId[j] |= (byte) (c - 'a' + 10);
            } else if ( c >= 'A' && c <= 'F' ) {
                this.mId[j] |= (byte) (c - 'A' + 10);
            } else {
                throw new NumberFormatException( "Non-hex character '" + c + "'" );
            }
            ++i;
        }
    }

    /**
     * Default cloning behaviour (bitwise copy) is just fine...
     *
     * Could clear out cached string presentation, but there's
     * probably no point in doing that.
     */
    public Object clone() {
        try {
            return super.clone();
        } catch ( final CloneNotSupportedException e ) {
            // shouldn't happen
            return null;
        }
    }

    /* *** Configuration: *** */
    public static void setDescCaching(final boolean state) {
        UUID.sDescCaching = state;
    }

    /* *** Accessors: *** */

    /**
     * Accessor for getting the shared null UUID
     *
     * @return the shared null UUID
     */
    public static UUID getNullUUID() {
        return UUID.sNullUUID;
    }

    public boolean isNullUUID() {
        // Assuming null uuid is usually used for nulls:
        if ( this == UUID.sNullUUID ) {
            return true;
        }
        // Could also check hash code; null uuid has -1 as hash?
        final byte[] data = this.mId;
        int i = this.mId.length;
        final byte zero = (byte) 0;
        while ( --i >= 0 ) {
            if ( data[i] != zero ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the UUID type code
     *
     * @return UUID type
     */
    public int getType() {
        return (this.mId[UUID.INDEX_TYPE] & 0xFF) >> 4;
    }

    /**
     * Returns the UUID as a 16-byte byte array
     *
     * @return 16-byte byte array that contains UUID bytes in the network
     *   byte order
     */
    public byte[] asByteArray() {
        final byte[] result = new byte[16];
        toByteArray( result );
        return result;
    }

    /**
     * Fills in the 16 bytes (from index pos) of the specified byte array
     * with the UUID contents.
     *
     * @param dst Byte array to fill
     * @param pos Offset in the array
     */
    public void toByteArray(final byte[] dst,
                            final int pos) {
        final byte[] src = this.mId;
        for ( int i = 0; i < 16; ++i ) {
            dst[pos + i] = src[i];
        }
    }

    public void toByteArray(final byte[] dst) {
        toByteArray( dst,
                     0 );
    }

    /**
     * 'Synonym' for 'asByteArray'
     */
    public byte[] toByteArray() {
        return asByteArray();
    }

    /* *** Standard methods from Object overridden: *** */

    /**
     * Could use just the default hash code, but we can probably create
     * a better identity hash (ie. same contents generate same hash)
     * manually, without sacrificing speed too much. Although multiplications
     * with modulos would generate better hashing, let's use just shifts,
     * and do 2 bytes at a time.
     *<p>
     * Of course, assuming UUIDs are randomized enough, even simpler
     * approach might be good enough?
     *<p>
     * Is this a good hash? ... one of these days I better read more about
     * basic hashing techniques I swear!
     */
    private final static int[] kShifts = {3, 7, 17, 21, 29, 4, 9};

    public int hashCode() {
        if ( this.mHashCode == 0 ) {
            // Let's handle first and last byte separately:
            int result = this.mId[0] & 0xFF;

            result |= (result << 16);
            result |= (result << 8);

            for ( int i = 1; i < 15; i += 2 ) {
                final int curr = (this.mId[i] & 0xFF) << 8 | (this.mId[i + 1] & 0xFF);
                final int shift = UUID.kShifts[i >> 1];

                if ( shift > 16 ) {
                    result ^= (curr << shift) | (curr >>> (32 - shift));
                } else {
                    result ^= (curr << shift);
                }
            }

            // and then the last byte:
            final int last = this.mId[15] & 0xFF;
            result ^= (last << 3);
            result ^= (last << 13);

            result ^= (last << 27);
            // Let's not accept hash 0 as it indicates 'not hashed yet':
            if ( result == 0 ) {
                this.mHashCode = -1;
            } else {
                this.mHashCode = result;
            }
        }
        return this.mHashCode;
    }

    public String toString() {
        /* Could be synchronized, but there isn't much harm in just taking
         * our chances (ie. in the worst case we'll form the string more
         * than once... but result is the same)
         */

        if ( this.mDesc == null ) {
            final StringBuffer b = new StringBuffer( 36 );

            for ( int i = 0; i < 16; ++i ) {
                // Need to bypass hyphens:
                switch ( i ) {
                    case 4 :
                    case 6 :
                    case 8 :
                    case 10 :
                        b.append( '-' );
                }
                final int hex = this.mId[i] & 0xFF;
                b.append( UUID.kHexChars.charAt( hex >> 4 ) );
                b.append( UUID.kHexChars.charAt( hex & 0x0f ) );
            }
            if ( !UUID.sDescCaching ) {
                return b.toString();
            }
            this.mDesc = b.toString();
        }
        return this.mDesc;
    }

    /* *** Comparison methods: *** */

    private final static int[] sTimeCompare = new int[]{UUID.INDEX_CLOCK_HI, UUID.INDEX_CLOCK_HI + 1, UUID.INDEX_CLOCK_MID, UUID.INDEX_CLOCK_MID + 1, UUID.INDEX_CLOCK_LO, UUID.INDEX_CLOCK_LO + 1, UUID.INDEX_CLOCK_LO + 2, UUID.INDEX_CLOCK_LO + 3,};

    /**
     * Let's also make UUIDs sortable. This will mostly/only be useful with
     * time-based UUIDs; they will sorted by time of creation. The order
     * will be strictly correct with UUIDs produced over one JVM's lifetime;
     * that is, if more than one JVMs create UUIDs and/or system is rebooted
     * the order may not be 100% accurate between UUIDs created under
     * different JVMs.
     *
     * For all UUIDs, type is first compared, and UUIDs of different types
     * are sorted together (ie. null UUID is before all other UUIDs, then
     * time-based UUIDs etc). If types are the same, time-based UUIDs'
     * time stamps (including additional clock counter) are compared, so
     * UUIDs created first are ordered first. For all other types (and for
     * time-based UUIDs with same time stamp, which should only occur
     * when comparing a UUID with itself, or with UUIDs created on
     * different JVMs or external systems) binary comparison is done
     * over all 16 bytes.
     *
     * @param o Object to compare this UUID to; should be a UUID
     *
     * @return -1 if this UUID should be ordered before the one passed,
     *   1 if after, and 0 if they are the same
     *
     * @throws ClassCastException if o is not a UUID.
     */
    public int compareTo(final Object o) {
        final UUID other = (UUID) o;

        final int thisType = getType();
        final int thatType = other.getType();

        /* Let's first order by type:
         */
        if ( thisType > thatType ) {
            return 1;
        } else if ( thisType < thatType ) {
            return -1;
        }

        /* And for time-based UUIDs let's compare time stamps first,
         * then the rest... For all other types, we'll just do straight
         * byte-by-byte comparison.
         */
        final byte[] thisId = this.mId;
        final byte[] thatId = other.mId;
        int i = 0;
        if ( thisType == UUID.TYPE_TIME_BASED ) {
            for ( ; i < 8; ++i ) {
                final int index = UUID.sTimeCompare[i];
                final int cmp = ((thisId[index]) & 0xFF) - ((thatId[index]) & 0xFF);
                if ( cmp != 0 ) {
                    return cmp;
                }
            }
            // Let's fall down to full comparison otherwise
        }

        for ( ; i < 16; ++i ) {
            final int cmp = ((thisId[i]) & 0xFF) - ((thatId[i]) & 0xFF);
            if ( cmp != 0 ) {
                return cmp;
            }
        }

        return 0;
    }

    /**
     * Checking equality of UUIDs is easy; just compare the 128-bit
     * number.
     */
    public boolean equals(final Object o) {
        if ( !(o instanceof UUID) ) {
            return false;
        }
        final byte[] otherId = ((UUID) o).mId;
        final byte[] thisId = this.mId;
        for ( int i = 0; i < 16; ++i ) {
            if ( otherId[i] != thisId[i] ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Constructs a new UUID instance given the canonical string
     * representation of an UUID.
     *
     * Note that calling this method returns the same result as would
     * using the matching (1 string arg) constructor.
     *
     * @param id Canonical string representation used for constructing
     *  an UUID instance
     *
     * @throws NumberFormatException if 'id' is invalid UUID
     */
    public static UUID valueOf(final String id) throws NumberFormatException {
        return new UUID( id );
    }

    /**
     * Constructs a new UUID instance given a byte array that contains
     * the (16 byte) binary representation.
     *
     * Note that calling this method returns the same result as would
     * using the matching constructor
     *
     * @param src Byte array that contains the UUID definition
     * @param start Offset in the array where the UUID starts
     */
    public static UUID valueOf(final byte[] src,
                               final int start) {
        return new UUID( src,
                         start );
    }

    /**
     * Constructs a new UUID instance given a byte array that contains
     * the (16 byte) binary representation.
     *
     * Note that calling this method returns the same result as would
     * using the matching constructor
     *
     * @param src Byte array that contains the UUID definition
     */
    public static UUID valueOf(final byte[] src) {
        return new UUID( src );
    }

    private void copyFrom(final UUID src) {
        final byte[] srcB = src.mId;
        final byte[] dstB = this.mId;

        for ( int i = 0; i < 16; ++i ) {
            dstB[i] = srcB[i];
        }

        this.mDesc = UUID.sDescCaching ? src.mDesc : null;
    }

}
