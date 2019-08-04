#!/usr/bin/env bash
set -exuo pipefail

node --version
npm --version

# Install Nodejs dependencies
npm install unirest

# Find all unirest.node.js files and execute them
find $CODE_EXAMPLES -type f -name unirest.node.js -exec node {} \;





