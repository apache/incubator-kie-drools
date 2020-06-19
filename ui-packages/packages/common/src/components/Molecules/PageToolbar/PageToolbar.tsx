import React, { useState } from 'react';
import {
  Dropdown,
  DropdownToggle,
  Toolbar,
  ToolbarGroup,
  ToolbarItem,
  DropdownItem,
  DropdownSeparator
} from '@patternfly/react-core';
import accessibleStyles from '@patternfly/react-styles/css/utilities/Accessibility/accessibility';
import { css } from '@patternfly/react-styles';
import AboutModalBox from '../AboutModalBox/AboutModalBox';
import Keycloak from 'keycloak-js';

const PageToolbar: React.FunctionComponent = () => {
  let userName = 'Anonymous';
  let kcInfo;

  if (process.env.KOGITO_AUTH_ENABLED) {
    kcInfo = JSON.parse(localStorage.getItem('keycloakData') || '{}');
    userName = kcInfo.tokenParsed.preferred_username;
  }

  const handleLogout = () => {
    const keycloakConf = {
      realm: process.env.KOGITO_KEYCLOAK_REALM || '',
      url: process.env.KOGITO_KEYCLOAK_URL || '' + '/auth',
      clientId: process.env.KOGITO_KEYCLOAK_CLIENT_ID || ''
    };
    const kcInstance = Keycloak(keycloakConf);
    kcInstance.init(kcInfo).success(kcInstance.logout);
  };

  const [isDropdownOpen, setDropdownOpen] = useState(false);
  const [modalToggle, setmodalToggle] = useState(false);

  const handleModalToggle = () => {
    setmodalToggle(modalToggle ? false : true);
  };

  const onDropdownToggle = _isDropdownOpen => {
    setDropdownOpen(_isDropdownOpen);
  };

  const onDropdownSelect = () => {
    setDropdownOpen(!isDropdownOpen);
  };
  const userDropdownItems = [
    <DropdownItem key={1} onClick={handleModalToggle}>
      About
    </DropdownItem>
  ];

  if (process.env.KOGITO_AUTH_ENABLED) {
    userDropdownItems.push(
      <DropdownSeparator key={2} />,
      <DropdownItem component="button" key={3} onClick={handleLogout}>
        Log out
      </DropdownItem>
    );
  }

  return (
    <React.Fragment>
      <AboutModalBox
        isOpenProp={modalToggle}
        handleModalToggleProp={handleModalToggle}
      />
      <Toolbar>
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
                <DropdownToggle onToggle={onDropdownToggle}>
                  {userName}
                </DropdownToggle>
              }
              dropdownItems={userDropdownItems}
            />
          </ToolbarItem>
        </ToolbarGroup>
      </Toolbar>
    </React.Fragment>
  );
};

export default PageToolbar;
