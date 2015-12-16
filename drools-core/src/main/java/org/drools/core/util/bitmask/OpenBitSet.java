/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util.bitmask;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;


/** An "open" BitSet implementation that allows direct access to the array of words
 * storing the bits.
 * <p/>
 * Unlike java.util.bitset, the fact that bits are packed into an array of longs
 * is part of the interface.  This allows efficient implementation of other algorithms
 * by someone other than the author.  It also allows one to efficiently implement
 * alternate serialization or interchange formats.
 * <p/>
 * <code>OpenBitSet</code> is faster than <code>java.util.BitSet</code> in most operations
 * and *much* faster at calculating cardinality of sets and results of set operations.
 * It can also handle sets of larger cardinality (up to 64 * 2**32-1)
 * <p/>
 * The goals of <code>OpenBitSet</code> are the fastest implementation possible, and
 * maximum code reuse.  Extra safety and encapsulation
 * may always be built on top, but if that's built in, the cost can never be removed (and
 * hence people re-implement their own version in order to get better performance).
 * If you want a "safe", totally encapsulated (and slower and limited) BitSet
 * class, use <code>java.util.BitSet</code>.
 * <p/>
 * <h3>Performance Results</h3>
 *
 Test system: Pentium 4, Sun Java 1.5_06 -server -Xbatch -Xmx64M
 <br/>BitSet size = 1,000,000
 <br/>Results are java.util.BitSet time divided by OpenBitSet time.
 <table border="1">
 <tr>
 <th></th> <th>cardinality</th> <th>intersect_count</th> <th>union</th> <th>nextSetBit</th> <th>get</th> <th>iterator</th>
 </tr>
 <tr>
 <th>50% full</th> <td>3.36</td> <td>3.96</td> <td>1.44</td> <td>1.46</td> <td>1.99</td> <td>1.58</td>
 </tr>
 <tr>
 <th>1% full</th> <td>3.31</td> <td>3.90</td> <td>&nbsp;</td> <td>1.04</td> <td>&nbsp;</td> <td>0.99</td>
 </tr>
 </table>
 <br/>
 Test system: AMD Opteron, 64 bit linux, Sun Java 1.5_06 -server -Xbatch -Xmx64M
 <br/>BitSet size = 1,000,000
 <br/>Results are java.util.BitSet time divided by OpenBitSet time.
 <table border="1">
 <tr>
 <th></th> <th>cardinality</th> <th>intersect_count</th> <th>union</th> <th>nextSetBit</th> <th>get</th> <th>iterator</th>
 </tr>
 <tr>
 <th>50% full</th> <td>2.50</td> <td>3.50</td> <td>1.00</td> <td>1.03</td> <td>1.12</td> <td>1.25</td>
 </tr>
 <tr>
 <th>1% full</th> <td>2.51</td> <td>3.49</td> <td>&nbsp;</td> <td>1.00</td> <td>&nbsp;</td> <td>1.02</td>
 </tr>
 </table>
 */

public class OpenBitSet implements BitMask {
    protected long[] bits;
    protected int wlen;   // number of words (elements) used in the array

    // Used only for assert:
    private long numBits;

    /** Constructs an OpenBitSet large enough to hold <code>numBits</code>.
     */
    public OpenBitSet(long numBits) {
        this.numBits = numBits;
        bits = new long[bits2words(numBits)];
        wlen = bits.length;
    }

    public OpenBitSet() {
        this(64);
    }

    /** Constructs an OpenBitSet from an existing long[].
     * <br/>
     * The first 64 bits are in long[0],
     * with bit index 0 at the least significant bit, and bit index 63 at the most significant.
     * Given a bit index,
     * the word containing it is long[index/64], and it is at bit number index%64 within that word.
     * <p>
     * numWords are the number of elements in the array that contain
     * set bits (non-zero longs).
     * numWords should be &lt= bits.length, and
     * any existing words in the array at position &gt= numWords should be zero.
     *
     */
    public OpenBitSet(long[] bits, int numWords) {
        this.bits = bits;
        this.wlen = numWords;
        this.numBits = wlen * 64;
    }

    /** Returns the current capacity in bits (1 greater than the index of the last bit) */
    public long capacity() { return bits.length << 6; }

    /**
     * Returns the current capacity of this set.  Included for
     * compatibility.  This is *not* equal to {@link #cardinality}
     */
    public long size() {
        return capacity();
    }

    public int length() {
        return bits.length << 6;
    }

    /** Returns true if there are no set bits */
    public boolean isEmpty() { return cardinality()==0; }

    /** Expert: returns the long[] storing the bits */
    public long[] getBits() { return bits; }

    /** Expert: sets a new long[] to use as the bit storage */
    public void setBits(long[] bits) { this.bits = bits; }

    /** Expert: gets the number of longs in the array that are in use */
    public int getNumWords() { return wlen; }

    /** Expert: sets the number of longs in the array that are in use */
    public void setNumWords(int nWords) { this.wlen=nWords; }



    /** Returns true or false for the specified bit index. */
    public boolean get(int index) {
        int i = index >> 6;               // div 64
        // signed shift will keep a negative index and force an
        // array-index-out-of-bounds-exception, removing the need for an explicit check.
        if (i>=bits.length) return false;

        int bit = index & 0x3f;           // mod 64
        long bitmask = 1L << bit;
        return (bits[i] & bitmask) != 0;
    }


