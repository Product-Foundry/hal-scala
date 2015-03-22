package com.productfoundry.play.hal

import play.api.mvc.RequestHeader

trait DocumentRenderer[A] {

  def mediaType: String

  def render(namespaces: Namespaces, resource: Resource)(implicit request: RequestHeader) : A
}
