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
    	
    	//public float score2;							//����2-gram�÷�
    	public float score3;    						//����3-gram�÷�
        public List<String> wrong = new ArrayList<String>();
    	
        public void CheckSentence() throws Exception 				//�жϾ����Ƿ����
        {				
        	
            //String inputfile2 = "document\\2-gram.arpa";
            String inputfile3 = "document\\3-gram.arpa";

            //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);				//2-gram����ѹ��
            ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram����ѹ��
            
            
   		 	String result= "document\\result.txt";						//������ڼ���ԭʼ���ݣ��������
   		 	String temp= "document\\temp.txt";							//�ȴ�ŷִʺ���ӣ��ٴ�ſ��ܳ���Ĵ�
            
            
   		 	WordSegmenter.segWithStopWords(new File(result), new File(temp));										//�ִʣ�����ԭʼ����		 	

   		 	
   		 	Path path = Paths.get(temp);					//��ȡ�ִʺ�����
   		 	byte[] data = Files.readAllBytes(path);
   		 	String raw = new String(data, "utf-8");
   		 	
   		    String sentence = "";									//ȥ����(���ӵ����һ�����Ŵ����Ϊһ������)
   		    for (int i = 0; i < raw.length(); i++) 
   		    {
   		    	if (raw.charAt(i) != '\n') {
   		    		sentence += raw.charAt(i);
   		    	}
   		    }			
   		 	
            FileOutputStream writerStream = new FileOutputStream(result);            						//�ִʺ�д��result
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            writer.write(sentence);
            writer.close(); 
   		 	
            System.out.println("��������:"+'\n'+raw);
            

             
            String [] words = sentence.split(" ");					//תΪlist
            List<String> list = new ArrayList<String>();
            for(String word : words)
            {
               list.add(word);
            }
            
            /*System.out.println("����:");						//����
            score2 = model2.scoreSentence(list);
            System.out.println("2-gram:"+score2);*/
            
            score3 = model3.scoreSentence(list);
            System.out.println("3-gram:"+score3);
        }

        public void CheckPosition() throws IOException 				//�жϳ���λ��
        {				
            //String inputfile2 = "document\\2-gram.arpa";
            String inputfile3 = "document\\3-gram.arpa";

            //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);			//2-gram����ѹ��
            ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram����ѹ��
            
   		 	String temp= "document\\temp.txt";	
   		 	Path path = Paths.get(temp);							//��ȡ�ִʺ�����
   		 	byte[] data = Files.readAllBytes(path);
   		 	String raw = new String(data, "utf-8");
   		 	   
   		    String sentence = "";									//ȥ����(���ӵ����һ�����Ŵ����Ϊһ������)
   		    for (int i = 0; i < raw.length(); i++) 
   		    {
   		    	if (raw.charAt(i) != '\n') {
   		    		sentence += raw.charAt(i);
   		    	}
   		    }	
   		    
            String [] words = sentence.split(" ");					
            int length=words.length;								//���ӵĴ���
            
            
            int [] symbol=new int[length+3];					//����һ�����ʾ���ӷִʺ�ÿ���ʺ��ֵ�������0Ϊ���֣�1Ϊ����
            for(int i=0;i<length+3;i++)
            {
            	if(i>length-1)
            	{
            		symbol[i]=-1;
            	}
            	else if(words[i].length()==1)		//����
            	{
            		symbol[i]=0;
            	}
            	else if(words[i].length()>1)			//����
            	{
            		symbol[i]=1;
            	}

            }
            
            int [] error=new int[length+3];					//����һ�����ʾ���ӷִʺ�Ĵ��Ƿ�Ӧ���о�����0�������1�����
           
            error[0]=1;									//3-gram���ľ����ԣ���������������1,��2�����		
            error[1]=1;
            
            
            error[length+2]=-1;
            error[length+1]=-1;
            error[length]=-1;

        	
            /*System.out.println('\n'+"2-gram���:"); 					//2-gram���
            for(int i=0;i<length-1;i++)
            {
                List<String> list = new ArrayList<String>();
                list.add(words[i]);
                list.add(words[i+1]);
                
            	float s2 = model2.getLogProb(list);        
                System.out.println("����:"+words[i+1]+'\n'+"�÷�:"+s2);
                if(s2<-5)
                {
                	wrong.add(words[i+1]);             	
                }
            }*/
            
            System.out.println('\n'+"3-gram���:");					//3-gram���
            for(int i=0;i<length-2;i++)
            {
                List<String> list = new ArrayList<String>();
                list.add(words[i]);
                list.add(words[i+1]);
                list.add(words[i+2]);
                
            	float s3 = model3.getLogProb(list);        
                System.out.println("����:"+words[i+2]+'\n'+"�÷�:"+s3);
                if(s3<-3)
                {
                    error[i+2]=1;
                }
                else
                {
                    error[i+2]=0;                	
                }
            }       

            	
            for(int i=0;i<length;i++)				//�������Ĵʼ����ѡ����
            {
            	if(error[i]==1&&symbol[i]==1)		//�������Ķ���ֱ�ӷ���
            	{
            		wrong.add(words[i]);
            	}
            	else if(error[i]==1&&symbol[i]==0)							//�����ĵ��ֽ����жϺ��Ƿ���������ĵ��֣������ڣ��ϲ��ɶ��֣�����
            	{
            		if(error[i+1]==1&&symbol[i+1]==0)				//�����ĵ�һ��Ϊ�����������
            		{
            			if(error[i+2]==1&&symbol[i+2]==0)			//�����ĵڶ���ҲΪ�����������
            			{
                			if(error[i+3]==1&&symbol[i+3]==0)		//�����ĵ�����ҲΪ�����������
                			{
                				wrong.add(words[i]+words[i+1]+words[i+2]+words[i+3]);
                				i=i+3;
                			}
                			else					//������������
                			{
                    			wrong.add(words[i]+words[i+1]+words[i+2]);
                				i=i+2;
                			}
            			}
            			else						//������������
            			{
                			wrong.add(words[i]+words[i+1]);
            				i=i+1;
            			}
            		}
            		else							//��һ������
            		{
            			wrong.add(words[i]);
            		}
            	}
            }
                      
        	System.out.println("���ܳ������:"+wrong+'\n');         
        }
             
        public static void main(String[] args) throws Exception 
        {
       
        	Check c =new Check();
        	c.CheckSentence();
        	        	
        	if(c.score3<-10)
        	{
                System.out.println('\n'+"���ӳ�����Ҫ����!"+'\n');
                c.CheckPosition();
                
        	}
        	else {
        		System.out.println('\n'+"������ȷ!");
        	}
        	
   		 	String temp= "document\\temp.txt";							//�ȴ�ŷִʺ���ӣ��ٴ�ſ��ܳ���Ĵ�
            FileOutputStream writerStream = new FileOutputStream(temp); 
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
            for(String l:c.wrong)
            {
            writer.write(l+" ");
            }
            writer.close(); 
           
        }
    }