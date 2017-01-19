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

public class PrintManagerTest {

	private static final String OUTPUT_PATH = "./resources/output/";

	public static void main(String[] args) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Map<String, Object> params = new HashMap<>();
			params.put("moduleDocument", builder.parse(PrintManagerTest.class
					.getResourceAsStream("/xml/module.xml")));

			// transform template to XHTML document
			Source stylesheet = new StreamSource(
					PrintManagerTest.class
							.getResourceAsStream("/xml/resolve.xsl"));
			Source template = new StreamSource(
					PrintManagerTest.class
							.getResourceAsStream("/xml/diplomaTemplate.xml"));
			Document xhtmlDocument = PrintManager.transform(stylesheet,
					template, params);
			writeDocument(xhtmlDocument, "document.html");

			// transform XHTML document to FO document
			stylesheet = new StreamSource(
					PrintManagerTest.class
							.getResourceAsStream("/xml/makeFO.xsl"));
			Document foDocument = PrintManager.transform(stylesheet,
					new DOMSource(xhtmlDocument));
			writeDocument(foDocument, "diploma.fo");

			// render FO document to PDF document
			File pdfFile = new File(OUTPUT_PATH + "/diploma.pdf");
			PrintManager.renderToPDF(new DOMSource(foDocument), pdfFile);
		} catch (Exception ex) {
			Logger.getLogger(PrintManagerTest.class.getName()).log(
					Level.SEVERE, null, ex);
		}
	}

	static void writeDocument(Document document, String name) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(OUTPUT_PATH + name));
			transformer.transform(source, result);
		} catch (TransformerException ex) {
			Logger.getLogger(PrintManager.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}
}
