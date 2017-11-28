package codalog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;

import codalog.interfaces.QueryInterface;
import codalog.output.*;

import codalog.CodalogException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
public class NewCodalog {
	
			public static BufferedReader buffer;
			public static Scanner inputDataFile;
			public static String strDataFile;
			public static Scanner bufferScan;
			public static BufferedReader getCommand(){
				
				return buffer;
			}
			public static void main(String... args) throws CodalogException, IOException{
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
	            DatalogInterpreter codalog = new DatalogInterpreter();
	            System.out.println("Codalog Engine\n Press \n 1:Load File \n 2:Parsing Evaluation \n 3:Display Rules and Facts\n 4:Query \n 5:Exit");
	        

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
					codalog.executeAll(reader, qo);
					System.out.println("Operation Successfull"); 
		            System.out.println("Codalog Engine\n Press \n 1:Load File \n 2:Parsing Evaluation \n 3:Display Rules and Facts\n 4:Query \n 5:Exit");
				}
				catch(IOException io){
					System.out.println("File Not Found");
				}
            }
	        
            else if(operation == 2){
            	
            	 codalog.validate();
            	 System.out.println("File Successfully Parsed");
		         System.out.println("Codalog Engine\n Press \n 1:Load File \n 2:Parsing Evaluation \n 3:Display Rules and Facts\n 4:Query \n 5:Exit");

            }
            else if(operation==3){
                System.out.println(codalog);
	            System.out.println("Codalog Engine\n Press \n 1:Load File \n 2:Parsing Evaluation \n 3:Display Rules and Facts\n 4:Query \n 5:Exit");

            }
            else if(operation==4){
            	System.out.println("Please choose between naive or seminaive evualuation.");
				Scanner sc = new Scanner(System.in);
				String eval = sc.nextLine();
				if(eval.equals("naive")){codalog.setIsNaive(true);}
				else if(eval.equals("seminaive")){codalog.setIsNaive(false);}
				else{codalog.setIsNaive(false);}
				System.out.println("Please enter a query: ");
				BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
				String line = reader.readLine();
				System.out.println("Query : " + line);
				long start = System.currentTimeMillis();
                Collection<Map<String, String>> executed = codalog.executeAll(line);
                double runTime = (System.currentTimeMillis() - start)/1000.0;
                
				if (executed != null) 
				{
					String result = OutputUtils.answersToString(executed);
					System.out.println("Result is : " + result);
                    System.out.println(String.format("%.3fs Running time: ", runTime));
				}        
	            System.out.println("Codalog Engine\n Press \n 1:Load File \n 2:Parsing Evaluation \n 3:Display Rules and Facts\n 4:Query \n 5:Exit");

            }
            else if(operation ==5){
            	System.out.println("CoDalog Program Successfully Closed");
            	break; }
            else{
            	continue;
            }
            
            Scanner nextOperation = new Scanner(System.in);
            String strOperation = nextOperation.next();
            operation = Integer.parseInt(strOperation);
            
          }
            
        }
	}
}