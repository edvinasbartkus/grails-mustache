package org.grails.plugins.mustache

import grails.util.BuildSettingsHolder

class MustacheModule {
  String name
  List<Map> files

  public MustacheModule() {
    files = new ArrayList<Map>()
  }

  String wrapped() {
    return files.collect {
      def path = it.path as String
      def id = it.id as String
      
      String filename = path.endsWith(".mustache") ? path : path + ".mustache"
      File file = new File(BuildSettingsHolder.settings.baseDir, filename)
      return wrapped(file, id)
    }.join(System.getProperty("line.separator"))
  }
  
  String wrapped(File file, String id = null) {
    if(!file.exists()) {
      if(log.debugEnabled) {
        log.debug("File ${file.absolutePath} not found!")
      }

      return ''
    }
    
    
    String filename = file.name.lastIndexOf('.') >= 0 ?
      file.name[0 .. file.name.lastIndexOf('.')-1] :
      file.name
    id = id ?: "mustache-${filename}"
    
    def head = id ? "<javascript type=\"text/javascript\" id=\"${id}\">" : "<javascript type=\"\">"
    def content = file.text
    def end = "</javascript>"

    return head + content + end
  }

  File getRealPath(path = "/web-app/mustache") {
    return new File(BuildSettingsHolder.settings.baseDir, path)
  }
  
  File cache() {
    File dir = new File(BuildSettingsHolder.settings.baseDir, "/web-app/mustache")
    if(!dir.exists()) dir.mkdir()
    File cacheFile = new File(dir, "${name}.html")
    cacheFile.write(this.wrapped())
    return cacheFile
  }
}
