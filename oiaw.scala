package  eu.budabe.oiaw

import scala.xml._
import scala.xml.dtd._

abstract class Construct() {
  //def toXTM(): Elem
  def toOWL() : Elem
}

object Construct {
  var base_uri : String = ""
  val scope = new NamespaceBinding("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#", TopScope)

  val existing_url_re = """http://(.*)""".r
  val existing_type_re = """.*(xsd:\w+).*""".r

  def toUri(resource_uri : String) : String = {
    resource_uri match {
      case existing_url_re(rest) => resource_uri
      case _ => base_uri + resource_uri
    }
  }

  def toValueDomain(resource_uri : String) : String = {
    resource_uri match {
      case existing_type_re(xsd_type) => xsd_type
      case "" => "xsd:string"
      case _ => "xsd:string" //free text descriptions
    }
  }
}

case class Property(val property_name : String, 
		    val property_description : String, 
		    val property_id : String, 
		    val alternate_id : String,
		    val range : String,
		    val value_domain : String) extends Construct() {
  def toOWL() = {
    <owl:DatatypeProperty rdf:about={Construct.toUri(property_id)}
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdfs:label>{property_name}</rdfs:label>
        {
	  if(alternate_id != "")
	     <owl:sameAs rdf:resource={alternate_id}/>
	}
        <rdfs:domain rdf:resource={Construct.toUri(range)}/>
	<rdfs:range rdf:resource={Construct.toValueDomain(value_domain)}/> 
    </owl:DatatypeProperty>
  }
  
}

case class Relationship(val relationship_name : String, 
			val relationship_description : String, 
			val relationship_id : String,
			val range : String,
			val player_type1 : String, 
			val role_type1 : String,
			val player_type2 : String,
			val role_type2 : String) extends Construct() {
  def toOWLBasic() : Elem = {
    <owl:ObjectProperty rdf:about={Construct.toUri(relationship_id)}
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdfs:label>{relationship_name}</rdfs:label>
    </owl:ObjectProperty>
  }
  def toOWL() = {
    <owl:ObjectProperty rdf:about={Construct.toUri(player_type1 + "-" + relationship_id + "-" + player_type2)}
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
        <rdfs:label>{relationship_name}</rdfs:label>
        <rdfs:domain rdf:resource={Construct.toUri(player_type2)}/>
        <rdfs:range rdf:resource={Construct.toUri(player_type1)}/>
        <rdfs:subPropertyOf rdf:resource={Construct.toUri(relationship_id)}/>
    </owl:ObjectProperty>
  }
  
}

case class Topic (val classname : String, 
		  val class_id : String, 
		  val subclass_id : String,
		  val properties : List[Property],
		  val relationships : List[Relationship]) extends Construct() {
  def toOWL() = {
    <owl:Class rdf:about={Construct.toUri(class_id)}
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdfs:label>{classname}</rdfs:label>
    {
      if(subclass_id != "")
	<rdfs:subClassOf rdf:resource={Construct.toUri(subclass_id)}/>
    }
    </owl:Class>
  }
}

case class OIAW(topics : List[Topic], base_uri: String) extends Construct() {
  Construct.base_uri = base_uri

  /**
   * This internal class exists only to program around a bug in the scala ParsedEntityDecl
   * the toString method is not correctly overridden in that class
   */
  class PE(name: String, entdef: EntityDef) extends scala.xml.dtd.ParsedEntityDecl(name, entdef) {
    override def toString : String = {
      val sb = new StringBuilder()
      
      return this.toString(sb).toString
    }
  }
  val docType = scala.xml.dtd.DocType("rdf:RDF", SystemID(""), 
				      List(new PE("rdf", new IntDef("http://www.w3.org/1999/02/22-rdf-syntax-ns#")), 
					   new PE("owl", new IntDef("http://www.w3.org/2002/07/owl#")),
					   new PE("xsd", new IntDef("http://www.w3.org/2001/XMLSchema#")),
					   new PE("rdfs", new IntDef("http://www.w3.org/2000/01/rdf-schema#")),
					   new PE("oiaw", new IntDef("http://www.budabe.eu/oiaw"))))
  def toOWL() = {
    <rdf:RDF xmlns="http://psi.egovpt.org/types/"
    xml:base={Construct.base_uri}
    xmlns:owl2xml="http://www.w3.org/2006/12/owl2-xml#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:types="http://psi.egovpt.org/types/"
    xmlns:owl="http://www.w3.org/2002/07/owl#">
    <owl:Ontology rdf:about={Construct.base_uri + "ontology"}/>
    {topics.map {top => top.toOWL} }
    {topics.map {top => top.properties.map {prop => prop.toOWL} } }
    {topics.map {top => top.relationships.map {rel => rel.toOWLBasic} } }
    {topics.map {top => top.relationships.map {rel => rel.toOWL} } }
    </rdf:RDF>
  }

  def saveOWL(filename : String) {
    //XML.saveFull(filename, toOWL, "UTF-8", true, oiaw.docType)
    XML.saveFull(filename, toOWL, "UTF-8", true, null)
  }
}

