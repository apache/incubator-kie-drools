import React from 'react';
import { shallow } from 'enzyme';
import DataToolbarComponent from '../DataToolbarComponent';

const props = {
    checkedArray: ['ACTIVE', 'COMPLETED', 'ERROR', 'ABORTED', 'SUSPENDED'],
    filterClick: jest.fn(),
    setCheckedArray: jest.fn(),
    setIsStatusSelected: jest.fn(),
    filters: ['ACTIVE,COMPLETED'],
    setFilters: jest.fn()

}



describe('DataToolbar component tests', () => {
    it('Snapshot tests', () => {
        const wrapper = shallow(<DataToolbarComponent {...props} />);
        expect(wrapper).toMatchSnapshot();
    });
});
