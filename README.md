# kamon-logback <img align="right" src="https://rawgit.com/kamon-io/Kamon/master/kamon-logo.svg" height="150px" style="padding-left: 20px"/>
[![Build Status](https://travis-ci.org/kamon-io/kamon-logback.svg?branch=kamon-agent)](https://travis-ci.org/kamon-io/kamon-logback)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/kamon-io/Kamon?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.kamon/experimental-kamon-logback_2.12/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.kamon/experimental-kamon-logback_2.12)


The <b>kamon-logback</b> module require you to start your application using the [Kamon Agent].

### Getting Started

Kamon Logback is currently available for Scala 2.10, 2.11 and 2.12.

Supported releases and dependencies are shown below.

| kamon  | status | jdk  | scala            
|:------:|:------:|:----:|------------------
|  1.0.0-RC | experimental | 1.8+ | 2.10, 2.11, 2.12

To get started with SBT, simply add the following to your `build.sbt` or `pom.xml`
file:

```scala
libraryDependencies += "io.kamon" %% "kamon-logback" % "experimental-1.0.1-RC1"
```

```xml
<dependency>
    <groupId>io.kamon</groupId>
    <artifactId>experimental-kamon-logback_2.12</artifactId>
    <version>1.0.0</version>
</dependency>
```

[Kamon Agent]: https://github.com/kamon-io/kamon-agent
