package com.zeus.graph

import scala.collection.immutable.{Map => MMap}
import scala.collection.mutable.{Map, Set}

abstract class Graph(val id_ : Symbol) {
	
	type Node <: INode
	type Edge <: IEdge
	type Properties = MMap[String, String]
	
	abstract class INode {
		def -> (_node : Node) : Option[Edge]
		def -!> (_node : Node)
		
		def id : Symbol
		def parents : Map[Node, Edge]
		def children : Map[Node, Edge]
		def properties : Properties
	}
	
	abstract class IEdge {
		def source : Node
		def target : Node
		def properties : Properties
		def propertiesAre(_properties : Properties) : Unit
	}
	
	def EmptyProperties : Properties = MMap()
	
	def += (_id : Symbol, _properties : Properties = EmptyProperties) : Node
	def -= (_Id : Symbol)
	def -= (_node : Node)
	
	def connect(_from : Symbol, _to : Symbol, _properties : Properties = EmptyProperties) : Option[Edge]
	
	def node(_id : Symbol) : Option[Node]
	
	def id : Symbol = id_
	
	def nodes : Map[Symbol, Node]
	def edges : Set[Edge]
	
}

abstract class DirectedGraph(override val id_ : Symbol) extends Graph(id_) {
	
	type Node <: NodeImpl
	type Edge <: EdgeImpl
	
	class NodeImpl(protected val id_ : Symbol, protected val parents_ : Map[Node, Edge], protected val children_ : Map[Node, Edge], private val properties_ : Properties) extends INode {
		self : Node =>
	
		def this(_id : Symbol, _properties : Properties = EmptyProperties) = this(_id, Map(), Map(), _properties)
		
		override def -> (_node : Node) : Option[Edge] = {
			if(!(children_ contains _node)) {
				val edge = newEdge(this, _node)
				
				children_ += ((_node, edge))
				_node.parents_ += ((this, edge))
				edges += edge
				
				Some(edge)
			} else {
				None
			}
		}
		
		override def -!> (_node : Node) {
			children_ get _node match {
				case Some(edge) => 
					edges -= edge
					
					children_ -= _node
					_node.parents_ -= this
				case None => 
			}
		}
		
		override def id : Symbol = id_
		
		override def parents : Map[Node, Edge] = parents_
		
		override def children : Map[Node, Edge] = children_
		
		override def properties : Properties = properties_
		
	}
	
	class EdgeImpl(from_ : Node, to_ : Node, private var properties_ : Properties = EmptyProperties) extends IEdge {
		override def source : Node = from_
		override def target : Node = to_
		override def properties : Properties = properties_
		override def propertiesAre(_properties : Properties) { properties_ = _properties }
	}
	
	override def += (_id : Symbol, _properties : Properties = EmptyProperties) : Node = {
		val ret = newNode(_id, _properties)
		nodes  += ((_id, ret))
		ret
	}
	
	override def -= (_id : Symbol) {
		nodes get _id match {
			case Some(node) => 
				node.children foreach (n => node -!> n._1)
				node.parents foreach (n => n._1 -!> node)
				
				nodes  -= _id
			case None => 
		}
	}
	
	override def -= (_node : Node) {
		this -= _node.id
	}
	
	override def connect(_from : Symbol, _to : Symbol, _properties : Properties = EmptyProperties) : Option[Edge] = {
		(node(_from), node(_to)) match {
			case (Some(fromNode), Some(toNode)) => 
				val edge = fromNode -> toNode
				edge match {
					case Some(e) => e propertiesAre _properties
					case _ => 
				}
				edge
			case _ =>
				None
		}
	}
	
	override def node(_id : Symbol) : Option[Node] = 
		nodes get _id
		
	protected def newNode(_id : Symbol, _properties : Properties = EmptyProperties) : Node
	protected def newEdge(from_ : Node, to_ : Node, _properties : Properties = EmptyProperties) : Edge
	
	var nodes : Map[Symbol, Node] = Map()
	var edges : Set[Edge] = Set()
	
}

class DirectedGraphImpl(override val id_ : Symbol) extends DirectedGraph(id_) {
	
	type Node = NodeImpl
	type Edge = EdgeImpl
	
	override protected def newNode(_id : Symbol, _properties : Properties = EmptyProperties) : Node = new NodeImpl(_id, _properties)
	
	override protected def newEdge(from_ : Node, to_ : Node, properties_ : Properties = EmptyProperties) : Edge = new EdgeImpl(from_, to_, properties_)
	
}
