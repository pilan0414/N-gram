  	package whu.myw;
    
import java.io.File;
import java.util.ArrayList;
import java.util.List;
    



    import edu.berkeley.nlp.lm.ConfigOptions;
import edu.berkeley.nlp.lm.StringWordIndexer;
import edu.berkeley.nlp.lm.io.ArpaLmReader;
import edu.berkeley.nlp.lm.io.LmReaders;
    
    public class Train 
    {
         
    	public static void main(String[] args) 
        {
      
            String inputfile = "document\\segmented.txt";						
            String outputfile2 = "document\\2-gram.arpa";
            String outputfile3 = "document\\3-gram.arpa";
                
               
            final List<String> inputFiles = new ArrayList<String>();
            inputFiles.add(inputfile);
           
            final StringWordIndexer wordIndexer = new StringWordIndexer();
            wordIndexer.setStartSymbol(ArpaLmReader.START_SYMBOL);
            wordIndexer.setEndSymbol(ArpaLmReader.END_SYMBOL);
            wordIndexer.setUnkSymbol(ArpaLmReader.UNK_SYMBOL);
            LmReaders.createKneserNeyLmFromTextFiles(inputFiles, wordIndexer, 2, new File(outputfile2), new ConfigOptions());		//2-gram
            LmReaders.createKneserNeyLmFromTextFiles(inputFiles, wordIndexer, 3, new File(outputfile3), new ConfigOptions());		//3-gram          

            System.out.println("训练完成");
        }
    
    }