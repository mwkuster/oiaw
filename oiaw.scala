package  eu.budabe.oiaw

import scala.xml._
import scala.xml.dtd._

abstract class Construct() {
  /**
   * Creates the basic topic representing a construct (class, occurrence-class or association-class)
   */
  def toXTM() : Elem
  /**
   * Creates the association between the construct and its constraints
   */
  //def toXTM_type() : Elem
  def toOWL() : List[Elem]
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
      case existing_type_re(xsd_type) => xsd_type.replace("xsd:", "http://www.w3.org/2001/XMLSchema#")
      case existing_url_re(uri) => uri
      case "" => "http://www.w3.org/2001/XMLSchema#string"
      case _ => "http://www.w3.org/2001/XMLSchema#string" //free text descriptions
    }
  }

  /**
   * Creation of the cardinality restrictions for the various properties
   *
   * The function supports only those cardinalities that are primarily needed, could easily be extended to cover more generic cases
   */
  def toCardinality(domain : String, property_id : String, cardinality : String, range : String) : List[Elem] = {
    cardinality match {
      case "1" => {
	List(
	  <owl:Class rdf:about={Construct.toUri(domain)}
	  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	  xmlns:owl="http://www.w3.org/2002/07/owl#" 
	  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
          <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource={Construct.toUri(property_id)}/>
                <owl:cardinality rdf:datatype="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">1</owl:cardinality>
	        {if(range != "") List(<owl:onClass rdf:resource={Construct.toUri(range)}/>) else List()}
            </owl:Restriction>
          </rdfs:subClassOf>
	  </owl:Class>
	)
      }
      case "0..1" => List(
	 <owl:Class rdf:about={Construct.toUri(domain)}
	  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	  xmlns:owl="http://www.w3.org/2002/07/owl#" 
	  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
          <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource={Construct.toUri(property_id)}/>
                <owl:maxQualifiedCardinality rdf:datatype="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">1</owl:maxQualifiedCardinality>
	        {if(range != "") List(<owl:onClass rdf:resource={Construct.toUri(range)}/>) else List()}
            </owl:Restriction>
          </rdfs:subClassOf>
	  </owl:Class>
      )	
      case "1..*" => List(
	 <owl:Class rdf:about={Construct.toUri(domain)}
	  xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	  xmlns:owl="http://www.w3.org/2002/07/owl#" 
	  xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
          <rdfs:subClassOf>
            <owl:Restriction>
                <owl:onProperty rdf:resource={Construct.toUri(property_id)}/>
                <owl:minQualifiedCardinality rdf:datatype="http://www.w3.org/2001/XMLSchema#nonNegativeInteger">1</owl:minQualifiedCardinality>
	        {if(range != "") List(<owl:onClass rdf:resource={Construct.toUri(range)}/>) else List()}
            </owl:Restriction>
          </rdfs:subClassOf>
	  </owl:Class>
      )	//maximal cardinality is implied to be *
      case _ => List() //includes 0..* which means effectively no restriction
    }
  }
}

case class Property(val property_name : String, 
		    val property_description : String, 
		    val property_id : String, 
		    val alternate_id : String,
		    val domain : String,
		    val value_range : String,
		    val cardinality : String) extends Construct() {
  def toOWL() = {
    println("Cardinality: " + cardinality)
    println("Property_id: " + property_id)
    println("Domain: " + domain)
    println("Value_range: " + value_range)
    
    List(
      <owl:DatatypeProperty rdf:about={Construct.toUri(domain + "_" + property_id)}
      xmlns:owl="http://www.w3.org/2002/07/owl#"
      xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
      <rdfs:comment>{property_description}</rdfs:comment>
      <rdfs:label>{property_name}</rdfs:label>
      <rdfs:subPropertyOf rdf:resource={Construct.toUri(property_id)}/>
      {
	if(alternate_id != "")
	  <owl:sameAs rdf:resource={alternate_id}/>
      }
      <rdfs:domain rdf:resource={Construct.toUri(domain)}/>
      <rdfs:range rdf:resource={Construct.toValueDomain(value_range)}/> 
      </owl:DatatypeProperty>,
      <owl:DatatypeProperty rdf:about={Construct.toUri(property_id)}  
      xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"/>
    ) ++ Construct.toCardinality(domain, domain + "_" + property_id, cardinality, "")
  }

  def toXTM() = {
    <topic id={property_id}> 
    <subjectIdentifier href={Construct.toUri(property_id)}/>
    {
      if(alternate_id != "")
	      <subjectIdentifier href={Construct.toUri(alternate_id)}/>
    }
    <instanceOf><topicRef href="t6"/></instanceOf>
    <name><value>{property_name}</value></name>
    </topic>
  }

  def toXTM_type() = {
<association> 
<type><topicRef href="is-property-of"/></type>
<role>
<type><topicRef href="type"/></type>
<topicRef href={domain}/>
</role>
<role>
<type><topicRef href="property"/></type>
<topicRef href={property_id}/>
</role>
</association>
  }

}

