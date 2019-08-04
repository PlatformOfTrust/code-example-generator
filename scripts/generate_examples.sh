#!/usr/bin/env bash
set -exuo pipefail

# Create a folder for code examples
mkdir -p $CODE_EXAMPLES

# Run code examples generator
# Generate request to mockbin.org
java -jar $TRAVIS_BUILD_DIR/target/raml2http.jar \
     -s $RAML_FILES \
     -d $CODE_EXAMPLES \
     -S $SCHEME \
     -H $HOST


