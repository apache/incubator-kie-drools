const tsc = require('typescript');
const tsConfig = require('../../tsconfig.json');

module.exports = {
    process(src, path) {
        if (path.endsWith('.ts') || path.endsWith('.tsx') || path.endsWith('.js')) {
            return tsc.transpile(src, tsConfig.compilerOptions, path, []);
        }
        return src;
    },
};