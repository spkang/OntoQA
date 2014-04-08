import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;

public class Test {
	 /**
	  * ×Ö·û´®Ìæ»»º¯Êý
	  * @param from ÒªÌæ»»µÄ×Ö·û
	  * @param to ÒªÌæ»»³ÉµÄÄ¿±ê×Ö·û
	  * @param source ÒªÌæ»»µÄ×Ö·û´®
	  * @return Ìæ»»ºóµÄ×Ö·û´®
	  */

	  public static String str_replace(String from,String to,String source) {
	    StringBuffer bf= new StringBuffer("");
	    StringTokenizer st = new StringTokenizer(source,from,true);
	    while (st.hasMoreTokens()) {
	      String tmp = st.nextToken();
	      if(tmp.equals(from)) {
	        bf.append(to);
	      } else {
	        bf.append(tmp);
	      }
	    }
	    return bf.toString();
	  }
	public static void main(String[] args) throws Exception {
		String inputFile  = "data/geobase1.3.rdf";
		String outputFile = "data/geobase1.5.rdf";
		//prefix
		String geo = "http://ir.hit.edu/nli/geo/";
		String rdfs = "http://www.w3.org/2000/01/rdf-schema#";
		
		Model model = ModelFactory.createDefaultModel();
		FileManager.get().readModel(model, inputFile);
		
		Property hasCapitalProperty = model.getProperty(geo + "hasCapital");
		Property hasNameProperty = model.getProperty(geo + "hasName");
		Property inCountryProperty = model.getProperty(geo + "inCountry");
		Property labelProperty = model.getProperty(rdfs + "label");
		
		RDFNode nullNode = null;
		StmtIterator iter1 = model.listStatements(null, hasCapitalProperty, nullNode);
		List<Statement> resList = new ArrayList<Statement>();
		while(iter1.hasNext()){
			resList.add(iter1.nextStatement());
		}
		for(Statement stmtTmp : resList){
			Resource stateNode = stmtTmp.getSubject();
			StmtIterator iter2 = model.listStatements(stateNode, hasNameProperty, nullNode);
			String stateName = iter2.nextStatement().getString();
			String cityName = stmtTmp.getString();
			System.out.println(geo + "city/" + str_replace(" ", "_", stateName) + "/" + cityName);
			
			Resource cityNode = model.getResource(geo + "city/" + str_replace(" ", "_", stateName) + "/" + str_replace(" ", "_", cityName));
			if(cityNode.hasProperty(labelProperty) == false){
				cityNode.addProperty(hasNameProperty, cityName);
				cityNode.addProperty(labelProperty, cityName);
			}
			model.remove(stmtTmp);
			model.add(stateNode, hasCapitalProperty, cityNode);
			System.out.println("city <" + cityName + ">:" + cityNode.getURI());
		}
		StmtIterator iter2 = model.listStatements(null, inCountryProperty, nullNode);
		List<Statement> inCountryList = new ArrayList<Statement>();
		while(iter2.hasNext()){
			inCountryList.add(iter2.nextStatement());
		}
		model.remove(inCountryList);
		inCountryProperty.removeProperties();
		PrintWriter out = new PrintWriter(new File(outputFile));
		model.write(out);
		out.close();
		System.out.println("THE END!");
	}
}
