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
import {
  getUserName,
  handleLogout,
  isAuthEnabled
} from '../../../utils/KeycloakClient';
import { componentOuiaProps, OUIAProps } from '../../../utils/OuiaUtils';

const PageToolbar: React.FunctionComponent<OUIAProps> = ({
  ouiaId,
  ouiaSafe
}) => {
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

  if (isAuthEnabled()) {
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
                <DropdownToggle onToggle={onDropdownToggle}>
                  {getUserName()}
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
