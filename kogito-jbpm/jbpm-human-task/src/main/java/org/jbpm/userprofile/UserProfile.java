/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.userprofile;

/**
 * UserProfile is a base class to represent user profile related information. As the user profile information various 
 * from application to application, the only common information we have in this base class is user id.
 * Then it is up to the sub class to provide application specific information, see DroolsTaskUserProfile.
 *   
 */
public class UserProfile {
	String id;
	
    public String getID(){
    	return id;
    }
    
    public void setID(String id){
    	this.id = id;
    }
 }
