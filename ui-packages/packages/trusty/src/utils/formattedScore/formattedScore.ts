const formattedScore = (inputScore: number) => {
  return Math.abs(inputScore) < 0.01 && Math.abs(inputScore) > 0
    ? `\u003c ${Math.sign(inputScore) * 0.01}`
    : inputScore.toFixed(2);
};

export default formattedScore;
