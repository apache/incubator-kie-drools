module.exports = {
  presets: [
    [
      "@babel/env",
      {
        modules: "cjs",
        targets: {
          node: "current",
        },
      },
    ],
    "@babel/react",
  ],
};
