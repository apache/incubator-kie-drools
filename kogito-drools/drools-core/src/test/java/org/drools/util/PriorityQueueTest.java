package org.drools.util;

/*
 * The RedHill Consulting, Pty. Ltd. Software License, Version 1.0
 *
 * Copyright (c) 2003-04 RedHill Consulting, Pty. Ltd.  All rights reserved.
 *
 * Redistribution and use in source or binary forms IS NOT PERMITTED
 * without prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL REDHILL CONSULTING OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

import java.io.Serializable;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 * @version $Id: PriorityQueueTest.java,v 1.1 2005/07/26 01:06:34 mproctor Exp $
 */
public class PriorityQueueTest extends TestCase {
    public void testIsSerializable() {
        assertTrue( Serializable.class.isAssignableFrom( PriorityQueue.class ) );
    }
}