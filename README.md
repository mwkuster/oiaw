# oiaw
Automatically exported from code.google.com/p/oiaw

Ontology in a Wiki is a simple but flexible Scala library + program to express ontologies in simple Wiki markup and build both OWL and Topic Map renderings based on Wiki pages

Just executing
```
./oiaw -f OWL -s test.wiki -t oiaw.template -b "http://www.budabe.eu/oiaw/test#" -o /tmp/oiaw2.xml 
```

will generate the corresponding OWL ontology

Of course, you can also do the same starting directly from the raw version Wiki page on the web:
```
./oiaw -f OWL -u "http://www.egovpt.org/fg/oiaw_test?action=raw" -t oiaw.template 
  -b "http://www.budabe.eu/oiaw/test#" -o /tmp/oiaw3.xml 
```

OiaW has been tested mainly against the popular MoinMoin wiki, but as in oiaw.template you can freely configure the markup to be evaluated it should work equally well with other line-oriented Wiki markup systems.

Usage:

You must specify at least the format, a source (file or url), the template and a base URI

Command line parameters:
```
  -b,--baseuri <arg>      URI to be used as base URI for the generated
                         ontology
 -f,--format <arg>       Ontology format to generate. Permitted values:
                         OWL or TM
 -h,--help               Help message
 -i,--import <arg>       URI or path for an ontology that is imported
 -o,--outputfile <arg>   Output file to put the generated URL into
 -s,--sourcefile <arg>   Source file to load the Wiki page from
 -t,--template <arg>     Template file
 -u,--url <arg>          URL to load the Wiki page from
```

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
export SCALA_HOME=/opt/local/share/scala-2.12
```

If you wish you can move the executing shell script and the jars (oiaw.jar and the jars in lib) to some other directory and put it in the path. Just ensure that the respective structures remain unchanged
