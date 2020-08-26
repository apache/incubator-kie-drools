import formattedScore from '../formattedScore';

describe('format scores', () => {
  test('greater then 0.01', () => {
    expect(formattedScore(2.3234)).toBe('2.32');
  });

  test('lesser then |0.01|', () => {
    expect(formattedScore(0.002)).toBe('< 0.01');
    expect(formattedScore(-0.002)).toBe('< -0.01');
  });
  test('equal to 0', () => {
    expect(formattedScore(0)).toBe('0.00');
  });
});
