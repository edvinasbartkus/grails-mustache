package org.grails.plugins.mustache

import java.io.Reader
import java.io.BufferedReader
import com.sampullara.util.FutureWriter
import com.sampullara.mustache.MustacheBuilder
import org.codehaus.groovy.grails.web.pages.TagLibraryLookup

class MustacheTagLib {
  static namespace = "mustache"

  def pageTemplates = [:]
  
  /**
   *  The render tag closure for Mustache gsp markup.
   *  </br>
   *  The render tag will either interpret the resource provided as a mustache template
   *  using the data model provided. The template may be included in-line, or may be
   *  external and referred to by the href attributes.
   *
   *  @param attrs must include model. If href is provided, the body is skipped and the
   *  file (relative to the server root) is interpreted. Providing a tid, when a body is
   *  provided enables later inclusion of the template for use as a javascript template
   *  with the mustache:layoutTemplates tag.
   */
  def render = { attrs, body ->
    if (!attrs.model) {
      log.error "model attribute was not specified for ${controllerName}.${actionName}"
      return
    }
    
    if (attrs.href) {
      try {
        def template = applicationContext.getResourceByPath(attrs.href as String)?.getFile()

        out << compileMustache(attrs.model, new BufferedReader(new FileReader(template)))        
      }
      catch (Exception ex) {
        log.error "render failed for ${controllerName}.${actionName} with message: ${ex.getMessage()}"
      }
      return
    }
    
    if (attrs.tid) {
      pageTemplates[attrs.tid] = body
    }
        
    out << compileMustache(attrs.model, new StringReader(body() as String))
  }
  
  /**
   *  The loyoutTemplates tag closure for Mustache gsp markup.
   *  </br>
   *  The tag will write each of the Mustache template bodies found in the gsp file that were
   *  provided with an id attribute. Each Mustache template will be written as a script dom
   *  element of type="text/template" with the id provided in the earlier mustache:render
   *  markup as tid='template-id'.
   */
  def layoutTemplates = { attrs, body ->
    pageTemplates.each() { id, template ->
      def elementBegin = "<script id=\"${id}\" type=\"text/template\">"
      out << elementBegin << (template() as String) << "</script>"
    }
  }
  
  private def compileMustache(def model, Reader reader) {
    java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream()
    FutureWriter writer = new FutureWriter(new OutputStreamWriter(baos))
    
    new MustacheBuilder()
            .build(reader, "mustacheOutput")
            .execute(writer, model as Map)
    writer.flush()
    return baos.toString()    
  }

}
