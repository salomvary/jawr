import net.jawr.web.servlet.JawrRequestHandler;
import org.codehaus.groovy.grails.commons.*;

class JawrGrailsPlugin {
    def version = 0.1
    def dependsOn = [:]
    def appContext;
    def jawrConf;
	def watchedResources =  "file:./grails-app/conf/Config.groovy";
   
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
    	appContext = applicationContext;
    	jawrConf = conf.jawr;
    	
    }

    
	
    def onChange = { event ->
    	// TODO: only refresh when jawr config is updated. 
        doWithApplicationContext(appContext);
    }
   
}
