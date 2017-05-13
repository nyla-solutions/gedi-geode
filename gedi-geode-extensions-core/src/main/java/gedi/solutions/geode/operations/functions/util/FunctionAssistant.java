package gedi.solutions.geode.operations.functions.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class FunctionAssistant
{
	private static final String newline = System.getProperty("line.separator");
	/**
	 * 
	 * @param fileName  the full file path of which to read
	 * @return string data
	 * @throws IOException
	 */
	public static String readFile(String fileName, Charset charSet) throws IOException
	{
		if (fileName == null || fileName.length() == 0)
			return null;

		BufferedReader buffreader = null;
		File file = new File(fileName);
		try
		{

			buffreader = new BufferedReader(new InputStreamReader(
					new FileInputStream(file), charSet));

			String tmp = buffreader.readLine();
			if (tmp == null)
				return null;

			StringBuilder results = new StringBuilder(tmp);

		
			do
			{

				tmp = buffreader.readLine();

				if (tmp != null)
					results.append(newline).append(tmp);
			}
			while (tmp != null);

			return results.toString();
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
		finally
		{
			if (buffreader != null)
				try
				{
					buffreader.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

		}
	}// --------------------------------------------------
	/**
	 * 
	 * @param file the file to read
	 * @return binary file content
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static byte[] readBinaryFile(File file)
			throws FileNotFoundException, IOException
	{
		BufferedInputStream in = null;
		try
		{
			in = new BufferedInputStream(new FileInputStream(file));

			byte[] b = new byte[Long.valueOf(file.length()).intValue()];

			in.read(b);
			return b;
		}
		finally
		{
			if (in != null)
				try
				{
					in.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
		}
	}// --------------------------------

	
	public static String stackTrace(Throwable t)
	{
		if (t == null)
		{
			return "Debugger.stackTrace(null) NULL EXCEPTION (NO TRACE AVAILABLE)!!";
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	} // -----------------------------------------
}
