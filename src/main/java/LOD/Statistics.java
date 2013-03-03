package LOD;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

public class Statistics {
	
	private Model model;
	
	public Statistics(Model model1)
	{
		model = model1;
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
		  return execCountQuery(str,model);
	  } 
	  
	  public List<String> getExternalLinksCount()
	  {	 
		  String str = "prefix afn: <http://jena.hpl.hp.com/ARQ/function#> "+ 
		  				"SELECT (count(*) As ?no)"+
		  				"{ ?s ?p ?o filter(isIRI(?s) && isIRI(?o) && (afn:namespace(?s)!=afn:namespace(?o)))}"; 
		  return execCountQuery(str,model);
	  } 
	  
	  public List<String> getlinksCount()// && or ||
	  {	 
		  String str ="prefix afn: <http://jena.hpl.hp.com/ARQ/function#>"+
				  " SELECT (count(*) As ?no) "+
				  "{ ?s ?p ?o filter(isIRI(?s) && isIRI(?o))}";// && (afn:namespace(?s)!=afn:namespace(?p) || afn:namespace(?s)!=afn:namespace(?o))
		  return execCountQuery(str,model);
	  } 
	  
    public List<String> getTriplesCount()
    {
    	 String str ="SELECT (COUNT(*) AS ?no) { ?s ?p ?o  }";
    	  return execCountQuery(str,model);
    }
    
    public List<String> getPropertiesCount()
    {
    	 String str ="SELECT (COUNT(distinct ?p) AS ?no) { ?s ?p ?o  }";
    	  return execCountQuery(str,model);
    }
    
	  public List<String> getEntitiesCount()
	  {
		  String str ="SELECT (COUNT(distinct ?s) AS ?no) { ?s a []  }";
		  return execCountQuery(str,model);	  
	  }

	  public List<String> getDistinctResourceURIsCount()
	  {
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) { { ?s ?p ?o  } UNION { ?o ?p ?s } FILTER(!isBlank(?s) && !isLiteral(?s)) }";
		  return execCountQuery(str,model);
	  }
	  
	  public List<String> getLiteralsCount()
	  {
		  String str ="SELECT (COUNT(DISTINCT ?o ) AS ?no)"+
				 " {  ?s ?p ?o  filter(isLiteral(?o)) }";
		  return execCountQuery(str,model);
	  }
	  
	  public List<String> getTypedStringLength()
	  {
		  String str ="prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				  " prefix fn: <http://www.w3.org/2005/xpath-functions#>"+
				  " SELECT (avg(fn:string-length(?o) ) AS ?no)"+
				  " {  ?s ?p ?o  filter(isLiteral(?o) && datatype(?o)=xsd:string) }";
		  return execCountQuery(str,model);
	  } 
	  
	  public List<String> getUntypedStringLength()
	  {
		  String str ="prefix xsd: <http://www.w3.org/2001/XMLSchema#>"+
				  " prefix fn: <http://www.w3.org/2005/xpath-functions#>"+
				  " SELECT (avg(fn:string-length(?o) ) AS ?no)"+
				  " {  ?s ?p ?o  filter(isLiteral(?o) && datatype(?o)='') }";
		  return execCountQuery(str,model);
	  }
	  
	  public List<String> getTypedSubjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s a ?o }";
		  return execCountQuery(str,model);
	  }	  
	  
	  public List<String> getLabeledSubjectCount()
	  {			  
		  String str ="prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				  " SELECT (COUNT(DISTINCT ?s ) AS ?no)"+
				  " {  ?s ?p ?o filter(?p=rdfs:label)}";
		  return execCountQuery(str,model);
	  }	
	  
	  public List<String> getCommentSubjectCount()
	  {			  
		  String str ="prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>"+
				  " SELECT (COUNT(DISTINCT ?s ) AS ?no)"+
				  " {  ?s ?p ?o filter(?p=rdfs:comment)}";
		  return execCountQuery(str,model);
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
	  	return execCountQuery(str,model);
	  }	
	  
	  public List<String> getInstantiatedClassesCount()
	  {	  
		  String str ="SELECT (COUNT(DISTINCT ?type) AS ?no)"+
			  " { [] a ?type . FILTER (isURI(?type)) }";
		  /*We require classes to have URIs. Anonymous types do not count.*/
	  return execCountQuery(str,model);
	  }
	  
	  public List<String> getClassesUsedCount()
	  {			  
		  String str ="SELECT (count(DISTINCT ?type) As ?no) { ?s a ?type }";
		  return execCountQuery(str,model);
	  }

	  public List<String> getClassInstancesCount()
	  {			  
		  String str ="SELECT  ?class (COUNT(?s) AS ?no ) "+
			  "{ ?s a ?class } GROUP BY ?class ORDER BY ?no";
		  return execCountQuery(str,model);
	  }	 
	  
	  public List<String> getDistinctSubjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o   }";
		  return execCountQuery(str,model);
	  }

	  public List<String> getBlankSubjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?s ) AS ?no) {  ?s ?p ?o   filter(isBlank(?s))}";
		  return execCountQuery(str,model);
	  }
	  
	  public List<String> getDistinctObjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(!isLiteral(?o)) }";
		  return execCountQuery(str,model);
	  }
	  
	  public List<String> getBlankObjectCount()
	  {			  
		  String str ="SELECT (COUNT(DISTINCT ?o ) AS ?no) {  ?s ?p ?o  filter(isBlank(?o)) }";
		  return execCountQuery(str,model);
	  }

	  public List<String> getDistSubjUsingPropertyCount()
	  {			  
		  String str ="SELECT  ?p (COUNT(DISTINCT ?s ) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no";
		  return execCountQuery(str,model);
	  }
	  
	  public List<String> getTriplesUsingPropertyCount()
	  {			  
		  String str ="SELECT  ?p (COUNT(?s) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no";
		  return execCountQuery(str,model);
	  }

	  public List<String> getDistObjUsingPropertyCount()
	  {			  
		  String str ="SELECT  ?p (COUNT(DISTINCT ?o ) AS ?no ) { ?s ?p ?o } GROUP BY ?p ORDER BY ?no";
		  return execCountQuery(str,model);
	  }	  
	 
	  public List<String> execCountQuery (String queryString, Model model)
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
