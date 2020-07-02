  	package whu.myw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;

import edu.berkeley.nlp.lm.ArrayEncodedProbBackoffLm;
import edu.berkeley.nlp.lm.io.LmReaders;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


public class Correct  
{	
	
	public HashMap<String,String> map_1 = new HashMap<>();								//�ֵ�ƴ��
	//public HashMap<String,String> map_n = new HashMap<>();							//�ʵ�ƴ��
	public String set_pinyin[]=new String[62];											//ģ����,62��
	
	public Set<String> getKeys(HashMap<String, String> map, String value)  				//ͨ��value��key����
	{
		Set<String> keys = new HashSet<>();
		for (Map.Entry<String,String> m : map.entrySet()) 
		{
			if (m.getValue().equals(value)) 
			{
				keys.add(m.getKey());
			}
		}
		return keys;
	}

	public float getEditDistance(String str, String target) 					//�༭����
	{	
		int d[][]; 				// ����
		int n = str.length();
		int m = target.length();
		int i; 					// ����str��
		int j; 					// ����target��
		char ch1; 				// str��
		char ch2; 				// target��
		int temp; 				// ��¼��ͬ�ַ�,��ĳ������λ��ֵ������,����0����1
		if (n == 0 || m == 0) 
		{
			return 0;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) 
		{ 
			d[i][0] = i;									// ��ʼ����һ��
		}

		for (j = 0; j <= m; j++) 
		{ 
			d[0][j] = j;									// ��ʼ����һ��
		}

		for (i = 1; i <= n; i++) 
		{ 
			ch1 = str.charAt(i - 1);						// ����str
			for (j = 1; j <= m; j++) 						// ȥƥ��target
			{
				ch2 = target.charAt(j - 1);
				if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) 
				{
					temp = 0;
				} else 
				{
					temp = 1;
				}
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + temp);	// ���+1,�ϱ�+1, ���Ͻ�+tempȡ��С
			}
		}

		return (1 - (float) d[n][m] / Math.max(str.length(), target.length())) * 100F;
	}
	
	public float getSimilarity(String str, String target) throws 				//ģ����
IOException
	{
		String str1=" "+str+" ";
		String str2=" "+target+" ";
        for(int i=0;i<62;i++)										//һ�ζ�һ��
        {						
        	if(set_pinyin[i].contains(str1)&&set_pinyin[i].contains(str2))
        	{
        		return 1;	
        	}
        }
        return 0;
	}
	
    public String Pinyin(String word) throws BadHanyuPinyinOutputFormatCombination 		//��ȡƴ��
    {			
        char[] charArray = word.toCharArray();
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < charArray.length; i++) 
        {
            if (Character.toString(charArray[i]).matches("[\\u4E00-\\u9FA5]+")) 
            {
                 String[] hanyuPinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(charArray[i],defaultFormat);
                String string =hanyuPinyinStringArray[0];
                pinyin.append(string);
            } else 
            {
                pinyin.append(charArray[i]);
            }
        }
        return pinyin.toString();
    }
 
    public String Pinyin(char word) throws BadHanyuPinyinOutputFormatCombination 		//��ȡƴ��
    {			
        StringBuilder pinyin = new StringBuilder();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        if (Character.toString(word).matches("[\\u4E00-\\u9FA5]+")) 
        {
           String[] hanyuPinyinStringArray = PinyinHelper.toHanyuPinyinStringArray(word,defaultFormat);
           String string =hanyuPinyinStringArray[0];
           pinyin.append(string);
        } 
        else 
        {
           pinyin.append(word);
        }
        return pinyin.toString();
    }
    	
    public void InitMap(String input) throws IOException, BadHanyuPinyinOutputFormatCombination	//���map
	{		
		
		 Path path = Paths.get(input);							//������
		 byte[] data = Files.readAllBytes(path);
		 String raw = new String(data, "utf-8");
		 
		 /*String sentence = "";									//ȥ����
		 for (int i = 0; i < raw.length(); i++) 
		 {
		    if ((raw.charAt(i) != '\n')) 		    	
		    {
		    	sentence += raw.charAt(i);
		    }
		 }					 
		 
         String [] words = sentence.split(" ");					//ƴ�����
         for(String word : words)
         {       	
        	String pinyin=Pinyin(word);         
            map_n.put(word,pinyin);
         }        
         //System.out.print("\n" + map_n+"\n" );*/
         
         
		 String sentence = "";									//ȥ������ո�
		 for (int i = 0; i < raw.length(); i++) 
		 {
		    if ((raw.charAt(i) != '\n')&&(raw.charAt(i) != ' ')) 		    	
		    {
		    	sentence += raw.charAt(i);
		    }
		 }		
         
         String [] wordss = sentence.split("");					//ƴ�����
         for(String word : wordss)
         {       	
        	String pinyin=Pinyin(word);      
            map_1.put(word,pinyin);
         }        
         //System.out.print("\n" + map_1);
         
         File file = new File("document\\samepy.txt");			
         BufferedReader br = new BufferedReader(new FileReader(file));			
         String s = null;
         int i=0;
         while((s = br.readLine())!=null)										//һ�ζ�һ��
         {		
        	 set_pinyin[i]=s;
             //System.out.println(set_pinyin[i]);
        	 i++;

         }
         br.close();;

	}
    
    public void Correct_1(String word) throws BadHanyuPinyinOutputFormatCombination  		//������
