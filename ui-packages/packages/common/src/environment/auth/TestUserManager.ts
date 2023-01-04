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

import { DefaultUser, User } from './Auth';

export const TEST_USERS: User[] = [
  { id: 'john', groups: ['employees'] },
  { id: 'mary', groups: ['managers'] },
  { id: 'poul', groups: ['interns', 'managers'] }
];

/**
 * Definition of a UserManager for test purposes, provides a set of predefined
 * users and allows adding new test users in-memory.
 */
export interface TestUserManager {
  /**
   * Lists the test users that the have been added
   */
  listUsers(): User[];

  /**
   * Lists all users stored, including the predefined ones
   */
  listAllUsers(): User[];

  /**
   * Lists the logins of the system users
   */
  systemUsers(): string[];

  /**
   * Adds a new test user, these test users are in-memory and reloading the app
   * will cause losing them. If the userId matches an existing test user,
   * the user will be overwritten with the new roles. System users won't be
   * overwritten.
   * @param userId the id for the new user
   * @param groups the groups/roles the user belongs to.
   */
  addUser(userId: string, groups: string[]): void;

  /**
   * Removes the user identified by the userId. System users won't be removed.
   * @param userId the identifier of the user to remove
   */
  removeUser(userId: string);

  /**
   * Retrieves the user identified by the userId or undefined if not found.
   * @param userId the identifier of the user to find.
   */
  getUser(userId: string): User | undefined;
}

export class TestUserManagerImpl implements TestUserManager {
  private readonly system: string[] = TEST_USERS.map((user) => user.id);
  private readonly users: User[] = [];

  constructor(storedUsers?: User[]) {
    this.users.push(...storedUsers);
  }

  addUser(userId: string, groups: string[]): void {
    const user = this.getUser(userId);

    if (user) {
      if (this.system.includes(user.id)) {
        return;
      }
      this.removeUser(userId);
    }

    this.users.push(new DefaultUser(userId, groups));
  }

  getUser(userId: string): User | undefined {
    const allUsers = this.listAllUsers();
    return allUsers.find((user) => user.id === userId);
  }

  listAllUsers(): User[] {
    return TEST_USERS.concat(this.users);
  }

  listUsers(): User[] {
    return this.users;
  }

  removeUser(userId: string) {
    const userToRemove = this.getUser(userId);
    if (userToRemove && !this.system.includes(userId)) {
      const index = this.users.indexOf(userToRemove);
      this.users.splice(index, 1);
    }
  }

  systemUsers(): string[] {
    return this.system;
  }
}
