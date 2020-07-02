  	package whu.myw;
  	
import java.io.File;


import org.apdplat.word.WordSegmenter;



    public class Segmenter 
    {
	
	 public static void main(String[] args) throws Exception 
	 {

		 String input = "document\\initiated.txt";					//用于分词
		 String output = "document\\segmented.txt";
		 
         																										//双向匹配算法
		 WordSegmenter.segWithStopWords(new File(input), new File(output));				//分词
		 
         System.out.println("分词完成");


	 }

    }
