import React from 'react';

const MockedProcessDetailsProcessVariables = ({
  setDisplayLabel
}): React.ReactElement => {
  React.useEffect(() => {
    setDisplayLabel(true);
  }, []);
  return <></>;
};

export default MockedProcessDetailsProcessVariables;
