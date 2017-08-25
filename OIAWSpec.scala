package eu.budabe.oiaw.tests

import org.scalatest.WordSpec
import org.scalatest.Suite
//import org.scalatest.matchers.ShouldMatchers
import org.scalatest.words.ShouldVerb

import scala.xml._
import scala.xml.dtd._

import eu.budabe.oiaw._

class OIAWSpec extends WordSpec with ShouldVerb  {
  val w = new WikiParser(scala.io.Source.fromFile("test.wiki"), "oiaw.template")
  val tops = w.get_topics()

  "WikiParser" should {
    "find 3 classes in test.wiki" in {
      assert(tops.length == 3)
    }
    "find altogether 4 properties in test.wiki" in {
      val num_props = tops.foldLeft(0){(i, t2) => i + t2.properties.length}
      assert(num_props == 4)
    }
    "find altogether 8 relationships in test.wiki" in {
      val num_rels = tops.foldLeft(0){(i, t2) => i + t2.relationships.length}
      assert(num_rels == 7 + 1)
    }
    "be called Work and have the id work" in {
      val res = tops(0)
      assert(res.classname == "Work")
      assert(res.class_id == "work")
      assert(res.subclass_id == "")
    }
    "have 4 properties for work" in {
      val res = tops(0)
      assert(res.properties.length == 4)
    }
    "have as second property the identifier for work" in {
      val res = tops(0)
      val id_property = res.properties(1)
      assert(id_property.property_name == "Work identifier")
      assert(id_property.value_range == "xsd:string")
      assert(id_property.property_id == "id")
      assert(id_property.deprecated == true)
    }
    "have 7 relationships for work" in {
      val res = tops(0)
      assert(res.relationships.length == 7)
    }
    "have as second relationship for work cited_by" in {
      val res = tops(0)
      val cited_by = res.relationships(1)
      assert(cited_by.relationship_name == "Work cited by work")
      assert(cited_by.player_type1 == "work")
      assert(cited_by.player_type2 == "work")
      assert(cited_by.deprecated == false)
    }
  }

  "OAIW" should {
    val oiaw = new OIAW(tops, "http://www.budabe.eu/oiaw/test#", "")
    val owl = oiaw.toOWL
    val xtm = oiaw.toXTM

    "generate all required OWL classes" in {
      //val class_elems = oiaw.toOWL \\ "{http://www.w3.org/2002/07/owl#}Class"
      val class_elems  = owl \\ "Class"
      //3 named classes and 2 classes coming from restrictions on properties
      assert(class_elems.length == 3 + 2)
    }
    "generate 8 OWL data properties (4 real, 4 superproperties)" in {
      val props_elems  = owl \\ "DatatypeProperty"
      assert(props_elems.length == 4 * 2)
    }
    "generate 8 * 2 object properties" in {
      val props_elems  = owl \\ "ObjectProperty"
      assert(props_elems.length == 8 * 2) //every object property
					   //exists as an "abstract"
					   //one and as a concrete one
					   //with domain and range
    }
    "generate expected identifiers" in {
      val res_class = (owl \\ "Class")(0)
      val res_id = res_class.attribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about")
      assert(res_id.isDefined)
      assert(res_id.get.toString == "http://www.budabe.eu/oiaw/test#work")
    }
    "generate 3 + 4 + 8 + 16 topics (16 is the number of base topics)" in {
      val res_tops = xtm \\ "topic"
      assert(res_tops.length == 3 + 4 + 8 + 16)
    }
    "generate 3 + 4 + 8 associations" in {
      val res_ass = xtm \\ "association"
      assert(res_ass.length == 3 + 4 + 8)
    }
  }
}
