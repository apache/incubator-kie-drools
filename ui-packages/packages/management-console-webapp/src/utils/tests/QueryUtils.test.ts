import { ProcessInstanceState } from '@kogito-apps/management-console-shared/dist/types';
import { buildProcessListWhereArgument } from '../QueryUtils';

describe('QueryUtils test', () => {
  it('buildWhereArgument', () => {
    const filtersWithoutBusinessKey = {
      status: [ProcessInstanceState.Active],
      businessKey: []
    };

    const filtersWithBusinessKey = {
      status: [ProcessInstanceState.Active],
      businessKey: ['GMR31']
    };
    const result1 = buildProcessListWhereArgument(filtersWithoutBusinessKey);
    const result2 = buildProcessListWhereArgument(filtersWithBusinessKey);
    expect(result1.or).toBe(undefined);
    expect(result2.or).toEqual([
      { businessKey: { like: filtersWithBusinessKey.businessKey[0] } }
    ]);
  });
});
