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
	
	public HashMap<String,String> map_1 = new HashMap<>();								//字到拼音
	//public HashMap<String,String> map_n = new HashMap<>();							//词到拼音
	public String set_pinyin[]=new String[62];											//模糊音,62行
	
	public Set<String> getKeys(HashMap<String, String> map, String value)  				//通过value找key集合
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

	public float getEditDistance(String str, String target) 					//编辑距离
	{	
		int d[][]; 				// 矩阵
		int n = str.length();
		int m = target.length();
		int i; 					// 遍历str的
		int j; 					// 遍历target的
		char ch1; 				// str的
		char ch2; 				// target的
		int temp; 				// 记录相同字符,在某个矩阵位置值的增量,不是0就是1
		if (n == 0 || m == 0) 
		{
			return 0;
		}
		d = new int[n + 1][m + 1];
		for (i = 0; i <= n; i++) 
		{ 
			d[i][0] = i;									// 初始化第一列
		}

		for (j = 0; j <= m; j++) 
		{ 
			d[0][j] = j;									// 初始化第一行
		}

		for (i = 1; i <= n; i++) 
		{ 
			ch1 = str.charAt(i - 1);						// 遍历str
			for (j = 1; j <= m; j++) 						// 去匹配target
			{
				ch2 = target.charAt(j - 1);
				if (ch1 == ch2 || ch1 == ch2 + 32 || ch1 + 32 == ch2) 
				{
					temp = 0;
				} else 
				{
					temp = 1;
				}
				d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + temp);	// 左边+1,上边+1, 左上角+temp取最小
			}
		}

		return (1 - (float) d[n][m] / Math.max(str.length(), target.length())) * 100F;
	}
	
	public float getSimilarity(String str, String target) throws 				//模糊音
IOException
	{
		String str1=" "+str+" ";
		String str2=" "+target+" ";
        for(int i=0;i<62;i++)										//一次读一行
        {						
        	if(set_pinyin[i].contains(str1)&&set_pinyin[i].contains(str2))
        	{
        		return 1;	
        	}
        }
        return 0;
	}
	
    public String Pinyin(String word) throws BadHanyuPinyinOutputFormatCombination 		//获取拼音
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
 
    public String Pinyin(char word) throws BadHanyuPinyinOutputFormatCombination 		//获取拼音
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
    	
    public void InitMap(String input) throws IOException, BadHanyuPinyinOutputFormatCombination	//填充map
	{		
		
		 Path path = Paths.get(input);							//读数据
		 byte[] data = Files.readAllBytes(path);
		 String raw = new String(data, "utf-8");
		 
		 /*String sentence = "";									//去换行
		 for (int i = 0; i < raw.length(); i++) 
		 {
		    if ((raw.charAt(i) != '\n')) 		    	
		    {
		    	sentence += raw.charAt(i);
		    }
		 }					 
		 
         String [] words = sentence.split(" ");					//拼音与词
         for(String word : words)
         {       	
        	String pinyin=Pinyin(word);         
            map_n.put(word,pinyin);
         }        
         //System.out.print("\n" + map_n+"\n" );*/
         
         
		 String sentence = "";									//去换行与空格
		 for (int i = 0; i < raw.length(); i++) 
		 {
		    if ((raw.charAt(i) != '\n')&&(raw.charAt(i) != ' ')) 		    	
		    {
		    	sentence += raw.charAt(i);
		    }
		 }		
         
         String [] wordss = sentence.split("");					//拼音与词
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
         while((s = br.readLine())!=null)										//一次读一行
         {		
        	 set_pinyin[i]=s;
             //System.out.println(set_pinyin[i]);
        	 i++;

         }
         br.close();;

	}
    
    public void Correct_1(String word) throws BadHanyuPinyinOutputFormatCombination  		//纠正字
