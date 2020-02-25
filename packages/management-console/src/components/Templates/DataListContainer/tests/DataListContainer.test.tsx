import React from 'react';
import { shallow } from 'enzyme';
import DataListContainer from '../DataListContainer';

describe('DataListContainer component tests', () => {
    it('Snapshot tests', () => {
        const wrapper = shallow(<DataListContainer />);
        expect(wrapper).toMatchSnapshot();
    });
});
