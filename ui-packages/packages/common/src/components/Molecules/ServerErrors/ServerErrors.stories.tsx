import React from 'react';
import { BrowserRouter } from 'react-router-dom';
import { withKnobs, text } from '@storybook/addon-knobs';
import ServerErrors from './ServerErrors';

export default {
  title: 'Server errors',
  decorators: [withKnobs]
};

export const defaultView = () => {
  const error = text(
    'Error message',
    'Network error: Response not successful: Received status code 400'
  );
  return (
    <BrowserRouter>
      <ServerErrors error={error} />
    </BrowserRouter>
  );
};
