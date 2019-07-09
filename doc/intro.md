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

## Adding, removing and modifying templates

TODO 
--------------------------------------------------------------------------------
Copyright © 2019 Platform Of Trust

[jre]: https://docs.oracle.com/goldengate/1212/gg-winux/GDRAD/java.htm
