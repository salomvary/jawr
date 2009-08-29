package net.jawr.web.test;

import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import net.jawr.web.test.utils.Utils;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.JavaScriptPage;
import com.gargoylesoftware.htmlunit.TextPage;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlScript;

/**
 * Test case for standard page in debug mode.
 * 
 * @author ibrahim Chaehoi
 */
@JawrTestConfigFiles(webXml = "net/jawr/web/debug/config/web.xml", jawrConfig = "net/jawr/web/debug/config/jawr.properties")
public class MainPageDebugTest extends MainPageTest {

	@Test
	public void testPageLoad() throws Exception {

		final List<String> expectedAlerts = Collections
				.singletonList("A little message retrieved from the message bundle : Hello $ world!");
		assertEquals(expectedAlerts, collectedAlerts);

		assertContentEquals("/net/jawr/web/debug/resources/index-jsp-result-debug-mode-expected.txt", page);
		
	}

	@Test
	public void checkGeneratedJsLinks() {
		// Test generated Script link
		final List<?> scripts = page.getByXPath("html/head/script");
		assertEquals(10, scripts.size());
		HtmlScript script = (HtmlScript) scripts.get(7);
		assertEquals(
				CONTEXT_PATH+"/jawr_generator.js?generationConfigParam=messages%3Amessages%40en_US",
				script.getSrcAttribute());
		script = (HtmlScript) scripts.get(8);
		assertEquals(
				CONTEXT_PATH+"/jawr_generator.js?generationConfigParam=testJs%3AgeneratedContent.js%40en_US",
				script.getSrcAttribute());
	}

	@Test
	public void testJsBundleContent() throws Exception {

		final List<?> scripts = page.getByXPath("html/head/script");
		HtmlScript script = (HtmlScript) scripts.get(7);
		JavaScriptPage page = getJavascriptPage(script);
		assertContentEquals("/net/jawr/web/debug/resources/msg-debug.js", page);
		
		script = (HtmlScript) scripts.get(8);
		page = getJavascriptPage(script);
		assertContentEquals("/net/jawr/web/debug/resources/generatedContent.js", page);
	}

	@Test
	public void checkGeneratedCssLinks() {
		// Test generated Css link
		final List<?> styleSheets = page.getByXPath("html/head/link");
		assertEquals(2, styleSheets.size());
		HtmlLink css = (HtmlLink) styleSheets.get(0);
		Utils.assertGeneratedLinkEquals(
				CONTEXT_PATH+"/jawr_generator.css?generationConfigParam=jar%3Afwk%2Fcss%2Ftemp.css%40en_US",
				css.getHrefAttribute());
		
		css = (HtmlLink) styleSheets.get(1);
		Utils.assertGeneratedLinkEquals(
				CONTEXT_PATH+"/css/one.css?d=11111",css.getHrefAttribute());
	}

	@Test
	public void testCssBundleContent() throws Exception {

		final List<?> styleSheets = page.getByXPath("html/head/link");
		HtmlLink css = (HtmlLink) styleSheets.get(0);
		TextPage page = getCssPage(css);
		assertContentEquals("/net/jawr/web/debug/resources/jar_temp.css", page);
		
		css = (HtmlLink) styleSheets.get(1);
		page = getCssPage(css);
		assertContentEquals("/net/jawr/web/debug/resources/one.css", page);
	}

}
