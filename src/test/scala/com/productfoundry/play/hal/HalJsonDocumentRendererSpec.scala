package com.productfoundry.play.hal

import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json
import play.api.test.FakeRequest

class HalJsonDocumentRendererSpec extends WordSpec with Matchers {

  implicit val request = FakeRequest()

  "property rendering" must {

    "render empty properties" in {
      val resource = Resource("/")

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"}}}""")
      )
    }

    "render single property" in {
      val resource = Resource("/")
        .withProperty("first", "1")

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"}},"first":"1"}""")
      )
    }

    "render multiple properties" in {
      val resource = Resource("/")
        .withProperty("first", "1")
        .withProperty("second", "2")
        .withProperty("third", "3")
        .withProperty("fourth", "4")

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"}},"first":"1","second":"2","third":"3","fourth":"4"}""")
      )
    }

    "keep multiple properties ordering" in {
      val resource = Resource("/")
        .withProperty("first", "1")
        .withProperty("second", "2")
        .withProperty("third", "3")
        .withProperty("fourth", "4")

      HalJsonDocumentRenderer.render(Namespaces.empty, resource).toString() should equal(
        """{"_links":{"self":{"href":"http:///"}},"first":"1","second":"2","third":"3","fourth":"4"}"""
      )
    }
  }

  "related link rendering" must {

    "render single related link" in {
      val resource = Resource("/")
        .withLink(RelatedLink("/rels/resource", Link("/resource")))

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"},"/rels/resource":[{"href":"http:///resource"}]}}""")
      )
    }

    "render single related link with single cardinality" in {
      val resource = Resource("/")
        .withLink(RelatedLink("/rels/resource", Link("/resource"), Related.Cardinality.Single))

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"},"/rels/resource":{"href":"http:///resource"}}}""")
      )
    }

    "render multiple related links" in {
      val resource = Resource("/")
        .withLink(RelatedLink("/rels/resource", Link("/resource/1")))
        .withLink(RelatedLink("/rels/resource", Link("/resource/2")))
        .withLink(RelatedLink("/rels/resource", Link("/resource/3")))

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"},"/rels/resource":[{"href":"http:///resource/1"},{"href":"http:///resource/2"},{"href":"http:///resource/3"}]}}""")
      )
    }

    "keep multiple related links ordering" in {
      val resource = Resource("/")
        .withLink(RelatedLink("/rels/resource", Link("/resource/3")))
        .withLink(RelatedLink("/rels/resource", Link("/resource/2")))
        .withLink(RelatedLink("/rels/resource", Link("/resource/1")))

      HalJsonDocumentRenderer.render(Namespaces.empty, resource).toString() should equal(
        """{"_links":{"self":{"href":"http:///"},"/rels/resource":[{"href":"http:///resource/3"},{"href":"http:///resource/2"},{"href":"http:///resource/1"}]}}"""
      )
    }
  }

  "embedded resource rendering" must {

    "render single embedded resource" in {
      val resource = Resource("/")
        .withResource(
          RelatedResource("/rels/resource", Resource("/resource")
            .withProperty("first", "1")
            .withProperty("second", "2")
          )
        )

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"}},"_embedded":{"/rels/resource":[{"_links":{"self":{"href":"http:///resource"}},"first":"1","second":"2"}]}}""")
      )
    }

    "render multiple embedded resources" in {
      val resource = Resource("/")
        .withResource(
          RelatedResource("/rels/resource", Resource("/resource/1")
            .withProperty("s", "a")
            .withProperty("n", "1")
          )
        )
        .withResource(
          RelatedResource("/rels/resource", Resource("/resource/2")
            .withProperty("s", "b")
            .withProperty("n", "2")
          )
        )
        .withResource(
          RelatedResource("/rels/resource", Resource("/resource/3")
            .withProperty("s", "c")
            .withProperty("n", "3")
          )
        )

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.parse( """{"_links":{"self":{"href":"http:///"}},"_embedded":{"/rels/resource":[{"_links":{"self":{"href":"http:///resource/1"}},"s":"a","n":"1"},{"_links":{"self":{"href":"http:///resource/2"}},"s":"b","n":"2"},{"_links":{"self":{"href":"http:///resource/3"}},"s":"c","n":"3"}]}}""")
      )
    }
  }

  "HAL+JSON renderer" must {

    "have correct media type" in {
      HalJsonDocumentRenderer.mediaType should equal("application/hal+json")
    }

    "contain self link" in {
      val resource = Resource("/orders")

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj(
              "href" -> "http:///orders"
            )
          )
        )
      )
    }

    "contain single links" in {
      val resource = Resource("/orders/523")
        .withLink(RelatedLink("warehouse", Link("/warehouse/56")).single())
        .withLink(RelatedLink("invoice", Link("/invoices/873")).single())

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj(
              "href" -> "http:///orders/523"
            ),
            "warehouse" -> Json.obj(
              "href" -> "http:///warehouse/56"
            ),
            "invoice" -> Json.obj(
              "href" -> "http:///invoices/873"
            )
          )
        )
      )
    }

    "contain properties" in {
      val resource = Resource("/orders/523")
        .withProperty("currency", "USD")
        .withProperty("status", "shipped")
        .withProperty("total", 10.20)

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj(
              "href" -> "http:///orders/523"
            )
          ),
          "currency" -> "USD",
          "status" -> "shipped",
          "total" -> 10.20
        )
      )
    }

    "contain embedded resources" in {
      val resource = Resource("/orders")
        .withResource(
          RelatedResource("orders", Resource("/orders/123")
            .withProperty("currency", "USD")
            .withProperty("status", "shipped")
            .withProperty("total", 10.20)
          )
        )

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj(
              "href" -> "http:///orders"
            )
          ),
          "_embedded" -> Json.obj(
            "orders" -> Json.arr(
              Json.obj(
                "_links" -> Json.obj(
                  "self" -> Json.obj(
                    "href" -> "http:///orders/123"
                  )
                ),
                "currency" -> "USD",
                "status" -> "shipped",
                "total" -> 10.20
              )
            )
          )
        )
      )
    }

    "contain curies" in {
      val namespaces = Namespaces.empty.withNamespace("acme", "/relations/{rel}")
      val resource = Resource("/orders").withLink(RelatedLink("/relations/widgets", Link("/widgets")).single())

      HalJsonDocumentRenderer.render(namespaces, resource) should equal(
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj(
              "href" -> "http:///orders"
            ),
            "curies" -> Json.arr(
              Json.obj(
                "name" -> "acme",
                "href" -> "http:///relations/{rel}",
                "templated" -> true
              )
            ),
            "acme:widgets" -> Json.obj(
              "href" -> "http:///widgets"
            )
          )
        )
      )
    }

    "contain curies once" in {
      val namespaces = Namespaces.empty.withNamespace("acme", "/relations/{rel}")
      val resource = Resource("/orders")
        .withLink(RelatedLink("/relations/widgets", Link("/widgets")).single())
        .withResource(RelatedResource("/relations/creator", Resource("/user/1234")))

      HalJsonDocumentRenderer.render(namespaces, resource) should equal(
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj(
              "href" -> "http:///orders"
            ),
            "curies" -> Json.arr(
              Json.obj(
                "name" -> "acme",
                "href" -> "http:///relations/{rel}",
                "templated" -> true
              )
            ),
            "acme:widgets" -> Json.obj(
              "href" -> "http:///widgets"
            )
          ),
          "_embedded" -> Json.obj(
            "acme:creator" -> Json.arr(
              Json.obj(
                "_links" -> Json.obj(
                  "self" -> Json.obj(
                    "href" -> "http:///user/1234"
                  )
                )
              )
            )
          )
        )
      )
    }

    "render complete example from spec" in {
      val resource = Resource("/orders")
        .withLink(RelatedLink("next", Link("/orders?page=2")).single())
        .withLink(RelatedLink("find", Link.withTemplate("/orders{?id}")).single())
        .withResource(
          RelatedResource("orders",
            Resource("/orders/123")
              .withLink(RelatedLink("basket", Link("/baskets/98712")).single())
              .withLink(RelatedLink("customer", Link("/customers/7809")).single())
              .withProperty("total", 30.00)
              .withProperty("currency", "USD")
              .withProperty("status", "shipped")
          )
        )
        .withResource(
          RelatedResource("orders",
            Resource("/orders/124")
              .withLink(RelatedLink("basket", Link("/baskets/97213")).single())
              .withLink(RelatedLink("customer", Link("/customers/12369")).single())
              .withProperty("total", 20.00)
              .withProperty("currency", "USD")
              .withProperty("status", "processing")
          )
        )
        .withProperty("currentlyProcessing", 14)
        .withProperty("shippedToday", 20)

      HalJsonDocumentRenderer.render(Namespaces.empty, resource) should equal(
        Json.obj(
          "_links" -> Json.obj(
            "self" -> Json.obj(
              "href" -> "http:///orders"
            ),
            "next" -> Json.obj(
              "href" -> "http:///orders?page=2"
            ),
            "find" -> Json.obj(
              "href" -> "http:///orders{?id}",
              "templated" -> true
            )
          ),
          "_embedded" -> Json.obj(
            "orders" -> Json.arr(
              Json.obj(
                "_links" -> Json.obj(
                  "self" -> Json.obj(
                    "href" -> "http:///orders/123"
                  ),
                  "basket" -> Json.obj(
                    "href" -> "http:///baskets/98712"
                  ),
                  "customer" -> Json.obj(
                    "href" -> "http:///customers/7809"
                  )
                ),
                "total" -> 30.00,
                "currency" -> "USD",
                "status" -> "shipped"
              ),
              Json.obj(
                "_links" -> Json.obj(
                  "self" -> Json.obj(
                    "href" -> "http:///orders/124"
                  ),
                  "basket" -> Json.obj(
                    "href" -> "http:///baskets/97213"
                  ),
                  "customer" -> Json.obj(
                    "href" -> "http:///customers/12369"
                  )
                ),
                "total" -> 20.00,
                "currency" -> "USD",
                "status" -> "processing"
              )
            )
          ),
          "currentlyProcessing" -> 14,
          "shippedToday" -> 20
        )
      )
    }
  }
}
