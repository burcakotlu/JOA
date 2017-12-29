/**
 * 
 */
package auxiliary;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author burcak 
 * @date Dec 12, 2017
 * @project JOA
 */
public class FileOperations {
	
	public static FileReader createFileReader( String directoryNameandfileName) throws IOException {

		return new FileReader( directoryNameandfileName);
	}

	
	public static FileWriter createFileWriter( String path) throws IOException {

		File f = new File( path);

		FileWriter fileWriter = null;

		if( f.isDirectory() && !f.exists())
			f.mkdirs();
		else if( !f.isDirectory() && !f.getParentFile().exists())
			f.getParentFile().mkdirs();

		if( !f.isDirectory() && f.exists())
			f.delete();

		fileWriter = new FileWriter( f, false);

		return fileWriter;
	}

	
	public static FileWriter createFileWriter( String path, boolean appendMode) throws IOException {

		if( !appendMode)
			return createFileWriter( path);

		File f = new File( path);
		FileWriter fileWriter = null;

		if( f.isDirectory() && !f.exists())
			f.mkdirs();
		else if( !f.isDirectory() && !f.getParentFile().exists())
			f.getParentFile().mkdirs();

		fileWriter = new FileWriter( path, appendMode);

		return fileWriter;
	}
	
	
	public static boolean checkIntervalSetFileNamesStringArray(String[] intervalSetFilesArray) {
		
		if (intervalSetFilesArray == null ) {
			System.out.println("Interval Set File is null");
	        return false;
	    }
		
		if (intervalSetFilesArray.length == 0 ) {
			System.out.println("Interval Set Files is empty, meaning it has no element");
	        return false; 
	    }
		
	    for ( String s : intervalSetFilesArray ) {
	        if (s == null) {
	        	System.out.println("Interval Set Files contain null element");
	            return false;
	        }
	        if (s.length() == 0) {
	        	System.out.println("Interval Set Files contain empty string");
	            return false; 
	        }
	        // or
	        if (s.isEmpty()) {
	        	System.out.println("Interval Set Files contain empty string");
	            return false;
	        }
	    }

	    return true;		
		
	}
          

}
