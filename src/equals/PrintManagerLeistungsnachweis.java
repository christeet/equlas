package equals;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class PrintManagerLeistungsnachweis {

	private static final String OUTPUT_PATH = "resources/output/";

	public static void main(String[] args) {
		try {
			File stylesheet = new File("resources/xml/resolveSourceLeistungsnachweis.xsl");
			File datafile = new File("resources/xml/module.xml");

			TransformerFactory factory = TransformerFactory.newInstance();
			
			Source xsl = new StreamSource(stylesheet);
			Templates template = factory.newTemplates(xsl);
			Transformer transformer = template.newTransformer();
			
			Source xml = new StreamSource(datafile);
      Result result = new StreamResult(OUTPUT_PATH + "autoLeistungsnachweis.html");
      transformer.transform(xml, result);
      
	  	// transform XHTML document to FO document
	    Source htmlDocument = new StreamSource(OUTPUT_PATH + "autoLeistungsnachweis.html");    
	  	Source foStylesheet = new StreamSource("resources/xml/makeFOLeistungsnachweis.xsl");
	  	Document foDocument = PrintManager.transform(foStylesheet, htmlDocument);
	  	writeDocument(foDocument, "autoLeistungsnachweis.fo");
	      
	    //render FO document to PDF document
	    File htmlfile = new File(OUTPUT_PATH + "autoLeistungsnachweis.fo");
	    Source html = new StreamSource(htmlfile);
	  	File pdfFile = new File(OUTPUT_PATH + "/fertigLeistungsnachweis.pdf");
	  	PrintManager.renderToPDF(html, pdfFile);
  	} catch (Exception ex) {
  	Logger.getLogger(PrintManagerLeistungsnachweis.class.getName()).log(
  			Level.SEVERE, null, ex);
		}
	}
	
	static void writeDocument(Document document, String name) {
		try {
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new File(OUTPUT_PATH + name));
		transformer.transform(source, result);
		} catch (TransformerException ex) {
		Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE,
			null, ex);
		}
	}

}
