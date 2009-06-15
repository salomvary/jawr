/**
 * 
 */
package test.net.jawr.web;

import javax.servlet.ServletException;

import net.jawr.web.BundleProcessor;

/**
 * Bundle processor test case
 * 
 * @author Ibrahim Chaehoi
 */
public class RunBundlePostProcessor {

	public static void main(String[] args) throws Exception{
		
		String baseDirPath = "E:/Prog/eclipseData/workspace3.3/jawr3.0/src/main/webapp";
		String tmpDirPath = FileUtils.getClasspathRootDir()+"/bundleProcessor/tmpDir";
		String destDirPath = FileUtils.getClasspathRootDir()+"/bundleProcessor/destDir";
		
		FileUtils.clearDirectory(tmpDirPath);
		FileUtils.clearDirectory(destDirPath);
		
		BundleProcessor bundleProcessor = new BundleProcessor();
		
		try{
			bundleProcessor.process(baseDirPath, tmpDirPath, destDirPath);
		}catch(Exception e){
			e.printStackTrace();
			if(e instanceof ServletException){
				((ServletException)e).getRootCause().printStackTrace();
			}
		}
		
		//String bundlePath = FileUtils.getClasspathRootDir()+"/bundleProcessor/tmpDir/jawrTmp/text/bundle/global.js";
		//Assert.assertTrue("Bundle has not been created", new File(bundlePath).exists());
	}
}
