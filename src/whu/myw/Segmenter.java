  	package whu.myw;
  	
import java.io.File;


import org.apdplat.word.WordSegmenter;



    public class Segmenter 
    {
	
	 public static void main(String[] args) throws Exception 
	 {

		 String input = "document\\initiated.txt";					//���ڷִ�
		 String output = "document\\segmented.txt";
		 
         																										//˫��ƥ���㷨
		 WordSegmenter.segWithStopWords(new File(input), new File(output));				//�ִ�
		 
         System.out.println("�ִ����");


	 }

    }
