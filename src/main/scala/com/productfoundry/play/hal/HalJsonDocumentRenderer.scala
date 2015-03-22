package com.productfoundry.play.hal

import play.api.libs.json._
import play.api.mvc.RequestHeader

object HalJsonDocumentRenderer extends DocumentRenderer[JsValue] {

  override def mediaType: String = "application/hal+json"

  override def render(namespaces: Namespaces, resource: Resource)(implicit request: RequestHeader): JsValue = {

    def absoluteURL(url: String) = "http" + (if (request.secure) "s" else "") + "://" + request.host + url

    def selfLink(resource: Resource) = Seq("self" -> Json.obj("href" -> absoluteURL(resource.href)))

    def related[A](relatedItems: Seq[Related[A]], toJson: (A) => JsValue): Seq[(String, JsValue)] = {
      val grouped = relatedItems.groupBy(related => related.name)
      val groupedSeq = grouped.toSeq

      groupedSeq.map {case (href, relatedSeq) =>
        val rel = namespaces.curieHref(href)

        val relatedJsonSeq = relatedSeq.map(relatedLink => toJson(relatedLink.item))

        relatedSeq match {
          case relatedLink :: Nil if relatedLink.cardinality == Related.Cardinality.Single => rel -> relatedJsonSeq.head
          case _ => rel -> JsArray(relatedJsonSeq)
        }
      }
    }

    def linkJson(link: Link): JsValue = {
      JsObject(
        Seq(
          Some(("href", JsString(absoluteURL(link.href)))),
          link.templatedOption.map(v => ("templated", JsBoolean(v))),
          link.typeOption.map(v => ("type", JsString(v))),
          link.deprecationOption.map(v => ("deprecation", JsString(v))),
          link.nameOption.map(v => ("name", JsString(v))),
          link.profileOption.map(v => ("profile", JsString(v))),
          link.titleOption.map(v => ("title", JsString(v))),
          link.hreflangOption.map(v => ("hreflang", JsString(v)))
        ).flatten
      )
    }

    def resourceJson(curies: Seq[(String, JsValue)])(resource: Resource): JsObject = {
      val links = selfLink(resource) ++ curies ++ related(resource.links, linkJson)
      val embeddedResources = related(resource.resources, resourceJson(Seq.empty))

      JsObject(Seq.empty ++
        Seq("_links" -> JsObject(links)) ++
        resource.properties ++
        (if (embeddedResources.isEmpty) Seq.empty else Seq("_embedded" -> JsObject(embeddedResources)))
      )
    }

    def curies: Seq[(String, JsValue)] = {
      if (namespaces.isEmpty) Seq.empty else Seq(
        "curies" -> JsArray(
          namespaces.all.map {namespace =>
            linkJson(Link.withTemplate(namespace.reference).withName(namespace.prefix))
          }.toSeq
        )
      )
    }

    resourceJson(curies)(resource)
  }
}
