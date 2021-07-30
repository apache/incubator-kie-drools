/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, { useContext } from 'react';
import { User } from '@kogito-apps/consoles-common';

export interface DevUIAppContext {
  getCurrentUser(): User;
  getAllUsers(): User[];
  switchUser(userId: string): void;
  onUserChange(listener: UserChangeListener): UnSubscribeHandler;
}

export interface UserChangeListener {
  onUserChange: (user: User) => void;
}

export interface UnSubscribeHandler {
  unSubscribe: () => void;
}

export class DevUIAppContextImpl implements DevUIAppContext {
  private users: User[];
  private currentUser: User;
  private readonly userListeners: UserChangeListener[] = [];

  constructor(users) {
    this.users = users;
    if (users.length > 0) {
      this.currentUser = users[0];
    }
  }

  getCurrentUser(): User {
    return this.currentUser;
  }

  getAllUsers(): User[] {
    return this.users;
  }

  switchUser(userId: string): void {
    const switchedUser = this.users.find(user => user.id === userId);
    if (switchedUser) {
      this.currentUser = switchedUser;
      this.userListeners.forEach(listener =>
        listener.onUserChange(switchedUser)
      );
    }
  }

  onUserChange(listener: UserChangeListener): UnSubscribeHandler {
    this.userListeners.push(listener);

    return {
      unSubscribe: () => {
        const index = this.userListeners.indexOf(listener);
        if (index > -1) {
          this.userListeners.splice(index, 1);
        }
      }
    };
  }
}

const RuntimeToolsDevUIAppContext = React.createContext<DevUIAppContext>(null);

export default RuntimeToolsDevUIAppContext;

export const useDevUIAppContext = () =>
  useContext<DevUIAppContext>(RuntimeToolsDevUIAppContext);
