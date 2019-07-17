# How to use Code Example Generator CLI tool

## Installation

### Prerequisites

1. [Java Runtime Environment][jre]

You can run `java --version` to check if you already have it installed.

### Download jar file

Go to https://github.com/PlatformOfTrust/code-examples-generator/releases and 
choose either specific release or `latest` and download  `raml2http.jar` from 
release assets. 

You can also use cURL to download it from command line.

```
curl -L -o raml2http.jar https://github.com/PlatformOfTrust/code-examples-generator/releases/download/latest/raml2http.jar > /dev/null
```

## Using the CLI tool


run `java -jar raml2http.jar` to display command line help.

```
java -jar raml2http.jar
  -s, --source PATH                     Required RAML file or a directory that contains RAML files.
  -d, --dest PATH      ./pot-examples   Optional Directory for generated code examples.
  -H, --host HOST      api.oftrust.net  Optional URI host.
  -S, --scheme SCHEME  https            Optional URI scheme (`https` or `http`).
  -h, --help
  -v, --version
```

### Examples 

Example 1: default host and scheme (`https://api.oftrust.net`)

```
java -jar raml2http.jar -s ../docs/raml2markdown/src -d ../code-examples
```

Example 2: custom host and scheme (`http://mockbin.com/request`)

```
java -jar raml2http.jar \
    --source ./raml-files \
    --destination ./code-examples \
    --host mockbin.com/request \
    --scheme http
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
 :query-string "offset%3F=200&limit%3F=400"
 :desc "Lists all available products."
 :ok { :status :200
       :body "{ \"@context\": \"https://schema.org/\", \"@type\": \"collection\", \"ItemList\": []}"}}

```

### 2. Example python template (`resources/templates/requests.py`)
```
import requests

response = requests.{{request-method|name}}(
    '{{scheme}}://{{server-name}}{{uri}}',
    {% if query-string %}params='{{query-string|safe}}',{% endif %}
    {% if headers %}headers={{headers|json|safe|default:"{}"}},{% endif %}
    {% if body %}data=({{body|json|safe}}){% endif %}
)

# Inspect some attributes of the `requests` repository
json_response = response.json()
print({'raw_body': json_response, 'status': response.status_code, 'code': response.status_code})
```

### 3. Rendered code example in python.
```
import requests

response = requests.get(
    'https://pot.org/products/{version}',
    params='offset%3F=200&limit%3F=400',
)

# Inspect some attributes of the `requests` repository
json_response = response.json()
print(json_response);
```

### 4. Code example location 

Location is determined based on the following input:
- RAML file path e.g. `/<RAML_ROOT>/product-api/product-api.raml`
- resource name e.g. `/products/{version}`
- HTTP method e.g. `GET`
- template filename in `resources/templates` e.g. `python.py, curl, unirest.node.js`


```
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/curl
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/python.py
/<EXAMPLES_ROOT>/product-api/product-api.raml/_products_{version}/GET/unirest.node.js
```
## Templates

Templates are located in [resources/templates][templates] and currently include 
the following:
- 2xx-response.json - successful response example.
- curl - [cURL](https://curl.haxx.se/).
- python.py - Python using [urllib](https://docs.python.org/3/library/urllib.html).
- slate.md - markdown file for [Slate](slate) API docs of [PlatformOfTrust/docs](pot-docs) .
- unirest.node.js - Node.js using [unirest](http://unirest.io/).

### 2xx-response.json 

`2xx-response.json` is a JSON parsed from RAML files and contains an example 2XX response (201, 204, 200) etc. This could be used for response structure validation.

```
{
  "status": "201",
  "body": {
    "@context": "<Context URL>",
    "@type": "<Identity type>",
    "@id": "<Identity ID>",
    "name": "<Identity name>",
    "data": {
      "key-1": "Value 1",
      "key-2": "Value 2"
    },
    "createdBy": "<User ID>",
    "updatedBy": "<User ID>",
    "createdAt": "2018-02-28T16:41:41.090Z",
    "updatedAt": "2018-02-28T16:41:41.090Z",
    "status": 0,
    "inLinks": [],
    "outLinks": []
  }
}

```

### slate.md

`slate.md` is a markdown file for [Slate][slate] that combines  all specified 
code examples, response example and some extra formatting. See [example](./slate.md).


## Adding, Modifying and removing code examples

This project uses [Selmer][selmer] templating engine for describing code example 
templates. You can modify and remove existing templates located at 
`resources/templates` and the changes will reflect in generated examples. Adding 
a new file to `resources/templates` will result in additional code example.

Example: `touch ./resources/templates/unirest.php` will create a new code example 
`./code-examples/product-api/product-api.raml/_products_{version}/GET/unirest.php`.

For the code examples to included in `slate.md` files add the following lines to 
`resources/templates/slate.md`.

```php
{% include "templates/unirest.php.js" %}
```

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

# View saved code example
$ cat ./code-examples/product-api/product-api.raml/_products_{version}/GET/unirest.php
https://pot.net/products/{version}
```

--------------------------------------------------------------------------------
Copyright © 2019 Platform Of Trust

[jre]: https://docs.oracle.com/goldengate/1212/gg-winux/GDRAD/java.htm
[selmer]: https://github.com/yogthos/Selmer
[templates]: ../resources/templates
[slate]: https://github.com/lord/slate
[pot-docs]: https://github.com/PlatformOfTrust/docs
