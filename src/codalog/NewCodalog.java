package codalog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

import codalog.interfaces.QueryInterface;
import codalog.output.DefaultQueryOutput;
import codalog.CodalogException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;
public class NewCodalog {
	
			public static BufferedReader buffer;
			public static Scanner inputDataFile;
			public static String strDataFile;
			public static Scanner bufferScan;
			public static BufferedReader getCommand(){
				
				return buffer;
			}
			public static void main(String... args) throws CodalogException{
	    	Log log;
	        if(args.length > 0) {
	            // Read input from a file...
	            try {
	                DatalogInterpreter jatalog = new DatalogInterpreter();
	                QueryInterface qo = new DefaultQueryOutput();
	                for (String arg : args) {
	                    try (Reader reader = new BufferedReader(new FileReader(arg))) {
	                        jatalog.executeAll(reader, qo);
	                    }
	                }                
	            } catch (CodalogException | IOException e) {
	                e.printStackTrace();
	            }
	        } else {
	            // Get input from command line
	            DatalogInterpreter jatalog = new DatalogInterpreter();
	            System.out.println("Neew CoDalog: Concordia Datalog Engine\n Press \n 1:Load File \n 2:Parsing Evaluation \n 3:Display Rules and Facts\n 4:Query \n 5:Exit");
	        

            Scanner operationType = new Scanner(System.in);
            String operationString = operationType.next();
            int operation = Integer.parseInt(operationString);
        
            if(operation == 1){
        		System.out.println("Enter Name of the File:");
        		inputDataFile = new Scanner(System.in);
        		strDataFile = inputDataFile.next() + ".cdl";
        	}
          
            while(true){
            	
            if (operation ==1){
            	
				QueryInterface qo = new DefaultQueryOutput();
				
				File dataFile = new File("src/" + strDataFile);
				
				try (Reader reader = new BufferedReader(new FileReader(dataFile))){
					jatalog.executeAll(reader, qo);
					System.out.println("Operation Successfully"); 
				
				}
				catch(IOException io){
					System.out.println("File Not Found");
				}
            }
	        
            else if(operation == 2){
            	
            	 jatalog.validate();
            	 System.out.println("File Successfully Parsed");
            }
            else if(operation==3){
            	
            }
            else if(operation ==6){
            	System.out.println("Next Command");
            	
            	
            	continue;
            }
            else{
            	System.out.println("Hie");
            	continue;
            }
            
            Scanner nextOperation = new Scanner(System.in);
            String strOperation = nextOperation.next();
            operation = Integer.parseInt(strOperation);
            
          }
            
        }
	}
}