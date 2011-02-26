/**
 * 
 */
package net.jawr.web.resource.bundle.global.postprocessor.google.closure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.jawr.web.JawrConstant;
import net.jawr.web.config.JawrConfig;
import net.jawr.web.exception.BundlingProcessException;
import net.jawr.web.exception.ResourceNotFoundException;
import net.jawr.web.resource.bundle.JoinableResourceBundle;
import net.jawr.web.resource.bundle.global.processor.AbstractChainedGlobalProcessor;
import net.jawr.web.resource.bundle.global.processor.GlobalProcessingContext;
import net.jawr.web.resource.bundle.handler.ResourceBundlesHandler;
import net.jawr.web.resource.bundle.variant.VariantSet;
import net.jawr.web.resource.bundle.variant.VariantUtils;
import net.jawr.web.util.FileUtils;
import net.jawr.web.util.StringUtils;

import org.apache.log4j.Logger;

import com.google.common.io.CharStreams;
import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.JSModule;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.JawrCommandLineRunner;


/**
 * 
 * @author ibrahim Chaehoi
 */
public class ClosureGlobalPostProcessor extends AbstractChainedGlobalProcessor {

	/** The logger */
	private static final Logger LOGGER = Logger
			.getLogger(ClosureGlobalPostProcessor.class);
	
	private String srcDir;
	private String destDir;
	private String tempDir;
	
	/**
	 * Constructor
	 */
	public ClosureGlobalPostProcessor() {
		super(JawrConstant.GLOBAL_GOOGLE_CLOSURE_POSTPROCESSOR_ID);
	}
	
	/**
	 * Constructor
	 */
	public ClosureGlobalPostProcessor(String srcDir, String tempDir, String destDir) {
		super(JawrConstant.GLOBAL_GOOGLE_CLOSURE_POSTPROCESSOR_ID);
		this.srcDir = srcDir;
		this.destDir = destDir;
		this.tempDir = tempDir;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.jawr.web.resource.bundle.global.postprocessor.GlobalPostprocessor
	 * #processBundles(net.jawr.web.resource.bundle.global.postprocessor.
	 * GlobalPostprocessingContext, java.util.List)
	 */
	public void processBundles(GlobalProcessingContext ctx,
			List<JoinableResourceBundle> bundles) {
		
		String workingDir = ctx.getRsReaderHandler().getWorkingDirectory(); 
		
		if(srcDir == null || destDir == null || tempDir == null){
			srcDir = ((ResourceBundlesHandler) ctx.getJawrConfig().getContext().getAttribute(JawrConstant.JS_CONTEXT_ATTRIBUTE)).getBundleTextDirPath();
			destDir = srcDir;
			tempDir = workingDir + "/googleClosure/";
		}
		
		// Copy the bundle files in a temp directory
		try {
			FileUtils.copyDirectory(new File(srcDir), new File(
					tempDir));

			//Properties =
			Map<String, String> resultBundleMapping = new HashMap<String, String>();
			JawrClosureCommandLineRunner cmdRunner = new JawrClosureCommandLineRunner(
					ctx, bundles, resultBundleMapping);
			cmdRunner.doRun();

		} catch (Exception e) { // Check exception because FlagUsageException is
								// not public
			throw new BundlingProcessException(e);
		}

	}

