import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public class GeobaseNew {
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
		String test = "hello world";
		System.out.println(str_replace(" ", "_", test));
		String inputFile  = "data/geobase.rdf";
		String outputFile = "data/geobase_new.rdf";
		//prefix
		String geo = "http://ir.hit.edu/nli/geo/";
		String rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
		String rdfs = "http://www.w3.org/2000/01/rdf-schema#";

		Model model = ModelFactory.createDefaultModel();
		FileManager.get().readModel(model, inputFile);

		Property typeProperty = model.getProperty(rdf + "type");
		RDFNode stateNode = model.getResource(geo + "state");
		Property hasHighestPointProperty = model.getProperty(geo + "hasHighestPoint");
		Property hasHighestElevationProperty = model.getProperty(geo + "hasHighestElevation");
		Property hasLowestPointProperty = model.getProperty(geo + "hasLowestPoint");
		Property hasLowestElevationProperty = model.getProperty(geo + "hasLowestElevation");
		Resource rdfsClass = model.getResource(rdfs + "Class");
		Property labelProperty = model.getProperty(rdfs + "label");
		Resource rdfProperty = model.getResource(rdf + "Property");
		Property hasNameProperty = model.getProperty(geo + "hasName");

		StmtIterator iter = model.listStatements(null, typeProperty, stateNode);

		//create point
		Resource point = model.createResource(geo + "point");
		point.addProperty(typeProperty, rdfsClass);
		point.addProperty(labelProperty, "point");
		//create hasElevation
		Property hasElevationProperty = model.createProperty(geo + "hasElevation");
		hasElevationProperty.addProperty(typeProperty, rdfProperty);
		hasElevationProperty.addProperty(labelProperty, "elevation");

		while(iter.hasNext()){
			StmtIterator iterTmp;
			String null_string = null;
			RDFNode null_node = null;
			Statement stmt = iter.nextStatement();
			Resource stateTmp = stmt.getSubject();
			iterTmp = model.listStatements(stateTmp, labelProperty, null_node);
			String state_name = iterTmp.nextStatement().getObject().toString();

			if(stateTmp.hasProperty(hasHighestPointProperty) == true){
				String name;
				int elevation = 0;
				Statement stmt1, stmt2 = null;

				iterTmp = model.listStatements(stateTmp, hasHighestPointProperty, null_string);
				stmt1 = iterTmp.nextStatement();
				name = stmt1.getObject().toString();

				if(stateTmp.hasProperty(hasHighestElevationProperty) == false){
					System.err.println("error!");
				}else{
					iterTmp = model.listStatements(stateTmp,hasHighestElevationProperty, null_node);
					stmt2 = iterTmp.nextStatement();
					elevation = stmt2.getInt();
				}
				//create point/name_in_state_high
				String new_uri = geo + "point/" + name + "_in_" + state_name + "_high";
				Resource new_point = model.createResource(str_replace(" ", "_", new_uri));
				new_point.addProperty(labelProperty, name);
				new_point.addProperty(typeProperty, point);
				new_point.addProperty(hasNameProperty, name);
				new_point.addProperty(hasElevationProperty, 
							Integer.toString(elevation), XSDDatatype.XSDint);

				Statement new_stmt = model.createStatement(stateTmp, 
										hasHighestPointProperty, new_point);
				model.add(new_stmt);
				model.remove(stmt1);
				model.remove(stmt2);
			}
			if(stateTmp.hasProperty(hasLowestPointProperty) == true){
				String name;
				int elevation = 0;
				Statement stmt1, stmt2 = null;
				//get name
				iterTmp = model.listStatements(stateTmp, hasLowestPointProperty, null_string);
				stmt1 = iterTmp.nextStatement();
				name = stmt1.getObject().toString();
				//get elevation
				if(stateTmp.hasProperty(hasLowestElevationProperty) == false){
					System.err.println("error!");
				}else{
					iterTmp = model.listStatements(stateTmp
									, hasLowestElevationProperty, null_node);
					stmt2 = iterTmp.nextStatement();
					elevation = stmt2.getInt();
				}

				//create point/name_in_state_low
				String new_uri = geo + "point/" + name + "_in_" + state_name + "_low";
				Resource new_point = model.createResource(str_replace(" ", "_", new_uri));
				new_point.addProperty(labelProperty, name);
				new_point.addProperty(typeProperty, point);
				new_point.addProperty(hasNameProperty, name);
				new_point.addProperty(hasElevationProperty
							, Integer.toString(elevation), XSDDatatype.XSDlong);

				Statement new_stmt = model.createStatement(stateTmp
										, hasLowestPointProperty, new_point);
				model.add(new_stmt);
				model.remove(stmt1);
				model.remove(stmt2);
			}
		}
		hasHighestElevationProperty.removeProperties();
		hasLowestElevationProperty.removeProperties();
		PrintWriter out = new PrintWriter(new File(outputFile));
		model.write(out);
		out.close();
		System.out.println("THE END!");
	}
}
