#!/usr/bin/env bash
set -exuo pipefail

python --version

# Find all Python examples and execute them
find $CODE_EXAMPLES -type f -name python.py -exec python {} \;