	/**
	 * Returns the closure compiler arguments
	 * @param ctx the global processing context
	 * @param tmpBundles the bundles
	 * @param resultBundlePathMapping the object which defines the mapping between the bundle name and the bundle path 
	 * @return
	 */
	private String[] getClosureCompilerArgs(GlobalProcessingContext ctx,
			List<JoinableResourceBundle> tmpBundles, Map<String, String> resultBundlePathMapping) {

		List<String> args = new ArrayList<String>();
		JawrConfig config = ctx.getJawrConfig();
		List<JoinableResourceBundle> bundles = new ArrayList<JoinableResourceBundle>(
				tmpBundles);
		String externs = config.getProperty("jawr.js.closure.externs");

		// TODO Handle All closure parameter
		args.add("--warning_level");
		args.add("VERBOSE");

		String compilationLevel = config.getProperty("jawr.js.closure.compilation.level");
		if(!"ADVANCED_OPTIMIZATIONS".equalsIgnoreCase(compilationLevel)
				&& !"WHITESPACE_ONLY".equalsIgnoreCase(compilationLevel)
				&& !"SIMPLE_OPTIMIZATIONS".equalsIgnoreCase(compilationLevel)){
		
			if(StringUtils.isNotEmpty(compilationLevel)){
				LOGGER.debug("Closure compilation level defined in config '"+compilationLevel+"' is not part of the available " +
					"ones [WHITESPACE_ONLY, SIMPLE_OPTIMIZATIONS, ADVANCED_OPTIMIZATIONS");
			}
			compilationLevel = "WHITESPACE_ONLY";
		}
		
		LOGGER.debug("Closure compilation level used : "+compilationLevel);
		
		args.add("--compilation_level");
		args.add(compilationLevel);
		
		// --externs
		if (StringUtils.isNotEmpty(externs)) {
			args.add("--externs");
			args.add(externs);
		}
		
		// handle modules
		Map<String, JoinableResourceBundle> bundleMap = new HashMap<String, JoinableResourceBundle>();
		for (JoinableResourceBundle bundle : bundles) {
			bundleMap.put(bundle.getName(), bundle);
		}

		String modules = config.getProperty("jawr.js.closure.modules");
		List<String> depModulesArgs = new ArrayList<String>();
		
		List<JoinableResourceBundle> globalBundles = getRsBundlesHandler(ctx).getGlobalBundles();
		List<String> globalBundleDependencies = new ArrayList<String>();
		// TODO Handle globalBundle deep dependencies
		for (JoinableResourceBundle globalBundle : globalBundles) {
			globalBundleDependencies.add(globalBundle.getName());
		}
		
		// Define Jawr root module
		// The JAWR_ROOT_MODULE is a fake module to give a root module to the dependency graph
		// This is it's only purpose. It is the root dependency for any module
		// This is used because Google Closure use a unique module as root for dependency management
		// in advance mode
		args.add("--js");
		args.add("/JAWR_ROOT_MODULE.js");

		args.add("--module");
		args.add("JAWR_ROOT_MODULE:1:");
		resultBundlePathMapping.put("JAWR_ROOT_MODULE", "/JAWR_ROOT_MODULE.js");
		
		if(StringUtils.isNotEmpty(modules)){
			String[] moduleSpecs = modules.split(";");
			for (String moduleSpec : moduleSpecs) {
				int moduleNameSeparatorIdx = moduleSpec.indexOf(":");
				if (moduleNameSeparatorIdx < 0) {
					throw new BundlingProcessException(
							"The property 'jawr.js.closure.modules' is not properly defined. Please check your configuration.");
				}
	
				// Check module name
				String bundleName = moduleSpec.substring(0, moduleNameSeparatorIdx);
				checkBundleName(bundleName, bundleMap);
				JoinableResourceBundle bundle = bundleMap.get(bundleName);
				List<String> dependencies = Arrays.asList(moduleSpec.substring(
						moduleNameSeparatorIdx + 1).split(","));
				dependencies.addAll(0, globalBundleDependencies);
				generateBundleModuleArgs(depModulesArgs, bundleMap, resultBundlePathMapping, bundle,
						dependencies);
	
				// Remove the bundle from the list of bundle to treat
				bundles.remove(bundle);
			}
		}
		
		// handle the other bundles
		for (JoinableResourceBundle bundle : bundles) {
			generateBundleModuleArgs(args, bundleMap, resultBundlePathMapping, bundle, globalBundleDependencies);
		}

		// Add dependency modules args after to conform to dependency definition
		// of closure args
		args.addAll(depModulesArgs);

		if(LOGGER.isDebugEnabled()){
			StringBuilder strArg = new StringBuilder();
			for (String arg : args) {
				strArg.append(arg+" ");
			}
			
			LOGGER.debug("Closure Compiller Args : "+strArg.toString());
		}
		return args.toArray(new String[] {});
	}

