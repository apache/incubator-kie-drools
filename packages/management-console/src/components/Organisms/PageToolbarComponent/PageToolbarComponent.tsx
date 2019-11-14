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
import AboutModalBox from '../../Molecules/AboutModalComponent/AboutModal';
export interface IOwnProps {}

const PageToolbarComponent: React.FunctionComponent<IOwnProps> = () => {
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
    </DropdownItem>,
    <DropdownSeparator key={2} />,
    <DropdownItem component="button" key={3}>
      Log out
    </DropdownItem>
  ];
  return (
    <React.Fragment>
      <AboutModalBox isOpenProp={modalToggle} handleModalToggleProp={handleModalToggle} />
      <Toolbar>
        <ToolbarGroup>
          <ToolbarItem className={css(accessibleStyles.screenReader, accessibleStyles.visibleOnMd)}>
            <Dropdown
              isPlain
              position="right"
              onSelect={onDropdownSelect}
              isOpen={isDropdownOpen}
              toggle={<DropdownToggle onToggle={onDropdownToggle}>User</DropdownToggle>}
              dropdownItems={userDropdownItems}
            />
          </ToolbarItem>
        </ToolbarGroup>
      </Toolbar>
    </React.Fragment>
  );
};

export default PageToolbarComponent;
