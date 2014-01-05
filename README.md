jacoco-coverage
===============

IntelliJ IDEA coverage plugin based on [JaCoCo](http://www.eclemma.org/jacoco/trunk/index.html).
The purpose of the plugin is to provide coverage information for [IntelliJ IDEA Community Edition](http://www.jetbrains.com/idea/download/index.html).

It allows generation of a HTML report and viewing the coverage data for all instrumented classes.


Developing
----------

### Get the code
Clone project from Github:
```sh    
    git clone git@github.com:resios/jacoco-coverage.git
```

### IntelliJ IDEA plugin SDK setup

* Open the project (*File > Open project*).
* Open the module settings (*Ctrl + Alt + Shift + S*).
* Setup the project SDK with an *Intellij IDEA Plugin SDK*.
* Edit your *Intellij IDEA Plugin SDK* and add `$IDEA_HOME/lib/idea.jar` to its classpath.

### Building

* Use the run configurations to run the plugin.
* Use *Build > Prepare plugin for deployment* to generate the release package.