case class Relationship(val relationship_name : String, 
			val relationship_description : String, 
			val relationship_id : String,
			val domain : String,
			val player_type1 : String, 
			val role_type1 : String,
			val player_type2 : String,
			val role_type2 : String,
			val relationship_characteristics : String,
			val cardinality : String) extends Construct() {
  val inverseOf_re = """\s*inverseOf:\s*(\w+)\s*""".r
  val inverseOf_transitive_re = """\s*transitiveProperty;\s*inverseOf:\s*(\w+)\s*""".r
  val transitive_re = """\s*(transitiveProperty)\s*""".r

  def get_characteristics() : List[Elem] = {
    relationship_characteristics match {
      case inverseOf_re(inverse_of_id) => {
	List(<owl:inverseOf 
           rdf:resource={Construct.toUri(player_type2 + "_" + inverse_of_id + "_" + player_type1)}
	   xmlns:owl="http://www.w3.org/2002/07/owl#"/>)
      }
      case inverseOf_transitive_re(inverse_of_id) => {
	      List(<owl:inverseOf 
             rdf:resource={Construct.toUri(player_type2 + "_" + inverse_of_id + "_" + player_type1)}
	     xmlns:owl="http://www.w3.org/2002/07/owl#"/>,
             <rdf:type rdf:resource="http://www.w3.org/2002/07/owl#TransitiveProperty"/>)
      }
      case transitive_re(tr) => {
	List(<rdf:type rdf:resource="http://www.w3.org/2002/07/owl#TransitiveProperty"/>)
      }
      case _ => Nil
    }
  }
  def toOWLBasic() : Elem = {
    <owl:ObjectProperty rdf:about={Construct.toUri(relationship_id)}
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdfs:label>{relationship_name}</rdfs:label>
    </owl:ObjectProperty>
  }
  def toOWL() = {
    println("Cardinality: " + cardinality)
    println("Relationship_id: " + player_type1 + "_" + relationship_id + "_" + player_type2)
    println("Player1: " + player_type1)
    println("Player2: " + player_type2)

      <owl:ObjectProperty rdf:about={Construct.toUri(player_type1 + "_" + relationship_id + "_" + player_type2)}
      xmlns:owl="http://www.w3.org/2002/07/owl#"
      xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
      <rdfs:label>{relationship_name}</rdfs:label>
      <rdfs:comment>{relationship_description}</rdfs:comment>
      <rdfs:domain rdf:resource={Construct.toUri(player_type1)}/>
      <rdfs:range rdf:resource={Construct.toUri(player_type2)}/>
      <rdfs:subPropertyOf rdf:resource={Construct.toUri(relationship_id)}/>
      {get_characteristics()}
      </owl:ObjectProperty> ::  Construct.toCardinality(player_type1, player_type1 + "_" + relationship_id + "_" + player_type2, cardinality, player_type2)
  }

  def toXTM() = {
    <topic id={relationship_id}> 
    <subjectIdentifier href={Construct.toUri(relationship_id)}/>
    <instanceOf><topicRef href="t7"/></instanceOf>
    <name><value>{relationship_name}</value></name>
    </topic>
  }

  def toXTM_type() = {
<association> 
<type><topicRef href="is-association-of"/></type>
<role>
<type><topicRef href="type"/></type>
<topicRef href={domain}/>
</role>
<role>
<type><topicRef href="association"/></type>
<topicRef href={relationship_id}/>
</role>
</association>
  }
  
}

case class Topic (val classname : String, 
		  val class_id : String, 
		  val class_description : String, 
		  val subclass_id : String,
		  val properties : List[Property],
		  val relationships : List[Relationship]) extends Construct() {

  def subclass_toOWL() : List[Elem] = {
    if(subclass_id != "")
      List(<rdfs:subClassOf rdf:resource={Construct.toUri(subclass_id)}/>)
    else
      Nil
  }

  def toOWL() = {
    List(
      <owl:Class rdf:about={Construct.toUri(class_id)}
      xmlns:owl="http://www.w3.org/2002/07/owl#"
      xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
      xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
      <rdfs:label>{classname}</rdfs:label>
      {subclass_toOWL()}
      </owl:Class>
    )
  }
  def toXTM_type : Elem = {
    <association> 
    <type><topicRef href="#superclass-subclass"/></type>
    <role>
    <type><topicRef href="#superclass"/></type>
    <topicRef href={if(subclass_id != "") subclass_id else "t1"}/>
    </role>
    <role>
      <type><topicRef href="#subclass"/></type>
       <topicRef href={class_id}/>
     </role>
    </association>
  }
  def toXTM() = {
    <topic id={class_id}> 
    <subjectIdentifier href={Construct.toUri(class_id)}/>
   
    <instanceOf><topicRef href="t1"/></instanceOf>
    <name><value>{classname}</value></name>
    </topic>
  }
}