    /** Returns true or false for the specified bit index.
     * The index should be less than the OpenBitSet size
     */
    public boolean fastGet(int index) {
        assert index >= 0 && index < numBits;
        int i = index >> 6;               // div 64
        // signed shift will keep a negative index and force an
        // array-index-out-of-bounds-exception, removing the need for an explicit check.
        int bit = index & 0x3f;           // mod 64
        long bitmask = 1L << bit;
        return (bits[i] & bitmask) != 0;
    }



    /** Returns true or false for the specified bit index
     */
    public boolean get(long index) {
        int i = (int)(index >> 6);             // div 64
        if (i>=bits.length) return false;
        int bit = (int)index & 0x3f;           // mod 64
        long bitmask = 1L << bit;
        return (bits[i] & bitmask) != 0;
    }

    /** Returns true or false for the specified bit index.
     * The index should be less than the OpenBitSet size.
     */
    public boolean fastGet(long index) {
        assert index >= 0 && index < numBits;
        int i = (int)(index >> 6);               // div 64
        int bit = (int)index & 0x3f;           // mod 64
        long bitmask = 1L << bit;
        return (bits[i] & bitmask) != 0;
    }

  /*
  // alternate implementation of get()
  public boolean get1(int index) {
    int i = index >> 6;                // div 64
    int bit = index & 0x3f;            // mod 64
    return ((bits[i]>>>bit) & 0x01) != 0;
    // this does a long shift and a bittest (on x86) vs
    // a long shift, and a long AND, (the test for zero is prob a no-op)
    // testing on a P4 indicates this is slower than (bits[i] & bitmask) != 0;
  }
  */


    /** returns 1 if the bit is set, 0 if not.
     * The index should be less than the OpenBitSet size
     */
    public int getBit(int index) {
        assert index >= 0 && index < numBits;
        int i = index >> 6;                // div 64
        int bit = index & 0x3f;            // mod 64
        return ((int)(bits[i]>>>bit)) & 0x01;
    }


  /*
  public boolean get2(int index) {
    int word = index >> 6;            // div 64
    int bit = index & 0x0000003f;     // mod 64
    return (bits[word] << bit) < 0;   // hmmm, this would work if bit order were reversed
    // we could right shift and check for parity bit, if it was available to us.
  }
  */

    /** sets a bit, expanding the set size if necessary */
    public void set(long index) {
        int wordNum = expandingWordNum(index);
        int bit = (int)index & 0x3f;
        long bitmask = 1L << bit;
        bits[wordNum] |= bitmask;
    }


    /** Sets the bit at the specified index.
     * The index should be less than the OpenBitSet size.
     */
    public void fastSet(int index) {
        assert index >= 0 && index < numBits;
        int wordNum = index >> 6;      // div 64
        int bit = index & 0x3f;     // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] |= bitmask;
    }

    /** Sets the bit at the specified index.
     * The index should be less than the OpenBitSet size.
     */
    public void fastSet(long index) {
        assert index >= 0 && index < numBits;
        int wordNum = (int)(index >> 6);
        int bit = (int)index & 0x3f;
        long bitmask = 1L << bit;
        bits[wordNum] |= bitmask;
    }

    /** Sets a range of bits, expanding the set size if necessary
     *
     * @param startIndex lower index
     * @param endIndex one-past the last bit to set
     */
    public void set(long startIndex, long endIndex) {
        if (endIndex <= startIndex) return;

        int startWord = (int)(startIndex>>6);

        // since endIndex is one past the end, this is index of the last
        // word to be changed.
        int endWord   = expandingWordNum(endIndex-1);

        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;  // 64-(endIndex&0x3f) is the same as -endIndex due to wrap

        if (startWord == endWord) {
            bits[startWord] |= (startmask & endmask);
            return;
        }

        bits[startWord] |= startmask;
        Arrays.fill(bits, startWord+1, endWord, -1L);
        bits[endWord] |= endmask;
    }



    protected int expandingWordNum(long index) {
        int wordNum = (int)(index >> 6);
        if (wordNum>=wlen) {
            ensureCapacity(index+1);
            wlen = wordNum+1;
        }
        assert (numBits = Math.max(numBits, index+1)) >= 0;
        return wordNum;
    }


    /** clears a bit.
     * The index should be less than the OpenBitSet size.
     */
    public void fastClear(int index) {
        assert index >= 0 && index < numBits;
        int wordNum = index >> 6;
        int bit = index & 0x03f;
        long bitmask = 1L << bit;
        bits[wordNum] &= ~bitmask;
        // hmmm, it takes one more instruction to clear than it does to set... any
        // way to work around this?  If there were only 63 bits per word, we could
        // use a right shift of 10111111...111 in binary to position the 0 in the
        // correct place (using sign extension).
        // Could also use Long.rotateRight() or rotateLeft() *if* they were converted
        // by the JVM into a native instruction.
        // bits[word] &= Long.rotateLeft(0xfffffffe,bit);
    }

    /** clears a bit.
     * The index should be less than the OpenBitSet size.
     */
    public void fastClear(long index) {
        assert index >= 0 && index < numBits;
        int wordNum = (int)(index >> 6); // div 64
        int bit = (int)index & 0x3f;     // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] &= ~bitmask;
    }

    /** clears a bit, allowing access beyond the current set size without changing the size.*/
    public void clear(long index) {
        int wordNum = (int)(index >> 6); // div 64
        if (wordNum>=wlen) return;
        int bit = (int)index & 0x3f;     // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] &= ~bitmask;
    }

