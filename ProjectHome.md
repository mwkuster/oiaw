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