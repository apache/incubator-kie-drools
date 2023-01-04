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

import React, { useState } from 'react';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import {
  ActionGroup,
  Alert,
  Button,
  Checkbox,
  Form,
  FormGroup,
  Modal,
  ModalVariant,
  Stack,
  StackItem,
  Text,
  TextContent,
  TextInput,
  TextVariants
} from '@patternfly/react-core';
import {
  AppContext,
  useKogitoAppContext
} from '../../../environment/context/KogitoAppContext';
import { TestUserContext } from '../../../environment/auth/TestUserContext';
import { TestUserManager } from '../../../environment/auth/TestUserManager';
import { isTestUserSystemEnabled } from '../../../utils/Utils';

interface IOwnProps {
  isOpen: boolean;
  toggleModal: () => void;
}

const AddTestUser: React.FC<IOwnProps & OUIAProps> = ({
  isOpen,
  toggleModal,
  ouiaId,
  ouiaSafe
}) => {
  const context: AppContext = useKogitoAppContext();

  const [userIdValidated, setUserIdValidated] = useState<any>('default');
  const [userIdError, setUserIdError] = useState<string>();

  const [userId, setUserId] = useState<string>();

  const [groupsValidated, setGroupsValidated] = useState<any>('default');
  const [groupsError, setGroupsError] = useState<string>();
  const [groups, setGroups] = useState<string>();
  const [login, setLogin] = useState<boolean>(true);

  const validateUserId = (newUserId: string): boolean => {
    setUserId(newUserId);

    if (!newUserId || !newUserId.trim()) {
      setUserIdValidated('error');
      setUserIdError('User Id cannot be empty.');
      return false;
    }

    const testUserSystem: TestUserContext =
      context.userContext as TestUserContext;
    const userManager: TestUserManager = testUserSystem.getUserManager();
    const user = userManager.getUser(newUserId);

    if (user) {
      if (userManager.systemUsers().includes(newUserId)) {
        setUserIdValidated('error');
        setUserIdError(
          `Already exists a system user identified by '${newUserId}'. Please choose another user id.`
        );
        return false;
      }
      setUserIdValidated('warning');
      setUserIdError(
        `Already exists a user identified by '${newUserId}'. Press 'Add' to replace it.`
      );
      return true;
    }

    setUserIdValidated('success');
    setUserIdError(null);
    return true;
  };

  const checkEmptyArray = (newGroups: string): boolean => {
    return (
      newGroups.split(',').filter((group) => group && group.trim().length > 0)
        .length > 0
    );
  };

  const validateGroups = (newGroups: string): boolean => {
    setGroups(newGroups);

    if (!newGroups || !newGroups.trim() || !checkEmptyArray(newGroups)) {
      setGroupsValidated('error');
      setGroupsError('User groups cannot be empty.');
      return false;
    }
    setGroupsValidated('success');
    setGroupsError(null);
    return true;
  };

  const addUser = () => {
    const isUserIdValidated = validateUserId(userId);
    const areGroupsValidated = validateGroups(groups);
    if (isUserIdValidated && areGroupsValidated) {
      const testUserSystem: TestUserContext =
        context.userContext as TestUserContext;

      const userGroups = groups
        .split(',')
        .map((group) => group.trim())
        .filter((group) => group.length > 0);

      testUserSystem.getUserManager().addUser(userId, userGroups);

      close();

      if (login) {
        testUserSystem.su(userId);
      }
    }
  };

  const close = () => {
    toggleModal();
    setUserId(null);
    setUserIdValidated('default');
    setUserIdError(null);
    setGroups(null);
    setGroupsValidated('default');
    setGroupsError(null);
    setLogin(true);
  };

  if (!isOpen || !isTestUserSystemEnabled()) {
    return null;
  }

  return (
    <Modal
      aria-label="add-test-user-modal"
      title="Add new test user"
      variant={ModalVariant.small}
      onClose={close}
      isOpen={isOpen}
      {...componentOuiaProps(ouiaId, 'add-test-user-form-modal', ouiaSafe)}
    >
      <Stack hasGutter>
        <StackItem>
          <Alert
            title="Adds a new user to the user system"
            variant="info"
            isInline
          >
            <TextContent>
              <Text component={TextVariants.p}>
                Temporarily adds a new user to the user system (only for testing
                purposes). Newly added users will be stored in-memory.
              </Text>
              <Text component={TextVariants.p}>
                Refresh the app to clear the test users and leave the user
                system in his original state.
              </Text>
            </TextContent>
          </Alert>
        </StackItem>
        <StackItem isFilled>
          <Form>
            <FormGroup
              fieldId={'userId'}
              label={'User Id'}
              helperTextInvalid={userIdError}
              validated={userIdValidated}
              isRequired
            >
              <TextInput
                id="userId"
                value={userId}
                validated={userIdValidated}
                onChange={validateUserId}
              />
            </FormGroup>
            <FormGroup
              fieldId={'groups'}
              label={'Groups'}
              validated={groupsValidated}
              helperTextInvalid={groupsError}
              isRequired
              helperText='Comma-separated list of groups for the user(i.e. "managers,human-resources")'
            >
              <TextInput
                id="groups"
                value={groups}
                validated={groupsValidated}
                onChange={validateGroups}
              />
            </FormGroup>
            <FormGroup fieldId="login">
              <Checkbox
                label="Log in with user after adding it"
                id="login"
                isChecked={login}
                onChange={(checked) => setLogin(checked)}
              />
            </FormGroup>
            <ActionGroup>
              <Button variant="primary" onClick={addUser} id="add-test-user">
                Add
              </Button>
              <Button
                variant="secondary"
                onClick={close}
                id="cancel-add-test-user"
              >
                Cancel
              </Button>
            </ActionGroup>
          </Form>
        </StackItem>
      </Stack>
    </Modal>
  );
};

export default AddTestUser;
