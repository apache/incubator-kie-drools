import React from 'react';
import DataListContainer from '../DataListContainer';
import { getWrapper } from '@kogito-apps/common';
import { MemoryRouter as Router} from 'react-router-dom';
import { MockedProvider } from '@apollo/react-testing';


jest.mock('../../../Molecules/DataToolbarComponent/DataToolbarComponent');
jest.mock('../../../Organisms/DataListComponent/DataListComponent');

describe('DataListContainer component tests', () => {
  it('Snapshot tests', () => {
    const wrapper = getWrapper(
      <Router>
        <MockedProvider>
          <DataListContainer />
        </MockedProvider>
      </Router>
      , 'DataListContainer');
    expect(wrapper).toMatchSnapshot();
  });
});
