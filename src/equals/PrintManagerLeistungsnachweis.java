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

	private static final String OUTPUT_PATH = "./resources/output/";

	public static void main(String[] args) {
		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Map<String, Object> params = new HashMap<>();
			params.put("moduleDocument", builder.parse(PrintManagerLeistungsnachweis.class
					.getResourceAsStream("/xml/module_orig.xml")));

			// transform template to XHTML document
			Source stylesheet = new StreamSource(
					PrintManagerLeistungsnachweis.class
							.getResourceAsStream("/xml/resolveLeistungsnachweis.xsl"));
			Source template = new StreamSource(
					PrintManagerLeistungsnachweis.class
							.getResourceAsStream("/xml/leistungsnachweisTemplate.xml"));
			Document xhtmlDocument = PrintManager.transform(stylesheet,
					template, params);
			writeDocument(xhtmlDocument, "documentCertifcate.html");

			// transform XHTML document to FO document
			stylesheet = new StreamSource(
					PrintManagerLeistungsnachweis.class
							.getResourceAsStream("/xml/makeFOLeistungsnachweis.xsl"));
			Document foDocument = PrintManager.transform(stylesheet,
					new DOMSource(xhtmlDocument));
			writeDocument(foDocument, "leistungsnachweis.fo");

			// render FO document to PDF document
			File pdfFile = new File(OUTPUT_PATH + "/leistungsnachweis.pdf");
			PrintManager.renderToPDF(new DOMSource(foDocument), pdfFile);
		} catch (Exception ex) {
			Logger.getLogger(PrintManagerLeistungsnachweis.class.getName()).log(
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