, IOException
    {	
    	
    	float max_r=50;											//���ƶ���ֵ
        
    	//float max_s2;												//2-gram������ֵ
    	
        //String inputfile2 = "document\\2-gram.arpa";
        String inputfile3 = "document\\3-gram.arpa";
        //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);				//2-gram
        ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram
        
		String result= "document\\result.txt";							//������ڼ���ԭʼ���ݣ��������
		Path path = Paths.get(result);													//��ȡ�ִʺ�����
		byte[] data = Files.readAllBytes(path);
		String raw = new String(data, "utf-8");
		
		String initial_s = "";									//ȥ����(���ӵ����һ�����Ŵ����Ϊһ������)
		for (int i = 0; i < raw.length(); i++) 
		{
		   if (raw.charAt(i) != '\n') 
		   {
			   initial_s += raw.charAt(i);
		   }
		}	
        String [] initial_w = initial_s.split(" ");					//תΪlist
        List<String> initial_l = new ArrayList<String>();
        for(String l : initial_w)
        {
        	initial_l.add(l);
        }       				       
        float max_s3 = model3.scoreSentence(initial_l);			//3-gram������ֵ							 	
    	String max_sentence=initial_s;							//������������µľ���
		
		

		Set<String> keys;
        String pinyin=Pinyin(word);
        //System.out.println("��������:"+word+'('+pinyin+')'+'\n'); 

        String judge="";										//��ֹvalue�ظ�
        for(String value:map_1.values())
        {
        	if(judge.contains(" "+value+" "))
        	{
        		continue;									//�ظ�����
        	}
        	else
        	{
        		judge=judge+" "+value+" ";
        		//float ratio=getEditDistance(value,pinyin);	//�༭����
        		//if(ratio>max_r)																							
        		float ratio=getSimilarity(value,pinyin);	//ģ����
        		if(ratio==1)																							
        		{
        			max_r=ratio;
        			keys=getKeys(map_1,value);

        			System.out.println("value:"+value);
        			System.out.println("keys:"+keys);

        			for(java.util.Iterator<String> iterator = keys.iterator(); iterator.hasNext();)						//��set�ж�����
        			{
        				String key = (String)iterator.next();
        				//System.out.println(key+ratio); 
                
        				StringBuilder strBuilder = new StringBuilder(raw);                									//�ÿ��ܴ��滻���ܳ����		
                		         
        				strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),key);			
        				String sentence=strBuilder.toString();
        		
        				sentence=sentence.replaceAll(" ", "");  					//ȥ�ո�
        			
        				List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//�ִ�	
                
        				String edit_sentence="";
                
        				for(Word w:Word_sentence)								//WordתΪString
        				{
        					edit_sentence=edit_sentence+w.toString()+" ";
        				}
                
        				//System.out.println("�滻��:"+edit_sentence);

                                
        				String [] words = edit_sentence.split(" ");					//תΪlist
        				List<String> list = new ArrayList<String>();
        				for(String l : words)
        				{
        					list.add(l);
        				}
                
        				//float score2 = model2.scoreSentence(list);						//����
                
        				float score3 = model3.scoreSentence(list);
        				System.out.println("3-gram:"+score3);
                
        				if(score3>max_s3)										//��������д��result
        				{
        					max_s3=score3;
        					max_sentence=edit_sentence;
        				}
        			}
        		}
        	}
        }
        
        FileOutputStream writerStream = new FileOutputStream(result);            						//���÷���ߵľ���д��
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
        writer.write(max_sentence);
        writer.close(); 
    }
   
    public void Correct_n(String word) throws BadHanyuPinyinOutputFormatCombination  		//������
