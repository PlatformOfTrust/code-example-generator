#!/usr/bin/env bash
set -exuo pipefail

# execute cURL files
find $CODE_EXAMPLES -type f -name curl -exec sh {} \;
