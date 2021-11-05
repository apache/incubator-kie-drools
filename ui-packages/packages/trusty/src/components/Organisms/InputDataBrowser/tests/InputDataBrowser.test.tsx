import React from 'react';
import InputDataBrowser from '../InputDataBrowser';
import { mount, shallow } from 'enzyme';
import {
  ItemObject,
  ItemObjectUnit,
  RemoteData,
  RemoteDataStatus
} from '../../../../types';

describe('InputDataBrowser', () => {
  test('renders a loading animation while fetching data', () => {
    const inputData = { status: RemoteDataStatus.LOADING } as RemoteData<
      Error,
      ItemObject[]
    >;
    const wrapper = shallow(<InputDataBrowser inputData={inputData} />);

    expect(wrapper).toMatchSnapshot();
  });

  test('renders a list of inputs', () => {
    const inputData = {
      status: RemoteDataStatus.SUCCESS,
      data: [
        {
          name: 'Asset Score',
          value: {
            kind: 'UNIT',
            type: 'number',
            value: 738
          }
        },
        {
          name: 'Asset Amount',
          value: {
            kind: 'UNIT',
            type: 'number',
            value: 700
          }
        },
        {
          name: 'Property',
          value: {
            kind: 'STRUCTURE',
            type: 'tProperty',
            value: {
              'Purchase Price': {
                kind: 'UNIT',
                type: 'number',
                value: 34000
              }
            }
          }
        }
      ]
    } as RemoteData<Error, ItemObject[]>;
    const wrapper = mount(<InputDataBrowser inputData={inputData} />);
    const dataList = wrapper.find('DataList.input-browser__data-list');

    expect(wrapper.find('.input-browser__section-list')).toHaveLength(1);
    expect(wrapper.find('.input-browser__section-list button')).toHaveLength(2);
    expect(
      wrapper
        .find('.input-browser__section-list button')
        .at(0)
        .text()
    ).toMatch('Root');
    expect(
      wrapper
        .find('.input-browser__section-list button')
        .at(1)
        .text()
    ).toMatch('Property');
    expect(dataList).toHaveLength(1);
    expect(dataList.find('DataListItem.input-browser__header')).toHaveLength(1);
    expect(dataList.find('CategoryLine')).toHaveLength(1);
    expect(dataList.find('CategoryLine').prop('categoryLabel')).toMatch('Root');
    expect(dataList.find('InputValue')).toHaveLength(2);
    expect(
      (dataList
        .find('InputValue')
        .at(0)
        .prop('inputValue') as ItemObjectUnit).value
    ).toBe(738);
    expect(
      (dataList
        .find('InputValue')
        .at(1)
        .prop('inputValue') as ItemObjectUnit).value
    ).toBe(700);

    wrapper
      .find('.input-browser__section-list button')
      .at(1)
      .simulate('click');
    const propertyDataList = wrapper.find('DataList.input-browser__data-list');

    expect(
      propertyDataList.find('DataListItem.input-browser__header')
    ).toHaveLength(1);
    expect(propertyDataList.find('CategoryLine')).toHaveLength(1);
    expect(propertyDataList.find('CategoryLine').prop('categoryLabel')).toMatch(
      'Property'
    );

    expect(propertyDataList.find('InputValue')).toHaveLength(1);
    expect(
      (propertyDataList.find('InputValue').prop('inputValue') as ItemObjectUnit)
        .value
    ).toBe(34000);
  });
});
