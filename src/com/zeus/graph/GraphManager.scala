package com.zeus.graph

class GraphManager(drawnGraph_ : GraphEditor.CustomGraph) {

	import scala.collection.mutable.{Map, Set}
	import scala.collection.JavaConversions._
	
	import com.mxgraph.model.mxCell
	import com.mxgraph.util.mxEventSource.mxIEventListener
	import com.mxgraph.util.mxEventObject
	
	private val DEFAULT_CELL_WIDTH = 50
	private val DEFAULT_CELL_HEIGHT = 50
	private val DEFAULT_CELL_STYLE = "ellipse"
	
	private val nodes_ : Map[Symbol, mxCell] = Map()
	private val edges_ : Map[Symbol, mxCell] = Map()
	
	private implicit def stringToSymol(_string : String) : Symbol = Symbol(_string)
	
	val insertionListener_ = new InsertionListener
	val deletionListener_ = new DeletionListener
	
	class InsertionListener extends mxIEventListener {
		
		override def invoke(sender : Object, event : mxEventObject) {
			val properties : Map[String, Object] = event.getProperties
			if(properties contains ("cells")) {
				val cells = properties("cells").asInstanceOf[Array[Object]]
				for(val cell <- cells; cell.isInstanceOf[mxCell]) {
					val c : mxCell = cell.asInstanceOf[mxCell]
					if(c isVertex) {
						nodes_ += ((c.getId, c))
					} else if(c.isEdge && c.getSource != null && c.getTarget != null) {
						edges_ += ((c.getId, c))
					}
				}
			}
		}
		
	}
	
	class DeletionListener extends mxIEventListener {
		
		override def invoke(sender : Object, event : mxEventObject) {
			val properties : Map[String, Object] = event.getProperties
			if(properties contains ("cells")) {
				val cells = properties("cells").asInstanceOf[Array[Object]]
				for(val cell <- cells; cell.isInstanceOf[mxCell]) {
					val c : mxCell = cell.asInstanceOf[mxCell]
					if(c isVertex) {
						nodes_ -= c.getId
					} else if(c.isEdge && c.getSource != null && c.getTarget != null) {
						edges_ -= c.getId
					}
				}
			}
		}
		
	}
	
	
	def insertionListener : InsertionListener = insertionListener_
	def deletionListener : DeletionListener = deletionListener_
	
	def importGraphML(_fileReader : java.io.FileReader) {
		val graph = GraphMlReader(_fileReader).first // TODO: not very pretty
		
		drawnGraph_.getModel.beginUpdate
		try {
			// Clear canvas
			drawnGraph_.removeCells(drawnGraph_.getChildCells(drawnGraph_.getDefaultParent))
			
			for((id, node) <- graph.nodes) {
				val name = node.properties get "name" match {
					case Some(text) => text
					case None => ""
				}
				drawnGraph_.insertVertex(drawnGraph_.getDefaultParent, id name, name, DEFAULT_CELL_WIDTH, DEFAULT_CELL_HEIGHT, DEFAULT_CELL_WIDTH, DEFAULT_CELL_HEIGHT, DEFAULT_CELL_STYLE) 
			}
			
			for(edge <- graph.edges) {
				val name = edge.properties get "name" match {
					case Some(text) => text
					case None => ""
				}
				val source = nodes_ get edge.source.id
				val target = nodes_ get edge.target.id
				
				(source, target) match {
					case (Some(s), Some(t)) => drawnGraph_.insertEdge(drawnGraph_.getDefaultParent, null, name, s, t)
					case _ => System.err.println("Imported file has edges without endpoints!")
				}
			}
		} finally {
			drawnGraph_.getModel.endUpdate
		}
	}
	
	def exportGraphML(_fileWriter : java.io.FileWriter, _graphId : String) {
		val graph : DirectedGraph = new DirectedGraphImpl(if(_graphId == null) 'mygraph else _graphId)
		
		def buildProperties(_cell : mxCell) : graph.Properties = {
			var properties : graph.Properties = graph.EmptyProperties
			if(_cell.getValue != null && _cell.getValue.toString.length != 0) {
				properties += (("name", _cell.getValue.toString))
			}
			return properties
		}
		
		for((nodeId, nodeCell) <- nodes_) {
			graph += (nodeId, buildProperties(nodeCell))
		}
		
		for((edgeId, edgeCell) <- edges_) {
			graph connect(edgeCell.getSource.getId, edgeCell.getTarget.getId, buildProperties(edgeCell))
		}
		
		GraphMlWriter(List(graph), _fileWriter)
	}
	
}