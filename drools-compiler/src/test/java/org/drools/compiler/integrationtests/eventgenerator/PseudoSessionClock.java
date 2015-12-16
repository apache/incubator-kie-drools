/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests.eventgenerator;


public class PseudoSessionClock
        {

         private long timer;

         public PseudoSessionClock() {
             this.timer = 0;
         }

         /* (non-Javadoc)
          * @see org.kie.temporal.SessionClock#getCurrentTime()
          */
         public long getCurrentTime() {
             return this.timer;
         }

         public long advanceTime( long millisecs ) {
             this.timer += millisecs;
             return this.timer;
         }

        public long setTime( long timer ) {
            this.timer = timer;
            return this.timer;
        }

         // using current system time as reference
         public long calcFuturePointInTime (long timeToAdd){
            return this.timer+timeToAdd;
        }

         // ------------------------------------------------------------------------------------
         // static convenience methods

         // using an arbitrary starting point as reference
         public static long calcFuturePointInTime (long currentTime, long timeToAdd){
             return currentTime+timeToAdd;
         }

         // convert seconds to milliseconds
         public static long timeInSeconds (long timeInSecs){
             return timeInSecs*1000;
         }

         // convert seconds to milliseconds
         public static long timeInMinutes (long timeInMins){
             return timeInSeconds(timeInMins)*60;
         }

         // convert seconds to milliseconds
         public static long timeInHours (long timeInHrs){
             return timeInMinutes(timeInHrs)*60;
         }

         // convert time given as hours, minutes, seconds, milliseconds to milliseconds
         public static long timeinHMSM (long hours, long mins, long secs, long msecs){
             return timeInHours(hours) + timeInMinutes(mins) + timeInSeconds(secs) + msecs;
         }
     }
