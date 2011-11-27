<%--
  Created by IntelliJ IDEA.
  User: edvinas
  Date: 11/28/11
  Time: 12:07 AM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
  <head><title>Simple GSP page</title></head>
  <body>
    <mustache:compile model="${[something:"world"]}">
        Hello {{something}}!
    </mustache:compile>
  </body>
</html>