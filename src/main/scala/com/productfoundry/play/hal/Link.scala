package com.productfoundry.play.hal

import play.api.mvc.Call

case class Link private(
  href: String,
  templatedOption: Option[Boolean] = None,
  typeOption: Option[String] = None,
  deprecationOption: Option[String] = None,
  nameOption: Option[String] = None,
  profileOption: Option[String] = None,
  titleOption: Option[String] = None,
  hreflangOption: Option[String] = None
) {

  def withName(name: String): Link = copy(nameOption = Some(name))

  def withTitle(title: String): Link = copy(titleOption = Some(title))

  def templated(replacements: (String, String)*): Link = {
    copy(
      href = replacements.foldLeft(href) { (_href, replacement) =>
        _href.replace(replacement._1, replacement._2)
      },
      templatedOption = Some(true)
    )
  }
}

object Link {
  def apply(href: String): Link = new Link(href)

  def apply(call: Call): Link = new Link(call.url)

  def withTemplate(template: String): Link = Link(href = template, templatedOption = Some(true))
}