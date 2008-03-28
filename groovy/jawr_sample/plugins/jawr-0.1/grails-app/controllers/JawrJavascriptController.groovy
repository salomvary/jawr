import net.jawr.web.servlet.JawrRequestHandler;

class JawrJavascriptController {
	def defaultAction = "doGet"
	JawrRequestHandler requestHandler;
	
	def doGet = {
		
		if(null == requestHandler)
			requestHandler = servletContext.getAttribute("JavascriptJawrRequestHandler");
		
			String path= request['javax.servlet.forward.servlet_path'];
			
			if(grailsApplication.config.jawr.js.mapping){
				path = path.replace(grailsApplication.config.jawr.js.mapping, '');
				
			}
				
		requestHandler.processRequest(path,request, response );
		
		return null;
	}
	
	
}