import React from 'react';
import PageNotFound from './PageNotFound';

export default {
  title: 'Page not found'
};

export const defaultView = (args) => {
  return <PageNotFound {...args} />;
};

defaultView.args = {
  defaultPath: '/DomainExplorer',
  defaultButton: 'Go to domain explorer',
  location: {}
};
