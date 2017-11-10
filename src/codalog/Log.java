package codalog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Date;
public class Log {

    private PrintStream out;
    private File file;

    /**
     * Instantiate a Log for a given name;
     *
     * @param id name of log
     */
    public Log() 
	    {
	    try 
	    	{
	    	file = new File("log\\P.err");
	        if (file.createNewFile()){System.out.println("Err file was created!");}
	        out = new PrintStream(new FileOutputStream("log\\P.err"));
	    	} 
	    catch (Exception e) 
	    	{
	        throw new RuntimeException(e);
	    	}
	    }

    /**
     * Log an entry with the name and date
     *
     * @param content content to log
     */
    public void log(String content) 
    	{
        Date date = new Date();
        out.println("[ERROR]: " + content + "\n");
        System.out.println("[ERROR]: " + content);
    	}

}
