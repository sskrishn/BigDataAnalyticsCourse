import java.io.IOException;
import java.util.*;
        
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

        
public class PhaseOneQues4Part1 
{
    public static class Map extends Mapper<LongWritable, Text, Text, IntWritable>
    {
	private final static IntWritable one = new IntWritable(1);
	private Text word = new Text();
        
	public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException 
	{
	    String doc = value.toString().toLowerCase().replaceAll("\\p{P}", "");
		StringTokenizer docPart = new StringTokenizer(doc);
	    String docName = ((FileSplit) context.getInputSplit()).getPath().getName().toString();
	    String tempStr=""; //temp string to construct the key part
		while (docPart.hasMoreTokens()) {
		tempStr = docPart.nextToken(); //removing special character and punctuation from the word
		tempStr = tempStr+","+docName;
		word.set(tempStr);//converting string to text writable
		context.write(word,one);
	    }
	}
    } 
        
    public static class Reduce extends Reducer<Text, IntWritable, Text, IntWritable>
    {

	public void reduce(Text key, Iterable<IntWritable> values, Context context) 
	    throws IOException, InterruptedException
	    {
		int sum = 0;
		for (IntWritable val : values) 
		{
		    sum += val.get();
		}
		context.write(key, new IntWritable(sum));
	    }
    }
        
    public static void main(String[] args) throws Exception
    {
	Configuration conf = new Configuration();
        
        Job job = new Job(conf, "PhaseOne");
    
	job.setOutputKeyClass(Text.class);
	job.setOutputValueClass(IntWritable.class);
	job.setJarByClass(PhaseOneQues4Part1.class);

	job.setMapperClass(Map.class);
	job.setReducerClass(Reduce.class);
        
	job.setInputFormatClass(TextInputFormat.class);
	job.setOutputFormatClass(TextOutputFormat.class);
        
	FileInputFormat.addInputPath(job, new Path(args[0]));
	FileOutputFormat.setOutputPath(job, new Path(args[1]));
        
	job.waitForCompletion(true);
    }
        
}
