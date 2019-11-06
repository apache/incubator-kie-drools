import React from 'react';
import { Avatar } from '@patternfly/react-core';

interface IOwnProps {}

const AvatarComponent: React.FC<IOwnProps> = () => {
  const userImage = require('../../../static/user.png');

  return <Avatar src={userImage} alt="Kogito Logo" />;
};

export default AvatarComponent;
