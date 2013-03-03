package LOD;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

public class App
{
	
	public App() throws FileNotFoundException, UnsupportedEncodingException, ParseException {
	Crawler crawling= new Crawler();
}

    public static void main(String [] args) throws FileNotFoundException, UnsupportedEncodingException //throws IOException
, ParseException
    {	
        App app = new App();   
    }

    }