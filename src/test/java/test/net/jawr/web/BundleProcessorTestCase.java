/**
 * 
 */
package test.net.jawr.web;

import java.io.File;

import junit.framework.Assert;
import junit.framework.TestCase;
import net.jawr.web.BundleProcessor;

/**
 * Bundle processor test case
 * 
 * @author Ibrahim Chaehoi
 */
public class BundleProcessorTestCase extends TestCase {

	public void testBundleProcessing() throws Exception{
		
		String baseDirPath = FileUtils.getClasspathRootDir()+"/bundleProcessor/wrkDir";
		String tmpDirPath = FileUtils.getClasspathRootDir()+"/bundleProcessor/tmpDir";
		String destDirPath = FileUtils.getClasspathRootDir()+"/bundleProcessor/destDir";
		
		FileUtils.clearDirectory(tmpDirPath);
		FileUtils.clearDirectory(destDirPath);
		
		BundleProcessor bundleProcessor = new BundleProcessor();
		
		bundleProcessor.process(baseDirPath, tmpDirPath, destDirPath);
		
		String bundlePath = FileUtils.getClasspathRootDir()+"/bundleProcessor/tmpDir/jawrTmp/text/bundle/global.js";
		Assert.assertTrue("Bundle has not been created", new File(bundlePath).exists());
	}
}
