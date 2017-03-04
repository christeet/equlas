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

public class PrintManagerCertificate {

	// private static final String OUTPUT_PATH = "resources/output/";
	private static final String PATH = "/Users/tgdetch1/equalsSd/resources/";

	public static void main(String[] args) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Map<String, Object> params = new HashMap<>();
			params.put("moduleDocument", builder.parse(PATH + "xml/module.xml"));

			// transform template to XHTML document
			Source stylesheet = new StreamSource(PATH + "xml/resolveCertificate.xsl");
			Source template = new StreamSource(PATH + "xml/certificateTemplate.xml");
			Document xhtmlDocument = PrintManager.transform(stylesheet, template, params);
			writeDocument(xhtmlDocument, PATH + "output/autoCertificate.html");

			// transform XHTML document to FO document
			Source htmlDocument = new StreamSource(PATH + "output/autoCertificate.html");
			Source foStylesheet = new StreamSource(PATH + "xml/makeFOCertificate.xsl");
			Document foDocument = PrintManager.transform(foStylesheet, htmlDocument);
			writeDocument(foDocument, PATH + "output/autoCertificate.fo");

			// render FO document to PDF document
			Source html = new StreamSource(PATH + "output/autoCertificate.fo");
			File pdfFile = new File(PATH + "output/fertigCertificate.pdf");
			PrintManager.renderToPDF(html, pdfFile);
		} catch (Exception ex) {
			Logger.getLogger(PrintManagerCertificate.class.getName()).log(Level.SEVERE, null, ex);
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
