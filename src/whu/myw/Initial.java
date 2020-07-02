  	package whu.myw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class Initial 
{
	
	public static void main(String[] args) throws IOException
	{

		 String input = "document\\raw.txt";						//下载的训练语料
		 String output = "document\\initiated.txt";					//预处理过数据
		 
		 
         InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(input), "UTF-8");	//读下载的训练语料
         BufferedReader br = new BufferedReader(inputStreamReader);			
         
         FileOutputStream writerStream = new FileOutputStream(output); 										//UTF-8格式写回
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
         
         String s = null;
         int n=0;
         while((s = br.readLine())!=null)														//进行处理								
         {	
        	n++;
          	if(n%2==1)				//删除中英文语料中的英文行，奇数行。
          	{
         	   continue;
          	}
          	
          	s=s.replaceAll("\"", "");						//删除一些字符
          	s=s.replaceAll("＂", "");
          	s=s.replaceAll("“", "");
          	s=s.replaceAll("”", "");
          	s=s.replaceAll("＂", "");
          	
          	s=s.replaceAll("。", "\r\n");						//替换一些字符
          	s=s.replaceAll("，", "\r\n");
          	s=s.replaceAll("；", "\r\n");
          	s=s.replaceAll("？", "\r\n");
          	s=s.replaceAll("！", "\r\n");
          	s=s.replaceAll("：", "\r\n");

 	 
        	writer.write(s);							//写回
        	//System.out.println(s);
         }
         br.close();;
         writer.close();  
         
     	 System.out.println("完成");
	}
}
