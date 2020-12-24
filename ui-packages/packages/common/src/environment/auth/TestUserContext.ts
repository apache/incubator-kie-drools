/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { User, UserContext } from './Auth';
import {
  TEST_USERS,
  TestUserManager,
  TestUserManagerImpl
} from './TestUserManager';

/**
 * Definition of a UserContext for testing purposes. It provides functionalities
 * not intended to be used on productive environments
 */
export interface TestUserContext extends UserContext {
  /**
   * Switches the context user.
   * @param userId The identifier of the user to switch to. The switch will only
   * happen if the userId belongs to an existing test user.
   */
  su(userId: string);

  /**
   * Retrieves an instance of a TestUserManager that can be used to access the
   * system users
   */
  getUserManager(): TestUserManager;
}

/**
 * Test implementation of a UserContext
 */
export class TestUserContextImpl implements TestUserContext {
  private static readonly STORAGE_KEY: string = 'kogito-test-user-context';

  private readonly userManager: TestUserManager;

  private currentUser: User;

  constructor() {
    const stateStr: string = window.sessionStorage.getItem(
      TestUserContextImpl.STORAGE_KEY
    );

    if (stateStr) {
      const state: State = JSON.parse(stateStr);
      this.userManager = new TestUserManagerImpl(state.users);
      this.currentUser = this.userManager.getUser(state.currentUser);
    } else {
      this.userManager = new TestUserManagerImpl();
      this.currentUser = TEST_USERS[0];
    }
  }

  getCurrentUser(): User {
    return this.currentUser;
  }

  su(userId: string) {
    const user = this.userManager.getUser(userId);

    if (user) {
      this.currentUser = user;

      const state: State = {
        currentUser: userId,
        users: this.userManager.listUsers()
      };

      window.sessionStorage.setItem(
        TestUserContextImpl.STORAGE_KEY,
        JSON.stringify(state)
      );
      // reloading app
      window.location.href = '/';
    }
  }

  public getUserManager(): TestUserManager {
    return this.userManager;
  }
}

interface State {
  users: User[];
  currentUser: string;
}
