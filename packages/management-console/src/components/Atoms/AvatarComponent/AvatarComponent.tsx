import React from 'react';
import { Avatar } from '@patternfly/react-core';
import userImage from '../../../static/avatar.svg';

const AvatarComponent: React.FC = () => {
  return <Avatar src={userImage} alt="Kogito Logo" />;
};

export default AvatarComponent;
