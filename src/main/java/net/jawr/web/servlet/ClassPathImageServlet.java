package net.jawr.web.servlet;

import javax.servlet.ServletException;

import org.apache.log4j.Logger;

/**
 * This servlet is responsible of providing images, which are define in the classpath. This image could be linked to a CSS file also loaded from the
 * classpath.
 * 
 * @author Ibrahim CHAEHOI
 */
public class ClassPathImageServlet extends JawrServlet {

	// ~---------- Variables ----------

	/** The logger */
	private static final Logger log = Logger.getLogger(ClassPathImageServlet.class);

	// ~---------- Methods ----------

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	public void init() throws ServletException {
		try {
			requestHandler = new JawrImageRequestHandler(getServletContext(),getServletConfig());
		}catch (ServletException e) {
			log.fatal("Jawr servlet with name" +  getServletConfig().getServletName() +" failed to initialize properly. ");
			log.fatal("Cause:");
			log.fatal(e.getMessage(),e);
			throw e;
		}catch (RuntimeException e) {
			log.fatal("Jawr servlet with name" +  getServletConfig().getServletName() +" failed to initialize properly. ");
			log.fatal("Cause: ");
			log.fatal(e.getMessage(),e);
			throw new ServletException(e);
		}
	}

}