case class OIAW(topics : List[Topic], base_uri: String, import_uri : String) extends Construct() {
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
    List(
    <rdf:RDF xmlns="http://psi.egovpt.org/types/"
    xml:base={Construct.base_uri}
    xmlns:owl2xml="http://www.w3.org/2006/12/owl2-xml#"
    xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:types="http://psi.egovpt.org/types/"
    xmlns:owl="http://www.w3.org/2002/07/owl#">
    <owl:Ontology rdf:about={Construct.base_uri + "ontology"}>
      {if(import_uri != "") List(<owl:imports rdf:resource={import_uri}/>) else List()}
      </owl:Ontology>
      {topics.map {top => top.toOWL} }
      {topics.map {top => top.properties.map {prop => prop.toOWL} } }
      {topics.map {top => top.relationships.map {rel => rel.toOWLBasic} } }
      {topics.map {top => top.relationships.map {rel => rel.toOWL} } }
      </rdf:RDF>
      )
  }

  def toXTM() = {
    <topicMap xmlns="http://www.topicmaps.org/xtm/" version="2.0">
    <topic id="t1">
    <subjectIdentifier href="http://www.networkedplanet.com/psi/npcl/meta-types/topic-type"/>
    <name><value>Topic Type</value></name>
    </topic>

    <topic id="t6">
    <itemIdentity href="http://psi.egovpt.org/itemIdentifiers#t6"/>
    <subjectIdentifier href="http://www.networkedplanet.com/psi/npcl/meta-types/occurrence-type"/>
    <instanceOf><topicRef href="#t1"/></instanceOf>
    <name><value>Occurrence Type</value></name>
    </topic>

    <topic id="t7">
    <itemIdentity href="http://psi.egovpt.org/itemIdentifiers#t7"/>
    <subjectIdentifier href="http://www.networkedplanet.com/psi/npcl/meta-types/association-type"/>
    <instanceOf><topicRef href="#t1"/></instanceOf>
    <name><value>Association Type</value></name>
    </topic>

    <topic id="t8">
    <itemIdentity href="http://psi.egovpt.org/itemIdentifiers#t8"/>
    <subjectIdentifier href="http://www.networkedplanet.com/psi/npcl/meta-types/association-role-type"/>
    <instanceOf><topicRef href="#t1"/></instanceOf>
    <name><value>Association Role Type</value></name>
    </topic>

    <topic id="superclass-subclass">
    <subjectIdentifier href="http://www.topicmaps.org/xtm/1.0/core.xtm#superclass-subclass"/>
    <name>
      <value>superclass-subclass</value>
    </name>
    </topic>

    <topic id="superclass">
    <subjectIdentifier href="http://www.topicmaps.org/xtm/1.0/core.xtm#superclass"/>
    <name>
      <value>superclass</value>
    </name>
    </topic>

    <topic id="subclass">
    <subjectIdentifier href="http://www.topicmaps.org/xtm/1.0/core.xtm#subclass"/>
    <name>
      <value>subclass</value>
    </name>
    </topic>
    <topic id="type">
    <subjectIdentifier href="http://psi.topicmaps.org/iso13250/model/type"/>
    </topic>
    <topic id="is-property-of">
    <subjectIdentifier href="http://www.isidor.us/constraints/is-property-of"/>
    </topic>
    <topic id="is-association-of">
    <subjectIdentifier href="http://www.isidor.us/constraints/is-association-of"/>
    </topic>
    <topic id="is-role-in">
    <subjectIdentifier href="http://www.isidor.us/constraints/is-role-in"/>
    </topic>
    <topic id="is-player-in">
    <subjectIdentifier href="http://www.isidor.us/constraints/is-player-in"/>
    </topic>
    <topic id="property">
    <subjectIdentifier href="http://www.isidor.us/constraints/property"/>
    </topic>
    <topic id="association">
    <subjectIdentifier href="http://www.isidor.us/constraints/association"/>
    </topic>
    <topic id="role-player">
   <subjectIdentifier href="http://www.isidor.us/constraints/role-player"/>
    </topic>
    <topic id="role-type">
   <subjectIdentifier href="http://www.isidor.us/constraints/role-type"/>
    </topic>
    {topics.map {top => top.toXTM} }
    {topics.map {top => top.toXTM_type} } 
    {topics.map {top => top.relationships.map {rel => rel.toXTM} } }
    {topics.map {top => top.relationships.map {rel => rel.toXTM_type} } }
    {topics.map {top => top.properties.map {prop => prop.toXTM} } }
    {topics.map {top => top.properties.map {prop => prop.toXTM_type} } }
    </topicMap>
  }

  def saveOWL(filename : String) {
    //XML.saveFull(filename, toOWL, "UTF-8", true, oiaw.docType)
    XML.saveFull(filename, toOWL()(0), "UTF-8", true, null)
  }

  def saveXTM(filename : String) {
    //XML.saveFull(filename, toOWL, "UTF-8", true, oiaw.docType)
    XML.saveFull(filename, toXTM, "UTF-8", true, null)
  }
}

