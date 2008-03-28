import net.jawr.web.servlet.JawrRequestHandler;
import org.codehaus.groovy.grails.commons.*;

class JawrGrailsPlugin {
    def version = 0.1
    def dependsOn = [:]
    
	  def watchedResources = ConfigurationHolder.config.jawr;
   
    def doWithApplicationContext = { applicationContext ->
    	def conf =  ConfigurationHolder.config;
    	if(conf.jawr) {
    	
	    	if(conf.jawr.js){
	    		Properties jawrProps = ConfigurationHolder.config.toProperties();
	    		
	    		def map = [type:"js"]
	    		if(conf.jawr.js.mapping)
	    			map.put('mapping',conf.jawr.js.mapping);
	    		JawrRequestHandler requestHandler = new JawrRequestHandler(applicationContext.servletContext , map, jawrProps );
	    		applicationContext.servletContext.setAttribute("JavascriptJawrRequestHandler", requestHandler)
	    	}
	    	if(conf.jawr.css){
	    		Properties jawrProps = ConfigurationHolder.config.toProperties();
	    		def map = [type:"css"]
	    		if(conf.jawr.css.mapping)
	    			map.put('mapping',conf.jawr.css.mapping);
	    		JawrRequestHandler requestHandler = new JawrRequestHandler(applicationContext.servletContext ,map, jawrProps );
	    		applicationContext.servletContext.setAttribute("CSSJawrRequestHandler", requestHandler)
	    	}
	    }
    	
    }
    
    


	
    def onChange = { event ->
        println "onChange update event"
    }
                                                                                  
    def onApplicationChange = { event ->
    	println "Application update event"
    	// TODO test this
    	//if(event.source.is(ConfigurationHolder.config))
    		doWithApplicationContext(event.applicationContext);
        // TODO Implement code that is executed when any class in a GrailsApplication changes
        // the event contain: event.source, event.application and event.applicationContext objects
    }
}
