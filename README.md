# hal-scala

[![Build Status](https://travis-ci.org/Product-Foundry/hal-scala.svg?branch=master)](https://travis-ci.org/Product-Foundry/hal-scala)

Small library to generate hal+json compatible output and rel documentation.

Warning
-------

Work in progress, extracted from a working application. Requires Play Framework 2.5.x.

Todo
----
- Create a proper DSL to improve usability
- Support hal+xml

Dependency
----------

To include this library into your `sbt` project, add the following lines to your `build.sbt` file:

    resolvers += "Product-Foundry at bintray" at "http://dl.bintray.com/productfoundry/maven"

    libraryDependencies += "com.productfoundry" %% "hal-scala" % "0.1.5"

This version of `hal-scala` is built against Scala 2.11.8.


