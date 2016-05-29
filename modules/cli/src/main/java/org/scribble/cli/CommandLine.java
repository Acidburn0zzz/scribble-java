package org.scribble.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.scribble.ast.Module;
import org.scribble.ast.ProtocolDecl;
import org.scribble.ast.global.GProtocolDecl;
import org.scribble.main.MainContext;
import org.scribble.main.ScribbleException;
import org.scribble.main.resource.DirectoryResourceLocator;
import org.scribble.main.resource.ResourceLocator;
import org.scribble.model.local.EndpointGraph;
import org.scribble.model.wf.WFState;
import org.scribble.sesstype.name.GProtocolName;
import org.scribble.sesstype.name.LProtocolName;
import org.scribble.sesstype.name.Role;
import org.scribble.util.ScribUtil;
import org.scribble.visit.Job;
import org.scribble.visit.JobContext;

// Maybe no point to be a Runnable
public class CommandLine //implements Runnable
{
	protected enum ArgFlag {
		MAIN,
		PATH,
		PROJECT,
		JUNIT,
		VERBOSE,
		FSM,
		FSM_DOT,
		SESS_API,
		SCHAN_API,
		EP_API,
		API_OUTPUT,
		SCHAN_API_SUBTYPES,
		GLOBAL_MODEL,
		GLOBAL_MODEL_DOT,
		OLD_WF,
		NO_LIVENESS,
		//PROJECTED_MODEL
	}
	
	private final Map<ArgFlag, String[]> args;  // Maps each flag to list of associated argument values
	
	public CommandLine(String... args)
	{
		this.args = new CommandLineArgParser(args).getArgs();
		if (!this.args.containsKey(ArgFlag.MAIN))
		{
			throw new RuntimeException("No main module has been specified\r\n");
		}
	}

	public static void main(String[] args) throws ScribbleException
	{
		new CommandLine(args).run();
	}

	//@Override
	public void run() throws ScribbleException
	{
		try
		{
			Job job = newJob(newMainContext());
			ScribbleException fail = null;
			try
			{
				job.checkWellFormedness();
			}
			catch (ScribbleException x)
			{
				fail = x;
			}
			try
			{
				if (this.args.containsKey(ArgFlag.PROJECT))
				{
					outputProjections(job);
				}
				if (this.args.containsKey(ArgFlag.FSM))
				{
					outputGraph(job);
				}
				if (this.args.containsKey(ArgFlag.FSM_DOT))
				{
					drawGraph(job);
				}
				if (this.args.containsKey(ArgFlag.SESS_API))
				{
					outputSessionApi(job);
				}
				if (this.args.containsKey(ArgFlag.SCHAN_API))
				{
					outputStateChannelApi(job);
				}
				if (this.args.containsKey(ArgFlag.EP_API))
				{
					outputEndpointApi(job);
				}
				if (this.args.containsKey(ArgFlag.GLOBAL_MODEL) || this.args.containsKey(ArgFlag.GLOBAL_MODEL_DOT))
				{
					if (job.useOldWf)
					{
						throw new RuntimeException("Incompatible flags: " + CommandLineArgParser.GLOBAL_MODEL_FLAG + " and " + CommandLineArgParser.OLD_WF_FLAG);
					}
					if (this.args.containsKey(ArgFlag.GLOBAL_MODEL_DOT))
					{
						drawGlobalModel(job);
					}
					else
					{
						outputGlobalModel(job);
					}
				}
				/*if (this.args.containsKey(ArgFlag.PROJECTED_MODEL))
				{
					outputProjectedModel(job);
				}*/
			}
			catch (ScribbleException x)
			{
				if (fail == null)
				{
					fail = x;
				}
			}
			if (fail != null)
			{
				throw fail;
			}
		}
		catch (ScribbleException e)  // Wouldn't need to do this if not Runnable (so maybe change)
		{
			if (this.args.containsKey(ArgFlag.JUNIT) || this.args.containsKey(ArgFlag.VERBOSE))
			{
				/*RuntimeScribbleException ee = new RuntimeScribbleException(e.getMessage());
				ee.setStackTrace(e.getStackTrace());
				throw ee;*/
				throw e;
			}
			else
			{
				System.err.println(e.getMessage());  // JUnit harness looks for an exception
				System.exit(1);
			}
		}
	}
	