    /** Clears a range of bits.  Clearing past the end does not change the size of the set.
     *
     * @param startIndex lower index
     * @param endIndex one-past the last bit to clear
     */
    public void clear(int startIndex, int endIndex) {
        if (endIndex <= startIndex) return;

        int startWord = (startIndex>>6);
        if (startWord >= wlen) return;

        // since endIndex is one past the end, this is index of the last
        // word to be changed.
        int endWord   = ((endIndex-1)>>6);

        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;  // 64-(endIndex&0x3f) is the same as -endIndex due to wrap

        // invert masks since we are clearing
        startmask = ~startmask;
        endmask = ~endmask;

        if (startWord == endWord) {
            bits[startWord] &= (startmask | endmask);
            return;
        }

        bits[startWord] &= startmask;

        int middle = Math.min(wlen, endWord);
        Arrays.fill(bits, startWord+1, middle, 0L);
        if (endWord < wlen) {
            bits[endWord] &= endmask;
        }
    }


    /** Clears a range of bits.  Clearing past the end does not change the size of the set.
     *
     * @param startIndex lower index
     * @param endIndex one-past the last bit to clear
     */
    public void clear(long startIndex, long endIndex) {
        if (endIndex <= startIndex) return;

        int startWord = (int)(startIndex>>6);
        if (startWord >= wlen) return;

        // since endIndex is one past the end, this is index of the last
        // word to be changed.
        int endWord   = (int)((endIndex-1)>>6);

        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;  // 64-(endIndex&0x3f) is the same as -endIndex due to wrap

        // invert masks since we are clearing
        startmask = ~startmask;
        endmask = ~endmask;

        if (startWord == endWord) {
            bits[startWord] &= (startmask | endmask);
            return;
        }

        bits[startWord] &= startmask;

        int middle = Math.min(wlen, endWord);
        Arrays.fill(bits, startWord+1, middle, 0L);
        if (endWord < wlen) {
            bits[endWord] &= endmask;
        }
    }



    /** Sets a bit and returns the previous value.
     * The index should be less than the OpenBitSet size.
     */
    public boolean getAndSet(int index) {
        assert index >= 0 && index < numBits;
        int wordNum = index >> 6;      // div 64
        int bit = index & 0x3f;     // mod 64
        long bitmask = 1L << bit;
        boolean val = (bits[wordNum] & bitmask) != 0;
        bits[wordNum] |= bitmask;
        return val;
    }

    /** Sets a bit and returns the previous value.
     * The index should be less than the OpenBitSet size.
     */
    public boolean getAndSet(long index) {
        assert index >= 0 && index < numBits;
        int wordNum = (int)(index >> 6);      // div 64
        int bit = (int)index & 0x3f;     // mod 64
        long bitmask = 1L << bit;
        boolean val = (bits[wordNum] & bitmask) != 0;
        bits[wordNum] |= bitmask;
        return val;
    }

    /** flips a bit.
     * The index should be less than the OpenBitSet size.
     */
    public void fastFlip(int index) {
        assert index >= 0 && index < numBits;
        int wordNum = index >> 6;      // div 64
        int bit = index & 0x3f;     // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] ^= bitmask;
    }

    /** flips a bit.
     * The index should be less than the OpenBitSet size.
     */
    public void fastFlip(long index) {
        assert index >= 0 && index < numBits;
        int wordNum = (int)(index >> 6);   // div 64
        int bit = (int)index & 0x3f;       // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] ^= bitmask;
    }

    /** flips a bit, expanding the set size if necessary */
    public void flip(long index) {
        int wordNum = expandingWordNum(index);
        int bit = (int)index & 0x3f;       // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] ^= bitmask;
    }

    /** flips a bit and returns the resulting bit value.
     * The index should be less than the OpenBitSet size.
     */
    public boolean flipAndGet(int index) {
        assert index >= 0 && index < numBits;
        int wordNum = index >> 6;      // div 64
        int bit = index & 0x3f;     // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] ^= bitmask;
        return (bits[wordNum] & bitmask) != 0;
    }

    /** flips a bit and returns the resulting bit value.
     * The index should be less than the OpenBitSet size.
     */
    public boolean flipAndGet(long index) {
        assert index >= 0 && index < numBits;
        int wordNum = (int)(index >> 6);   // div 64
        int bit = (int)index & 0x3f;       // mod 64
        long bitmask = 1L << bit;
        bits[wordNum] ^= bitmask;
        return (bits[wordNum] & bitmask) != 0;
    }

    /** Flips a range of bits, expanding the set size if necessary
     *
     * @param startIndex lower index
     * @param endIndex one-past the last bit to flip
     */
    public void flip(long startIndex, long endIndex) {
        if (endIndex <= startIndex) return;
        int startWord = (int)(startIndex>>6);

        // since endIndex is one past the end, this is index of the last
        // word to be changed.
        int endWord   = expandingWordNum(endIndex-1);

        /*** Grrr, java shifting wraps around so -1L>>>64 == -1
         * for that reason, make sure not to use endmask if the bits to flip will
         * be zero in the last word (redefine endWord to be the last changed...)
         long startmask = -1L << (startIndex & 0x3f);     // example: 11111...111000
         long endmask = -1L >>> (64-(endIndex & 0x3f));   // example: 00111...111111
         ***/

        long startmask = -1L << startIndex;
        long endmask = -1L >>> -endIndex;  // 64-(endIndex&0x3f) is the same as -endIndex due to wrap

        if (startWord == endWord) {
            bits[startWord] ^= (startmask & endmask);
            return;
        }

        bits[startWord] ^= startmask;

        for (int i=startWord+1; i<endWord; i++) {
            bits[i] = ~bits[i];
        }

        bits[endWord] ^= endmask;
    }


