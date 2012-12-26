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
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Hello RDF-world!
 *
 */
public class App
{
	
	public App(String location) {
		model = ModelFactory.createDefaultModel();
		FileManager.get().readModel(model, location);
	}
	
	private Model model;
	
    public static void main( String[] args )
    {	
    	
    	System.out.println("First program");
        
   //App app = new App(args[0]);
    //	System.out.println(app.queryForPropertyValues(RDFS.label));
      
    }
    
    
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
    	
    	
    }
}
