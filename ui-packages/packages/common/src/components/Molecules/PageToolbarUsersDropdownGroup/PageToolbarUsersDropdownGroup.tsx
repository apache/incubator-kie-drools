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

import React from 'react';
import {
  AppContext,
  useKogitoAppContext
} from '../../../environment/context/KogitoAppContext';
import { DropdownGroup, DropdownItem, Tooltip } from '@patternfly/react-core';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { TestUserContext } from '../../../environment/auth/TestUserContext';
import { isTestUserSystemEnabled } from '../../../utils/Utils';

interface IOwnProps {
  toggleAddUsersModal: () => void;
}

const PageToolbarUsersDropdownGroup: React.FC<IOwnProps & OUIAProps> = ({
  toggleAddUsersModal,

  ouiaId,
  ouiaSafe
}) => {
  const context: AppContext = useKogitoAppContext();

  if (!isTestUserSystemEnabled()) {
    return null;
  }

  const testUserSystem: TestUserContext =
    context.userContext as TestUserContext;

  const users = testUserSystem.getUserManager().listAllUsers();

  return (
    <DropdownGroup
      key={'kogito-user-management-group'}
      label={'Test users'}
      {...componentOuiaProps(ouiaId, 'test-user-dropdown-group', ouiaSafe)}
    >
      {users.map((user, index) => {
        if (user === testUserSystem.getCurrentUser()) {
          return null;
        }
        return (
          <DropdownItem
            key={`${'kogito-user-management-group-test-user__'}${user.id}`}
            onClick={() => testUserSystem.su(user.id)}
          >
            <Tooltip content={`Roles: ${user.groups.join(', ')}`}>
              <div>{user.id}</div>
            </Tooltip>
          </DropdownItem>
        );
      })}
      <DropdownItem
        key={'kogito-user-management-group-add'}
        onClick={() => toggleAddUsersModal()}
      >
        + Add new user
      </DropdownItem>
    </DropdownGroup>
  );
};

export default PageToolbarUsersDropdownGroup;
