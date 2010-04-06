package eu.budabe.oiaw

import scala.io._
import org.apache.commons.cli._


object OIAWrun {
  /**
   * Evaluates the command line arguments and generates errors as necessary
   * The implementation uses the Apache CLI library that also fetchData uses
   * 
   * args: the array of command line arguments
   */ 
  def evaluate_options(args : Array[String]) : CommandLine = {
    try {
      val options = new Options()
     
      val format_option = new Option("f", "format", true, "Ontology format to generate. Permitted values: OWL or TM")
      format_option.setRequired(true)
      options.addOption(format_option)

      val base_uri_option = new Option("b", "baseuri", true, "URI to be used as base URI for the generated ontology")
      base_uri_option.setRequired(true)
      options.addOption(base_uri_option)

      val template_option = new Option("t", "template", true, "Template file")
      template_option.setRequired(true)
      options.addOption(template_option)

      val source_options = new OptionGroup()
      source_options.addOption(new Option("u", "url", true, "URL to load the Wiki page from"))
      source_options.addOption(new Option("s", "sourcefile", true, "Source file to load the Wiki page from"))
      source_options.setRequired(true)
      options.addOptionGroup(source_options)

      val output_option = new Option("o", "outputfile", true, "Output file to put the generated URL into")
      output_option.setRequired(true)
      options.addOption(output_option)

      val parser = new org.apache.commons.cli.PosixParser()
      
      parser.parse( options, args)
    } catch {
      case moe : org.apache.commons.cli.MissingOptionException => {
	      println("Error in the evaluation of the command line. The program exits")
	      throw new Exception(moe.getMessage())
      }
    }
  }

  def main(args : Array[String]) = {
    //Analyse command line options and generate errors if necessary
    try {
      val cmd = evaluate_options(args)

      val src = 
	if(cmd.hasOption('s')) {
	  Source.fromFile(cmd.getOptionValue("s"), "UTF-8")
	} else {
	  Source.fromURL(cmd.getOptionValue("u"))
	}
      val w = new WikiParser(src, cmd.getOptionValue("t"))

      val oiaw = new OIAW(w.get_topics, cmd.getOptionValue('b'))
      if(cmd.getOptionValue('f') == "OWL") {
	oiaw.saveOWL(cmd.getOptionValue('o'))
      } else {
	println("Option " + cmd.getOptionValue('f') + " not implemented")
      }
      
    }
  }
}