  /*
  public static int pop(long v0, long v1, long v2, long v3) {
    // derived from pop_array by setting last four elems to 0.
    // exchanges one pop() call for 10 elementary operations
    // saving about 7 instructions... is there a better way?
      long twosA=v0 & v1;
      long ones=v0^v1;

      long u2=ones^v2;
      long twosB =(ones&v2)|(u2&v3);
      ones=u2^v3;

      long fours=(twosA&twosB);
      long twos=twosA^twosB;

      return (pop(fours)<<2)
             + (pop(twos)<<1)
             + pop(ones);

  }
  */


    /** @return the number of set bits */
    public long cardinality() {
        return BitUtil.pop_array(bits, 0, wlen);
    }

    /** Returns the popcount or cardinality of the intersection of the two sets.
     * Neither set is modified.
     */
    public static long intersectionCount(OpenBitSet a, OpenBitSet b) {
        return BitUtil.pop_intersect(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
    }

    /** Returns the popcount or cardinality of the union of the two sets.
     * Neither set is modified.
     */
    public static long unionCount(OpenBitSet a, OpenBitSet b) {
        long tot = BitUtil.pop_union(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
        if (a.wlen < b.wlen) {
            tot += BitUtil.pop_array(b.bits, a.wlen, b.wlen - a.wlen);
        } else if (a.wlen > b.wlen) {
            tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
        }
        return tot;
    }

    /** Returns the popcount or cardinality of "a and not b"
     * or "intersection(a, not(b))".
     * Neither set is modified.
     */
    public static long andNotCount(OpenBitSet a, OpenBitSet b) {
        long tot = BitUtil.pop_andnot(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
        if (a.wlen > b.wlen) {
            tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
        }
        return tot;
    }

    /** Returns the popcount or cardinality of the exclusive-or of the two sets.
     * Neither set is modified.
     */
    public static long xorCount(OpenBitSet a, OpenBitSet b) {
        long tot = BitUtil.pop_xor(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
        if (a.wlen < b.wlen) {
            tot += BitUtil.pop_array(b.bits, a.wlen, b.wlen - a.wlen);
        } else if (a.wlen > b.wlen) {
            tot += BitUtil.pop_array(a.bits, b.wlen, a.wlen - b.wlen);
        }
        return tot;
    }


    /** Returns the index of the first set bit starting at the index specified.
     *  -1 is returned if there are no more set bits.
     */
    public int nextSetBit(int index) {
        int i = index>>6;
        if (i>=wlen) return -1;
        int subIndex = index & 0x3f;      // index within the word
        long word = bits[i] >> subIndex;  // skip all the bits to the right of index

        if (word!=0) {
            return (i<<6) + subIndex + Long.numberOfTrailingZeros(word);
        }

        while(++i < wlen) {
            word = bits[i];
            if (word!=0) return (i<<6) + Long.numberOfTrailingZeros(word);
        }

        return -1;
    }

    /** Returns the index of the first set bit starting at the index specified.
     *  -1 is returned if there are no more set bits.
     */
    public long nextSetBit(long index) {
        int i = (int)(index>>>6);
        if (i>=wlen) return -1;
        int subIndex = (int)index & 0x3f; // index within the word
        long word = bits[i] >>> subIndex;  // skip all the bits to the right of index

        if (word!=0) {
            return (((long)i)<<6) + (subIndex + Long.numberOfTrailingZeros(word));
        }

        while(++i < wlen) {
            word = bits[i];
            if (word!=0) return (((long)i)<<6) + Long.numberOfTrailingZeros(word);
        }

        return -1;
    }


    /** Returns the index of the first set bit starting downwards at
     *  the index specified.
     *  -1 is returned if there are no more set bits.
     */
    public int prevSetBit(int index) {
        int i = index >> 6;
        final int subIndex;
        long word;
        if (i >= wlen) {
            i = wlen - 1;
            if (i < 0) return -1;
            subIndex = 63;  // last possible bit
            word = bits[i];
        } else {
            if (i < 0) return -1;
            subIndex = index & 0x3f;  // index within the word
            word = (bits[i] << (63-subIndex));  // skip all the bits to the left of index
        }

        if (word != 0) {
            return (i << 6) + subIndex - Long.numberOfLeadingZeros(word); // See LUCENE-3197
        }

        while (--i >= 0) {
            word = bits[i];
            if (word !=0 ) {
                return (i << 6) + 63 - Long.numberOfLeadingZeros(word);
            }
        }

        return -1;
    }

    /** Returns the index of the first set bit starting downwards at
     *  the index specified.
     *  -1 is returned if there are no more set bits.
     */
    public long prevSetBit(long index) {
        int i = (int) (index >> 6);
        final int subIndex;
        long word;
        if (i >= wlen) {
            i = wlen - 1;
            if (i < 0) return -1;
            subIndex = 63;  // last possible bit
            word = bits[i];
        } else {
            if (i < 0) return -1;
            subIndex = (int)index & 0x3f;  // index within the word
            word = (bits[i] << (63-subIndex));  // skip all the bits to the left of index
        }

        if (word != 0) {
            return (((long)i)<<6) + subIndex - Long.numberOfLeadingZeros(word); // See LUCENE-3197
        }

        while (--i >= 0) {
            word = bits[i];
            if (word !=0 ) {
                return (((long)i)<<6) + 63 - Long.numberOfLeadingZeros(word);
            }
        }

        return -1;
    }

    @Override
    public OpenBitSet clone() {
        try {
            OpenBitSet obs = (OpenBitSet)super.clone();
            obs.bits = obs.bits.clone();  // hopefully an array clone is as fast(er) than arraycopy
            return obs;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /** this = this AND other */
    public void intersect(OpenBitSet other) {
        int newLen= Math.min(this.wlen,other.wlen);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        // testing against zero can be more efficient
        int pos=newLen;
        while(--pos>=0) {
            thisArr[pos] &= otherArr[pos];
        }
        if (this.wlen > newLen) {
            // fill zeros from the new shorter length to the old length
            Arrays.fill(bits,newLen,this.wlen,0);
        }
        this.wlen = newLen;
    }

    /** this = this OR other */
    public void union(OpenBitSet other) {
        int newLen = Math.max(wlen,other.wlen);
        ensureCapacityWords(newLen);
        assert (numBits = Math.max(other.numBits, numBits)) >= 0;

        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        int pos=Math.min(wlen,other.wlen);
        while(--pos>=0) {
            thisArr[pos] |= otherArr[pos];
        }
        if (this.wlen < newLen) {
            System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen-this.wlen);
        }
        this.wlen = newLen;
    }


    /** Remove all elements set in other. this = this AND_NOT other */
    public void remove(OpenBitSet other) {
        int idx = Math.min(wlen,other.wlen);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        while(--idx>=0) {
            thisArr[idx] &= ~otherArr[idx];
        }
    }

    /** this = this XOR other */
    public void xor(OpenBitSet other) {
        int newLen = Math.max(wlen,other.wlen);
        ensureCapacityWords(newLen);
        assert (numBits = Math.max(other.numBits, numBits)) >= 0;

        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        int pos=Math.min(wlen,other.wlen);
        while(--pos>=0) {
            thisArr[pos] ^= otherArr[pos];
        }
        if (this.wlen < newLen) {
            System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen-this.wlen);
        }
        this.wlen = newLen;
    }


    // some BitSet compatability methods

    //** see {@link intersects} */
    public void and(OpenBitSet other) {
        intersect(other);
    }

    //** see {@link union} */
    public void or(OpenBitSet other) {
        union(other);
    }

    //** see {@link andNot} */
    public void andNot(OpenBitSet other) {
        remove(other);
    }

    /** returns true if the sets have any elements in common */
    public boolean intersects(OpenBitSet other) {
        int pos = Math.min(this.wlen, other.wlen);
        long[] thisArr = this.bits;
        long[] otherArr = other.bits;
        while (--pos>=0) {
            if ((thisArr[pos] & otherArr[pos])!=0) return true;
        }
        return false;
    }



    /** Expand the long[] with the size given as a number of words (64 bit longs).
     * getNumWords() is unchanged by this call.
     */
    public void ensureCapacityWords(int numWords) {
        if (bits.length < numWords) {
            bits = grow(bits, numWords);
        }
    }

    public static long[] grow(long[] array, int minSize) {
        assert minSize >= 0: "size must be positive (got " + minSize + "): likely integer overflow?";
        if (array.length < minSize) {
            long[] newArray = new long[oversize(minSize, NUM_BYTES_LONG)];
            System.arraycopy(array, 0, newArray, 0, array.length);
            return newArray;
        } else
            return array;
    }


    public final static int NUM_BYTES_LONG = 8;
    public static final String OS_ARCH = System.getProperty("os.arch");
    public static final boolean JRE_IS_MINIMUM_JAVA7;
    public static final boolean JRE_IS_MINIMUM_JAVA8;

    /** True iff running on a 64bit JVM */
    public static final boolean JRE_IS_64BIT;

    static {
        boolean is64Bit = false;
        try {
            final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            final Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            final Object unsafe = unsafeField.get(null);
            final int addressSize = ((Number) unsafeClass.getMethod("addressSize")
                                                         .invoke(unsafe)).intValue();
            //System.out.println("Address size: " + addressSize);
            is64Bit = addressSize >= 8;
        } catch (Exception e) {
            final String x = System.getProperty("sun.arch.data.model");
            if (x != null) {
                is64Bit = x.indexOf("64") != -1;
            } else {
                if (OS_ARCH != null && OS_ARCH.indexOf("64") != -1) {
                    is64Bit = true;
                } else {
                    is64Bit = false;
                }
            }
        }
        JRE_IS_64BIT = is64Bit;

        // this method only exists in Java 7:
        boolean v7 = true;
        try {
            Throwable.class.getMethod("getSuppressed");
        } catch (NoSuchMethodException nsme) {
            v7 = false;
        }
        JRE_IS_MINIMUM_JAVA7 = v7;

        if (JRE_IS_MINIMUM_JAVA7) {
            // this method only exists in Java 8:
            boolean v8 = true;
            try {
                Collections.class.getMethod("emptySortedSet");
            } catch (NoSuchMethodException nsme) {
                v8 = false;
            }
            JRE_IS_MINIMUM_JAVA8 = v8;
        } else {
            JRE_IS_MINIMUM_JAVA8 = false;
        }
    }

    /** Returns an array size >= minTargetSize, generally
     *  over-allocating exponentially to achieve amortized
     *  linear-time cost as the array grows.
     *
     *  NOTE: this was originally borrowed from Python 2.4.2
     *  listobject.c sources (attribution in LICENSE.txt), but
     *  has now been substantially changed based on
     *  discussions from java-dev thread with subject "Dynamic
     *  array reallocation algorithms", started on Jan 12
     *  2010.
     *
     * @param minTargetSize Minimum required value to be returned.
     * @param bytesPerElement Bytes used by each element of
     * the array.
     *
     * @lucene.internal
     */

    public static int oversize(int minTargetSize, int bytesPerElement) {

        if (minTargetSize < 0) {
            // catch usage that accidentally overflows int
            throw new IllegalArgumentException("invalid array size " + minTargetSize);
        }

        if (minTargetSize == 0) {
            // wait until at least one element is requested
            return 0;
        }

        // asymptotic exponential growth by 1/8th, favors
        // spending a bit more CPU to not tie up too much wasted
        // RAM:
        int extra = minTargetSize >> 3;

        if (extra < 3) {
            // for very small arrays, where constant overhead of
            // realloc is presumably relatively high, we grow
            // faster
            extra = 3;
        }

        int newSize = minTargetSize + extra;

        // add 7 to allow for worst case byte alignment addition below:
        if (newSize+7 < 0) {
            // int overflowed -- return max allowed array size
            return Integer.MAX_VALUE;
        }

        if (JRE_IS_64BIT) {
            // round up to 8 byte alignment in 64bit env
            switch(bytesPerElement) {
                case 4:
                    // round up to project of 2
                    return (newSize + 1) & 0x7ffffffe;
                case 2:
                    // round up to project of 4
                    return (newSize + 3) & 0x7ffffffc;
                case 1:
                    // round up to project of 8
                    return (newSize + 7) & 0x7ffffff8;
                case 8:
                    // no rounding
                default:
                    // odd (invalid?) size
                    return newSize;
            }
        } else {
            // round up to 4 byte alignment in 64bit env
            switch(bytesPerElement) {
                case 2:
                    // round up to project of 2
                    return (newSize + 1) & 0x7ffffffe;
                case 1:
                    // round up to project of 4
                    return (newSize + 3) & 0x7ffffffc;
                case 4:
                case 8:
                    // no rounding
                default:
                    // odd (invalid?) size
                    return newSize;
            }
        }
    }


    /** Ensure that the long[] is big enough to hold numBits, expanding it if necessary.
     * getNumWords() is unchanged by this call.
     */
    public void ensureCapacity(long numBits) {
        ensureCapacityWords(bits2words(numBits));
    }

    /** Lowers numWords, the number of words in use,
     * by checking for trailing zero words.
     */
    public void trimTrailingZeros() {
        int idx = wlen-1;
        while (idx>=0 && bits[idx]==0) idx--;
        wlen = idx+1;
    }

    /** returns the number of 64 bit words it would take to hold numBits */
    public static int bits2words(long numBits) {
        return (int)(((numBits-1)>>>6)+1);
    }


    /** returns true if both sets have the same bits set */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OpenBitSet)) return false;
        OpenBitSet a;
        OpenBitSet b = (OpenBitSet)o;
        // make a the larger set.
        if (b.wlen > this.wlen) {
            a = b; b=this;
        } else {
            a=this;
        }

        // check for any set bits out of the range of b
        for (int i=a.wlen-1; i>=b.wlen; i--) {
            if (a.bits[i]!=0) return false;
        }

        for (int i=b.wlen-1; i>=0; i--) {
            if (a.bits[i] != b.bits[i]) return false;
        }

        return true;
    }


    @Override
    public int hashCode() {
        // Start with a zero hash and use a mix that results in zero if the input is zero.
        // This effectively truncates trailing zeros without an explicit check.
        long h = 0;
        for (int i = bits.length; --i>=0;) {
            h ^= bits[i];
            h = (h << 1) | (h >>> 63); // rotate left
        }
        // fold leftmost bits into right and add a constant to prevent
        // empty sets from returning 0, which is too common.
        return (int)((h>>32) ^ h) + 0x98761234;
    }

    public static final class BitUtil {

        private static final byte[] BYTE_COUNTS = {  // table of bits/byte
                                                     0, 1, 1, 2, 1, 2, 2, 3, 1, 2, 2, 3, 2, 3, 3, 4,
                                                     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                                                     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                                                     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                                                     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                                                     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                                                     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                                                     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                                                     1, 2, 2, 3, 2, 3, 3, 4, 2, 3, 3, 4, 3, 4, 4, 5,
                                                     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                                                     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                                                     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                                                     2, 3, 3, 4, 3, 4, 4, 5, 3, 4, 4, 5, 4, 5, 5, 6,
                                                     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                                                     3, 4, 4, 5, 4, 5, 5, 6, 4, 5, 5, 6, 5, 6, 6, 7,
                                                     4, 5, 5, 6, 5, 6, 6, 7, 5, 6, 6, 7, 6, 7, 7, 8
        };

        // The General Idea: instead of having an array per byte that has
        // the offsets of the next set bit, that array could be
        // packed inside a 32 bit integer (8 4 bit numbers).  That
        // should be faster than accessing an array for each index, and
        // the total array size is kept smaller (256*sizeof(int))=1K
        /***** the python code that generated bitlist
         def bits2int(val):
         arr=0
         for shift in range(8,0,-1):
         if val & 0x80:
         arr = (arr << 4) | shift
         val = val << 1
         return arr

         def int_table():
         tbl = [ hex(bits2int(val)).strip('L') for val in range(256) ]
         return ','.join(tbl)
         ******/
        private static final int[] BIT_LISTS = {
                0x0, 0x1, 0x2, 0x21, 0x3, 0x31, 0x32, 0x321, 0x4, 0x41, 0x42, 0x421, 0x43,
                0x431, 0x432, 0x4321, 0x5, 0x51, 0x52, 0x521, 0x53, 0x531, 0x532, 0x5321,
                0x54, 0x541, 0x542, 0x5421, 0x543, 0x5431, 0x5432, 0x54321, 0x6, 0x61, 0x62,
                0x621, 0x63, 0x631, 0x632, 0x6321, 0x64, 0x641, 0x642, 0x6421, 0x643,
                0x6431, 0x6432, 0x64321, 0x65, 0x651, 0x652, 0x6521, 0x653, 0x6531, 0x6532,
                0x65321, 0x654, 0x6541, 0x6542, 0x65421, 0x6543, 0x65431, 0x65432, 0x654321,
                0x7, 0x71, 0x72, 0x721, 0x73, 0x731, 0x732, 0x7321, 0x74, 0x741, 0x742,
                0x7421, 0x743, 0x7431, 0x7432, 0x74321, 0x75, 0x751, 0x752, 0x7521, 0x753,
                0x7531, 0x7532, 0x75321, 0x754, 0x7541, 0x7542, 0x75421, 0x7543, 0x75431,
                0x75432, 0x754321, 0x76, 0x761, 0x762, 0x7621, 0x763, 0x7631, 0x7632,
                0x76321, 0x764, 0x7641, 0x7642, 0x76421, 0x7643, 0x76431, 0x76432, 0x764321,
                0x765, 0x7651, 0x7652, 0x76521, 0x7653, 0x76531, 0x76532, 0x765321, 0x7654,
                0x76541, 0x76542, 0x765421, 0x76543, 0x765431, 0x765432, 0x7654321, 0x8,
                0x81, 0x82, 0x821, 0x83, 0x831, 0x832, 0x8321, 0x84, 0x841, 0x842, 0x8421,
                0x843, 0x8431, 0x8432, 0x84321, 0x85, 0x851, 0x852, 0x8521, 0x853, 0x8531,
                0x8532, 0x85321, 0x854, 0x8541, 0x8542, 0x85421, 0x8543, 0x85431, 0x85432,
                0x854321, 0x86, 0x861, 0x862, 0x8621, 0x863, 0x8631, 0x8632, 0x86321, 0x864,
                0x8641, 0x8642, 0x86421, 0x8643, 0x86431, 0x86432, 0x864321, 0x865, 0x8651,
                0x8652, 0x86521, 0x8653, 0x86531, 0x86532, 0x865321, 0x8654, 0x86541,
                0x86542, 0x865421, 0x86543, 0x865431, 0x865432, 0x8654321, 0x87, 0x871,
                0x872, 0x8721, 0x873, 0x8731, 0x8732, 0x87321, 0x874, 0x8741, 0x8742,
                0x87421, 0x8743, 0x87431, 0x87432, 0x874321, 0x875, 0x8751, 0x8752, 0x87521,
                0x8753, 0x87531, 0x87532, 0x875321, 0x8754, 0x87541, 0x87542, 0x875421,
                0x87543, 0x875431, 0x875432, 0x8754321, 0x876, 0x8761, 0x8762, 0x87621,
                0x8763, 0x87631, 0x87632, 0x876321, 0x8764, 0x87641, 0x87642, 0x876421,
                0x87643, 0x876431, 0x876432, 0x8764321, 0x8765, 0x87651, 0x87652, 0x876521,
                0x87653, 0x876531, 0x876532, 0x8765321, 0x87654, 0x876541, 0x876542,
                0x8765421, 0x876543, 0x8765431, 0x8765432, 0x87654321
        };

        private BitUtil() {} // no instance

        /** Return the number of bits sets in b. */
        public static int bitCount(byte b) {
            return BYTE_COUNTS[b & 0xFF];
        }

        /** Return the list of bits which are set in b encoded as followed:
         * <code>(i >>> (4 * n)) & 0x0F</code> is the offset of the n-th set bit of
         * the given byte plus one, or 0 if there are n or less bits set in the given
         * byte. For example <code>bitList(12)</code> returns 0x43:<ul>
         * <li><code>0x43 & 0x0F</code> is 3, meaning the the first bit set is at offset 3-1 = 2,</li>
         * <li><code>(0x43 >>> 4) & 0x0F</code> is 4, meaning there is a second bit set at offset 4-1=3,</li>
         * <li><code>(0x43 >>> 8) & 0x0F</code> is 0, meaning there is no more bit set in this byte.</li>
         * </ul>*/
        public static int bitList(byte b) {
            return BIT_LISTS[b & 0xFF];
        }

        // The pop methods used to rely on bit-manipulation tricks for speed but it
        // turns out that it is faster to use the Long.bitCount method (which is an
        // intrinsic since Java 6u18) in a naive loop, see LUCENE-2221

        /** Returns the number of set bits in an array of longs. */
        public static long pop_array(long[] arr, int wordOffset, int numWords) {
            long popCount = 0;
            for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
                popCount += Long.bitCount(arr[i]);
            }
            return popCount;
        }

        /** Returns the popcount or cardinality of the two sets after an intersection.
         *  Neither array is modified. */
        public static long pop_intersect(long[] arr1, long[] arr2, int wordOffset, int numWords) {
            long popCount = 0;
            for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
                popCount += Long.bitCount(arr1[i] & arr2[i]);
            }
            return popCount;
        }

