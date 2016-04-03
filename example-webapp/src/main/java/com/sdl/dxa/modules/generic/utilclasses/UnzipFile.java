package com.sdl.dxa.modules.generic.utilclasses;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class will allow you to perform basic pkzip compatible data uncompression.
 * The basic steps are:
 * 1. get the zipped input stream
 * 2. get the zipped entries
 * 3. prepare the uncompressed output stream
 * 4. read source zipped data and write to uncompressed stream 
 * 5. close the source and target stream
 * Command syntax
 * Usage: java utilClassesV4.CUnzip [-v] zipfile where option includes:
 * -v List zip file contents
 *  NOTE:  This version expands entries w/ directory structures.
 */
public class UnzipFile {
    private static Logger logger= LoggerFactory.getLogger(UnzipFile.class);

	protected String zipFileName;
	protected boolean showListingFlag = false;
	protected final int DATA_BLOCK_SIZE = 2048;
	private String strZipPath;
	private String strOutputPath;
	private String zipFileNameWithoutExtension;

    /**
	 * The constructor is used to create a new utilClassesV4.CUnzip object based on the command line arguments.
	 * Command syntax
	 * Usage: java utilClassesV4.CUnzip [-v] zipfile where option includes:
	 * -v List zip file contents
	 *  
	 * @param args a string array of command line arguments.
	 */
	public UnzipFile(String args[])
	{
		parseCommandLineArgs(args);
		if (zipFileName == null)
			System.exit(1);
		setZipPath("");
		setOutputPath("");
	}

	/**
	 * The constructor is used to create a new utilClassesV4.CUnzip object based on the command line arguments.
	 * Command syntax
	 * Usage: java utilClassesV4.CUnzip [-v] zipfile where option includes:
	 * -v List zip file contents
	 *
	 * @param args          a string array of command line arguments.
	 * @param strOutputPath a string representing the output path to extract the zip.
	 */
	public UnzipFile(String args[], String strOutputPath)
	{
		parseCommandLineArgs(args);
		if (zipFileName == null)
			System.exit(1);
		setZipPath("");
		setOutputPath(strOutputPath);
	}

	/**
	 * The constructor is used to create a new utilClassesV4.CUnzip object based on the command line arguments.
	 * Command syntax
	 * Usage: java utilClassesV4.CUnzip [-v] zipfile where option includes:
	 * -v List zip file contents
	 *
	 * @param args          a string array of command line arguments.
	 * @param strZipPath    a string representing the path where the zip file is stored.
	 * @param strOutputPath a string representing the output path to extract the zip.
	 */
	public UnzipFile(String args[], String strZipPath, String strOutputPath, PrintWriter objOut)
	{
		parseCommandLineArgs(args);
		if (zipFileName == null)
			System.exit(1);
		setZipPath(strZipPath);
		setOutputPath(strOutputPath);
		unzipIt();
	}

	/**
	 * Parses the command line arguments. <br>
	 *
	 * @param args command line args as an array of strings.
	 */
	protected void parseCommandLineArgs(String args[])
	{
		int length = args.length;
		if (length == 0)
			System.exit(1);
		if (length == 1)
		{
			if (!(args[0].equals("-v")))
			{
				zipFileName = args[0];
				zipFileNameWithoutExtension = zipFileName.substring(0, zipFileName.indexOf(".zip"));
				return;
			}
			else
			{
				System.exit(1);
			}
		}
		else
		{
			zipFileName = args[1];
			zipFileNameWithoutExtension = zipFileName.substring(0, zipFileName.indexOf(".zip"));
			showListingFlag = true;
		}
	}

