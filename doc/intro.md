# How to use Code Example Generator CLI tool

## Installation

### Prerequisites

1. [Java Runtime Environment][jre]

You can run `java --version` to check if you already have it installed.

### Download jar file

TODO!


## Using the CLI tool


run `java -jar raml2http.jar` to display command line help.

```
java -jar raml2http.jar
  -s, --source PATH                    Required RAML file or a directory that contains RAML files.
  -d, --dest PATH      ./pot-examples  Optional Directory for generated code examples.
  -H, --host HOST      pot.org         Required URI host e.g. `pot.org`.
  -S, --scheme SCHEME  https           Optional URI scheme (`https` or `http`).
  -h, --help
  -v, --version
```

### Examples 

Example 1: Read RAML files from `./raml-files` and save code examples to 
`./code-examples`.

```
java -jar raml2http.jar -s ./raml-files -d ./code-examples
```

or

```
java -jar raml2http.jar --source ./raml-files --destination ./code-examples
```

Example 2: Specify host and scheme (http://mockbin.com/request).

```
java -jar raml2http.jar -s ./raml-files -d ./code-examples -H mockbin.com/request -S http
```

Example 3: Specify host. Default scheme (`https`) is used.

```
java -jar raml2http.jar -s ./raml-files -d ./code-examples -H pot.org
```

## How code example generation works

1. Code examples generator will parse RAML files in source folder and generate 
a context map for each HTTP request that has been documented. 
2. Then it will look up all the templates located in `resources/templates` 
(included in the binary).
3. Next it will render each template with provided context map resulting in
an code example.
4. Finally code example will be saved as a file.

### 1. Example context map for `GET /v1/products/{version}`
```
{:request-method :get
 :scheme "https"
 :server-name "pot.org"
 :uri "/products/{version}"
 :query-string "offset%3F=200&limit%3F=400"}

```

### 2. Example python template (`resources/templates/requests.py`)
```
import requests

response = requests.{{request-method|name}}(
    '{{scheme}}://{{server-name}}{{uri}}',
    {% if query-string %}params={{query-string|safe}},{% endif %}
    {% if headers %}headers={{headers|json|safe|default:"{}"}},{% endif %}
    {% if body %}data=({{body|json|safe}}){% endif %}
)

# Inspect some attributes of the `requests` repository
json_response = response.json()
print(json_response);
```

### 3. Rendered code example in python.
```
import requests

response = requests.get(
    'https://pot.org/products/{version}',
    params=offset%3F=200&limit%3F=400,
)

# Inspect some attributes of the `requests` repository
json_response = response.json()
print(json_response);
```

### 4. Code example location 

Location is determined base on the following input:
- RAML file path e.g. `/<RAML_ROOT>/product-api/product-api.raml`
- resource name e.g. `/products/{version}`
- HTTP method e.g. `GET`
- template filename in `resources/templates` e.g. `python.py, curl, unirest.node.js`


```
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/curl
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/python.py
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/unirest.node.js
```

## Adding, Modifying and removing code examples

This project uses [Selmer][selmer] templating engine for describing code example 
templates. You can modify and remove existing templates located at 
`resources/templates` and the changes will reflect in generated examples. Adding 
a new file to `resources/templates` will result in additional code example 

Example: `touch ./resources/templates/unirest.php` will create a new code example 
`./code-examples/product-api/product-api.raml/_products_{version}/GET/unirest.php`.

Setting up the development environment and running code example generator via 
`lein run` is required for this. Another option is to push changes to github 
and wait for the ci to build a new jar file.

### Overriding templates locally (this feature is not implemented yet!)

It is possible to override templates by providing jar file with custom template 
source. This will make the code example generator ignore bundled template files 
and look for templates from user provided path.

```
# Add new template
$ echo "{{scheme}}://{{server-name}}{{uri}}" > ./my-templates/unirest.php 

# Run code examples generator
$ java -jar raml2http.jar -s ./raml-files -d ./code-examples -t ./my-templates -H pot.net

# View rendered code example
$ cat ./code-examples/product-api/product-api.raml/_products_{version}/GET/unirest.php
https://pot.net/products/{version}
```

--------------------------------------------------------------------------------
Copyright © 2019 Platform Of Trust

[jre]: https://docs.oracle.com/goldengate/1212/gg-winux/GDRAD/java.htm
[selmer]: https://github.com/yogthos/Selmer
