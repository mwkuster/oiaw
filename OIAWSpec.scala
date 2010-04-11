package eu.budabe.oiaw.tests

import org.scalatest.WordSpec
import org.scalatest.Suite
import org.scalatest.matchers.ShouldMatchers

import scala.xml._
import scala.xml.dtd._

import eu.budabe.oiaw._

class OIAWSpec extends WordSpec with ShouldMatchers  {
  val w = new WikiParser(scala.io.Source.fromFile("test.wiki"), "oiaw.template")
  val tops = w.get_topics()

  "WikiParser" should {
    "find 14 classes in test.wiki" in {
      assert(tops.length == 14)
    }
    "find altogether 31 properties in test.wiki" in {
      val num_props = tops.foldLeft(0){(i, t2) => i + t2.properties.length}
      assert(num_props == 31)
    }
    "find altogether 23 relationships in test.wiki" in {
      val num_rels = tops.foldLeft(0){(i, t2) => i + t2.relationships.length}
      assert(num_rels == 23)
    }
    "be called Resource and have the id resource" in {
      val res = tops(0)
      assert(res.classname == "Resource")
      assert(res.class_id == "resource")
      assert(res.subclass_id == "")
    }
    "have 10 properties for resource" in {
      val res = tops(0)
      assert(res.properties.length == 10)
    }
    "have as second property the name for resource" in {
      val res = tops(0)
      val name_property = res.properties(1)
      assert(name_property.property_name == "Name")
      assert(name_property.value_range == "")
      assert(name_property.property_id == "name")
    }
    "have 6 relationships for resource" in {
      val res = tops(0)
      assert(res.relationships.length == 6)
    }
    "have as second relationship for resource is-provided-by" in {
      val res = tops(0)
      val provided_by = res.relationships(1)
      assert(provided_by.relationship_name == "is-provided-by")
      assert(provided_by.player_type1 == "resource")
      assert(provided_by.player_type2 == "institution")
    }
  }

  "OAIW" should {
    val oiaw = new OIAW(tops, "http://www.budabe.eu/oiaw/test#")
    val owl = oiaw.toOWL
    val xtm = oiaw.toXTM

    "generate all required OWL classes" in {
      //val class_elems = oiaw.toOWL \\ "{http://www.w3.org/2002/07/owl#}Class"
      val class_elems  = owl \\ "Class"
      assert(class_elems.length == 14)
    }
    "generate 31 OWL data properties" in {
      val props_elems  = owl \\ "DatatypeProperty"
      assert(props_elems.length == 31)
    }
    "generate 23 * 2 object properties" in {
      val props_elems  = owl \\ "ObjectProperty"
      assert(props_elems.length == 23 * 2) //every object property
					   //exists as an "abstract"
					   //one and as a concrete one
					   //with domain and range
    }
    "generate expected identifiers" in {
      val res_class = (owl \\ "Class")(0)
      val res_id = res_class.attribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about")
      assert(res_id.isDefined)
      assert(res_id.get == "http://www.budabe.eu/oiaw/test#resource")
    }
    "generate 14 + 31 + 23 + 16 topics (16 is the number of base topics)" in {
      val res_tops = xtm \\ "topic"
      assert(res_tops.length == 14 + 31 + 23 + 16)
    }
    "generate 14 + 31 + 23 associations" in {
      val res_ass = xtm \\ "association"
      assert(res_ass.length == 14 + 31 + 23)
    }
  }
}
