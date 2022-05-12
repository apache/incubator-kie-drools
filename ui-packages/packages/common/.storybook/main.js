module.exports = {
  "stories": [
    "../src/**/*.stories.mdx",
    "../src/**/*.stories.@(js|jsx|ts|tsx)"
  ],
  "addons": [
    "@storybook/addon-actions/register",
    "@storybook/addon-essentials",
    "@storybook/addon-links",
    "@storybook/addon-interactions",
    "@storybook/addon-controls",
    "@storybook/addon-storysource/register",
    "@storybook/addon-a11y/register"
  ],
  "framework": "@storybook/react"
}