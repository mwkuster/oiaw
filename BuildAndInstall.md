# Building OiaW #

  * Check out the OiaW sources in to a directory of your choice
  * Ensure that you have Scala >= 2.7 installed
  * Copy build.xml\_template to build.xml and adapt the path to the scala standard libraries

Then execute in the directory into which you have checked out OiaW
```
ant test
```
and verify that all unit tests pass without problems. Then build the actual oiaw.jar with
```
ant dist
```


# Installing under Unix #

Ensure that the environment variable SCALA\_HOME is set and points to the directory with the scala standard libraries. For a scala installed via port under Mac OS X this would for example be:
```
export SCALA_HOME=/opt/local/share/scala-2.7
```

If you which you can move the executing shell script and the jars (oiaw.jar and the jars in lib) to some other directory and put it in the path. Just ensure that the respective structures remain unchanged