package com.productfoundry.play.hal

import com.productfoundry.play.hal.DocumentedLink.Requirement
import play.api.libs.json.{Writes, JsObject, Json, JsValue}

import scala.xml.NodeSeq

case class DocumentedRelation(
  methods: Vector[DocumentedMethod] = Vector.empty
) {
  def withMethod(method: DocumentedMethod): DocumentedRelation = copy(methods = methods :+ method)
}

case class DocumentedMethod(
  verb: String,
  description: NodeSeq = NodeSeq.Empty,
  requests: Vector[DocumentedRequest] = Vector.empty,
  responses: Vector[DocumentedResponse] = Vector.empty
) {
  def withRequest(request: DocumentedRequest): DocumentedMethod = copy(requests = requests :+ request)

  def withResponse(response: DocumentedResponse): DocumentedMethod = copy(responses = responses :+ response)
}

case class DocumentedRequest(
  payload: JsValue = Json.obj()
) {
  def withPayload[A](payload: A)(implicit writes: Writes[A]) = copy(payload = Json.toJson(payload))
}

case class DocumentedResponse(
  statusCode: Int,
  description: NodeSeq = NodeSeq.Empty,
  documentedResponseBodyOption:Option[DocumentedResponseBody] = None
) {
  lazy val status: String = DocumentedResponse.StatusByCode.getOrElse(statusCode, "")

  def withBody(body: DocumentedResponseBody): DocumentedResponse = copy(documentedResponseBodyOption = Some(body))
}

case class DocumentedResponseBody(
  state: JsValue = Json.obj(),
  links: Vector[DocumentedLink] = Vector.empty,
  headers: Vector[DocumentedHeader] = Vector.empty
) {
  def withState(state: JsValue): DocumentedResponseBody = copy(state = state)

  def withState(resource: Resource): DocumentedResponseBody = copy(state = JsObject(resource.properties))

  def withLink(link: Link, requirement: DocumentedLink.Requirement.Value = Requirement.Required): DocumentedResponseBody = copy(links = links :+ DocumentedLink(link, requirement))

  def withHeader(header: DocumentedHeader): DocumentedResponseBody = copy(headers = headers :+ header)
}

case class DocumentedLink(
  link: Link,
  requirement: DocumentedLink.Requirement.Value
)

object DocumentedLink {
  object Requirement extends Enumeration {
    val Required, Optional = Value
  }
}

case class DocumentedHeader(
  name: String,
  value: String,
  description: NodeSeq
)

object DocumentedResponse{
  val StatusByCode = Map(
    100 -> "Continue",
    101 -> "Switching Protocols",
    102 -> "Processing",
    200 -> "OK",
    201 -> "Created",
    202 -> "Accepted",
    203 -> "Non-Authoritative Information",
    204 -> "No Content",
    205 -> "Reset Content",
    206 -> "Partial Content",
    300 -> "Multiple Choices",
    301 -> "Moved Permanently",
    302 -> "Found",
    303 -> "See Other",
    304 -> "Not Modified",
    305 -> "Use Proxy",
    306 -> "Unused",
    307 -> "Temporary Redirect",
    400 -> "Bad Request",
    401 -> "Unauthorized",
    402 -> "Payment Required",
    403 -> "Forbidden",
    404 -> "Not Found",
    405 -> "Method Not Allowed",
    406 -> "Not Acceptable",
    407 -> "Proxy Authentication Required",
    408 -> "Request Timeout",
    409 -> "Conflict",
    410 -> "Gone",
    411 -> "Length Required",
    412 -> "Precondition Required",
    413 -> "Request Entry Too Large",
    414 -> "Request-URI Too Long",
    415 -> "Unsupported Media Type",
    416 -> "Requested Range Not Satisfiable",
    417 -> "Expectation Failed",
    418 -> "I\'m a teapot",
    500 -> "Internal Server Error",
    501 -> "Not Implemented",
    502 -> "Bad Gateway",
    503 -> "Service Unavailable",
    504 -> "Gateway Timeout",
    505 -> "HTTP Version Not Supported"
  )
}