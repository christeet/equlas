package equals;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

public class PrintManagerLeistungsnachweis {

	private static final String PATH = "resources";

	public PrintManagerLeistungsnachweis() {
	
	}
	
	public void generateXMLDocument() {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Map<String, Object> params = new HashMap<>();
			params.put("moduleDocument", builder.parse(PATH + "/xml/module.xml"));

			// transform template to XHTML document
			Source stylesheet = new StreamSource(PATH + "/xml/resolveModuleTemplate.xsl");
			Source template = new StreamSource(PATH + "/xml/leistungsnachweisTemplate.xml");
			Document xhtmlDocument = PrintManager.transform(stylesheet, template, params);
			writeDocument(xhtmlDocument, PATH + "/output/autoLeistungsnachweis.html");

			// transform XHTML document to FO document
			Source htmlDocument = new StreamSource(PATH + "/output/autoLeistungsnachweis.html");
			Source foStylesheet = new StreamSource(PATH + "/xml/makeFODocuments.xsl");
			Document foDocument = PrintManager.transform(foStylesheet, htmlDocument);
			writeDocument(foDocument, PATH + "/output/autoLeistungsnachweis.fo");

			// render FO document to PDF document
			Source html = new StreamSource(PATH + "/output/autoLeistungsnachweis.fo");
			File pdfFile = new File(PATH + "/output/fertigLeistungsnachweis.pdf");
			PrintManager.renderToPDF(html, pdfFile);
		} catch (Exception ex) {
			Logger.getLogger(PrintManagerLeistungsnachweis.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	static void writeDocument(Document document, String name) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(name);
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
