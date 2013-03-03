package LOD;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.hp.hpl.jena.rdf.model.Model;

public class QualityDimensions {
	
	private Model model;
	private Statistics statistic;
	
	public QualityDimensions(Model model1)
	{
		model = model1;
		statistic= new Statistics(model);
	}
    //Quality Measures
    
    //pg19 Interpretability
    int MisinterpretationOfMissingValues() 
    {   
    	int blankobj = Integer.parseInt(statistic.getBlankObjectCount().get(0));
    	int blanksubj = Integer.parseInt(statistic.getBlankSubjectCount().get(0)); 	
    	int blanks=blankobj + blanksubj;
    	int distinctUri = Integer.parseInt(statistic.getlinksCount().get(0));
    	return distinctUri /(blanks+distinctUri); //hogan 2012,pg14
    }
    
    //p19 understandability
    double HumanReadableLabelling()
    {
    	int labeledsubj = Integer.parseInt(statistic.getLabeledSubjectCount().get(0));
    	int commentsubj = Integer.parseInt(statistic.getCommentSubjectCount().get(0));
    	int totalsubj = Integer.parseInt(statistic.getEntitiesCount().get(0));
    	double LabeledEntitiestoTotalEnities = (labeledsubj+commentsubj)/totalsubj;
    	return LabeledEntitiestoTotalEnities;
    }
    
    //p8 AmountofData
    double DataVolume_triples()
    {
    	
    int triples = Integer.parseInt(statistic.getTriplesCount().get(0));
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
    int triples = Integer.parseInt(statistic.getTriplesCount().get(0)); 
    int elinks = Integer.parseInt(statistic.getExternalLinksCount().get(0));
   	return elinks/triples;
    }
    
    double DataVolume_DataScope()
    {	
    int triples = Integer.parseInt(statistic.getTriplesCount().get(0));
    int entities = Integer.parseInt(statistic.getEntitiesCount().get(0));
   	return entities / triples;
    }
    
    double DataVolume_LevelofDetail()
    {  	
    int triples = Integer.parseInt(statistic.getTriplesCount().get(0));  
    int properties = Integer.parseInt(statistic.getPropertiesCount().get(0)); 
    //getClassInstancesCount()
    	return properties / triples;
    }
    
    //p14 interlinking
    int LinksExistence()
    {
    	int links = Integer.parseInt(statistic.getSameAsCount().get(0)) + Integer.parseInt(statistic.getExternalLinksCount().get(0));    	
    	return links;
    }
    
    //verifiability flemming 2010
    public double BasicProvenanceInfo()
    {
    	String str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:contributor)}" ;

    	int count_met = Integer.parseInt(statistic.execCountQuery(str,model).get(0)); 
	
    	str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:creator)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0)); 
    	
    	str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		     " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:publisher)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0));
    	
      	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:has_creator)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0));

    	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    			" SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:has_modifier)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0));    	

    	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:owner_of)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0)); 
    	
    	str=    "prefix foaf: <http://xmlns.com/foaf/0.1/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=foaf:maker)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0));  
    	
    	str=    "prefix dc: <http//purl.org/dc/elements/1.1/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dc:provenance)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0)); 
    	
    	str=    "prefix dc: <http//purl.org/dc/elements/1.1/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dc:source)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0)); 
    	
    	str=   "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    			" SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:earlier_version)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0)); 
    	
    	str=    "prefix sioc: <http://rdfs.org/sioc/ns#>" +
    			" SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=sioc:previous_version)}" ;
    	count_met += Integer.parseInt(statistic.execCountQuery(str,model).get(0));  
    	
    	return count_met/11;
    }
 // p10 provenance/
    public int MetaDataIndication() 
    { 
    	String str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    		    " SELECT (COUNT(?o ) AS ?no)"+
    		    " {  ?s ?p ?o filter(?p=dcterms:title)}" ;
        int title = Integer.parseInt(statistic.execCountQuery(str,model).get(0));
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
        int license = Integer.parseInt(statistic.execCountQuery(str,model).get(0));
        if(license>=1)
        	return 1;
        else
        	return 0;
    } 
    
    //need revision 
    public int SEP() 
    {   
    	
String str=   "PREFIX void:     <http://rdfs.org/ns/void#> "+
	"SELECT DISTINCT ?endpoint "   	    +
	"{ ?dataset a void:Dataset .   ?dataset void:sparqlEndpoint ?endpoint }";
	statistic.execCountQuery(str,model);		
    return 1;
    }
    
    // recency
    public double TimeCloseness(int range) throws ParseException 
    {   
String str=    "prefix dcterms:  <http://purl.org/dc/terms/>" +
    " PREFIX : <.>"+
    " SELECT ((?o) AS ?no)"+
    " {  ?s ?p ?o filter(?p=dcterms:modified)}" ;
String mod_date = statistic.execCountQuery(str,model).get(0);
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
    
  
}
