package LOD;

import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.io.*;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

public class App
{
	
	public App()  throws IOException{
		model = ModelFactory.createDefaultModel();
		model_ttl = ModelFactory.createDefaultModel();

    	// use the FileManager to find the input file
   	 try{  
		InputStream in = FileManager.get().open("los.rdf");//ami.xml
		InputStream in_ttl = FileManager.get().open("void.ttl");
		model.read(in," ");  
		model_ttl.read(in_ttl," ","TURTLE");
   		  
   		  //Write into output file
		  PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
		  writer.println("Statistics:");
		  writer.println("");
		  
	 	  writer.println("Triples = "+getTriplesCount());
		  writer.println("Properties = "+ getPropertiesCount());
		  writer.println("Entities = "+getEntitiesCount());
		  writer.println("Resource URIs = "+getDistinctResourceURIsCount());		  
		  writer.println("Instantiated classes = "+ getInstantiatedClassesCount());
		 		  
		  writer.println("Subjects = "+getDistinctSubjectCount());
		  writer.println("Objects = "+getDistinctObjectCount());

		 writer.println("Instances of the class = "+getClassInstancesCount());
		 writer.println("Classes used = "+getClassesUsedCount());
		 writer.println("Literals = "+ getLiteralsCount()); 
		  writer.println("Blank subjects = "+ getBlankSubjectCount());
		  writer.println("Blank objects = " +getBlankObjectCount());
		  writer.println("Subjects in triples using the property= "+getDistSubjUsingPropertyCount());
		  writer.println("Triples using the property = "+getTriplesUsingPropertyCount());
		  
		  writer.println("Objects in triples using the property = "+getDistObjUsingPropertyCount());
		  writer.println("Typed string length = "+getTypedStringLength());
		  writer.println("Untyped string length = "+getUntypedStringLength());
		  writer.println("Typed subjects = "+ getTypedSubjectCount());
		  writer.println("Labeled subjects " +getLabeledSubjectCount());
		  
		  writer.println("Links " +getlinksCount());
 		  writer.println("InternalLinks " +getInternalLinksCount());
		  writer.println("ExternalLinks " +getExternalLinksCount());
		  writer.println("SameAs " +getSameAsCount());
		  writer.println("");	
		  
		  writer.println("Data Quality");
		  writer.println("");		  
		  writer.println("Interpretability: " + MisinterpretationOfMissingValues() );
		  writer.println("Understandability: " +HumanReadableLabelling());
		  writer.println("AmountofData: " );
		  writer.println("Indicator_triples " +DataVolume_triples());
		  writer.println("Indicator_ExternalLinks " +DataVolume_ExternalLinks());
		  writer.println("Indicator_ScopeofData " +DataVolume_DataScope());
		  writer.println("Indicator_LevelofDetail " +DataVolume_LevelofDetail());
		  writer.println("Interlinking: " +LinksExistence());
		  writer.println("Verifiability: " +BasicProvenanceInfo());
		  writer.println("Provenance: " +MetaDataIndication() );
		  writer.println("Licensing: " +MachineReadableLicense());
		  writer.println("Recency: " +TimeCloseness(365));	//range in number of days	  

  		  writer.close();		   
   	 }
		 catch(Exception e){ System.out.println("error-"+e);}		
		
}
	private Model model;
	private Model model_ttl;
	
    public static void main(String [] args) throws IOException
    {	
        App app = new App();
    
    }
 
    //Quality Measures
    
    //pg19 Interpretability
    int MisinterpretationOfMissingValues() 
    {   
    	int blankobj = Integer.parseInt(getBlankObjectCount().get(0));
    	int blanksubj = Integer.parseInt(getBlankSubjectCount().get(0)); 	
    	int blanks=blankobj + blanksubj;
    	int distinctUri = Integer.parseInt(getlinksCount().get(0));
    	return distinctUri /(blanks+distinctUri); //hogan 2012,pg14
    }
    
    //p19 understandability
    double HumanReadableLabelling()
    {
    	int labeledsubj = Integer.parseInt(getLabeledSubjectCount().get(0));
    	int commentsubj = Integer.parseInt(getCommentSubjectCount().get(0));
    	int totalsubj = Integer.parseInt(getEntitiesCount().get(0));
    	double LabeledEntitiestoTotalEnities = (labeledsubj+commentsubj)/totalsubj;
    	return LabeledEntitiestoTotalEnities;
    }
    
