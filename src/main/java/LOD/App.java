package LOD;


import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
//import com.hp.hpl.jena.rdf.model.Model;
//import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
//import com.hp.hpl.jena.vocabulary.RDFS;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
//import com.hp.hpl.jena.rdf.model.Property;


import java.io.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;


//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;

public class App
{
	
	public App()  throws IOException{
		model = ModelFactory.createDefaultModel();
	int i=0;
    	// use the FileManager to find the input file
   	 try{
		InputStream in = FileManager.get().open("ami.xml");
   		  model.read(in," ");
   		  
		  model.write(System.out);
		  /*StmtIterator iter = model.listStatements( new SimpleSelector( null, null, (RDFNode)null ) );  
		  while (iter.hasNext())
		  {
			  i++;
			  iter.next();			  
		  }
		  System.out.println("has="+i);
		  */
		  System.out.println("Statistics:");
		 
		  System.out.println("triples = "+ getTriplesCount());
		  System.out.println("Properties = "+ getPropertiesCount());
		  System.out.println("entities = "+getEntitiesCount());
		  System.out.println("distinct entities = "+getDistinctEntitiesCount());
		  /*System.out.println("distinct resource URIs = "+getDistinctResourceURIsCount());
		  
		  System.out.println("instantiated classes = "+ getInstantiatedClassesCount());
		 		  
		  System.out.println("distinct subject nodes = "+getDistinctSubjectCount());
		  System.out.println("distinct object nodes = "+getDistinctObjectCount());

		  System.out.println("instances of the class = "+getClassInstancesCount());
		  System.out.println("distinct subjects in triples using the property= "+getDistSubjUsingPropertyCount());
		  System.out.println("triples using the property = "+getTriplesUsingPropertyCount());
		  
		  System.out.println("distinct objects in triples using the property = "+getDistObjUsingPropertyCount());
		  System.out.println("classes used = "+getClassesUsedCount());
		  System.out.println("literals = "+ getLiteralsCount());*/
  
   	 }
		 catch(Exception e){ System.out.println("error-"+e);}		
		
}
	  public List<String> getCount()
	  {			  
		  return ExecQuery("SELECT (count(DISTINCT ?type) As ?no) { ?s a ?type }");
	  }
	private Model model;
	
    public static void main(String [] args) throws IOException
    {	
        App app = new App();
    
    }
 
    public List<String> getTriplesCount()
    {
    	  return ExecQuery("SELECT (COUNT(*) AS ?no) { ?s ?p ?o  }");
    }
    
    public List<String> getPropertiesCount()
    {
    	  return ExecQuery("SELECT (COUNT(distinct ?p) AS ?no) { ?s ?p ?o  }");
    }
    
	  public List<String> getDistinctEntitiesCount()
	  {
		  return ExecQuery("SELECT (COUNT(distinct ?s) AS ?no) { ?s a []  }");	  
	  }
	  public List<String> getEntitiesCount()
	  {
		  return ExecQuery("SELECT (COUNT (?s) AS ?no) { ?s a []  }");	  
	  }	
	  public List<String> getDistinctResourceURIsCount()
	  {
		  return ExecQuery("SELECT (COUNT(DISTINCT ?s ) AS ?no) { { ?s ?p ?o  } UNION { ?o ?p ?s } FILTER(!isBlank(?s) && !isLiteral(?s)) }");
	  }
	  
	  public List<String> getLiteralsCount()
	  {
		  return ExecQuery("SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(isLiteral(?o)) }");
	  }

	  public List<String> getInstantiatedClassesCount()
	  {
		  /*We require classes to have URIs. Anonymous types do not count.*/
		  return ExecQuery("SELECT (COUNT(DISTINCT ?type) AS ?no) { [] a ?type . FILTER (isURI(?type)) }");
	  }
	  public List<String> getClassesUsedCount()
	  {			  
		  return ExecQuery("SELECT (count(DISTINCT ?type) As ?no) { ?s a ?type }");
	  }
	 
	  public List<String> getDistinctSubjectCount()
	  {			  
		  return ExecQuery("SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o   }");
	  }
		
	  public List<String> getDistinctObjectCount()
	  {			  
		  return ExecQuery("SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(!isLiteral(?o)) }");
	  }

	  public List<String> getClassInstancesCount()
	  {			  
		  return ExecQuery("SELECT  ?class (COUNT(?s) AS ?no ) { ?s a ?class } GROUP BY ?class ORDER BY ?no");
	  }
	  public List<String> getDistSubjUsingPropertyCount()
	  {			  
		  return ExecQuery("SELECT  ?p (COUNT(DISTINCT ?s ) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no");
	  }
	  
	  public List<String> getTriplesUsingPropertyCount()
	  {			  
		  return ExecQuery("SELECT  ?p (COUNT(?s) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no");
	  }

	  public List<String> getDistObjUsingPropertyCount()
	  {			  
		  return ExecQuery("SELECT  ?p (COUNT(DISTINCT ?o ) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no");
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
   /*
    public List<String> queryForPropertyValues(Resource property){
    	String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  SELECT ?message WHERE  { ?s <" + property.getURI() +"> ?message}";
    	Query query = QueryFactory.create(queryString);
    	QueryExecution exec = QueryExecutionFactory.create(query, model);
    	ResultSet rs = exec.execSelect();
    	List<String> resultStrings = new ArrayList<String>();
    	
    	while(rs.hasNext()){
    		QuerySolution sol = rs.next();
    		resultStrings.add(sol.get("?message").asLiteral().getString());
    	}
    	
    	return resultStrings;
    }*/
    }