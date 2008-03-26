import net.jawr.web.servlet.JawrRequestHandler;
import org.codehaus.groovy.grails.commons.*;

class JawrGrailsPlugin {
    def version = 0.1
    def dependsOn = [:]
    
    def requestHandler;
	def initParams = [type:"js"];
	
   
    def doWithApplicationContext = { applicationContext ->
    	def conf =  ConfigurationHolder.config;
    	
    	if(null != conf.jawr && null != conf.jawr.js){
    		requestHandler = new JawrRequestHandler(applicationContext.servletContext , [type:"js"], ConfigurationHolder.config.toProperties() );
    		applicationContext.servletContext.setAttribute("JavascriptJawrRequestHandler", requestHandler)
    	}
    	if(null != conf.jawr && null != conf.jawr.css){
    		requestHandler = new JawrRequestHandler(applicationContext.servletContext , [type:"css"], ConfigurationHolder.config.toProperties() );
    		applicationContext.servletContext.setAttribute("CSSJawrRequestHandler", requestHandler)
    	}
    	
    }
    
    


	
    def onChange = { event ->
        // TODO Implement code that is executed when this class plugin class is changed  
        // the event contains: event.application and event.applicationContext objects
    }
                                                                                  
    def onApplicationChange = { event ->
    	// TODO test this
    	if(event.source.is(ConfigurationHolder.config))
    		doWithApplicationContext(event.applicationContext);
        // TODO Implement code that is executed when any class in a GrailsApplication changes
        // the event contain: event.source, event.application and event.applicationContext objects
    }
}
