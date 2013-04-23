package com.zeus.graph

import scala.xml._
import java.io.Reader

object GraphMlReader extends (Reader => Seq[Graph]) {
	
	def apply(_reader : Reader) : Seq[Graph] = {
		val graphMlNode = xml.XML.load(_reader)
		val graphNodes = graphMlNode \ "graph"
		
		for(graphNode <- graphNodes) yield {
			graphNode match {
				case g @ <graph>{ _* }</graph> if (g \ "@edgedefault" text) == "directed" => 
					val nodeNodes = g \ "node"
					val edgeNodes = g \ "edge"
					
					val graph = new DirectedGraphImpl(g \ "@id" text)
					
					for(nodeNode <- nodeNodes) {
						nodeNode match {
							case n @ <node/> =>
								graph += (n \ "@id" text)
							case n @ <node>{ _* }</node> => 
								val dataNodes = n \ "data"
								
								val properties : graph.Properties =
									(for(dataNode <- dataNodes) yield {
										((dataNode \ "@key" text), (dataNode text))
									}).toMap[String, String]
								
								graph += ((n \ "@id" text), properties)
						}
					}
					
					for(edgeNode <- edgeNodes) {
						edgeNode match {
							case e @ <edge/> =>
								graph connect (e \ "@source" text, e \ "@target" text)
							case e @ <edge>{ _* }</edge> => 
								val dataNodes = e \ "data"
								
								val properties : graph.Properties =
									(for(dataNode <- dataNodes) yield {
										((dataNode \ "@key" text), (dataNode text))
									}).toMap[String, String]
								
								graph connect (e \ "@source" text, e \ "@target" text, properties)
						}
					}
					
					graph
					
				case _ =>
					throw new RuntimeException("Unsupported graph type!")
			}
		}
		
	}
	
	private implicit def stringToSymol(_string : String) : Symbol = Symbol(_string)
	
}