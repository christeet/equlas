package xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

public class GenerateDocuments {

	private static final String PATH = "temp";
	private File userPDFPath;
	private String document;

	public GenerateDocuments(File userPDFPath, String document) {
		this.userPDFPath = userPDFPath;
		this.document = document;
	}
		
	public void generateXMLDocument() {
		try {
			// copy images to temporary
			File tempDir = new File(PATH);
			
			File temporaryFile = new File(tempDir, "logo.png");
			InputStream templateStream = getClass().getResourceAsStream("resources/logo.png");
			IOUtils.copy(templateStream, new FileOutputStream(temporaryFile));
			
			temporaryFile = new File(tempDir, "schmidhauser.jpg");
			templateStream = getClass().getResourceAsStream("resources/schmidhauser.jpg");
			IOUtils.copy(templateStream, new FileOutputStream(temporaryFile));
			//String absolutePath = temporaryFile.getAbsolutePath();
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Map<String, Object> params = new HashMap<>();
			params.put("moduleDocument", builder.parse(PATH + "/module.xml"));

			// transform template to XHTML document
			Source stylesheet = new StreamSource(
					GenerateDocuments.class.getResourceAsStream("resolveModuleTemplate.xsl"));
			Source template = new StreamSource(
					GenerateDocuments.class.getResourceAsStream(document + "Template.xml"));
			Document xhtmlDocument = PrintManager.transform(stylesheet, template, params);
			writeDocument(xhtmlDocument, PATH + "/auto" + document + ".html");

			// transform XHTML document to FO document
			Source htmlDocument = new StreamSource(PATH + "/auto" + document + ".html");
			Source foStylesheet = new StreamSource(
					GenerateDocuments.class.getResourceAsStream("makeFODocuments.xsl"));
			Document foDocument = PrintManager.transform(foStylesheet, htmlDocument);
			writeDocument(foDocument, PATH + "/auto" + document + ".fo");

			// render FO document to PDF document
			Source html = new StreamSource(PATH + "/auto" + document + ".fo");
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