    //p8 AmountofData
    double DataVolume_triples()
    {
    	
    int triples = Integer.parseInt(getTriplesCount().get(0));
    double t_val = 0;
    
    if (triples>1000000000)
    	t_val = 1;
    else
    if(triples>=10000000 && triples<=1000000000)
    	t_val = 0.75;
    else
    if(triples>=500000 && triples<10000000)
    	t_val = 0.5;
    else
    if(triples>=10000 && triples<500000)
    	t_val = 0.25;
    else
    if(triples<10000)
    	t_val = 0;
    
    return t_val;
    }
    
    double DataVolume_ExternalLinks()
    {
    int triples = Integer.parseInt(getTriplesCount().get(0)); 
    int elinks = Integer.parseInt(getExternalLinksCount().get(0));
   	return elinks/triples;
    }
    
    double DataVolume_DataScope()
    {	
    int triples = Integer.parseInt(getTriplesCount().get(0));
    int entities = Integer.parseInt(getEntitiesCount().get(0));
   	return entities / triples;
    }
    
    double DataVolume_LevelofDetail()
    {  	
    int triples = Integer.parseInt(getTriplesCount().get(0));  
    int properties = Integer.parseInt(getPropertiesCount().get(0)); 
    //getClassInstancesCount()
    	return properties / triples;
    }
    
    //p14 interlinking
    int LinksExistence()
    {
    	int links = Integer.parseInt(getSameAsCount().get(0)) + Integer.parseInt(getExternalLinksCount().get(0));    	
    	return links;
    }
    
