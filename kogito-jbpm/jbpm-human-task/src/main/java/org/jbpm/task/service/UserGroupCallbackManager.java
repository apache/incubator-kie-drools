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
package org.jbpm.task.service;

import java.io.InputStream;
import java.util.Properties;

public class UserGroupCallbackManager {
    public static final String USER_GROUP_CALLBACK_KEY = "jbpm.usergroup.callback";
    private static UserGroupCallbackManager instance;
    private UserGroupCallback callback = null;
    
    private UserGroupCallbackManager() {
        try {
            if(System.getProperty(USER_GROUP_CALLBACK_KEY) != null) {
                callback = (UserGroupCallback) Class.forName(System.getProperty(USER_GROUP_CALLBACK_KEY)).newInstance();
            } else {
                InputStream in = getClass().getResourceAsStream(USER_GROUP_CALLBACK_KEY + ".properties");
                if(in != null) {
                    Properties callbackproperties = new Properties();
                    callbackproperties.load(in);
                    if (!isEmpty(callbackproperties.getProperty(USER_GROUP_CALLBACK_KEY))) {
                        callback = (UserGroupCallback) Class.forName(callbackproperties.getProperty(USER_GROUP_CALLBACK_KEY)).newInstance();
                    }
                    in.close();
                }
            }
        } catch (Throwable t) {
            callback = null;
        } 
    }
    
    public static UserGroupCallbackManager getInstance() {
        if(instance == null) {
            instance = new UserGroupCallbackManager();
        }
        return instance;
    }
    
    public static void resetCallback() {
        instance = null;
    }
    
    public void setCallback(UserGroupCallback callback) {
        this.callback = callback;
    }
    
    public boolean existsCallback() {
        return callback != null;
    }
    
    public UserGroupCallback getCallback() {
        return callback;
    }
    
    private boolean isEmpty(final CharSequence str) {
        if ( str == null || str.length() == 0 ) {
            return true;
        }
        for ( int i = 0, length = str.length(); i < length; i++ ){
            if ( str.charAt( i ) != ' ' ) {
                return false;
            }
        }
        return true;
    }
}
