package com.productfoundry.play.hal

import play.api.http.{ContentTypeOf, MimeTypes, Writeable}
import play.api.libs.json._
import play.api.mvc._

trait ResourceWriteables
  extends Controller {

  implicit def namespaces: Namespaces

  implicit def contentTypeOf_Resource(implicit codec: Codec, request: RequestHeader): ContentTypeOf[Resource] = {
    ContentTypeOf[Resource](Some(withCharset(HalJsonDocumentRenderer.mediaType)))
  }

  implicit def writeableOf_ApiResult(implicit codec: Codec, request: RequestHeader): Writeable[Resource] = {
    Writeable {
      resource =>
        if (request.accepts(MimeTypes.JSON)) {
          codec.encode(Json.prettyPrint(HalJsonDocumentRenderer.render(namespaces, resource)))
        } else {
          codec.encode("")
        }
    }
  }
}