, IOException
    {	
    	
    	float max_r=50;											//相似度起步值
        
    	//float max_s2;												//2-gram评分阈值
    	
        //String inputfile2 = "document\\2-gram.arpa";
        String inputfile3 = "document\\3-gram.arpa";
        //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);				//2-gram
        ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram
        
		String result= "document\\result.txt";							//存放用于检错的原始数据，与纠错结果
		Path path = Paths.get(result);													//读取分词后数据
		byte[] data = Files.readAllBytes(path);
		String raw = new String(data, "utf-8");
		
		String initial_s = "";									//去换行(句子的最后一个符号处理后为一个换行)
		for (int i = 0; i < raw.length(); i++) 
		{
		   if (raw.charAt(i) != '\n') 
		   {
			   initial_s += raw.charAt(i);
		   }
		}	
        String [] initial_w = initial_s.split(" ");					//转为list
        List<String> initial_l = new ArrayList<String>();
        for(String l : initial_w)
        {
        	initial_l.add(l);
        }       				       
        float max_s3 = model3.scoreSentence(initial_l);			//3-gram评分阈值							 	
    	String max_sentence=initial_s;							//保存最高评分下的句子
		
		

		Set<String> keys;
        String pinyin=Pinyin(word);
        //System.out.println("待纠正字:"+word+'('+pinyin+')'+'\n'); 

        String judge="";										//防止value重复
        for(String value:map_1.values())
        {
        	if(judge.contains(" "+value+" "))
        	{
        		continue;									//重复跳出
        	}
        	else
        	{
        		judge=judge+" "+value+" ";
        		//float ratio=getEditDistance(value,pinyin);	//编辑距离
        		//if(ratio>max_r)																							
        		float ratio=getSimilarity(value,pinyin);	//模糊音
        		if(ratio==1)																							
        		{
        			max_r=ratio;
        			keys=getKeys(map_1,value);

        			System.out.println("value:"+value);
        			System.out.println("keys:"+keys);

        			for(java.util.Iterator<String> iterator = keys.iterator(); iterator.hasNext();)						//从set中读数据
        			{
        				String key = (String)iterator.next();
        				//System.out.println(key+ratio); 
                
        				StringBuilder strBuilder = new StringBuilder(raw);                									//用可能词替换可能出错词		
                		         
        				strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),key);			
        				String sentence=strBuilder.toString();
        		
        				sentence=sentence.replaceAll(" ", "");  					//去空格
        			
        				List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//分词	
                
        				String edit_sentence="";
                
        				for(Word w:Word_sentence)								//Word转为String
        				{
        					edit_sentence=edit_sentence+w.toString()+" ";
        				}
                
        				//System.out.println("替换后:"+edit_sentence);

                                
        				String [] words = edit_sentence.split(" ");					//转为list
        				List<String> list = new ArrayList<String>();
        				for(String l : words)
        				{
        					list.add(l);
        				}
                
        				//float score2 = model2.scoreSentence(list);						//评分
                
        				float score3 = model3.scoreSentence(list);
        				System.out.println("3-gram:"+score3);
                
        				if(score3>max_s3)										//分数高者写回result
        				{
        					max_s3=score3;
        					max_sentence=edit_sentence;
        				}
        			}
        		}
        	}
        }
        
        FileOutputStream writerStream = new FileOutputStream(result);            						//将得分最高的句子写回
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
        writer.write(max_sentence);
        writer.close(); 
    }
   
    public void Correct_n(String word) throws BadHanyuPinyinOutputFormatCombination  		//纠正词
