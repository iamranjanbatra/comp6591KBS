package codalog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import codalog.interfaces.QueryInterface;
import codalog.output.DefaultQueryOutput;
import codalog.output.OutputUtils;


public class CoDalog {

	public static void main(String... args) {
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
            System.out.println("CoDalog: Concordia Datalog Engine\n Press \n 1:Load File \n 2:Parsing Evaluation \n 3:Display Rules and Facts\n 4:Query \n 5:Exit");
            
            Scanner operationType = new Scanner(System.in);
            String operationString = operationType.next();
            int operation = Integer.parseInt(operationString);
            if(operation ==1 ){
            	System.out.println("Enter the name of the file");
            }
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            
            List<String> history = new LinkedList<>();
            
            while(true) {
                try {
                    String line = buffer.readLine();
                    if(line == null) {
                        break; // EOF
                    }
                    line = line.trim();
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    if(!tokenizer.hasMoreTokens())
                    	continue;
                    String commandType = tokenizer.nextToken().toLowerCase();
                    int command = Integer.parseInt(commandType);
                    // Intercept some special commands
                    
                    if(command==5) {
                    	System.out.println("breaking ");
                        System.exit(0);
                        } else if(command==3) {			// same as dump
                        System.out.println(jatalog);
						history.add(line);
						continue;
                    } else if (operation ==1){
                    	if(command==1) {
						if(!tokenizer.hasMoreTokens()) {
							System.err.println("error: filename expected");
							continue;
						}
						
						QueryInterface qo = new DefaultQueryOutput();
						
						try (Reader reader = new BufferedReader(new FileReader("R:/Workspace Latest/codalog/src/codalog/kbsfile.cdl"))) {
							jatalog.executeAll(reader, qo);
						}
                        System.out.println("File Loaded Successfully"); // exception not thrown
						history.add(line);
						continue;
					} }else if(command==2) {
                        jatalog.validate();
                        System.out.println("OK."); // exception not thrown
                        history.add(line);
                        continue;
                    } 
                    
                    long start = System.currentTimeMillis();                    
                    Collection<Map<String, String>> answers = jatalog.executeAll(line);
                    double elapsed = (System.currentTimeMillis() - start)/1000.0;
                    
					if (answers != null) {
						// line contained a query with an answer.
						String result = OutputUtils.answersToString(answers);
						System.out.println(result);
	                    
	                    }        
                    history.add(line);

                } catch (CodalogException | IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
