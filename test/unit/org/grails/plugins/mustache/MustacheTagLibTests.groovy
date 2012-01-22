package org.grails.plugins.mustache

import grails.test.mixin.*
import org.junit.*

@TestFor(MustacheTagLib)
class MustacheTagLibTests {

  void testRender() {
    StringWriter out = new StringWriter()
    MustacheTagLib.metaClass.out = out
    
    new MustacheTagLib().render([
      model: [
        name: "Edvinas",
        value: 2,
        taxed_value: 1.5,
        in_ca: true
      ]
    ], { ->
      return '''Hello {{name}}. You have just won ${{value}}! {{#in_ca}} Well, ${{taxed_value}}, after taxes. {{/in_ca}}'''
    })

    assertEquals 'Hello Edvinas. You have just won $2!  Well, $1.5, after taxes. ', out.toString()

    def remove = GroovySystem.metaClassRegistry.&removeMetaClass
    remove MustacheTagLib
  }
}
