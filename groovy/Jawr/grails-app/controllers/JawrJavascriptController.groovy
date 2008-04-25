import net.jawr.web.servlet.JawrRequestHandler;

/**
 * Jawr controller for javascript requests. 
 * It will delegate in the corresponding requestHandler to attend requests. 
 */
class JawrJavascriptController {
	def defaultAction = "doGet"
	JawrRequestHandler requestHandler;
	
	def doGet = {
		
		if(null == requestHandler)
			requestHandler = servletContext.getAttribute("JavascriptJawrRequestHandler");
		
			// In grails the request is always internally forwarded. This takes account for that. 
			String path = request['javax.servlet.forward.servlet_path'];			
			if(grailsApplication.config.jawr.js.mapping){
				path = path.replace(grailsApplication.config.jawr.js.mapping, '');
				
			}
				
		requestHandler.processRequest(path,request, response );
		
		return null;
	}
	
	
}