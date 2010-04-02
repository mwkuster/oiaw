package eu.budabe.oiaw.tests

import org.scalatest.WordSpec
import org.scalatest.Suite
import org.scalatest.matchers.ShouldMatchers

import eu.budabe.oiaw._

class WikiParserSpec extends WordSpec with ShouldMatchers  {
  val w = new WikiParser("test.wiki", "oiaw.template")
  val tops = w.get_topics()

  "WikiParser" should {
    "find 14 classes in test.wiki" in {
      assert(tops.length == 14)
    }
    "be called Resource and have the id resource" in {
      val res = tops(0)
      assert(res.classname == "Resource")
      assert(res.class_id == "resource")
      assert(res.subclass_id == "")
    }
    "have 10 properties" in {
      val res = tops(0)
      assert(res.properties.length == 10)
    }
    "have as second property the name" in {
      val res = tops(0)
      val name_property = res.properties(1)
      assert(name_property.property_name == "Name")
      assert(name_property.value_domain == "")
      assert(name_property.property_id == "name")
    }
  }
}
