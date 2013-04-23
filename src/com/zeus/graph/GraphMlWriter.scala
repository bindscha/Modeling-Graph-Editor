package com.zeus.graph

import scala.xml._
import java.io.Writer

object GraphMlWriter extends ((Seq[Graph], Writer) => Boolean) {
	
	private val prettyPrinter = new PrettyPrinter(999, 4)
	
	private def makeDocument(_graphs : Seq[Elem]) : Node = 
		<graphml xmlns="http://graphml.graphdrawing.org/xmlns"  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="http://graphml.graphdrawing.org/xmlns http://graphml.graphdrawing.org/xmlns/1.0/graphml.xsd">
			{ _graphs }
		</graphml>
		
	def apply(_graphs : Seq[Graph], _writer : Writer) : Boolean = {
		val elements = 	for(graph <- _graphs) yield {
							<graph id={ graph.id name } edgedefault="directed"> { 
								val nodes = for((id, node) <- graph.nodes) yield {
												if(node.properties isEmpty) {
													<node id={ id name }/>
												} else {
													<node id={ id name }>
															{ for(property <- node.properties) yield 
															<data key={ property._1 }>{ property._2 }</data> }
													</node>
												}
											}
								val edges = for(edge <- graph.edges) yield {
												if(edge.properties isEmpty) {
													<edge source={ edge.source.id name } target={ edge.target.id name }/>
												} else {
													<edge source={ edge.source.id name } target={ edge.target.id name }>
															{ for(property <- edge.properties) yield 
															<data key={ property._1 }>{ property._2 }</data> }
													</edge>
												}
											}
								nodes ++ edges
							} </graph>
						}
						
		val document = makeDocument(elements)
		
		val printed = """<?xml version="1.0" encoding="UTF-8"?>""" + "\n" + prettyPrinter.format(document)
		
		_writer write printed
		_writer.flush
		
		return true
	}
	
	private implicit def stringToSymol(_string : String) : Symbol = Symbol(_string)
	
}
