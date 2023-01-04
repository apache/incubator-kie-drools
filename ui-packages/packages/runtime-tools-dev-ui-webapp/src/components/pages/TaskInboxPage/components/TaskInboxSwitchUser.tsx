import React, { useState } from 'react';
import { Dropdown, DropdownToggle, DropdownItem } from '@patternfly/react-core';
import { UserIcon, CaretDownIcon } from '@patternfly/react-icons';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { useDevUIAppContext } from '../../../contexts/DevUIAppContext';
import '../../../styles.css';

interface IOwnProps {
  user: string;
}

const TaskInboxSwitchUser: React.FC<IOwnProps & OUIAProps> = ({
  user,
  ouiaId,
  ouiaSafe
}) => {
  const appContext = useDevUIAppContext();
  const [isDropDownOpen, setIsDropDownOpen] = useState(false);
  const [currentUser, setCurrentUser] = useState(user);
  const allUsers = appContext.getAllUsers();

  const onSelect = (event): void => {
    const selectedUser = event.target.innerHTML;
    appContext.switchUser(selectedUser);
    setCurrentUser(selectedUser);
    setIsDropDownOpen(!isDropDownOpen);
  };

  const dropdownItems = (): JSX.Element[] => {
    const userIds = [];
    allUsers.forEach((userObj) => {
      userIds.push(<DropdownItem key={userObj.id}>{userObj.id}</DropdownItem>);
    });
    return userIds;
  };

  const onToggle = (isOpen): void => {
    setIsDropDownOpen(isOpen);
  };

  return (
    <Dropdown
      onSelect={onSelect}
      toggle={
        <DropdownToggle
          onToggle={onToggle}
          aria-label="Applications"
          id="toggle-id-7"
          toggleIndicator={CaretDownIcon}
          icon={<UserIcon />}
        >
          {currentUser}
        </DropdownToggle>
      }
      isOpen={isDropDownOpen}
      isPlain
      dropdownItems={dropdownItems()}
      className="DevUI-switchUser-dropdown-styling"
      {...componentOuiaProps(ouiaId, 'task-inbox-switch-user', ouiaSafe)}
    />
  );
};

export default TaskInboxSwitchUser;
