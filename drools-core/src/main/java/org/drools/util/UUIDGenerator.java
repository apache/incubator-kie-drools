
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

import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

/**
 * Ripped from JUG
 * 
 * @see org.safehaus.jug
 */
public final class UUIDGenerator
{
    private final static UUIDGenerator sSingleton = new UUIDGenerator();

    /**
     * Random-generator, used by various UUID-generation methods:
     */
    private Random mRnd = null;

    /**
     * MD5 hasher for name-based digests:
     */
    private MessageDigest mHasher = null;

    /*
    /////////////////////////////////////////////////////
    // Life-cycle
    /////////////////////////////////////////////////////
     */

    /**
     * Constructor is private to enforce singleton access.
     */
    private UUIDGenerator() { }

    /**
     * Method used for accessing the singleton generator instance.
     */
    public static UUIDGenerator getInstance()
    {
        return sSingleton;
    }
    
    /*
    /////////////////////////////////////////////////////
    // Configuration
    /////////////////////////////////////////////////////
     */


    /**
     * Method for getting the shared random number generator used for
     * generating the UUIDs. This way the initialization cost is only
     * taken once; access need not be synchronized (or in cases where
     * it has to, SecureRandom takes care of it); it might even be good
     * for getting really 'random' stuff to get shared access...
     */
    public Random getRandomNumberGenerator()
    {
        /* Could be synchronized, but since side effects are trivial
         * (ie. possibility of generating more than one SecureRandom,
         * of which all but one are dumped) let's not add synchronization
         * overhead:
         */
        if (mRnd == null) {
            mRnd = new SecureRandom();
        }
        return mRnd;
    }

    /**
     * Method that can  be called to specify alternative random
     * number generator to use. This is usually done to use
     * implementation that is faster than
     * {@link SecureRandom} that is used by default.
     *<p>
     * Note that to avoid first-time initialization penalty
     * of using {@link SecureRandom}, this method has to be called
     * before generating the first random-number based UUID.
     */
    public void setRandomNumberGenerator(Random r)
    {
        mRnd = r;
    }

    /* Method for getting the shared message digest (hash) algorithm.
     * Whether to use the shared one or not depends; using shared instance
     * adds synchronization overhead (access has to be sync'ed), but
     * using multiple separate digests wastes memory.
     */
    public MessageDigest getHashAlgorithm()
    {
        /* Similar to the shared random number generator, it's not necessary
         * to synchronize initialization. However, use of the hash instance
         * HAS to be synchronized by the caller to prevent problems with
         * multiple threads updating digest etc.
         */
        if (mHasher == null) {
            try {
                mHasher = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException nex) {
                throw new Error("Couldn't instantiate an MD5 MessageDigest instance: "+nex.toString());
            }
        }
        return mHasher;
    }

    /*
    /////////////////////////////////////////////////////
    // UUID generation methods
    /////////////////////////////////////////////////////
     */

    /**
     * Method for generating (pseudo-)random based UUIDs, using the
     * default (shared) SecureRandom object.
     * 
     * Note that the first time
     * SecureRandom object is used, there is noticeable delay between
     * calling the method and getting the reply. This is because SecureRandom
     * has to initialize itself to reasonably random state. Thus, if you
     * want to lessen delay, it may be be a good idea to either get the
     * first random UUID asynchronously from a separate thread, or to
     * use the other generateRandomBasedUUID passing a previously initialized
     * SecureRandom instance.
     *
     * @return UUID generated using (pseudo-)random based method
     */
    public UUID generateRandomBasedUUID()
    {
        return generateRandomBasedUUID(getRandomNumberGenerator());
    }

    /**
     * Method for generating (pseudo-)random based UUIDs, using the
     * specified  SecureRandom object. To prevent/avoid delay JDK's
     * default SecureRandom object causes when first random number
     * is generated, it may be a good idea to initialize the SecureRandom
     * instance (on a separate thread for example) when app starts.
     * 
     * @param randomGenerator Random number generator to use for getting the
     *   random number from which UUID will be composed.
     *
     * @return UUID generated using (pseudo-)random based method
     */
    public UUID generateRandomBasedUUID(Random randomGenerator)
    {
        byte[] rnd = new byte[16];
        
        randomGenerator.nextBytes(rnd);
        
        return new UUID(UUID.TYPE_RANDOM_BASED, rnd);
    }
}
