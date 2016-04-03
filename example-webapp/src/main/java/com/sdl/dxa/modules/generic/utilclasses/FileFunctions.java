package com.sdl.dxa.modules.generic.utilclasses;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileFunctions {
	private static Logger logger = LoggerFactory.getLogger(FileFunctions.class);

	public static void copyFile(java.io.File srcFile, String strTrgtFile)
    {
        try
        {
            java.io.File outputFile = new java.io.File(strTrgtFile);
            if (!outputFile.exists())
                outputFile.createNewFile();
            java.io.FileInputStream in = new java.io.FileInputStream(srcFile);
            java.io.FileOutputStream out = new java.io.FileOutputStream(outputFile);

            byte[] buf = new byte[4096];
            int i = 0;
            while ((i = in.read(buf)) != -1)
            {
                out.write(buf, 0, i);
                out.flush();
            }
            in.close();
            out.close();
            if(strTrgtFile.indexOf(srcFile.getName()) == -1)
                srcFile.delete();
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
    }

    public static boolean createZipArchive(HttpSession session, String srcFolder, long dTime) {
        try {
            if(srcFolder.indexOf(":") == -1)
                srcFolder = session.getServletContext().getRealPath(srcFolder);
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(new File(srcFolder + "-" + dTime + ".zip"));
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            int BUFFER = 2048;
            byte data[] = new byte[BUFFER];
            File subDir = new File(srcFolder);
            String files[];
            String subdirList[] = subDir.list();
            for(int i = 0; i < subdirList.length; i++) {
                // get a list of files from current directory
                File f = new File(srcFolder + "/" + subdirList[i]);
                if(f.isDirectory()) {
                    files = f.list();
                    for (int j = 0; j < files.length; j++) {
                        FileInputStream fi = new FileInputStream(srcFolder  + "/" + subdirList[i] + "/" + files[j]);
                        origin = new BufferedInputStream(fi, BUFFER);
                        ZipEntry entry = new ZipEntry(subdirList[i] + "/" + files[j]);
                        out.putNextEntry(entry);
                        int count;
                        while ((count = origin.read(data, 0, BUFFER)) != -1) {
                            out.write(data, 0, count);
                            out.flush();
                        }
                    }
                } else  {
                    FileInputStream fi = new FileInputStream(f);
                    origin = new BufferedInputStream(fi, BUFFER);
                    ZipEntry entry = new ZipEntry(subdirList[i]);
                    out.putNextEntry(entry);
                    int count;
                    while ((count = origin.read(data, 0, BUFFER)) != -1) {
                        out.write(data, 0, count);
                        out.flush();
                    }
                }
            }
            origin.close();
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
        return true;
    }
}
