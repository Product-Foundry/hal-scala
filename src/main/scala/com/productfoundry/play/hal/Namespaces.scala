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

  private val endIndex = startIndex + Rel.size
  private val left = reference.substring(0, startIndex)
  private val right = reference.substring(endIndex)

  def curie(href: String): Option[String] = {
    if (href.startsWith(left) && href.endsWith(right)) {
      Some(prefix + ":" + href.substring(startIndex, href.length - right.size))
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
    namespaceByPrefix.values.map(_.curie(href)).flatten.headOption.getOrElse(href)
  }
}

object Namespaces {
  val empty = new Namespaces()

  def apply(namespaces: (String, String)*): Namespaces = namespaces.foldLeft(empty) {case (_namespaces, (prefix, reference)) =>
    _namespaces.withNamespace(prefix, reference)
  }
}
