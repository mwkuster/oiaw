package eu.budabe.oiaw

import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.combinator._
import scala.util.matching.Regex
//import scala.util.matching.Match

import scala.io._

trait LineParser {
  type A
  def parse_lines(line : String, lines : List[String], 
                  start_re : Regex, attr_re : Regex, 
                  f : (Regex.Match) => A) : List[A] = {
     def parse_attrs(l : String, ls : List[String], attrs : List[A]) : List[A] = {
       ls match {
         case x :: xs if attr_re.findFirstMatchIn(l).isDefined => {
           val res = f(attr_re.findFirstMatchIn(l).get)
           parse_attrs(x, xs, res :: attrs)
         }
         case Nil if attr_re.findFirstMatchIn(l).isDefined => {
           val res = f(attr_re.findFirstMatchIn(l).get)
           res :: attrs
         }
         case _ => attrs
       }
     }
     lines match {
       case x :: xs if start_re.findFirstMatchIn(line).isDefined =>
         parse_attrs(x, xs, Nil).reverse // present them in their original sequence
       case x :: xs => parse_lines(x, xs, start_re, attr_re, f)
       case Nil => Nil
     }
                   
  }

  //def build_regexp(property : String) : Regex
}

class PropertyParser(val start_properties_regexp : Regex,
                     val properties_regexp : Regex) extends LineParser {
  type A = Property


  /**
   * Extracts the list of properties from the class definition and return it
   *
   * The algorithm works on the assumption that the properties follow each other in a given
   * text fragment, marked on the one side by the properties-header (e.g. a table header) and
   * on the other side by an end marker (e.g. end of table or new lines)
   */
  def parse_properties(range_class_id : String, lines : List[String]) : List[Property] = {
    parse_lines("", lines, start_properties_regexp, properties_regexp, 
                {hit => new Property(hit.group("property-name").trim, 
                                     hit.group("property-description").trim, 
                                     hit.group("property-id").trim,
		                                 hit.group("alternate-id").trim, range_class_id, 
                                     hit.group("value-domain").trim)})
  }
}

class RelationshipParser(val start_relationships_regexp : Regex,
                         val relationships_regexp : Regex) extends LineParser {
  type A = Relationship


  /**
   * Extracts the list of relationships from the class definition and return it
   *
   * The algorithm works on the assumption that the properties follow each other in a given
   * text fragment, marked on the one side by the properties-header (e.g. a table header) and
   * on the other side by an end marker (e.g. end of table or new lines)
   */
  def parse_relationships(range_class_id : String, lines : List[String]) : List[Relationship] = {
    parse_lines("", lines, start_relationships_regexp, relationships_regexp, 
	              { hit => new Relationship(hit.group("relationship-name").trim, 
                                          hit.group("relationship-description").trim, 
                                          hit.group("relationship-id").trim,
			                                    range_class_id,
			                                    hit.group("player-type1").trim, 
                                          hit.group("role-type1").trim,
			                                    hit.group("player-type2").trim, 
                                          hit.group("role-type2").trim) })
	}


}

//Implementation strategy:
//Parse template file to create regular expressions (open as Java properties file?)


//Split input files into separate parts corresponding to individual class declarations
//Use Regular Expressions extractors to extract the variables

class WikiParser(val wikipage : Source, val template_name : String) {
  //A subsection contains a single class declaration
  //A class definition consists of three parts
  // * a subclass declaration
  // * a list of properties
  // * a list of relationships

  val template = load_template(template_name)
  val class_strings = split_classes(wikipage)

  val start_class_regexp = build_regexp("class-header")
  val class_regexp = build_regexp("class-template")

  val rp = new RelationshipParser(build_regexp("relationships-header"),
                                  build_regexp("relationships-template"))
  val pp = new PropertyParser(build_regexp("properties-header"),
                              build_regexp("properties-template"))
  

  /**
   * Build the class definition with its properties and relationships and returns it
   * (or None, if no class definition could be found in the text fragment)
   */
  def parse_class(lines : List[String]) : Option[Topic] = {
    //Find subclass header in the class string and extract information
    //on the class in question
    def pc (line : String, lines : List[String]) : Option[Topic] = {
      lines match {
        case _ if start_class_regexp.findFirstMatchIn(line).isDefined => {
          val s = lines.foldLeft("")(_ + _)

          val class_match = class_regexp.findFirstMatchIn(s) //we know that there's only one
          if(class_match.isDefined) {
	          val properties = pp.parse_properties(class_match.get.group("class-id").trim, lines)
	          val relationships = rp.parse_relationships(class_match.get.group("class-id").trim, lines)
	          Some(new Topic(class_match.get.group("classname").trim, 
                           class_match.get.group("class-id").trim, 
                           class_match.get.group("subclass-of").trim,
		                       properties, relationships))
          } else {
	          None
          }
        }
        case x :: xs => pc(x, xs)
        case _ => None
      }
    }
    pc("", lines)
  }

  def get_topics() : List[Topic] = {
    for {
      s <- class_strings
      top = parse_class(s)
      if top.isDefined
    } yield top.get
  }

  def load_template(filename: String) : java.util.Properties = {
    var template = new java.util.Properties()
    template.load(new java.io.FileReader(filename))
    
    template
  }
  
  def split_classes(src : Source) : List[List[String]] = {
    val src_lines = src.getLines.toList
    val re = new Regex(template.getProperty("class-separator"))
    Utility.splitBefore(src_lines, {line => re.findFirstMatchIn(line).isDefined})
  }
  
  def build_regexp(property : String) : Regex = {
    val brute_regexp : String = template.getProperty(property)

    val regexp = """\{[\w-]+?\}""".r.replaceAllIn(brute_regexp, """([^\|]+)""") + """\s*"""
    val fieldnames =   (for {
      hit <- """\{([\w-]+)\}""".r findAllIn(brute_regexp) matchData
     } yield hit.group(1)) toList

    new Regex (regexp, fieldnames :_*)
  }

}


