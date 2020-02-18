import React from 'react';
import { Avatar } from '@patternfly/react-core';

const AvatarComponent: React.FC = () => {
  const userImage = require('../../../static/user.png');
  return <Avatar src={userImage} alt="Kogito Logo" />;
};

export default AvatarComponent;
