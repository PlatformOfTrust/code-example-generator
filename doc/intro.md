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

## Adding, removing and modifying code examples


### How code example generation works

1. Code examples generator will parse RAML files in source folder and generate 
a context map for each HTTP request that has been documented. 
2. Then it will look up all the templates located in `resources/templates`
3. Then it will render each template with provided context map resulting in
an code example.
4. Finally code example will be saved as a file.

#### 1. Example context map for `GET /v1/products/{version}`
```
{:request-method :get
 :scheme "https"
 :server-name "pot.org"
 :uri "/products/{version}"
 :query-string "offset%3F=200&limit%3F=400"}

```

#### 2. Example python template (`resources/templates/requests.py`)
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

#### 3. Rendered code example in python.
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


#### 4. Code example location 

The final file location will be based on the following data:
- RAML file `/<RAML_ROOT>/product-api/product-api.raml`
- resource name `/products/{version}`
- HTTP method `GET`
- template filename in `resources/templates`:
  - `requests.py, curl, unirest.node.js`


```
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/curl
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/requests.py
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/unirest.node.js
```

### Adding new templates

Add new template file to `resources/templates` and run code examples generator.

For example adding `resources/templates/unirest.php` will create a new code
example.

`/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/unirest.php`

### Removing existing templates

Removing a template file from `resources/templates` will remove code examples 
based on this template.

### Modifying existing templates

This project uses [Selmer][selmer] templating engine.


### Override templates directory

 Not implemented!

--------------------------------------------------------------------------------
Copyright © 2019 Platform Of Trust

[jre]: https://docs.oracle.com/goldengate/1212/gg-winux/GDRAD/java.htm
[selmer]: https://github.com/yogthos/Selmer
