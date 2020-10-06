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

import {
  TEST_USERS,
  TestUserManager,
  TestUserManagerImpl
} from '../TestUserManager';

let userManager: TestUserManager;

describe('TestUserManagerImpl tests', () => {
  beforeEach(() => {
    userManager = new TestUserManagerImpl();
  });

  it('Test list users', () => {
    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length);
    expect(userManager.listAllUsers()).toStrictEqual(TEST_USERS);
  });

  it('Test system users', () => {
    expect(userManager.systemUsers()).toHaveLength(TEST_USERS.length);
  });

  it('Test add user', () => {
    userManager.addUser('newUser', ['group']);

    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length + 1);

    const user = userManager.getUser('newUser');

    expect(user).not.toBeNull();
    expect(user.id).toStrictEqual('newUser');
    expect(user.groups).toHaveLength(1);
    expect(user.groups).toContain('group');
  });

  it('Test add system user', () => {
    userManager.addUser(TEST_USERS[0].id, []);

    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length);

    const user = userManager.getUser(TEST_USERS[0].id);

    expect(user).not.toBeNull();
    expect(user).toStrictEqual(TEST_USERS[0]);
  });

  it('Test add existing user', () => {
    userManager.addUser('newUser', ['group']);

    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length + 1);

    let user = userManager.getUser('newUser');

    expect(user).not.toBeNull();
    expect(user.id).toStrictEqual('newUser');
    expect(user.groups).toHaveLength(1);
    expect(user.groups).toContain('group');

    userManager.addUser('newUser', ['group', 'group2']);

    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length + 1);

    user = userManager.getUser('newUser');

    expect(user).not.toBeNull();
    expect(user.id).toStrictEqual('newUser');
    expect(user.groups).toHaveLength(2);
    expect(user.groups).toContain('group');
    expect(user.groups).toContain('group2');
  });

  it('Test remove user', () => {
    userManager.addUser('newUser', ['group']);

    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length + 1);

    userManager.removeUser('newUser');

    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length);

    const user = userManager.getUser('newUser');

    expect(user).toBeUndefined();
  });

  it('Test remove system user', () => {
    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length);

    userManager.removeUser(TEST_USERS[0].id);

    expect(userManager.listAllUsers()).toHaveLength(TEST_USERS.length);

    const user = userManager.getUser(TEST_USERS[0].id);

    expect(user).not.toBeNull();
  });
});
