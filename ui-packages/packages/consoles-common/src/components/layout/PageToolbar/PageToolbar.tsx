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

import React, { useState } from 'react';
import {
  Dropdown,
  DropdownItem,
  DropdownSeparator,
  DropdownToggle
} from '@patternfly/react-core/dist/js/components/Dropdown';
import {
  Toolbar,
  ToolbarGroup,
  ToolbarItem
} from '@patternfly/react-core/dist/js/components/Toolbar';
import { Avatar } from '@patternfly/react-core/dist/js/components/Avatar';
import accessibleStyles from '@patternfly/react-styles/css/utilities/Accessibility/accessibility';
import { css } from '@patternfly/react-styles';

import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import AboutModalBox from '../AboutModalBox/AboutModalBox';
import userImage from '../../../static/avatar.svg';
import {
  ANONYMOUS_USER,
  LogoutUserContext,
  supportsLogout
} from '../../../environment/auth';
import {
  AppContext,
  useKogitoAppContext
} from '../../../environment/context/KogitoAppContext';

const PageToolbar: React.FunctionComponent<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
  const [isDropdownOpen, setDropdownOpen] = useState<boolean>(false);
  const [modalToggle, setmodalToggle] = useState<boolean>(false);

  const context: AppContext = useKogitoAppContext();

  const handleAboutModalToggle = () => {
    setmodalToggle(!modalToggle);
  };

  const onDropdownToggle = (_isDropdownOpen) => {
    setDropdownOpen(_isDropdownOpen);
  };

  const onDropdownSelect = () => {
    setDropdownOpen(!isDropdownOpen);
  };

  const getUserName = () => {
    if (context) {
      if (context.getCurrentUser() === ANONYMOUS_USER) {
        return 'Anonymous';
      }
      return context.getCurrentUser().id;
    }
    return 'Anonymous';
  };

  const handleLogout = () => {
    if (supportsLogout(context.userContext)) {
      const logout: LogoutUserContext =
        context.userContext as LogoutUserContext;
      logout.logout();
    }
  };

  const userDropdownItems: React.ReactElement[] = [];

  userDropdownItems.push(
    <DropdownItem
      key={userDropdownItems.length}
      onClick={handleAboutModalToggle}
    >
      About
    </DropdownItem>
  );

  if (supportsLogout(context.userContext)) {
    userDropdownItems.push(
      <DropdownSeparator key={userDropdownItems.length} />
    );
    userDropdownItems.push(
      <DropdownItem key={userDropdownItems.length} onClick={handleLogout}>
        Log out
      </DropdownItem>
    );
  }

  return (
    <React.Fragment>
      <AboutModalBox
        isOpenProp={modalToggle}
        handleModalToggleProp={handleAboutModalToggle}
      />
      <Toolbar {...componentOuiaProps(ouiaId, 'page-toolbar', ouiaSafe)}>
        <ToolbarGroup>
          <ToolbarItem
            className={css(
              accessibleStyles.screenReader,
              accessibleStyles.visibleOnMd
            )}
          >
            <Dropdown
              isPlain
              position="right"
              onSelect={onDropdownSelect}
              isOpen={isDropdownOpen}
              toggle={
                <DropdownToggle
                  onToggle={onDropdownToggle}
                  icon={<Avatar src={userImage} alt="User Avatar" />}
                >
                  {getUserName()}
                </DropdownToggle>
              }
              dropdownItems={userDropdownItems}
              data-testid={'pageToolbar-dropdown'}
            />
          </ToolbarItem>
        </ToolbarGroup>
      </Toolbar>
    </React.Fragment>
  );
};

export default PageToolbar;