    /**
     * Returns the list of filenames contained in the zipped file. <br>
     *
     * @param zipFileName name of the zipped file.
     */
    public String[] getContentsList(String zipFileName)
    {
        ZipFile zf;
        ZipEntry theEntry;
        try
        {
            zf = new ZipFile(strZipPath + zipFileName);
            String fileNames[] = new String[zf.size()];
            int iCount = 0;
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements())
            {
                theEntry = (ZipEntry) entries.nextElement();
                fileNames[iCount++] = theEntry.getName();
            }
            return fileNames;
        }
        catch (Exception exc)
        {
            logger.error(exc.getMessage(),exc);
            return null;
        }
    }

	/**
	 * Lists the contents of the zipped file. <br>
	 *
	 * @param zipFileName name of the zipped file.
	 */
	protected void listContents(String zipFileName)
	{
		ZipFile zf;
		ZipEntry theEntry;
		try
		{
			zf = new ZipFile(zipFileName);
			Enumeration<? extends ZipEntry> entries = zf.entries();
			while (entries.hasMoreElements())
			{
				theEntry = (ZipEntry) entries.nextElement();
			}

		}
		catch (Exception exc)
		{
            logger.error(exc.getMessage(),exc);
		}
	}

	/**
	 * Performs the different switches for the utilClassesV4.CUnzip object.
	 * Determines if it should list contents or extract files.
	 */
	public void unzipIt()
	{
		if (showListingFlag)
			listContents(zipFileName);
		else
			extractFiles(zipFileName);
	}

	/**
	 * Extracts the files contained in the zipped file.
	 * The basic steps are:
	 * 1. get the zipped input stream
	 * 2. get the zipped entries
	 * 3. prepare the uncompressed output stream
	 * 4. read source zipped data and write to uncompressed stream
	 * 5. close the source and target stream
	 *
	 * @param theZipFileName name of the zipped archive
	 */
	protected void extractFiles(String theZipFileName)
	{
		FileInputStream fis = null;
		ZipInputStream sourceZipStream;
		FileOutputStream fos = null;
		BufferedOutputStream targetStream;
		ZipEntry theEntry = null;
		String entryName = null;
		try
		{
			fis = new FileInputStream(strZipPath + theZipFileName);
			sourceZipStream = new ZipInputStream(fis);
			String strDirectoryPath = "";
			while ((theEntry = sourceZipStream.getNextEntry()) != null)
			{
				entryName = theEntry.getName();
				strDirectoryPath = "";
				StringTokenizer st = new StringTokenizer(entryName, "/");
				if (st.countTokens() == 1)
					strDirectoryPath = strOutputPath + zipFileNameWithoutExtension + "/";
				else
					strDirectoryPath = strOutputPath;
				try
				{
					fos = new FileOutputStream(strDirectoryPath + entryName);
				}
				catch (FileNotFoundException exc)
				{
					buildDirectory(strDirectoryPath + entryName);
					fos = new FileOutputStream(strDirectoryPath + entryName);
				}
				targetStream = new BufferedOutputStream(fos, DATA_BLOCK_SIZE);
				int byteCount;
				byte data[] = new byte[DATA_BLOCK_SIZE];
				while ((byteCount = sourceZipStream.read(data, 0, DATA_BLOCK_SIZE)) != -1)
				{
					targetStream.write(data, 0, byteCount);
				}
				targetStream.flush();
				targetStream.close();
			}
			sourceZipStream.close();
            fis.close();
		}
		catch (IOException exc)
		{
            logger.error(exc.getMessage(),exc);
		}
	}

	/**
	 * Creates the directory structure
	 */
	protected void buildDirectory(String entryName) throws IOException
	{
		StringTokenizer st = new StringTokenizer(entryName, "/");
		int levels = st.countTokens() - 1;
		StringBuffer directory = new StringBuffer();
		File newDir;
		for (int i = 0; i < levels; i++)
		{
			directory.append(st.nextToken() + "/");
		}
		newDir = new File(directory.toString());
		newDir.mkdirs();
	}

/*
	public static void main(String args[])
	{
		CSettings settings = new CSettings();
		utilClassesV4.CUnzip myApp = new utilClassesV4.CUnzip(args, settings.strFinancialsLocation);
		myApp.unzipIt();
	}
*/
	private void setZipPath(String strZipPath)
	{
		if (strZipPath != null)
			this.strZipPath = strZipPath + "/";
	}

	private void setOutputPath(String strOutputPath)
	{
		if (strOutputPath != null)
			this.strOutputPath = strOutputPath + "/";
	}
}