, IOException
    {			
        //String inputfile2 = "document\\2-gram.arpa";
        String inputfile3 = "document\\3-gram.arpa";
        //ArrayEncodedProbBackoffLm<String> model2 =LmReaders.readArrayEncodedLmFromArpa(inputfile2, false);				//2-gram
        ArrayEncodedProbBackoffLm<String> model3 =LmReaders.readArrayEncodedLmFromArpa(inputfile3, false);				//3-gram
           
		String result= "document\\result.txt";							//存放用于检错的原始数据，与纠错结果
		Path path = Paths.get(result);													//读取需改错句子，放进raw中
		byte[] data = Files.readAllBytes(path);
		String raw = new String(data, "utf-8");
		
    	float max_r=50;											//相似度起步值
        
    	//float max_s2;											//2-gram评分阈值
    	
		String initial_s = "";									//去换行(句子的最后一个符号处理后为一个换行)
		for (int i = 0; i < raw.length(); i++) 
		{
		   if (raw.charAt(i) != '\n') 
		   {
			   initial_s += raw.charAt(i);
		   }
		}	
        String [] initial_w = initial_s.split(" ");					//转为list
        List<String> initial_l = new ArrayList<String>();
        for(String l : initial_w)
        {
        	initial_l.add(l);
        }       				       
        float max_s3 = model3.scoreSentence(initial_l);			//3-gram评分阈值							 	
    	String max_sentence=initial_s;							//保存最高评分下的句子
		raw=raw.replaceAll(" ", "");  							//评分后去空格，省略一次分词过程


		/*Set<String> keys_n;
        String pinyin_n=Pinyin(word);
        //System.out.println("待纠正词:"+word+'('+pinyin_n+')'+'\n'); 
        
        for(String value:map_n.values())							//把词当作一个整体进行改错，并评分
        {
        	float ratio=getEditDistance(value,pinyin_n);
        	if(ratio>max_r)																							//相似度大于阈值判断评分
        	{
        		keys_n=getKeys(map_1,value);

        		for(java.util.Iterator<String> iterator = keys_n.iterator(); iterator.hasNext();)						//从set中读数据
        		{
        			String key_n = (String)iterator.next();
                    StringBuilder strBuilder = new StringBuilder(raw);                									//用可能词替换可能出错词		
   		         
                    strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),key_n);			
                    String sentence=strBuilder.toString();
            		
                    sentence=sentence.replaceAll(" ", "");  					//去空格
                    
                    List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//分词	
                    
                    String edit_sentence="";
                    
                    for(Word w:Word_sentence)								//Word转为String
                    {
                    	edit_sentence=edit_sentence+w.toString()+" ";
                    }
                    
                    //System.out.println("替换后:"+edit_sentence);
                                    
                    String [] words = edit_sentence.split(" ");					//转为list
                    List<String> list = new ArrayList<String>();
                    for(String l : words)
                    {
                       list.add(l);
                    }
                    
                    //float score2 = model2.scoreSentence(list);						//评分
                    
                    float score3 = model3.scoreSentence(list);
                    //System.out.println("3-gram:"+score3);

                    if(score3>max_s3)										//分数高者写回result
                    {
                    	max_s3=score3;
                    	max_sentence=max_sentence.replaceAll(max_sentence, edit_sentence);
                    }
        		}
        	}
        } */
        
        
		char[] word_1=new char[4];										//把词拆成字，词最大为4字，数组word_1存储每个字
        for(int i=0;i<word.length();i++)															
        {
        	word_1[i]=word.charAt(i);
        }
        
        
        String set[]={"","","",""};										//数组set存储每个字的候选集
        for(int i=0;i<word.length();i++)							//获取每个字的候选集										
        {
            String pinyin_1=Pinyin(word_1[i]);							//获取第i个字的拼音
            String judge="";										//防止value重复
        	for(String value:map_1.values())
        	{
        		Set<String> keys;
        		
        		//float ratio=getEditDistance(value,pinyin_1);	//编辑距离
            	//if(ratio>max_r)						
            	if(judge.contains(" "+value+" "))
            	{
            		continue;									//重复跳出
            	}
            	else
            	{
            		judge=judge+" "+value+" ";
            		float ratio=getSimilarity(value,pinyin_1);	//模糊音
            		if(ratio==1)	
            		{
            			keys=getKeys(map_1,value);

            			for(java.util.Iterator<String> iterator = keys.iterator(); iterator.hasNext();)						//从set中读数据
            			{
            				String key_1 = (String)iterator.next();
            				if(!set[i].contains(key_1))
            				{
            					set[i]=set[i]+key_1;								//填充第i个字的候选集
            				}
            			}
            		}
            	}
        	}
            //System.out.println("完成："+word_1[i]+"   "+set[i]);
        }
        
        for(int i=0;i<set[0].length();i++)								//一词最大4字
        {
        	for(int j=0;j<set[1].length();j++)
        	{
        		if(set[2].length()>0)
        		{
        			for(int k=0;k<set[2].length();k++)
        			{
                		if(set[3].length()>0)
                		{
                			for(int z=0;z<set[3].length();z++)											//4字的词
                			{
                				String test="";												//定义一候选集的字排列组合成的词
                            	test=test+set[0].charAt(i)+set[1].charAt(j)+set[2].charAt(k)+set[3].charAt(z);	
                                StringBuilder strBuilder = new StringBuilder(raw);                									//用可能词替换可能出错词		
                                strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),test);			
                                String sentence=strBuilder.toString();
                                sentence=sentence.replaceAll(" ", "");  					//去空格 
                                List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//分词	
                                String edit_sentence="";                    
                                for(Word w:Word_sentence)								//Word转为String
                                {
                                	edit_sentence=edit_sentence+w.toString()+" ";
                                }                    
                                //System.out.println("替换后:"+edit_sentence);                                  
                                String [] words = edit_sentence.split(" ");					//转为list
                                List<String> list = new ArrayList<String>();
                                for(String l : words)
                                {
                                   list.add(l);
                                }                    
                                //float score2 = model2.scoreSentence(list);						//评分                    
                                float score3 = model3.scoreSentence(list);
                                //System.out.println("3-gram:"+score3);
                                if(score3>max_s3)										//分数高者写回result
                                {
                                	max_s3=score3;
                                	max_sentence=edit_sentence;
                                }
                			}
                		}
                		else											//3字的词
                		{
                        	String test="";												//定义一候选集的字排列组合成的词
                        	test=test+set[0].charAt(i)+set[1].charAt(j)+set[2].charAt(k);	
                            StringBuilder strBuilder = new StringBuilder(raw);                									//用可能词替换可能出错词		
                            strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),test);			
                            String sentence=strBuilder.toString();
                            sentence=sentence.replaceAll(" ", "");  					//去空格 
                            List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//分词	
                            String edit_sentence="";                    
                            for(Word w:Word_sentence)								//Word转为String
                            {
                            	edit_sentence=edit_sentence+w.toString()+" ";
                            }                    
                            //System.out.println("替换后:"+edit_sentence);                                  
                            String [] words = edit_sentence.split(" ");					//转为list
                            List<String> list = new ArrayList<String>();
                            for(String l : words)
                            {
                               list.add(l);
                            }                    
                            //float score2 = model2.scoreSentence(list);						//评分                    
                            float score3 = model3.scoreSentence(list);
                            //System.out.println("3-gram:"+score3);
                            if(score3>max_s3)										//分数高者写回result
                            {
                            	max_s3=score3;
                            	max_sentence=edit_sentence;
                            }
                		}
        			}
        		}
        		else								//2字的词
        		{
                	String test="";												//定义一候选集的字排列组合成的词
                	test=test+set[0].charAt(i)+set[1].charAt(j);	
                    StringBuilder strBuilder = new StringBuilder(raw);                									//用可能词替换可能出错词		
                    strBuilder.replace(strBuilder.indexOf(word),strBuilder.indexOf(word)+word.length(),test);			
                    String sentence=strBuilder.toString();
                    sentence=sentence.replaceAll(" ", "");  					//去空格 
                    List<Word> Word_sentence= WordSegmenter.segWithStopWords(sentence);		//分词	
                    String edit_sentence="";                    
                    for(Word w:Word_sentence)								//Word转为String
                    {
                    	edit_sentence=edit_sentence+w.toString()+" ";
                    }                    
                    //System.out.println("替换后:"+edit_sentence);                                  
                    String [] words = edit_sentence.split(" ");					//转为list
                    List<String> list = new ArrayList<String>();
                    for(String l : words)
                    {
                       list.add(l);
                    }                    
                    //float score2 = model2.scoreSentence(list);						//评分                    
                    float score3 = model3.scoreSentence(list);
                    //System.out.println("3-gram:"+score3);
                    if(score3>max_s3)										//分数高者写回result
                    {
                    	max_s3=score3;
                    	max_sentence=edit_sentence;
                    }
        		}
        	}
        }
        
        //System.out.println(max_sentence+max_s3);

        FileOutputStream writerStream = new FileOutputStream(result);            						//将得分最高的句子写回
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
		
		c.InitMap(input);																					//初始化拼音map

		Path path1 = Paths.get(result);																		//读需改正句子
		byte[] data1 = Files.readAllBytes(path1);
		String error = new String(data1, "utf-8");
		error=error.replaceAll(" ", ""); 																				

		 Path path2 = Paths.get(wrong);																		//读错误处
		 byte[] data2 = Files.readAllBytes(path2);
		 String raw = new String(data2, "utf-8");
		 
         String [] words = raw.split(" ");
         int length=words.length;
         
         for(int i=0;i<length;i++)																			//改正过程
         {
        	 if(words[i].length()>1)			//为词,一次处理一个词，包括这种情况（连续出错的几个字（最多4个）被连在一起成为一个词）
        	 {
        		 c.Correct_n(words[i]);									//纠正词
        	 }
        	 else if(words[i].length()==1)		//为字，处理单独出现的一个字
        	 {
       
        		c.Correct_1(words[i]);									//纠正字
        	 }     	 
         }
	 
		 
		 Path path3 = Paths.get(result);																		//输出最终结果
		 byte[] data3 = Files.readAllBytes(path3);
		 String correct = new String(data3, "utf-8");
         
		 correct=correct.replaceAll(" ", "");    																//去空格
													
         FileOutputStream writerStream = new FileOutputStream(result); 											//UTF-8格式写回
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
         writer.write(correct);
         writer.close(); 
         
         System.out.println("待改正句子:"+error);
         System.out.println("改正后句子:"+correct);
		
    }
}