, IOException
    {			
        //String inputfile2 = "document\\2-gram.arpa";
        String inputfile3 = "document\\3-gram.arpa";
        //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);				//2-gram
        ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram
           
		String result= "document\\result.txt";							//������ڼ���ԭʼ���ݣ��������
		Path path = Paths.get(result);													//��ȡ��Ĵ���ӣ��Ž�raw��
		byte[] data = Files.readAllBytes(path);
		String raw = new String(data, "utf-8");
		
    	float max_r=50;											//���ƶ���ֵ
        
    	//float max_s2;											//2-gram������ֵ
    	
		String initial_s = "";									//ȥ����(���ӵ����һ�����Ŵ����Ϊһ������)
		for (int i = 0; i < raw.length(); i++) 
		{
		   if (raw.charAt(i) != '\n') 
		   {
			   initial_s += raw.charAt(i);
		   }
		}	
        String [] initial_w = initial_s.split(" ");					//תΪlist
        List<String> initial_l = new ArrayList<String>();
        for(String l : initial_w)
        {
        	initial_l.add(l);
        }       				       
        float max_s3 = model3.scoreSentence(initial_l);			//3-gram������ֵ							 	
    	String max_sentence=initial_s;							//������������µľ���
		raw=raw.replaceAll(" ", "");  							//���ֺ�ȥ�ո�ʡ��һ�ηִʹ���


		/*Set<String> keys_n;
        String pinyin_n=Pinyin(word);
        //System.out.println("��������:"+word+'('+pinyin_n+')'+'\n'); 
        
        for(String value:map_n.values())							//�Ѵʵ���һ��������иĴ�������
        {
        	float ratio=getEditDistance(value,pinyin_n);
        	if(ratio>max_r)																							//���ƶȴ�����ֵ�ж�����
        	{
        		keys_n=getKeys(map_1,value);

        		for(java.util.Iterator<String> iterator = keys_n.iterator(); iterator.hasNext();)						//��set�ж�����
        		{
        			String key_n = (String)iterator.next();
                    StringBuilder strBuilder = new StringBuilder(raw);                									//�ÿ��ܴ��滻���ܳ����		
   		         
                    strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),key_n);			
                    String sentence=strBuilder.toString();
            		
                    sentence=sentence.replaceAll(" ", "");  					//ȥ�ո�
                    
                    List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//�ִ�	
                    
                    String edit_sentence="";
                    
                    for(Word w:Word_sentence)								//WordתΪString
                    {
                    	edit_sentence=edit_sentence+w.toString()+" ";
                    }
                    
                    //System.out.println("�滻��:"+edit_sentence);
                                    
                    String [] words = edit_sentence.split(" ");					//תΪlist
                    List<String> list = new ArrayList<String>();
                    for(String l : words)
                    {
                       list.add(l);
                    }
                    
                    //float score2 = model2.scoreSentence(list);						//����
                    
                    float score3 = model3.scoreSentence(list);
                    //System.out.println("3-gram:"+score3);

                    if(score3>max_s3)										//��������д��result
                    {
                    	max_s3=score3;
                    	max_sentence=max_sentence.replaceAll(max_sentence, edit_sentence);
                    }
        		}
        	}
        } */
        
        
		char[] word_1=new char[4];										//�Ѵʲ���֣������Ϊ4�֣�����word_1�洢ÿ����
        for(int i=0;i<word.length();i++)															
        {
        	word_1[i]=word.charAt(i);
        }
        
        
        String set[]={"","","",""};										//����set�洢ÿ���ֵĺ�ѡ��
        for(int i=0;i<word.length();i++)							//��ȡÿ���ֵĺ�ѡ��										
        {
            String pinyin_1=Pinyin(word_1[i]);							//��ȡ��i���ֵ�ƴ��
            String judge="";										//��ֹvalue�ظ�
        	for(String value:map_1.values())
        	{
        		Set<String> keys;
        		
        		//float ratio=getEditDistance(value,pinyin_1);	//�༭����
            	//if(ratio>max_r)						
            	if(judge.contains(" "+value+" "))
            	{
            		continue;									//�ظ�����
            	}
            	else
            	{
            		judge=judge+" "+value+" ";
            		float ratio=getSimilarity(value,pinyin_1);	//ģ����
            		if(ratio==1)	
            		{
            			keys=getKeys(map_1,value);

            			for(java.util.Iterator<String> iterator = keys.iterator(); iterator.hasNext();)						//��set�ж�����
            			{
            				String key_1 = (String)iterator.next();
            				if(!set[i].contains(key_1))
            				{
            					set[i]=set[i]+key_1;								//����i���ֵĺ�ѡ��
            				}
            			}
            		}
            	}
        	}
            //System.out.println("��ɣ�"+word_1[i]+"   "+set[i]);
        }
        
        for(int i=0;i<set[0].length();i++)								//һ�����4��
        {
        	for(int j=0;j<set[1].length();j++)
        	{
        		if(set[2].length()>0)
        		{
        			for(int k=0;k<set[2].length();k++)
        			{
                		if(set[3].length()>0)
                		{
                			for(int z=0;z<set[3].length();z++)											//4�ֵĴ�
                			{
                				String test="";												//����һ��ѡ������������ϳɵĴ�
                            	test=test+set[0].charAt(i)+set[1].charAt(j)+set[2].charAt(k)+set[3].charAt(z);	
                                StringBuilder strBuilder = new StringBuilder(raw);                									//�ÿ��ܴ��滻���ܳ����		
                                strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),test);			
                                String sentence=strBuilder.toString();
                                sentence=sentence.replaceAll(" ", "");  					//ȥ�ո� 
                                List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//�ִ�	
                                String edit_sentence="";                    
                                for(Word w:Word_sentence)								//WordתΪString
                                {
                                	edit_sentence=edit_sentence+w.toString()+" ";
                                }                    
                                //System.out.println("�滻��:"+edit_sentence);                                  
                                String [] words = edit_sentence.split(" ");					//תΪlist
                                List<String> list = new ArrayList<String>();
                                for(String l : words)
                                {
                                   list.add(l);
                                }                    
                                //float score2 = model2.scoreSentence(list);						//����                    
                                float score3 = model3.scoreSentence(list);
                                //System.out.println("3-gram:"+score3);
                                if(score3>max_s3)										//��������д��result
                                {
                                	max_s3=score3;
                                	max_sentence=edit_sentence;
                                }
                			}
                		}
                		else											//3�ֵĴ�
                		{
                        	String test="";												//����һ��ѡ������������ϳɵĴ�
                        	test=test+set[0].charAt(i)+set[1].charAt(j)+set[2].charAt(k);	
                            StringBuilder strBuilder = new StringBuilder(raw);                									//�ÿ��ܴ��滻���ܳ����		
                            strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),test);			
                            String sentence=strBuilder.toString();
                            sentence=sentence.replaceAll(" ", "");  					//ȥ�ո� 
                            List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//�ִ�	
                            String edit_sentence="";                    
                            for(Word w:Word_sentence)								//WordתΪString
                            {
                            	edit_sentence=edit_sentence+w.toString()+" ";
                            }                    
                            //System.out.println("�滻��:"+edit_sentence);                                  
                            String [] words = edit_sentence.split(" ");					//תΪlist
                            List<String> list = new ArrayList<String>();
                            for(String l : words)
                            {
                               list.add(l);
                            }                    
                            //float score2 = model2.scoreSentence(list);						//����                    
                            float score3 = model3.scoreSentence(list);
                            //System.out.println("3-gram:"+score3);
                            if(score3>max_s3)										//��������д��result
                            {
                            	max_s3=score3;
                            	max_sentence=edit_sentence;
                            }
                		}
        			}
        		}
        		else								//2�ֵĴ�
        		{
                	String test="";												//����һ��ѡ������������ϳɵĴ�
                	test=test+set[0].charAt(i)+set[1].charAt(j);	
                    StringBuilder strBuilder = new StringBuilder(raw);                									//�ÿ��ܴ��滻���ܳ����		
                    strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),test);			
                    String sentence=strBuilder.toString();
                    sentence=sentence.replaceAll(" ", "");  					//ȥ�ո� 
                    List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//�ִ�	
                    String edit_sentence="";                    
                    for(Word w:Word_sentence)								//WordתΪString
                    {
                    	edit_sentence=edit_sentence+w.toString()+" ";
                    }                    
                    //System.out.println("�滻��:"+edit_sentence);                                  
                    String [] words = edit_sentence.split(" ");					//תΪlist
                    List<String> list = new ArrayList<String>();
                    for(String l : words)
                    {
                       list.add(l);
                    }                    
                    //float score2 = model2.scoreSentence(list);						//����                    
                    float score3 = model3.scoreSentence(list);
                    //System.out.println("3-gram:"+score3);
                    if(score3>max_s3)										//��������д��result
                    {
                    	max_s3=score3;
                    	max_sentence=edit_sentence;
                    }
        		}
        	}
        }
        
        //System.out.println(max_sentence+max_s3);

        FileOutputStream writerStream = new FileOutputStream(result);            						//���÷���ߵľ���д��
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
        writer.write(max_sentence);
        writer.close(); 
    }
      
    public static void main(String[] args) throws IOException, BadHanyuPinyinOutputFormatCombination  
    {
    	Correct c =new Correct();
    	
    	String input = "document\\segmented.txt";		  	
		String wrong = "document\\temp.txt";		
		String result= "document\\result.txt";						
		
		c.InitMap(input);																					//��ʼ��ƴ��map

		Path path1 = Paths.get(result);																		//�����������
		byte[] data1 = Files.readAllBytes(path1);
		String error = new String(data1, "utf-8");
		error=error.replaceAll(" ", ""); 																				

		 Path path2 = Paths.get(wrong);																		//������
		 byte[] data2 = Files.readAllBytes(path2);
		 String raw = new String(data2, "utf-8");
		 
         String [] words = raw.split(" ");
         int length=words.length;
         
         for(int i=0;i<length;i++)																			//��������
         {
        	 if(words[i].length()>1)			//Ϊ��,һ�δ���һ���ʣ����������������������ļ����֣����4����������һ���Ϊһ���ʣ�
        	 {
        		 c.Correct_n(words[i]);									//������
        	 }
        	 else if(words[i].length()==1)		//Ϊ�֣����������ֵ�һ����
        	 {
       
        		c.Correct_1(words[i]);									//������
        	 }     	 
         }
	 
		 
		 Path path3 = Paths.get(result);																		//������ս��
		 byte[] data3 = Files.readAllBytes(path3);
		 String correct = new String(data3, "utf-8");
         
		 correct=correct.replaceAll(" ", "");    																//ȥ�ո�
													
         FileOutputStream writerStream = new FileOutputStream(result); 											//UTF-8��ʽд��
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
         writer.write(correct);
         writer.close(); 
         
         System.out.println("����������:"+error);
         System.out.println("���������:"+correct);
		
    }
}
