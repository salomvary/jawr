import net.jawr.web.servlet.JawrRequestHandler;

class JawrCSSController {
	def defaultAction = "doGet"
	JawrRequestHandler requestHandler;
		
		 
		def doGet = {
			
			
			if(null == requestHandler)
				requestHandler = servletContext.getAttribute("CSSJawrRequestHandler");
			
			String path= request['javax.servlet.forward.servlet_path'];
			
			if(grailsApplication.config.jawr.css.mapping)
				path = path.replace(grailsApplication.config.jawr.css.mapping, '');
			
			
			requestHandler.processRequest(path,request, response );
			
			return null;
		}
}