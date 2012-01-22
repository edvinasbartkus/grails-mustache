package org.grails.plugins.mustache

import static org.junit.Assert.*

import grails.test.mixin.*
import grails.test.mixin.support.*
import org.junit.*

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

@TestMixin(GrailsUnitTestMixin)
class MustacheModuleTests {

  void setUp() {
    // Setup logic here
  }

  void tearDown() {
    // Tear down logic here
  }

  void testConfigDeclaration() {
    CH.config.mustache.modules = {
      test {
        src "src/mustache/demo.mustache"
      }
    }

    ModuleManager manager = new ModuleManager()
    assert manager.modules.size() == 1

    MustacheModule module = manager.modules.first()
    assertEquals module.files.first().path, "src/mustache/demo.mustache"
  }
  
  void testModuleOutput() {
    MustacheModule module = new MustacheModule(name: 'test', files :[ [path:"/src/mustache/demo.mustache"] ])
    assertEquals '''<javascript type="text/javascript" id="mustache-demo">Hello {{name}}
You have just won ${{value}}!
{{#in_ca}}
Well, ${{taxed_value}}, after taxes.
{{/in_ca}}</javascript>''', module.wrapped()
    
    File cache = module.cache()
    assert cache.exists()
    assertEquals cache.name, "test.html"

    cache.deleteOnExit()
  }
}