	private void generateBundleModuleArgs(List<String> args,
			Map<String, JoinableResourceBundle> bundleMap, Map<String, String> resultBundleMapping,
			JoinableResourceBundle bundle, List<String> dependencies) {
		
		Set<String> bundleDependencies = getClosureModuleDependencies(bundle,
				dependencies);
		
		// Generate a module for each bundle variant
		Map<String, VariantSet> bundleVariants = bundle.getVariants();
		List<Map<String, String>> variants = VariantUtils
				.getAllVariants(bundleVariants);
		// TODO should it be defined directly in getAllVariants or
		// bundle.getVariants ?
		
		// Add default variant
		if(variants.isEmpty()){
			variants.add(null);
		}
		for (Iterator<Map<String, String>> iterator = variants.iterator(); iterator
				.hasNext();) {
			Map<String, String> variant = iterator.next();

			String jsFile = VariantUtils.getVariantBundleName(bundle.getId(),
					variant);
			String moduleName = VariantUtils.getVariantBundleName(
					bundle.getName(), variant);
			
			resultBundleMapping.put(moduleName, jsFile);
			
			args.add("--js");
			args.add(jsFile);

			args.add("--module");
			StringBuilder moduleArg = new StringBuilder();
			moduleArg.append(moduleName + ":1:");
			for (String dep : bundleDependencies) {
				
				// Check module dependencies
				checkBundleName(dep, bundleMap);

				JoinableResourceBundle dependencyBundle = bundleMap.get(dep);
				// Generate a module for each bundle variant
				List<String> depVariantKeys = VariantUtils
						.getAllVariantKeysFromFixedVariants(
								dependencyBundle.getVariants(), variant);

				// TODO check if it should not be moved to variantUtils or
				// bundle
				if(depVariantKeys.isEmpty()){
					depVariantKeys.add(null);
				}
				for (Iterator<String> itDepVariantKey = depVariantKeys
						.iterator(); itDepVariantKey.hasNext();) {
					String depVariantKey = itDepVariantKey.next();
					String depBundleName = VariantUtils.getVariantBundleName(
							dep, depVariantKey);
					moduleArg.append(depBundleName);
					moduleArg.append(",");
				}
			}
			moduleArg.append("JAWR_ROOT_MODULE");
			args.add(moduleArg.toString());
		}
	}

	/**
	 * Returns the module bundle dependency from the bundle dependency and the declared dependencies 
	 * @param bundle the bundle
	 * @param dependencies the declared dependencies
	 * @return the list of the module dependency
	 */
	private Set<String> getClosureModuleDependencies(
			JoinableResourceBundle bundle, List<String> dependencies) {
		
		Set<String> bundleDependencies = new HashSet<String>();
		for (JoinableResourceBundle depBundle : bundle.getDependencies()) {
			bundleDependencies.add(depBundle.getName());
		}
		for (String depBundleName : dependencies) {
			bundleDependencies.add(depBundleName);
		}
		return bundleDependencies;
	}

	/**
	 * @param bundleName
	 * @param bundleMap
	 */
	private void checkBundleName(String bundleName,
			Map<String, JoinableResourceBundle> bundleMap) {
		if(!"JAWR_ROOT_MODULE".equals(bundleName)){
			boolean moduleExist = bundleMap.get(bundleName) != null;
			if (!moduleExist) {
				throw new BundlingProcessException(
						"The bundle name '"
								+ bundleName
								+ "' defined in 'jawr.js.closure.modules' is not defined in the configuration. Please check your configuration.");
			}
		}
	}
	

