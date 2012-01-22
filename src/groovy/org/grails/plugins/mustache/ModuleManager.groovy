package org.grails.plugins.mustache

import org.codehaus.groovy.grails.commons.ConfigurationHolder as CH

class ModuleManager {
  HashMap<String, MustacheModule> modulesHash
  static final HashMap<String,String> filesHash = new HashMap<String, String>()

  // Singleton. Is it right?
  static ModuleManager manager
  static ModuleManager getManager() {
    return this.manager
  }

  ModuleManager() {
    modulesHash = new HashMap<String, MustacheModule>()
  }

  def getModules() {
    modulesHash = [:]
    filesHash.clear()

    def builder = new ConfigBuilder(manager:this)
    CH.config.mustache.modules.each { dsl ->
      if(dsl instanceof Closure) {
        dsl.delegate = builder
        dsl.resolveStrategy = Closure.DELEGATE_FIRST
        dsl()
      }
    }

    def modules = modulesHash.values()
    modules.each { MustacheModule module ->
      module.files.each {
        String pathname = it.path as String
        String filename = pathname.endsWith(".mustache") ? pathname : pathname + ".mustache"
        filesHash[filename] = filesHash[filename] ?: []
        filesHash[filename] << "${module.name}"
      }
    }

    return modules.toList()
  }
}
