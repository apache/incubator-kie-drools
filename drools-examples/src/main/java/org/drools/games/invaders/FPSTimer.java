/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.games.invaders;


public class FPSTimer {
    private long lastTime;
    private long frameDiff;
    private long statsTime;
    private long frames = 0;

    public FPSTimer(long frameDiff) {
        this.frameDiff = frameDiff;
        lastTime = System.currentTimeMillis();
        statsTime = System.currentTimeMillis();
    }

    public void incFrame() {
        if (System.currentTimeMillis()-statsTime > 1000) {
            System.out.println( "fps :" + frames + "/s (" +  (System.currentTimeMillis() - statsTime) + ")" );
            frames = 0;
            statsTime = System.currentTimeMillis();
        }
        while (System.currentTimeMillis()-lastTime<frameDiff) {
            // do nothing.
        }
        frames++;
        lastTime = System.currentTimeMillis();
    }
}   
