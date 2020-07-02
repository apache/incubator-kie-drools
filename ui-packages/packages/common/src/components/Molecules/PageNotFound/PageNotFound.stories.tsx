import React from 'react';
import { withKnobs, text } from '@storybook/addon-knobs';
import PageNotFound from './PageNotFound';

export default {
  title: 'Page not found',
  decorators: [withKnobs]
};

export const defaultView = () => {
  const props = {
    defaultPath: '/DomainExplorer',
    defaultButton: text('Button text', 'Go to domain explorer'),
    location: {}
  };
  return <PageNotFound {...props} />;
};
