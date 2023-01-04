import React from 'react';
import ProcessDetailsNodeTrigger from '../ProcessDetailsNodeTrigger';
import { mount } from 'enzyme';
import { DropdownToggle, DropdownItem, FlexItem } from '@patternfly/react-core';
import { act } from 'react-dom/test-utils';
import axios from 'axios';
import ProcessDetailsErrorModal from '../../../Atoms/ProcessDetailsErrorModal/ProcessDetailsErrorModal';
import wait from 'waait';
jest.mock('axios');
const mockedAxios = axios as jest.Mocked<typeof axios>;

/* tslint:disable:no-string-literal */
const ProcessInstanceData = {
  id: 'a1e139d5-4e77-48c9-84ae-34578e904e5a',
  processId: 'travels',
  serviceUrl: 'http://localhost:4000'
};
const mockTriggerableNodes = [
  {
    nodeDefinitionId: '_BDA56801-1155-4AF2-94D4-7DAADED2E3C0',
    name: 'Send visa application',
    id: 1,
    type: 'ActionNode',
    uniqueId: '1'
  },
  {
    nodeDefinitionId: '_175DC79D-C2F1-4B28-BE2D-B583DFABF70D',
    name: 'Book',
    id: 2,
    type: 'Split',
    uniqueId: '2'
  },
  {
    nodeDefinitionId: '_E611283E-30B0-46B9-8305-768A002C7518',
    name: 'visasrejected',
    id: 3,
    type: 'EventNode',
    uniqueId: '3'
  }
];

const getNodeTriggerWrapper = async () => {
  mockedAxios.get.mockResolvedValue({ data: mockTriggerableNodes });
  let wrapper;
  await act(async () => {
    wrapper = mount(
      <ProcessDetailsNodeTrigger processInstanceData={ProcessInstanceData} />
    );
    await wait(0);
    wrapper = wrapper.update().find('ProcessDetailsNodeTrigger');
  });
  return wrapper;
};

describe('Process details node trigger component tests', () => {
  it('snapshot testing with none selected', async () => {
    const wrapper = await getNodeTriggerWrapper();
    expect(wrapper).toMatchSnapshot();
  });

  it('select a node test ', async () => {
    let wrapper = await getNodeTriggerWrapper();
    await act(async () => {
      wrapper.find(DropdownToggle).find('button').simulate('click');
    });
    wrapper = wrapper.update();

    await act(async () => {
      wrapper.find(DropdownItem).at(1).simulate('click');
    });
    wrapper = wrapper.update();
    // snapshot with data displayed
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(FlexItem).length).toEqual(3);
    // Node name displayed
    expect(
      wrapper
        .find(FlexItem)
        .find('h6')
        .at(0)
        .children()
        .contains('Node name : ')
    ).toBeTruthy();
    // Node type displayed
    expect(
      wrapper
        .find(FlexItem)
        .find('h6')
        .at(1)
        .children()
        .contains('Node type : ')
    ).toBeTruthy();
    // Node id displayed
    expect(
      wrapper.find(FlexItem).find('h6').at(2).children().contains('Node id : ')
    ).toBeTruthy();
  });

  it('Node trigger success tests', async () => {
    let wrapper = await getNodeTriggerWrapper();
    await act(async () => {
      wrapper.find(DropdownToggle).find('button').simulate('click');
    });
    wrapper = wrapper.update();

    await act(async () => {
      wrapper.find(DropdownItem).at(1).simulate('click');
    });
    wrapper = wrapper.update();
    mockedAxios.post.mockResolvedValue({});
    await act(async () => {
      wrapper.find('#trigger').find('button').simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find('ProcessDetailsErrorModal');
    // takes snapshot of the success modal
    expect(wrapper).toMatchSnapshot();
    // check the modal content
    expect(
      wrapper.find('ProcessDetailsErrorModal').props()['errorString']
    ).toEqual('The node Book was triggered successfully');
  });

  it('Node trigger failure tests', async () => {
    let wrapper = await getNodeTriggerWrapper();
    await act(async () => {
      wrapper.find(DropdownToggle).find('button').simulate('click');
    });
    wrapper = wrapper.update();

    await act(async () => {
      wrapper.find(DropdownItem).at(1).simulate('click');
    });
    wrapper = wrapper.update();
    mockedAxios.post.mockRejectedValue({ message: '403 error' });
    await act(async () => {
      wrapper.find('#trigger').find('button').simulate('click');
    });
    wrapper = wrapper.update();
    wrapper = wrapper.find('ProcessDetailsErrorModal');
    // takes snapshot of the failed modal
    expect(wrapper).toMatchSnapshot();
    // check the modal content
    expect(
      wrapper.find('ProcessDetailsErrorModal').props()['errorString']
    ).toEqual('The node Book trigger failed. ErrorMessage : "403 error"');
  });
  it('failed to retrieve nodes', async () => {
    mockedAxios.get.mockRejectedValue({ message: '404 error' });
    let wrapper;
    await act(async () => {
      wrapper = mount(
        <ProcessDetailsNodeTrigger processInstanceData={ProcessInstanceData} />
      );
      await wait(0);
      wrapper = wrapper.update().find('ProcessDetailsNodeTrigger');
    });
    wrapper = wrapper.find(ProcessDetailsErrorModal);
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find(ProcessDetailsErrorModal).exists()).toBeTruthy();
    expect(
      wrapper.find(ProcessDetailsErrorModal).props()['errorString']
    ).toEqual('Retrieval of nodes failed with error: 404 error');
  });
});
