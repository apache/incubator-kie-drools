import React from 'react';
import NoData from './NoData';

export default {
  title: 'No data',
  component: NoData
};

export const defaultView = (args) => {
  return <NoData {...args} />;
};
defaultView.args = {
  location: {
    state: {
      prev: '/DomainExplorer',
      title: 'Domain not found',
      description: 'Domain with the name Travels not found',
      buttonText: 'Go to domain explorer'
    }
  },
  defaultPath: '/DomainExplorer',
  defaultButton: 'Go to domain explorer'
};
