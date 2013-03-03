package LOD;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class Crawler {
	
	Statistics statistic;
	QualityDimensions dimension;
	Model model;
	
	public Crawler() throws FileNotFoundException, UnsupportedEncodingException, ParseException
	{
	
//	for(int i=0; i<1; i++)
//	{
	loadModel("los.rdf");	
	statistic = new Statistics(model);
	output();
//	}
		  
 
}
	public void loadModel(String filename)
	{
		try
		{
		model = ModelFactory.createDefaultModel();
		// use the FileManager to find the input file		
		InputStream in = FileManager.get().open(filename);//ami.xml
		if(filename!="void.ttl") 
			model.read(in," ");  
		else
			model.read(in," ","TURTLE");
		}
		 catch(Exception e){ System.out.println("error-"+e);}			
	}
	public void output() throws FileNotFoundException, UnsupportedEncodingException, ParseException
	{
		  //Write into output file
	  PrintWriter writer;

		writer = new PrintWriter("output.txt", "UTF-8");

	  writer.println("Statistics:");
	  writer.println("");
	  
	  writer.println("Triples = "+statistic.getTriplesCount());
	  writer.println("Properties = "+ statistic.getPropertiesCount());
	  writer.println("Entities = "+statistic.getEntitiesCount());
	  writer.println("Resource URIs = "+statistic.getDistinctResourceURIsCount());		  
	  writer.println("Instantiated classes = "+ statistic.getInstantiatedClassesCount());
	 		  
	  writer.println("Subjects = "+statistic.getDistinctSubjectCount());
	  writer.println("Objects = "+statistic.getDistinctObjectCount());

	 writer.println("Instances of the class = "+statistic.getClassInstancesCount());
	 writer.println("Classes used = "+statistic.getClassesUsedCount());
	 writer.println("Literals = "+ statistic.getLiteralsCount()); 
	  writer.println("Blank subjects = "+ statistic.getBlankSubjectCount());
	  writer.println("Blank objects = " +statistic.getBlankObjectCount());
	  writer.println("Subjects in triples using the property= "+statistic.getDistSubjUsingPropertyCount());
	  writer.println("Triples using the property = "+statistic.getTriplesUsingPropertyCount());
	  
	  writer.println("Objects in triples using the property = "+statistic.getDistObjUsingPropertyCount());
	  writer.println("Typed string length = "+statistic.getTypedStringLength());
	  writer.println("Untyped string length = "+statistic.getUntypedStringLength());
	  writer.println("Typed subjects = "+ statistic.getTypedSubjectCount());
	  writer.println("Labeled subjects " +statistic.getLabeledSubjectCount());
	  
	  writer.println("Links " +statistic.getlinksCount());
		  writer.println("InternalLinks " +statistic.getInternalLinksCount());
	  writer.println("ExternalLinks " +statistic.getExternalLinksCount());
	  writer.println("SameAs " +statistic.getSameAsCount());
	  writer.println("");	
	  
	  loadModel("void.ttl");
	  dimension = new QualityDimensions(model);
	  	
	  writer.println("Data Quality");
	  writer.println("");		  
	  writer.println("Interpretability: " + dimension.MisinterpretationOfMissingValues() );
	  writer.println("Understandability: " +dimension.HumanReadableLabelling());
	  writer.println("AmountofData: " );
	  writer.println("Indicator_triples " +dimension.DataVolume_triples());
	  writer.println("Indicator_ExternalLinks " +dimension.DataVolume_ExternalLinks());
	  writer.println("Indicator_ScopeofData " +dimension.DataVolume_DataScope());
	  writer.println("Indicator_LevelofDetail " +dimension.DataVolume_LevelofDetail());
	  writer.println("Interlinking: " +dimension.LinksExistence());
	  writer.println("Verifiability: " +dimension.BasicProvenanceInfo());
	  writer.println("Provenance: " +dimension.MetaDataIndication() );
	  writer.println("Licensing: " +dimension.MachineReadableLicense());

		writer.println("Recency: " +dimension.TimeCloseness(365));
	  writer.close();		   

		
	}
}