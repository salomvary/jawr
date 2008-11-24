package org.apache.jsp;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;

public final class index_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static java.util.Vector _jspx_dependants;

  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_jwr_style_src_nobody;
  private org.apache.jasper.runtime.TagHandlerPool _jspx_tagPool_jwr_script_src_nobody;

  private org.apache.jasper.runtime.ResourceInjector _jspx_resourceInjector;

  public Object getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _jspx_tagPool_jwr_style_src_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _jspx_tagPool_jwr_script_src_nobody = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
  }

  public void _jspDestroy() {
    _jspx_tagPool_jwr_style_src_nobody.release();
    _jspx_tagPool_jwr_script_src_nobody.release();
  }

  public void _jspService(HttpServletRequest request, HttpServletResponse response)
        throws java.io.IOException, ServletException {

    JspFactory _jspxFactory = null;
    PageContext pageContext = null;
    HttpSession session = null;
    ServletContext application = null;
    ServletConfig config = null;
    JspWriter out = null;
    Object page = this;
    JspWriter _jspx_out = null;
    PageContext _jspx_page_context = null;


    try {
      _jspxFactory = JspFactory.getDefaultFactory();
      response.setContentType("text/html");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      String resourceInjectorClassName = config.getInitParameter("com.sun.appserv.jsp.resource.injector");
      if (resourceInjectorClassName != null) {
        _jspx_resourceInjector = (org.apache.jasper.runtime.ResourceInjector) Class.forName(resourceInjectorClassName).newInstance();
        _jspx_resourceInjector.setContext(application);
      }

      out.write("\n");
      out.write("<html>\n");
      out.write("<head>\n");
      out.write(" ");
      if (_jspx_meth_jwr_style_0(_jspx_page_context))
        return;
      out.write('\n');
      out.write(' ');
      if (_jspx_meth_jwr_script_0(_jspx_page_context))
        return;
      out.write("\n");
      out.write("</head>\n");
      out.write("<body>\n");
      out.write("<h2>Hello World!</h2>\n");
      out.write("</body>\n");
      out.write("</html>\n");
    } catch (Throwable t) {
      if (!(t instanceof SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          out.clearBuffer();
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
      }
    } finally {
      if (_jspxFactory != null) _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_jwr_style_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  jwr:style
    net.jawr.web.taglib.CSSBundleTag _jspx_th_jwr_style_0 = (net.jawr.web.taglib.CSSBundleTag) _jspx_tagPool_jwr_style_src_nobody.get(net.jawr.web.taglib.CSSBundleTag.class);
    _jspx_th_jwr_style_0.setPageContext(_jspx_page_context);
    _jspx_th_jwr_style_0.setParent(null);
    _jspx_th_jwr_style_0.setSrc("/bundles/globalcss.css");
    int _jspx_eval_jwr_style_0 = _jspx_th_jwr_style_0.doStartTag();
    if (_jspx_th_jwr_style_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_jwr_style_src_nobody.reuse(_jspx_th_jwr_style_0);
      return true;
    }
    _jspx_tagPool_jwr_style_src_nobody.reuse(_jspx_th_jwr_style_0);
    return false;
  }

  private boolean _jspx_meth_jwr_script_0(PageContext _jspx_page_context)
          throws Throwable {
    PageContext pageContext = _jspx_page_context;
    JspWriter out = _jspx_page_context.getOut();
    //  jwr:script
    net.jawr.web.taglib.JavascriptBundleTag _jspx_th_jwr_script_0 = (net.jawr.web.taglib.JavascriptBundleTag) _jspx_tagPool_jwr_script_src_nobody.get(net.jawr.web.taglib.JavascriptBundleTag.class);
    _jspx_th_jwr_script_0.setPageContext(_jspx_page_context);
    _jspx_th_jwr_script_0.setParent(null);
    _jspx_th_jwr_script_0.setSrc("/bundles/global.js");
    int _jspx_eval_jwr_script_0 = _jspx_th_jwr_script_0.doStartTag();
    if (_jspx_th_jwr_script_0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
      _jspx_tagPool_jwr_script_src_nobody.reuse(_jspx_th_jwr_script_0);
      return true;
    }
    _jspx_tagPool_jwr_script_src_nobody.reuse(_jspx_th_jwr_script_0);
    return false;
  }
}
