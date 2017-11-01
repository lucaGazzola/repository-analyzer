# Repository Analyzer

A simple Java program that, given a directory which contains repositories, looks in the commit messages and source code files for a specific keyword in each one of them.

## Getting Started

```
java RepositoryAnalyzer sourceDir keyword destFile 
```

`sourceDir` is the parent directory of the repositories that you want to analyze.

`keyword` is the keyword you want to look for.

`destFile` is where the analysis results are saved.


### Prerequisites

Java 8

Maven

All the dependencies are in the pom.xml file.