	// FIXME: option to write to file, like classes
	private void outputProjections(Job job)
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.PROJECT);
		for (int i = 0; i < args.length; i += 2)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			Role role = checkRoleArg(jcontext, fullname, args[i+1]);
			Map<LProtocolName, Module> projections = job.getProjections(fullname, role);
			System.out.println("\n" + projections.values().stream().map((p) -> p.toString()).collect(Collectors.joining("\n\n")));
		}
	}

	private void outputGraph(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.FSM);
		for (int i = 0; i < args.length; i += 2)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			Role role = checkRoleArg(jcontext, fullname, args[i+1]);
			buildEndointGraph(job, fullname, role);
			System.out.println("\n" + jcontext.getEndpointGraph(fullname, role));  // Endpoint graphs are "inlined" (a single graph is built)
		}
	}

	// FIXME: draw graphs once and cache, redrawing gives different state numbers
	private void drawGraph(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.FSM_DOT);
		for (int i = 0; i < args.length; i += 3)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			Role role = checkRoleArg(jcontext, fullname, args[i+1]);
			String png = args[i+2];
			buildEndointGraph(job, fullname, role);
			EndpointGraph fsm = jcontext.getEndpointGraph(fullname, role);
			runDot(fsm.init.toDot(), png);
		}
	}

	private void outputGlobalModel(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.GLOBAL_MODEL);
		for (int i = 0; i < args.length; i += 1)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			WFState model = jcontext.getGlobalModel(fullname);
			if (model == null)
			{
				throw new ScribbleException("Shouldn't see this: " + fullname);  // Should be suppressed by an earlier failure
			}
			System.out.println("\n" + model.toDot());  // FIXME: make a global equiv to EndpointGraph
		}
	}

	private void drawGlobalModel(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.GLOBAL_MODEL_DOT);
		for (int i = 0; i < args.length; i += 2)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			String png = args[i+1];
			WFState model = jcontext.getGlobalModel(fullname);
			if (model == null)
			{
				throw new ScribbleException("Shouldn't see this: " + fullname);  // Should be suppressed by an earlier failure
			}
			runDot(model.toDot(), png);
		}
	}
	
	/*private void outputProjectedModel(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.PROJECTED_MODEL);
		for (int i = 0; i < args.length; i += 2)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			Role role = checkRoleArg(jcontext, fullname, args[i+1]);
			System.out.println("\n" + jcontext.getGlobalModel(fullname).project(role));
		}
	}*/

	private void outputSessionApi(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.SESS_API);
		for (String fullname : args)
		{
			GProtocolName gpn = checkGlobalProtocolArg(jcontext, fullname);
			Map<String, String> classes = job.generateSessionApi(gpn);
			outputClasses(classes);
		}
	}
	
	private void outputStateChannelApi(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.SCHAN_API);
		for (int i = 0; i < args.length; i += 2)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			Role role = checkRoleArg(jcontext, fullname, args[i+1]);
			Map<String, String> classes = job.generateStateChannelApi(fullname, role, this.args.containsKey(ArgFlag.SCHAN_API_SUBTYPES));
			outputClasses(classes);
		}
	}

	private void outputEndpointApi(Job job) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		String[] args = this.args.get(ArgFlag.EP_API);
		for (int i = 0; i < args.length; i += 2)
		{
			GProtocolName fullname = checkGlobalProtocolArg(jcontext, args[i]);
			Map<String, String> sessClasses = job.generateSessionApi(fullname);
			outputClasses(sessClasses);
			Role role = checkRoleArg(jcontext, fullname, args[i+1]);
			Map<String, String> scClasses = job.generateStateChannelApi(fullname, role, this.args.containsKey(ArgFlag.SCHAN_API_SUBTYPES));
			outputClasses(scClasses);
		}
	}

	// filepath -> class source
	private void outputClasses(Map<String, String> classes) throws ScribbleException
	{
		Consumer<String> f;
		if (this.args.containsKey(ArgFlag.API_OUTPUT))
		{
			String dir = this.args.get(ArgFlag.API_OUTPUT)[0];
			f = (path) -> { ScribUtil.handleLambdaScribbleException(() ->
							{
								String tmp = dir + "/" + path;
								if (this.args.containsKey(ArgFlag.VERBOSE))
								{
									System.out.println("\n[DEBUG] Writing to: " + tmp);
								}
								writeToFile(tmp, classes.get(path)); return null; 
							}); };
		}
		else
		{
			f = (path) -> { System.out.println(path + ":\n" + classes.get(path)); };
		}
		classes.keySet().stream().forEach(f);
	}
	
	private static void runDot(String dot, String png) throws ScribbleException
	{
		String tmpName = png + ".tmp";
		File tmp = new File(tmpName);
		if (tmp.exists())
		{
			throw new RuntimeException("Cannot overwrite: " + tmpName);
		}
		try
		{
			writeToFile(tmpName, dot);
			
			ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", "-o" + png, tmpName);
			Process p = pb.start();
			p.waitFor();

			InputStream is = p.getInputStream(), eis = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is), eisr = new InputStreamReader(eis);
			BufferedReader br = new BufferedReader(isr), ebr = new BufferedReader(eisr);
			String line;
			while ((line = br.readLine()) != null)
			{
				System.out.println(line);
			}
			while ((line = ebr.readLine()) != null)
			{
				System.out.println(line);
			}
		}
		catch (IOException | InterruptedException x)
		{
			throw new ScribbleException(x);
		}
		finally
		{
			tmp.delete();
		}
	}

  // Endpoint graphs are "inlined", so only a single graph is built (cf. projection output)
	private void buildEndointGraph(Job job, GProtocolName fullname, Role role) throws ScribbleException
	{
		JobContext jcontext = job.getContext();
		GProtocolDecl gpd = (GProtocolDecl) jcontext.getMainModule().getProtocolDecl(fullname.getSimpleName());
		if (gpd == null || !gpd.header.roledecls.getRoles().contains(role))
		{
			throw new RuntimeException("Bad FSM construction args: " + Arrays.toString(this.args.get(ArgFlag.FSM)));
		}
		job.buildGraph(fullname, role);
	}
	
	private Job newJob(MainContext mc)
	{
		//Job job = new Job(cjob);  // Doesn't work due to (recursive) maven dependencies
		//return new Job(mc.jUnit, mc.debug, mc.getParsedModules(), mc.main, mc.useOldWF, mc.noLiveness);
		return new Job(mc.debug, mc.getParsedModules(), mc.main, mc.useOldWF, mc.noLiveness);
	}

	private MainContext newMainContext()
	{
		//boolean jUnit = this.args.containsKey(ArgFlag.JUNIT);
		boolean debug = this.args.containsKey(ArgFlag.VERBOSE);
		boolean useOldWF = this.args.containsKey(ArgFlag.OLD_WF);
		boolean noLiveness = this.args.containsKey(ArgFlag.NO_LIVENESS);
		Path mainpath = CommandLine.parseMainPath(this.args.get(ArgFlag.MAIN)[0]);
		List<Path> impaths = this.args.containsKey(ArgFlag.PATH)
				? CommandLine.parseImportPaths(this.args.get(ArgFlag.PATH)[0])
				: Collections.emptyList();
		ResourceLocator locator = new DirectoryResourceLocator(impaths);
		//return new MainContext(jUnit, debug, locator, mainpath, useOldWF, noLiveness);
		return new MainContext(debug, locator, mainpath, useOldWF, noLiveness);
	}
	
	private static Path parseMainPath(String path)
	{
		return Paths.get(path);
	}
	
	private static List<Path> parseImportPaths(String paths)
	{
		return Arrays.stream(paths.split(File.pathSeparator)).map((s) -> Paths.get(s)).collect(Collectors.toList());
	}
	
	private static void writeToFile(String path, String text) throws ScribbleException
	{
		File file = new File(path);
		File parent = file.getParentFile();
		if (parent != null)
		{
			parent.mkdirs();
		}
		//try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8")))  // Doesn't create missing directories
		try (FileWriter writer = new FileWriter(file))
		{
			writer.write(text);
		}
		catch (IOException e)
		{
			throw new ScribbleException(e);
		}
	}
	
	private static GProtocolName checkGlobalProtocolArg(JobContext jcontext, String simpname)
	{
		GProtocolName simpgpn = new GProtocolName(simpname);
		ProtocolDecl<?> pd = jcontext.getMainModule().getProtocolDecl(simpgpn);
		if (pd == null || !pd.isGlobal())
		{
			throw new RuntimeException("Global protocol not found: " + simpname);
		}
		return new GProtocolName(jcontext.main, simpgpn);
	}
	
	private static Role checkRoleArg(JobContext jcontext, GProtocolName fullname, String rolename)
	{
		ProtocolDecl<?> pd = jcontext.getMainModule().getProtocolDecl(fullname.getSimpleName());
		Role role = new Role(rolename);
		if (!pd.header.roledecls.getRoles().contains(role))
		{
			throw new RuntimeException("Role not declared for " + fullname + ": " + role);
		}
		return role;
	}
}
