import React, { useState, useEffect } from 'react';
import {
  DataToolbar,
  DataToolbarContent,
  DataToolbarToggleGroup,
  DataToolbarGroup,
  PageSection,
  Breadcrumb,
  BreadcrumbItem,
  Card,
  Bullseye
} from '@patternfly/react-core';
import { FilterIcon } from '@patternfly/react-icons';
import { Link } from 'react-router-dom';
import { Redirect } from 'react-router';
import './DomainExplorerDashboard.css';
import DomainExplorerColumnPicker from '../../Organisms/DomainExplorerColumnPicker/DomainExplorerColumnPicker';
import DomainExplorerTable from '../../Organisms/DomainExplorerTable/DomainExplorerTable';
import PageTitleComponent from '../../Molecules/PageTitleComponent/PageTitleComponent';
import SpinnerComponent from '../../Atoms/SpinnerComponent/SpinnerComponent';

import {
  useGetQueryTypesQuery,
  useGetQueryFieldsQuery,
  useGetInputFieldsFromQueryQuery,
  useGetColumnPickerAttributesQuery
} from '../../../graphql/types';

export interface IOwnProps {
  domains: any;
}

const DomainExplorerDashboard = props => {
  const domainName = props.match.params.domainName;
  let BreadCrumb = props.location.pathname.split('/');
  BreadCrumb = BreadCrumb.filter(item => {
    if (item !== '') {
      return item;
    }
  });
  const [pathName] = BreadCrumb.slice(-1);
  const [initData2, setInitData2] = useState<any>({
    __schema: { queryType: [] }
  });
  const [schemaDropdown, setSchemaDropDown] = useState(false);
  const [currentCategory, setCurrentCategory] = useState('Domains');
  const [currentSchema, setCurrentSchema] = useState([]);
  const [currentQuery, setCurrentQuery] = useState('');
  const [columnPickerType, setColumnPickerType] = useState('');
  const [isCategoryDropdownOpen, setIsCategoryDropdownOpen] = useState(false);
  const [columnFilters, setColumnFilters] = useState({});
  const [tableLoading, setTableLoading] = useState(true);
  const [displayTable, setDisplayTable] = useState(false);
  const [selected, setSelected] = useState([]);
  const [parameters, setParameters] = useState([
    { metadata: [{ processInstances: ['id','processName', 'state', 'start', 'lastUpdate','businessKey'] }] }
  ]);

  const temp = [];

  const getQuery = useGetQueryFieldsQuery();

  const getQueryTypes = useGetQueryTypesQuery();
  const getPicker = useGetColumnPickerAttributesQuery({
    variables: { columnPickerType }
  });

  useEffect(() => {
    setInitData2(getQueryTypes.data);
  }, [getQueryTypes.data]);

  useEffect(() => {
    setColumnPickerType(domainName);
    if (getQuery.data) {
      const _a =
        !getQuery.loading &&
        getQuery.data.__type.fields.find(item => {
          if (item.name === domainName) {
            return item;
          }
        });

      setCurrentQuery(_a.args[0].type.name);
    }
  }, []);

  let data = [];
  const tempArray = [];
  let selections = [];
  let defaultParams = [];
  !getPicker.loading &&
    getPicker.data.__type &&
    getPicker.data.__type.fields.filter(i => {
      if (i.type.kind === 'SCALAR') {
        tempArray.push(i);
      } else {
        data.push(i);
      }
    });
  data = tempArray.concat(data);
  const fields: any = [];
  data.filter(field => {
    if (field.type.fields !== null) {
      const obj = {};
      obj[`${field.name}`] = field.type.fields;
      fields.push(obj);
    }
  });

  fields.map(obj => {
    let value: any = Object.values(obj);
    const key = Object.keys(obj);
    value = value.flat();
    value.filter(item => {
      if (item.type.kind !== 'OBJECT') {
        const tempObj = {};
        selections.push(item.name + key);
        tempObj[`${key}`] = [item.name];
        defaultParams.push(tempObj);
      }
    });
  });

  selections = selections.slice(0, 5);
  defaultParams = defaultParams.slice(0, 5);

  useEffect(() => {
    setParameters(prev => [...defaultParams, ...prev]);
    setSelected(selections);
  }, [columnPickerType, selections.length > 0]);

  useEffect(() => {
    setColumnPickerType(domainName);
    if (getQuery.data) {
      const _a =
        !getQuery.loading &&
        getQuery.data.__type.fields.find(item => {
          if (item.name === domainName) {
            return item;
          }
        });

      setCurrentQuery(_a.args[0].type.name);
    }
  }, []);

  const getSchema: any = useGetInputFieldsFromQueryQuery({
    variables: { currentQuery }
  });

  useEffect(() => {
    setCurrentSchema(temp);
  }, [getSchema.data]);

  const renderToolbar = () => {
    return (
      <DataToolbar
        id="data-toolbar-with-chip-groups"
        className="pf-m-toggle-group-container"
        collapseListedFiltersBreakpoint="md"
      >
        <DataToolbarContent>
          <DataToolbarToggleGroup toggleIcon={<FilterIcon />} breakpoint="md">
            <DataToolbarGroup>
              {!getSchema.loading && (
                <DomainExplorerColumnPicker
                  columnPickerType={columnPickerType}
                  setColumnFilters={setColumnFilters}
                  setTableLoading={setTableLoading}
                  getQueryTypes={getQueryTypes}
                  setDisplayTable={setDisplayTable}
                  parameters={parameters}
                  setParameters={setParameters}
                  selected={selected}
                  setSelected={setSelected}
                  data={data}
                  getPicker={getPicker}
                />
              )}
            </DataToolbarGroup>
          </DataToolbarToggleGroup>
        </DataToolbarContent>
      </DataToolbar>
    );
  };

  return (
    <>
      {!getQuery.loading && !props.domains.includes(domainName) && !props.domains.includes(pathName) && <Redirect to={{
        pathname: '/NoData', state: {
          prev: location.pathname,
          title: 'Domain not found', description: `Domain with the name ${domainName} not found`,
          buttonText: 'Go to domain explorer'
        }
      }} />}
      <PageSection variant="light">
        <PageTitleComponent title="Domain Explorer" />
        <Breadcrumb>
          <BreadcrumbItem>
            <Link to={'/'}>Home</Link>
          </BreadcrumbItem>
          {BreadCrumb.map((item, index) => {
            if (index === BreadCrumb.length - 1) {
              return (
                <BreadcrumbItem isActive key={index}>
                  {item}
                </BreadcrumbItem>
              );
            } else {
              return (
                <BreadcrumbItem key={index}>
                  <Link to={'/DomainExplorer'}>
                    {item.replace(/([A-Z])/g, ' $1').trim()}
                  </Link>
                </BreadcrumbItem>
              );
            }
          })}
        </Breadcrumb>
      </PageSection>
      <PageSection>
        {renderToolbar()}

        {!tableLoading ? (<div className="kogito-management-console--domain-explorer__table-OverFlow">
          <DomainExplorerTable
            columnFilters={columnFilters}
            tableLoading={tableLoading}
            displayTable={displayTable}
          />
        </div>) : (
            <Card>
              <Bullseye>
                <SpinnerComponent spinnerText="Loading domain data..." />
              </Bullseye>
            </Card>
          )}
      </PageSection>
    </>
  );
};

export default React.memo(DomainExplorerDashboard);
