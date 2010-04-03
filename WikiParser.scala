package eu.budabe.oiaw

import scala.util.parsing.combinator.syntactical._
import scala.util.parsing.combinator._
import scala.util.matching.Regex

import scala.io._

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

  val start_properties_regexp = build_regexp("properties-header")
  val properties_regexp = build_regexp("properties-template")
  val end_properties_regexp = build_regexp("properties-end")


  val start_relationships_regexp = build_regexp("relationships-header")
  val relationships_regexp = build_regexp("relationships-template")
  val end_relationships_regexp = build_regexp("relationships-end")
  /**
   * Extracts the list of properties from the class definition and return it
   *
   * The algorithm works on the assumption that the properties follow each other in a given
   * text fragment, marked on the one side by the properties-header (e.g. a table header) and
   * on the other side by an end marker (e.g. end of table or new lines)
   */
  def parse_properties(range_class_id : String, s : String) : List[Property] = {
    val properties_start = start_properties_regexp.findFirstMatchIn(s)
    if(properties_start.isDefined) {
      val end = end_properties_regexp findFirstMatchIn(s.substring(properties_start.get.end))
      val end_of_hit = if(end.isDefined) properties_start.get.end + end.get.start else s.length
      val part = s.substring(properties_start.get.end, if(end_of_hit < s.length) end_of_hit else s.length)
      val match_it = properties_regexp.findAllIn(part)
      match_it.matchData.map {
	hit => {
	  new Property(hit.group("property-name").trim, hit.group("property-description").trim, hit.group("property-id").trim,
		       hit.group("alternate-id").trim, range_class_id, hit.group("value-domain").trim)
	}
      }.toList
    } else {
      Nil
    }
  }

  /**
   * Extracts the list of relationships from the class definition and return it
   *
   * The algorithm works on the assumption that the properties follow each other in a given
   * text fragment, marked on the one side by the properties-header (e.g. a table header) and
   * on the other side by an end marker (e.g. end of table or new lines)
   */
  def parse_relationships(range_class_id : String, s : String) : List[Relationship] = {
    val rel_start = start_relationships_regexp.findFirstMatchIn(s)
    if(rel_start.isDefined) {
      val end = end_relationships_regexp findFirstMatchIn(s.substring(rel_start.get.end))
      val end_of_hit = if(end.isDefined) rel_start.get.end + end.get.start else s.length
      val part = s.substring(rel_start.get.end, if(end_of_hit < s.length) end_of_hit else s.length)
      val match_it = relationships_regexp.findAllIn(part)
      match_it.matchData.map {
	hit => {
	  new Relationship(hit.group("relationship-name").trim, hit.group("relationship-description").trim, hit.group("relationship-id").trim,
			   range_class_id,
			   hit.group("player-type1").trim, hit.group("role-type1").trim,
			   hit.group("player-type2").trim, hit.group("role-type2").trim)
	}
      }.toList
    } else {
      Nil
    }
  }

  /**
   * Build the class definition with its properties and relationships and returns it
   * (or None, if no class definition could be found in the text fragment)
   */
  def parse_class(s : String) : Option[Topic] = {
    //Find subclass header in the class string and extract information
    //on the class in question
    val class_start = start_class_regexp.findFirstMatchIn(s)
    if(class_start.isDefined) {
      val class_def_part = s.substring(class_start.get.end)
      val class_match = class_regexp.findFirstMatchIn(class_def_part) //we know that there's only one
      if(class_match.isDefined) {
	val properties = parse_properties(class_match.get.group("class-id").trim, s)
	val relationships = parse_relationships(class_match.get.group("class-id").trim, s)
	Some(new Topic(class_match.get.group("classname").trim, class_match.get.group("class-id").trim, class_match.get.group("subclass-of").trim,
		properties, relationships))
      } else {
	None
      }
    } else {
      None
    }
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
  
  def split_classes(src : Source) : List[String] = {
    val src_lines = src.getLines //or fromURL(...).toString

    src_lines.foldLeft("")( _ + _ ).split(template.getProperty("class-separator")).filter(_ != "").toList
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


