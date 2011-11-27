package org.grails.plugins.mustache

import com.sampullara.util.FutureWriter
import com.sampullara.mustache.MustacheBuilder

class MustacheTagLib {
  static namespace = "mustache"
  def compile = { attrs, body ->
    java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream()
    FutureWriter writer = new FutureWriter(new OutputStreamWriter(baos))
    
    new MustacheBuilder()
            .build(new StringReader(body() as String), "mustacheOutput")
            .execute(writer, attrs.model as Map)
    writer.flush()
    out << baos.toString()
  }
}
