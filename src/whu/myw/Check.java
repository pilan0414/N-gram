  	package whu.myw;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
    




    import org.apdplat.word.WordSegmenter;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.io.LmReaders;
    
    public class Check 
    {
    	
    	//public float score2;							//句子2-gram得分
    	public float score3;    						//句子3-gram得分
        public List<String> wrong = new ArrayList<String>();
    	
        public void CheckSentence() throws Exception 				//判断句子是否出错
        {				
        	
            //String inputfile2 = "document\\2-gram.arpa";
            String inputfile3 = "document\\3-gram.arpa";

            //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);				//2-gram，非压缩
            ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram，非压缩
            
            
   		 	String result= "document\\result.txt";						//存放用于检错的原始数据，与纠错结果
   		 	String temp= "document\\temp.txt";							//先存放分词后句子，再存放可能出错的词
            
            
   		 	WordSegmenter.segWithStopWords(new File(result), new File(temp));										//分词，检错的原始数据		 	

   		 	
   		 	Path path = Paths.get(temp);					//读取分词后数据
   		 	byte[] data = Files.readAllBytes(path);
   		 	String raw = new String(data, "utf-8");
   		 	
   		    String sentence = "";									//去换行(句子的最后一个符号处理后为一个换行)
   		    for (int i = 0; i < raw.length(); i++) 
   		    {
   		    	if (raw.charAt(i) != '\n') {
   		    		sentence += raw.charAt(i);
   		    	}
   		    }			
   		 	
            FileOutputStream writerStream = new FileOutputStream(result);            						//分词后写回result
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            writer.write(sentence);
            writer.close(); 
   		 	
            System.out.println("待检错句子:"+'\n'+raw);
            

             
            String [] words = sentence.split(" ");					//转为list
            List<String> list = new ArrayList<String>();
            for(String word : words)
            {
               list.add(word);
            }
            
            /*System.out.println("评分:");						//评分
            score2 = model2.scoreSentence(list);
            System.out.println("2-gram:"+score2);*/
            
            score3 = model3.scoreSentence(list);
            System.out.println("3-gram:"+score3);
        }

        public void CheckPosition() throws IOException 				//判断出错位置
        {				
            //String inputfile2 = "document\\2-gram.arpa";
            String inputfile3 = "document\\3-gram.arpa";

            //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);			//2-gram，非压缩
            ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram，非压缩
            
   		 	String temp= "document\\temp.txt";	
   		 	Path path = Paths.get(temp);							//读取分词后数据
   		 	byte[] data = Files.readAllBytes(path);
   		 	String raw = new String(data, "utf-8");
   		 	   
   		    String sentence = "";									//去换行(句子的最后一个符号处理后为一个换行)
   		    for (int i = 0; i < raw.length(); i++) 
   		    {
   		    	if (raw.charAt(i) != '\n') {
   		    		sentence += raw.charAt(i);
   		    	}
   		    }	
   		    
            String [] words = sentence.split(" ");					
            int length=words.length;								//句子的词数
            
            
            int [] symbol=new int[length+3];					//定义一数组表示句子分词后每个词含字的数量，0为单字，1为多字
            for(int i=0;i<length+3;i++)
            {
            	if(i>length-1)
            	{
            		symbol[i]=-1;
            	}
            	else if(words[i].length()==1)		//单字
            	{
            		symbol[i]=0;
            	}
            	else if(words[i].length()>1)			//多字
            	{
            		symbol[i]=1;
            	}

            }
            
            int [] error=new int[length+3];					//定义一数组表示句子分词后的词是否应进行纠错处理，0不需纠错，1需纠错
           
            error[0]=1;									//3-gram查错的局限性，保险起见将任务词1,词2需纠错		
            error[1]=1;
            
            
            error[length+2]=-1;
            error[length+1]=-1;
            error[length]=-1;

        	
            /*System.out.println('\n'+"2-gram检错:"); 					//2-gram检错
            for(int i=0;i<length-1;i++)
            {
                List<String> list = new ArrayList<String>();
                list.add(words[i]);
                list.add(words[i+1]);
                
            	float s2 = model2.getLogProb(list);        
                System.out.println("词语:"+words[i+1]+'\n'+"得分:"+s2);
                if(s2<-5)
                {
                	wrong.add(words[i+1]);             	
                }
            }*/
            
            System.out.println('\n'+"3-gram检错:");					//3-gram检错
            for(int i=0;i<length-2;i++)
            {
                List<String> list = new ArrayList<String>();
                list.add(words[i]);
                list.add(words[i+1]);
                list.add(words[i+2]);
                
            	float s3 = model3.getLogProb(list);        
                System.out.println("词语:"+words[i+2]+'\n'+"得分:"+s3);
                if(s3<-3)
                {
                    error[i+2]=1;
                }
                else
                {
                    error[i+2]=0;                	
                }
            }       

            	
            for(int i=0;i<length;i++)				//将需纠错的词加入候选集中
            {
            	if(error[i]==1&&symbol[i]==1)		//将需纠错的多字直接放入
            	{
            		wrong.add(words[i]);
            	}
            	else if(error[i]==1&&symbol[i]==0)							//需纠错的单字进行判断后（是否存在连续的单字，若存在，合并成多字）放入
            	{
            		if(error[i+1]==1&&symbol[i+1]==0)				//若其后的第一个为单字且需纠错
            		{
            			if(error[i+2]==1&&symbol[i+2]==0)			//若其后的第二个也为单字且需纠错
            			{
                			if(error[i+3]==1&&symbol[i+3]==0)		//若其后的第三个也为单字且需纠错
                			{
                				wrong.add(words[i]+words[i+1]+words[i+2]+words[i+3]);
                				i=i+3;
                			}
                			else					//三个单字连续
                			{
                    			wrong.add(words[i]+words[i+1]+words[i+2]);
                				i=i+2;
                			}
            			}
            			else						//二个单字连续
            			{
                			wrong.add(words[i]+words[i+1]);
            				i=i+1;
            			}
            		}
            		else							//仅一个单字
            		{
            			wrong.add(words[i]);
            		}
            	}
            }
                      
        	System.out.println("可能出错词语:"+wrong+'\n');         
        }
             
        public static void main(String[] args) throws Exception 
        {
       
        	Check c =new Check();
        	c.CheckSentence();
        	        	
        	if(c.score3<-10)
        	{
                System.out.println('\n'+"句子出错，需要改正!"+'\n');
                c.CheckPosition();
                
        	}
        	else {
        		System.out.println('\n'+"句子正确!");
        	}
        	
   		 	String temp= "document\\temp.txt";							//先存放分词后句子，再存放可能出错的词
            FileOutputStream writerStream = new FileOutputStream(temp); 
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            for(String l:c.wrong)
            {
            writer.write(l+" ");
            }
            writer.close(); 
           
        }
    }