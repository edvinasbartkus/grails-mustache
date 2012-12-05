package org.grails.plugins.mustache

import grails.test.mixin.*
import org.junit.*

import org.springframework.core.io.*
import org.yaml.snakeyaml.Yaml

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
  void testSpec() {
    int specPasses = 0, specFails = 0
    StringWriter out = new StringWriter()
    MustacheTagLib.metaClass.out = out
    new File("test/unit/resources/spec").eachFileRecurse( groovy.io.FileType.FILES ) { file ->
        println "Testing file ${file}"
        def yaml = new Yaml() 
        def result = yaml.load(file.text)
        result.tests.each { test ->
            def map = [model: test.data as Map]
            println "Executing spec ${test.name}"
            try { //TODO - manage partials
                new MustacheTagLib().render(map) {
                    return test.template
                }
            } catch (Exception e) {
                e.printStackTrace()
            }
            if(!(test.expected.toString().trim().equals(out.toString().trim()) || test.expected.toString().trim().replaceAll('"', '').equals(out.toString().trim()))) { //FIXME - Quote issues in the matching...
                println "Spec ${test.name} failed -> ${test.expected} != ${out.toString()}"
                specFails++
            } else {
                println "Spec ${test.name} -> ${test.expected} == ${out.toString()} passed"
                specPasses++
            }
//            assert(test.expected.toString().equals(out.toString()) || test.expected.toString().replaceAll('"', '').equals(out.toString())) //FIXME - Quote issues in the matching...
            out.getBuffer().setLength(0)
        }
    }
    println "Spec fails: ${specFails}"
    println "Spec passes: ${specPasses}"
    def remove = GroovySystem.metaClassRegistry.&removeMetaClass
    remove MustacheTagLib
    assert specFails == 0 //The number of specs that fail should be zero
  }

}