    //verifiability flemming 2010
    public double BasicProvenanceInfo()
    {
    	String str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:contributor)}" ;

    	int count_met = Integer.parseInt(ExecQuery_ttl(str).get(0)); 
	
    	str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:creator)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0)); 
    	
    	str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		     " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:publisher)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0));
    	
      	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:has_creator)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0));

    	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    			" SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:has_modifier)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0));    	

    	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:owner_of)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0)); 
    	
    	str=    "prefix foaf: <http://xmlns.com/foaf/0.1/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=foaf:maker)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0));  
    	
    	str=    "prefix dc: <http//purl.org/dc/elements/1.1/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dc:provenance)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0)); 
    	
    	str=    "prefix dc: <http//purl.org/dc/elements/1.1/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dc:source)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0)); 
    	
    	str=   "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    			" SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:earlier_version)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0)); 
    	
    	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    			" SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:previous_version)}" ;
    	count_met += Integer.parseInt(ExecQuery_ttl(str).get(0));  
    	
    	return count_met/11;
    }
 // p10 provenance/
    public int MetaDataIndication() 
    { 
    	String str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:title)}" ;
        int title = Integer.parseInt(ExecQuery_ttl(str).get(0));
        if(title>=1)
        	return 1;
        else
        	return 0;
    }
    
    // p10 licensing
    public int MachineReadableLicense() 
    { 
        String str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
        	    " SELECT (COUNT(?o ) AS ?no)"+
        	    " {  ?s ?p ?o filter(?p=dcterms:license)}" ;
        int license = Integer.parseInt(ExecQuery_ttl(str).get(0));
        if(license>=1)
        	return 1;
        else
        	return 0;
    } 
    
    //need revision 
    public List<String> SEP() 
    {   
    	
String str=   "PREFIX void:     <http://rdfs.org/ns/void#> "+
	"SELECT DISTINCT ?endpoint "   	    +
	"{ ?dataset a void:Dataset .   ?dataset void:sparqlEndpoint ?endpoint }";
return ExecQuery(str);		
    }
    
    // recency
    public double TimeCloseness(int range) throws ParseException 
    {   
String str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    " PREFIX : <.>"+
    " SELECT ((?o) AS ?no)"+
    " {  ?s ?p ?o filter(?p=dcterms:modified)}" ;
String mod_date = ExecQuery_ttl(str).get(0);
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

Calendar cal = Calendar.getInstance(), cal1 = Calendar.getInstance();
cal1.setTime(sdf.parse(mod_date));

int m=cal.get(Calendar.MONTH)-cal1.get(Calendar.MONTH);
int y=cal.get(Calendar.YEAR)-cal1.get(Calendar.YEAR);
int d=cal.get(Calendar.DATE)-cal1.get(Calendar.DATE);
double val = y*365+m*30+d;
if (val>range)
	val=0;
else
	val=1;

return val;
}  
    
    /*Statistics: internal links, external links, triple, properties, entities,
    resource URIs, literals, subjects, typed subjects, labeled subjects, blank 
    subjects, comments, objects, blank objects, same as, typed string length, 
    untyped strings length, instantiated classes, classes used, class instances, 
    triples using the property, objects in triples using the property, 
    subjects in triples using the property */
    
    public List<String> getInternalLinksCount()
	  {	 
    	String str =  "prefix afn: <http://jena.hpl.hp.com/ARQ/function#>"+
    				"SELECT (count(*) As ?no) " +
    				"{ ?s ?p ?o filter(isIRI(?s) && isIRI(?o) && (afn:namespace(?s)=afn:namespace(?o)))}";
		  return ExecQuery(str);
	  } 
	  
	  public List<String> getExternalLinksCount()
	  {	 
		  String str = "prefix afn: <http://jena.hpl.hp.com/ARQ/function#> "+ 
		  				"SELECT (count(*) As ?no)"+
		  				"{ ?s ?p ?o filter(isIRI(?s) && isIRI(?o) && (afn:namespace(?s)!=afn:namespace(?o)))}"; 
		  return ExecQuery(str);
	  } 
	  
	  public List<String> getlinksCount()// && or ||
	  {	 
		  String str ="prefix afn: <http://jena.hpl.hp.com/ARQ/function#>"+
				  " SELECT (count(*) As ?no) "+
				  "{ ?s ?p ?o filter(isIRI(?s) && isIRI(?o))}";// && (afn:namespace(?s)!=afn:namespace(?p) || afn:namespace(?s)!=afn:namespace(?o))
		  return ExecQuery(str);
	  } 
	  
    public List<String> getTriplesCount()
    {
    	 String str ="SELECT (COUNT(*) AS ?no) { ?s ?p ?o  }";
    	  return ExecQuery(str);
    }
    
    public List<String> getPropertiesCount()
    {
    	 String str ="SELECT (COUNT(distinct ?p) AS ?no) { ?s ?p ?o  }";
    	  return ExecQuery(str);
    }
    
	  public List<String> getEntitiesCount()
	  {
		  String str ="SELECT (COUNT(distinct ?s) AS ?no) { ?s a []  }";
		  return ExecQuery(str);	  
	  }

	  public List<String> getDistinctResourceURIsCount()
	  {
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) { { ?s ?p ?o  } UNION { ?o ?p ?s } FILTER(!isBlank(?s) && !isLiteral(?s)) }";
		  return ExecQuery(str);
	  }
	  
	  public List<String> getLiteralsCount()
	  {
		  String str ="SELECT (COUNT(DISTINCT ?o ) AS ?no)"+
				 " {  ?s ?p ?o  filter(isLiteral(?o)) }";
		  return ExecQuery(str);
	  }
	  
	  public List<String> getTypedStringLength()
	  {
		  String str ="prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				  " prefix fn: <http://www.w3.org/2005/xpath-functions#>"+
				  " SELECT (avg(fn:string-length(?o) ) AS ?no)"+
				  " {  ?s ?p ?o  filter(isLiteral(?o) && datatype(?o)=xsd:string) }";
		  return ExecQuery(str);
	  } 
	  
	  public List<String> getUntypedStringLength()
	  {
		  String str ="prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				  " prefix fn: <http://www.w3.org/2005/xpath-functions#>"+
				  " SELECT (avg(fn:string-length(?o) ) AS ?no)"+
				  " {  ?s ?p ?o  filter(isLiteral(?o) && datatype(?o)='') }";
		  return ExecQuery(str);
	  }
	  
	  public List<String> getTypedSubjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s a ?o }";
		  return ExecQuery(str);
	  }	  
	  
	  public List<String> getLabeledSubjectCount()
	  {			  
		  String str ="prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				  " SELECT (COUNT(DISTINCT ?s ) AS ?no)"+
				  " {  ?s ?p ?o filter(?p=rdfs:label)}";
		  return ExecQuery(str);
	  }	
	  
	  public List<String> getCommentSubjectCount()
	  {			  
		  String str ="prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				  " SELECT (COUNT(DISTINCT ?s ) AS ?no)"+
				  " {  ?s ?p ?o filter(?p=rdfs:comment)}";
		  return ExecQuery(str);
	  }	
	  
	  public List<String> getSameAsCount()
	  {			  
		  String str ="prefix afn: <http://jena.hpl.hp.com/ARQ/function#> "+
				  "prefix fn: <http://www.w3.org/2005/xpath-functions#> "+
				  "prefix owl: <http://www.w3.org/2002/07/owl#>"+
				  " SELECT (COUNT (*) AS ?no)"+ 
				  " { ?s ?p ?o filter(fn:contains(afn:namespace(?p),'owl:sameAs'))}";
	/*	  StmtIterator iter = model.listStatements( new SimpleSelector( null, null, (RDFNode)null ) );  
	  int i=0;
	while (iter.hasNext())
	  {	  
		  Statement stmt      = iter.nextStatement();  // get next statement
	   	    Property predicate = stmt.getPredicate();   // get the predicate
	   	 if(predicate.getURI().toLowerCase().contains("owl#sameas"))
	   		 i++;		  
	  }
*/
	  	return ExecQuery(str);
	  }	
	  
	  public List<String> getInstantiatedClassesCount()
	  {	  
		  String str ="SELECT (COUNT(DISTINCT ?type) AS ?no)"+
			  " { [] a ?type . FILTER (isURI(?type)) }";
		  /*We require classes to have URIs. Anonymous types do not count.*/
	  return ExecQuery(str);
	  }
	  
	  public List<String> getClassesUsedCount()
	  {			  
		  String str ="SELECT (count(DISTINCT ?type) As ?no) { ?s a ?type }";
		  return ExecQuery(str);
	  }

	  public List<String> getClassInstancesCount()
	  {			  
		  String str ="SELECT  ?class (COUNT(?s) AS ?no ) "+
			  "{ ?s a ?class } GROUP BY ?class ORDER BY ?no";
		  return ExecQuery(str);
	  }	 
	  
	  public List<String> getDistinctSubjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o   }";
		  return ExecQuery(str);
	  }

	  public List<String> getBlankSubjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o   filter(isBlank(?s))}";
		  return ExecQuery(str);
	  }
	  
	  public List<String> getDistinctObjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(!isLiteral(?o)) }";
		  return ExecQuery(str);
	  }
	  
	  public List<String> getBlankObjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(isBlank(?o)) }";
		  return ExecQuery(str);
	  }

	  public List<String> getDistSubjUsingPropertyCount()
	  {			  
		  String str ="SELECT  ?p (COUNT(DISTINCT ?s ) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no";
		  return ExecQuery(str);
	  }
	  
	  public List<String> getTriplesUsingPropertyCount()
	  {			  
		  String str ="SELECT  ?p (COUNT(?s) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no";
		  return ExecQuery(str);
	  }

	  public List<String> getDistObjUsingPropertyCount()
	  {			  
		  String str ="SELECT  ?p (COUNT(DISTINCT ?o ) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no";
		  return ExecQuery(str);
	  }
	  
	  public List<String> ExecQuery (String queryString)
	  {
    	Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		ResultSet results = qe.execSelect();		
		List<String> resultStrings = new ArrayList<String>();
    	
    	while(results.hasNext()){
    		QuerySolution sol = results.next();
    		resultStrings.add(sol.get("?no").asLiteral().getString());
    	}
		// Free up resources used running the query
		qe.close();	
		
		return resultStrings;
    }

	  public List<String> ExecQuery_ttl (String queryString)
	  {
    	Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model_ttl);
		ResultSet results = qe.execSelect();		
		List<String> resultStrings = new ArrayList<String>();
    	
    	while(results.hasNext()){
    		QuerySolution sol = results.next();
    		resultStrings.add(sol.get("?no").asLiteral().getString());
    	}
		// Free up resources used running the query
		qe.close();	
		
		return resultStrings;
    }
    }