package com.productfoundry.play.hal

import org.scalatest.{Matchers, WordSpec}
import play.api.test.Helpers._
import play.api.test.{FakeApplication, FakeRequest}

class RelationHtmlSpec extends WordSpec with Matchers {

  implicit val namespaces = Namespaces("be" -> "/rels/{rel}")

  "relation view" must {

    "render rel template" in {
      running(FakeApplication()) {
        implicit val request = FakeRequest()
        val relation = DocumentedRelation()
        val html = hal.html.rel(relation)
        contentAsString(html) should include("<html>")
      }
    }

    "contain documented method" in {
      running(FakeApplication()) {
        implicit val request = FakeRequest()
        val relation = DocumentedRelation().withMethod(DocumentedMethod(GET, <p>List resources</p>))

        val html = hal.html.rel(relation)
        contentAsString(html) should include("GET")
        contentAsString(html) should include("List resources")
      }
    }
  }
}