	/**
	 * Returns the ResourcebundlesHandler
	 * @param the global processing context
	 * @return the ResourcebundlesHandler
	 */
	public ResourceBundlesHandler getRsBundlesHandler(GlobalProcessingContext ctx){
		return (ResourceBundlesHandler) ctx.getJawrConfig().getContext().getAttribute(JawrConstant.JS_CONTEXT_ATTRIBUTE);
	}
	
	private class JawrClosureCommandLineRunner extends JawrCommandLineRunner {

		private GlobalProcessingContext ctx;

		private Map<String, String> resultBundleMapping;
			
		public JawrClosureCommandLineRunner(GlobalProcessingContext ctx,
				List<JoinableResourceBundle> bundles, Map<String, String> resultBundleMapping) {
			super(getClosureCompilerArgs(ctx, bundles, resultBundleMapping));
			this.ctx = ctx;
			this.resultBundleMapping = resultBundleMapping;
		}

		/* (non-Javadoc)
		 * @see com.google.javascript.jscomp.JawrAbstractCommandLineRunner#checkModuleName(java.lang.String, java.util.Map)
		 */
		@Override
		protected void checkModuleName(String name) throws FlagUsageException{
			
		}
		    
		
		/**
		 * Creates inputs from a list of files.
		 * 
		 * @param files
		 *            A list of filenames
		 * @param allowStdIn
		 *            Whether '-' is allowed appear as a filename to represent
		 *            stdin. If true, '-' is only allowed to appear once.
		 * @return An array of inputs
		 * @throws
		 */
		@Override
		protected List<JSSourceFile> createInputs(List<String> files,
				boolean allowStdIn) throws IOException {
			List<JSSourceFile> inputs = new ArrayList<JSSourceFile>(
					files.size());

			ResourceBundlesHandler resourceBundlesHandler = getRsBundlesHandler(ctx);

			for (String filename : files) {
				if(filename.equals("/JAWR_ROOT_MODULE.js")){
					JSSourceFile newFile = JSSourceFile.fromCode(filename,
							"");
					inputs.add(newFile);
				}else if (!"-".equals(filename)) {
					Reader rd = null;
					StringWriter swr = new StringWriter();
					try {
						resourceBundlesHandler.writeBundleTo(filename, swr);
						rd = new StringReader(swr.getBuffer().toString());
					} catch (ResourceNotFoundException e) {
						// Do nothing
					}
					
					if(rd == null){
						try {
							rd = ctx.getRsReaderHandler().getResource(filename);
						} catch (ResourceNotFoundException e1) {
							throw new BundlingProcessException(e1);
						}
					}
					
					String jsCode = CharStreams.toString(rd);
					JSSourceFile newFile = JSSourceFile.fromCode(filename,
							jsCode);
					inputs.add(newFile);
				}
			}
			return inputs;
		}

		/**
		 * Converts a file name into a Writer. Returns null if the file name is
		 * null.
		 */
		@Override
		protected OutputStream fileNameToOutputStream(String fileName)
			throws IOException {
			
			if (fileName == null) {
				return null;
			}

			String bundleName = fileName.substring(0, fileName.length() - 3).substring(2);
			
			String bundlePath = resultBundleMapping.get(bundleName);
			File outFile = new File(destDir+"/"+bundlePath);
			outFile.getParentFile().mkdirs();
			return new FileOutputStream(outFile);
		}

		/* (non-Javadoc)
		 * @see com.google.javascript.jscomp.JawrAbstractCommandLineRunner#doRun()
		 */
		public int doRun() throws FlagUsageException, IOException {
			int result = super.doRun();
			// Delete JAWR_ROOT_MODULE file
			File jawrRootModuleFile = new File(destDir+"/"+resultBundleMapping.get("JAWR_ROOT_MODULE"));
			if(!jawrRootModuleFile.delete()){
				LOGGER.warn("Enable to delete JAWR_ROOT_MODULE.js file");
			}
			return result;
		}
	}
}
