package org.grails.plugins.mustache

class ModuleBuilder {
  MustacheModule module

  def invokeMethod(String name, args) {
    if(module) {
      switch(name) {
        case "output" :
          if(args.size() > 0) {
            module.output = args.first()
          }

          break
        case "src" :
          def src = [:]
          args.each {
            if(it instanceof String)
               src.path = it as String
            else if(it instanceof Map)
            {
              if(it.id) {
                src.id = it.id as String
              }
            }
          }

          module.files.push src

          break
        default :
          break
      }
    }
  }
}
