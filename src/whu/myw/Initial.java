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

		 String input = "document\\raw.txt";						//���ص�ѵ������
		 String output = "document\\initiated.txt";					//Ԥ���������
		 
		 
         InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(input), "UTF-8");	//�����ص�ѵ������
         BufferedReader br = new BufferedReader(inputStreamReader);			
         
         FileOutputStream writerStream = new FileOutputStream(output); 										//UTF-8��ʽд��
         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
         
         String s = null;
         int n=0;
         while((s = br.readLine())!=null)														//���д���								
         {	
        	n++;
          	if(n%2==1)				//ɾ����Ӣ�������е�Ӣ���У������С�
          	{
         	   continue;
          	}
          	
          	s=s.replaceAll("\"", "");						//ɾ��һЩ�ַ�
          	s=s.replaceAll("��", "");
          	s=s.replaceAll("��", "");
          	s=s.replaceAll("��", "");
          	s=s.replaceAll("��", "");
          	
          	s=s.replaceAll("��", "\r\n");						//�滻һЩ�ַ�
          	s=s.replaceAll("��", "\r\n");
          	s=s.replaceAll("��", "\r\n");
          	s=s.replaceAll("��", "\r\n");
          	s=s.replaceAll("��", "\r\n");
          	s=s.replaceAll("��", "\r\n");

 	 
        	writer.write(s);							//д��
        	//System.out.println(s);
         }
         br.close();;
         writer.close();  
         
     	 System.out.println("���");
	}
}
