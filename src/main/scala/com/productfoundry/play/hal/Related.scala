package com.productfoundry.play.hal

import play.api.mvc.Call

trait Related[A] {
  def name: String

  def item: A

  def cardinality: Related.Cardinality.Cardinality

  def single(): Related[A]
}

case class RelatedLink(
  name: String,
  item: Link,
  cardinality: Related.Cardinality.Cardinality = Related.Cardinality.Multiple
) extends Related[Link] {

  override def single(): RelatedLink = copy(cardinality = Related.Cardinality.Single)
}

object RelatedLink {
  def apply(call: Call, item: Link): RelatedLink = new RelatedLink(call.url, item, Related.Cardinality.Multiple)
}

case class RelatedResource(
  name: String,
  item: Resource,
  cardinality: Related.Cardinality.Cardinality = Related.Cardinality.Multiple
) extends Related[Resource] {

  override def single(): RelatedResource = copy(cardinality = Related.Cardinality.Single)
}

object RelatedResource {
  def apply(call: Call, item: Resource): RelatedResource = new RelatedResource(call.url, item, Related.Cardinality.Multiple)
}

object Related {

  object Cardinality extends Enumeration {
    type Cardinality = Value

    val Single, Multiple = Value
  }
}
