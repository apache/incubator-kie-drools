import React from 'react';
import { shallow } from 'enzyme';
import DataListItemComponent from '../DataListItemComponent';

const props = {
    id: 0,
    checkedArray: ['ACTIVE'],
    processInstanceData: {
        lastUpdate: '2019-10-22T03:40:44.089Z',
        id: 'c54ca5b0-b975-46e2-a9a0-6a86bf7ac21e',
        processId: 'flightBooking',
        parentProcessInstanceId: '8035b580-6ae4-4aa8-9ec0-e18e19809e0b',
        processName: 'FlightBooking',
        start: '',
        endpoint: 'http://localhost:4000',
        state: 'COMPLETED',
        addons: [],
        error: {
            nodeDefinitionId: 'a1e139d5-81c77-48c9-84ae-34578e90433n',
            message: 'Something went wrong'
        }
    }
}

describe('DataListItem component tests', () => {
    it('Snapshot tests', () => {
        const wrapper = shallow(<DataListItemComponent {...props} />);
        expect(wrapper).toMatchSnapshot();
    });
});
