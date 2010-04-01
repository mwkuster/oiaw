import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.combinator._
import scala.util.matching.Regex

//Implementation strategy:
//Parse template file to create regular expressions (open as Java properties file?)


//Split input files into separate parts corresponding to individual class declarations
//Use Regular Expressions extractors to extract the variables

case class Topic (val classname : String, 
	     val class_id : String, 
	     val subclass_id : String) {}

class WikiParser(val wikipage : String) {
  //A subsection contains a single class declaration
  //A class definition consists of three parts
  // * a subclass declaration
  // * a list of properties
  // * a list of relationships

  val props = load_properties("oiaw.template")
  val class_strings = split_classes(wikipage)

  val class_regexp = build_regexp("subclass-template")

  def parse_class(s : String) : Topic = {
    //TODO
    new Topic("", "", "")
  }

  def get_topics() : List[Topic] = {
    class_strings.map(s => parse_class(s))
  }

  def load_properties(filename: String) : java.util.Properties = {
    var props = new java.util.Properties()
    props.load(new java.io.FileReader(filename))
    
    props
  }
  
  def split_classes(filename : String) : List[String] = {
    val src_lines = scala.io.Source.fromFile(filename, "UTF-8").getLines //or fromURL(...).toString
    
    // val class_regexp = new Regex("""^\s*"""
// 				 + props.getProperty("class-separator") 
// 				 + """\s*$""")
    //TODO: That must be possible to be more elegant
    src_lines.foldLeft("")( _ + _ ).split(props.getProperty("class-separator")).filter(_ != "").toList
  }
  
  def build_regexp(property : String) : Regex = {
    val brute_regexp : String = props.getProperty(property)
    val subclass_regexp = """\{[\w-]+?\}""".r replaceAllIn(brute_regexp, "(\\\\w+)")
    val fieldnames =   (for {
      hit <- """\{([\w-]+)\}""".r findAllIn(brute_regexp) matchData
     } yield hit.group(1)) toList

    println(fieldnames)

    new Regex (subclass_regexp, fieldnames :_*)
  }

}


