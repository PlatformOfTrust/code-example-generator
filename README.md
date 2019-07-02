# Code Example Generator

[![Build Status](https://travis-ci.org/PlatformOfTrust/code-examples-generator.svg?branch=master)](https://travis-ci.org/PlatformOfTrust/code-examples-generator)

The purpose of this project is to create a command line tool that is able to 
parse Platform of Trust API documentation and examples from [RAML 1.0](RAML-spec) 
files and generate example HTTP requests in various languages.

For instructions how to use this tool see TODO!

## Getting Started

These instructions will get you a copy of the project up and running on your 
local machine for development and testing purposes.

### Prerequisites

This is a [Clojure][clj] project and requires:

1. [Java 8 or above][jdk]
2. [Leiningen 2.0 or above][lein] (OSX users can use `brew install leiningen`).

You can make sure that everything is installed by running `java --version && lein -v`.

```
$ java --version && lein -v
java 11.0.2 2018-10-16 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.2+7-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.2+7-LTS, mixed mode)
Leiningen 2.9.1 on Java 11.0.2 Java HotSpot(TM) 64-Bit Server VM
```

### Running the application

`lein run` will run the code example generator (and install dependencies). See 
TODO! how to use it.

## Running the tests

```
lein test                               # Run unit tests
lein cloverage                          # Generate code coverage report
lein test :integration                  # Run integration tests
lein test :all                          # Run all the tests
```

NB! This project is expected to have > 90% code coverage for unit tests and it 
has been set as a criteria for successful builds in CI.

### Integration tests

This tool will generate HTTP request examples according to provided HTTP 
requests templates and API documenation in RAML. See TODO! how it works. Unit 
tests should be sufficient to make sure that generate examples have been 
created correctly but they cannot guarantee that requests will actually work in 
their respetive environments due to errors in either documentation or templates.

Integration tests will take the generated HTTP request examples and run them 
against [Mockbin](mockbin) HTTP endpoints to make sure that requests work in 
their respective environments.

TODO! More details about setup and running.

Passing integration tests is a requirement for successful builds in CI!

## Deployment

Each commit to master branch will trigger a new build process that will build a 
binary (jar file e.g. `raml2http-<branch_name>.jar`) that will be uploaded to 
TODO! See TODO! how to download and use it.

TODO! Maybe add tagging which creates raml2http-1.0.2.jar etc. It would be nice 
but needs time to test and fiddle with CI.

## Contributing

You might want to...

- Follow [The Clojure Style Guide][bbatsov] for consistency.
- Commit and create PRs using [Conventional Commits standard](cnvc).
- Use [SemVer](semver).
- Update [Change Log](./CHANGELOG.md).

```
lein eastwood                           # Linter
lein kibit                              # Static code analyzer
lein bikeshed                           # Gives tips for writing better code
lein ancient                            # Check for outdated dependencies
```

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.

--------------------------------------------------------------------------------
Copyright © 2019 Platform Of Trust

[RAML-spec]: https://github.com/raml-org/raml-spec/blob/master/versions/raml-10/raml-10.md
[clj]: https://clojure.org/
[jdk]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
[lein]: https://leiningen.org/
[mockbin]: http://mockbin.org/
[bbatsov]: https://github.com/bbatsov/clojure-style-guide
[semver]: http://semver.org/
[cnvc]: https://www.conventionalcommits.org/
[braveclojure]: https://www.braveclojure.com/clojure-for-the-brave-and-true/
