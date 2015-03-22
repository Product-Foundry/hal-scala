package com.productfoundry.play.hal

import play.api.libs.json.{JsValue, Writes}
import play.api.mvc.Call

trait ResourceSorting {
  def sort(resources: Resource): Unit
}

case class Resource private(
  href: String,
  links: Seq[RelatedLink] = Seq.empty,
  resources: Seq[RelatedResource] = Seq.empty,
  properties: Seq[(String, JsValue)] = Seq.empty
) {

  def withProperty[A](key: String, value: A)(implicit writer: Writes[A]) = copy(properties = properties :+ key -> writer.writes(value))

  def withLink(relatedLink: RelatedLink) = copy(links = links :+ relatedLink)

  def withLinks(relatedLinks: Iterable[RelatedLink]) = copy(links = links ++ relatedLinks)

  def withResource(relatedResource: RelatedResource) = copy(resources = resources :+ relatedResource)

  def withResources(relatedResources: Iterable[RelatedResource]) = copy(resources = resources ++ relatedResources)
}

case object Resource {
  def apply(href: String): Resource = new Resource(href)

  def apply(call: Call): Resource = new Resource(call.url)
}