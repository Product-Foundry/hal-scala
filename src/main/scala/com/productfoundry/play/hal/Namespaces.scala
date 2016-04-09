package com.productfoundry.play.hal

import scala.collection.SortedMap
import scala.collection.immutable.TreeMap

case class Namespace(
  prefix: String,
  reference: String
) {

  import Namespace._

  private val startIndex = reference.indexOf(Rel)
  assert(startIndex != -1, s"Namespace '$prefix' does not contain $Rel URI template argument")

  private val endIndex = startIndex + Rel.length
  private val left = reference.substring(0, startIndex)
  private val right = reference.substring(endIndex)

  def curie(href: String): Option[String] = {
    if (href.startsWith(left) && href.endsWith(right)) {
      Some(prefix + ":" + href.substring(startIndex, href.length - right.length))
    } else {
      None
    }
  }
}

case object Namespace {
  val Rel = "{rel}"
}

case class Namespaces(
  private val namespaceByPrefix: SortedMap[String, Namespace] = TreeMap.empty
) {

  def isEmpty = namespaceByPrefix.isEmpty

  def all = namespaceByPrefix.values

  def withNamespace(prefix: String, reference: String) = {
    copy(
      namespaceByPrefix = namespaceByPrefix.updated(prefix, Namespace(prefix, reference))
    )
  }

  def curieHref(href: String): String = {
    val curiedRefs = namespaceByPrefix.values.flatMap(_.curie(href))
    val shortestRef = curiedRefs.reduceOption[String] { case (a, b) => if (a.length < b.length) a else b }
    shortestRef.getOrElse(href)
  }
}

object Namespaces {
  val empty = new Namespaces()

  def apply(namespaces: (String, String)*): Namespaces = namespaces.foldLeft(empty) {case (_namespaces, (prefix, reference)) =>
    _namespaces.withNamespace(prefix, reference)
  }
}
