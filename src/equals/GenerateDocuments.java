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

public class GenerateDocuments {

	// private static final String OUTPUT_PATH = "resources/output/";
	private static final String PATH = "resources";
	private File userPDFPath;
	private String document;

	public GenerateDocuments(File userPDFPath, String document) {
		this.userPDFPath = userPDFPath;
		this.document = document;
	}
		
	public void generateXMLDocument() {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Map<String, Object> params = new HashMap<>();
			params.put("moduleDocument", builder.parse(PATH + "/xml/module.xml"));

			// transform template to XHTML document
			Source stylesheet = new StreamSource(PATH + "/xml/resolveModuleTemplate.xsl");
			Source template = new StreamSource(PATH + "/xml/" + document + "Template.xml");
			Document xhtmlDocument = PrintManager.transform(stylesheet, template, params);
			writeDocument(xhtmlDocument, PATH + "/output/auto" + document + ".html");

			// transform XHTML document to FO document
			Source htmlDocument = new StreamSource(PATH + "/output/auto" + document + ".html");
			Source foStylesheet = new StreamSource(PATH + "/xml/makeFODocuments.xsl");
			Document foDocument = PrintManager.transform(foStylesheet, htmlDocument);
			writeDocument(foDocument, PATH + "/output/auto" + document + ".fo");

			// render FO document to PDF document
			Source html = new StreamSource(PATH + "/output/auto" + document + ".fo");
			File pdfFile = new File(userPDFPath + "/" + document + ".pdf");
			PrintManager.renderToPDF(html, pdfFile);
		} catch (Exception ex) {
			Logger.getLogger(GenerateDocuments.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	static void writeDocument(Document document, String name) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(name));
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