        /** Returns the popcount or cardinality of the union of two sets.
         *  Neither array is modified. */
        public static long pop_union(long[] arr1, long[] arr2, int wordOffset, int numWords) {
            long popCount = 0;
            for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
                popCount += Long.bitCount(arr1[i] | arr2[i]);
            }
            return popCount;
        }

        /** Returns the popcount or cardinality of A & ~B.
         *  Neither array is modified. */
        public static long pop_andnot(long[] arr1, long[] arr2, int wordOffset, int numWords) {
            long popCount = 0;
            for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
                popCount += Long.bitCount(arr1[i] & ~arr2[i]);
            }
            return popCount;
        }

        /** Returns the popcount or cardinality of A ^ B
         * Neither array is modified. */
        public static long pop_xor(long[] arr1, long[] arr2, int wordOffset, int numWords) {
            long popCount = 0;
            for (int i = wordOffset, end = wordOffset + numWords; i < end; ++i) {
                popCount += Long.bitCount(arr1[i] ^ arr2[i]);
            }
            return popCount;
        }

        /** returns the next highest power of two, or the current value if it's already a power of two or zero*/
        public static int nextHighestPowerOfTwo(int v) {
            v--;
            v |= v >> 1;
            v |= v >> 2;
            v |= v >> 4;
            v |= v >> 8;
            v |= v >> 16;
            v++;
            return v;
        }

        /** returns the next highest power of two, or the current value if it's already a power of two or zero*/
        public static long nextHighestPowerOfTwo(long v) {
            v--;
            v |= v >> 1;
            v |= v >> 2;
            v |= v >> 4;
            v |= v >> 8;
            v |= v >> 16;
            v |= v >> 32;
            v++;
            return v;
        }

    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        for ( int i = 0, length = getBits().length; i < length; i++ ) {
            if ( i > 0 ) {
                s.append( ", " );
            }
            s.append( getBits()[i] );
        }

        s.append( " : " );

        for ( long i = 0, j = 0, length = cardinality(); i < length; i++ ) {
            for ( long k = nextSetBit(j); j <= k; j++  ) {
                s.append( get( j ) ? 1 : 0 );
            }

        }

        return s.toString();
    }

    // ////////////////////////////////////////////////////////////////////////
    // // BitMask
    // ////////////////////////////////////////////////////////////////////////

    @Override
    public BitMask set(int index) {
        fastSet(index);
        return this;
    }

    @Override
    public BitMask setAll(BitMask mask) {
        if (mask instanceof OpenBitSet) {
            union((OpenBitSet)mask);
        } else if (mask instanceof AllSetBitMask) {
            return AllSetBitMask.get();
        } else if (mask instanceof AllSetButLastBitMask) {
            return isSet(0) ? AllSetBitMask.get() : AllSetButLastBitMask.get();
        } else if (mask instanceof EmptyButLastBitMask) {
            return set(0);
        } else if (mask instanceof LongBitMask) {
            this.bits[0] |= ((LongBitMask) mask).asLong();
        }
        return this;
    }

    @Override
    public BitMask reset(int index) {
        fastClear(index);
        return this;
    }

    @Override
    public BitMask resetAll(BitMask mask) {
        if (mask instanceof OpenBitSet) {
            remove((OpenBitSet)mask);
        } else if (mask instanceof AllSetBitMask) {
            for (int i = 0; i < this.bits.length; i++) {
                this.bits[i] = 0L;
            }
        } else if (mask instanceof AllSetButLastBitMask) {
            this.bits[0] = isSet(0) ? 1L : 0L;
            for (int i = 1; i < this.bits.length; i++) {
                this.bits[i] = 0L;
            }
        } else if (mask instanceof EmptyButLastBitMask) {
            return reset(0);
        } else if (mask instanceof LongBitMask) {
            this.bits[0] &= (-1L - ((LongBitMask) mask).asLong());
        }
        return this;
    }

    @Override
    public boolean isSet(int index) {
        return getBit(index) == 1;
    }

    @Override
    public boolean isAllSet() {
        for (int i = 0; i < this.bits.length; i++) {
            if (this.bits[i] != -1L) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean intersects(BitMask mask) {
        if (mask.isAllSet()) {
            return !isEmpty();
        }
        if (mask instanceof AllSetButLastBitMask) {
            return nextSetBit(1) != -1;
        }
        if (mask instanceof EmptyBitMask) {
            return false;
        }
        if (mask instanceof EmptyButLastBitMask) {
            return isSet(0);
        }
        return mask instanceof OpenBitSet ?
               intersects((OpenBitSet)mask) :
               (this.bits[0] & ((LongBitMask)mask).asLong()) != 0;
    }

    @Override
    public String getInstancingStatement() {
        StringBuilder sb = new StringBuilder("new " + OpenBitSet.class.getCanonicalName() + "(new long[] { ");
        sb.append(bits[0]).append("L");
        for (int i = 1; i < bits.length; i++) {
            sb.append(", ");
            sb.append(bits[i]).append("L");
        }
        sb.append(" }, ").append(wlen).append(")");
        return sb.toString();
    }
}

