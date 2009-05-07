<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>    
 <jwr:style src="/bundles/one.css" media="all" useRandomParam="false" />
 <jwr:style src="/bundles/two.css" media="print"  useRandomParam="false" />
 <jwr:style src="/bundles/cpStyle.css" media="all" />
 <jwr:script src="/bundles/global.js"/>
	<script>
		jawr.index();
	</script>
</head>
<body>
<div class="mandatory">
	For this element, the style and the image associated are retrieved directly from the classpath.
</div>
<div style="height: 20px">
	<jwr:img src="/img/appIcons/application.png"/> This HTML image use a generated path which force the caching for the browser. 
</div>
<div>
	<span class="calendar">The CSS image is retrieved from the webapp, which is a classic case.</span>
</div>
<div>
	<span class="clock">The CSS image is retrieved from the classpath. The CSS used here is defined in the webapp under the "css" folder.<br>
	This mean that you could reference a CSS image in the classpath from a standard CSS define in a bundle.</span>
</div>
<div style="height: 20px">
	<jwr:image value="temp" src="/img/cog.png"/> This input image use a generated path which force the caching for the browser.
</div>
</body>
</html>
