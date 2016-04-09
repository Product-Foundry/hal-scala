package com.productfoundry.play.hal

import org.scalatest.{Matchers, WordSpec}

class NamespacesSpec extends WordSpec with Matchers {

  "curie href" must {

    "succeed with no matching namespace" in {
      val namespaces = Namespaces()
      namespaces.curieHref("/rels/test") shouldBe "/rels/test"
    }

    "succeed with single matching namespace" in {
      val namespaces = Namespaces("r" -> "/rels/{rel}")
      namespaces.curieHref("/rels/test") shouldBe "r:test"
    }

    "pick shortest curie when multiple namespaces match" in {
      val namespaces = Namespaces(
        "r" -> "/rels/{rel}",
        "rs" -> "/rels/section/{rel}"
      )

      namespaces.curieHref("/rels/test") shouldBe "r:test"
      namespaces.curieHref("/rels/section") shouldBe "r:section"
      namespaces.curieHref("/rels/section/test") shouldBe "rs:test"
    }
  }
}
