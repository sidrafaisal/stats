package LOD;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
//import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
//import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
//import com.hp.hpl.jena.vocabulary.RDFS;

//import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
//import com.hp.hpl.jena.rdf.model.Property;

import com.hp.hpl.jena.ontology.*;
//import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.shared.PrefixMapping;
import com.hp.hpl.jena.util.iterator.Filter;

import java.io.PrintStream;
import java.util.*;

import java.io.*;
//import com.hp.hpl.jena.rdf.model.*;
//import com.hp.hpl.jena.vocabulary.*;


//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//sparql.function.library;


public class App
{
	
	public App()  throws IOException{
		model = ModelFactory.createDefaultModel();
	int i=0;
    	// use the FileManager to find the input file
   	 try{  
		InputStream in = FileManager.get().open("ami.xml");
   		  model.read(in," ");


		  PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
		  writer.println("Statistics:");
		  writer.println("");
		  
		  writer.println("triples = "+getTriplesCount());
		  writer.println("properties = "+ getPropertiesCount());
		  writer.println("distinct entities = "+getDistinctEntitiesCount());
		  writer.println("entities = "+getEntitiesCount());
		  writer.println("distinct resource URIs = "+getDistinctResourceURIsCount());		  
		  writer.println("instantiated classes = "+ getInstantiatedClassesCount());
		 		  
		  writer.println("distinct subject nodes = "+getDistinctSubjectCount());
		  writer.println("distinct object nodes = "+getDistinctObjectCount());

		  writer.println("instances of the class = "+getClassInstancesCount());
		  writer.println("classes used = "+getClassesUsedCount());
		  writer.println("literals = "+ getLiteralsCount()); 
		  writer.println("Blank subjects = "+ getBlankSubjectCount());
		  writer.println("Blank objects = " +getBlankObjectCount());
		  writer.println("distinct subjects in triples using the property= "+getDistSubjUsingPropertyCount());
		  writer.println("triples using the property = "+getTriplesUsingPropertyCount());
		  
		  writer.println("distinct objects in triples using the property = "+getDistObjUsingPropertyCount());
		  writer.println("typed string length = "+getTypedStringLength());
		  writer.println("untyped string length = "+getUntypedStringLength());
		  writer.println("typed subjects = "+ getTypedSubjectCount());
		  writer.println("labeled subjects " +getLabeledSubjectCount());
		  writer.println("links " +getlinksCount());
	//	  writer.println("labeled subjects " +getLabeledSubjectCount());
		  
		  writer.close();		   
   	 }
		 catch(Exception e){ System.out.println("error-"+e);}		
		
}

	private Model model;
	
    public static void main(String [] args) throws IOException
    {	
        App app = new App();
    
    }
	  public List<String> getlinksCount()
	  {			  
		  return ExecQuery("prefix ns: <http://example.org/ns#> SELECT (count(*) As ?no) { ?s ?p ?o filter(isIRI(?s) && isIRI(?o) && (?s)!=(?o))}");
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
	  
	  public List<String> getTypedStringLength()
	  {
		  return ExecQuery("prefix xsd: <http://www.w3.org/2001/XMLSchema#> prefix fn: <http://www.w3.org/2005/xpath-functions#> SELECT (avg(fn:string-length(?o) ) AS ?no) {  ?s ?p ?o  filter(isLiteral(?o) && datatype(?o)=xsd:string) }");
	  } 
	  
	  public List<String> getUntypedStringLength()
	  {
		  return ExecQuery("prefix xsd: <http://www.w3.org/2001/XMLSchema#> prefix fn: <http://www.w3.org/2005/xpath-functions#> SELECT (avg(fn:string-length(?o) ) AS ?no) {  ?s ?p ?o  filter(isLiteral(?o) && datatype(?o)='') }");
	  }
	  
	  public List<String> getTypedSubjectCount()
	  {			  
		  return ExecQuery("SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s a ?o }");
	  }	  
	  
	  public List<String> getLabeledSubjectCount()
	  {			  
		  return ExecQuery("prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o filter(?p=rdfs:label)}");
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

	  public List<String> getClassInstancesCount()
	  {			  
		  return ExecQuery("SELECT  ?class (COUNT(?s) AS ?no ) { ?s a ?class } GROUP BY ?class ORDER BY ?no");
	  }	 
	  public List<String> getDistinctSubjectCount()
	  {			  
		  return ExecQuery("SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o   }");
	  }

	  public List<String> getBlankSubjectCount()
	  {			  
		  return ExecQuery("SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o   filter(isBlank(?s))}");
	  }
	  
	  public List<String> getDistinctObjectCount()
	  {			  
		  return ExecQuery("SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(!isLiteral(?o)) }");
	  }
	  
	  public List<String> getBlankObjectCount()
	  {			  
		  return ExecQuery("SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(isBlank(?o)) }");
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

    }