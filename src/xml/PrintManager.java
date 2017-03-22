package xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXResult;

import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PrintManager extends DefaultHandler {

	private static final String TRANSFORMER_FACTORY = "com.saxonica.config.ProfessionalTransformerFactory";
	private static final URI BASE_URI = new File("temp").toURI();
	private static TransformerFactory transformerFactory;
	private static FopFactory fopFactory;

	static {
		try {
			transformerFactory = TransformerFactory.newInstance(
					TRANSFORMER_FACTORY, PrintManager.class.getClassLoader());
			
			fopFactory = FopFactory.newInstance(BASE_URI,
					PrintManager.class.getResourceAsStream("config.xml"));
		} catch (SAXException | IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	// xsl stylesheet transformation
	public static Document transform(Source stylesheet, Source data)
			throws TransformerException {
		Document document = transform(stylesheet, data, Collections.emptyMap());
		return document;
	}

	// xsl stylesheet transformation with parameters
	public static Document transform(Source stylesheet, Source data,
			Map<String, Object> params) throws TransformerException {
		Transformer transformer = transformerFactory.newTransformer(stylesheet);
		for (String name : params.keySet()) {
			transformer.setParameter(name, params.get(name));
		}
		DOMResult result = new DOMResult();
		transformer.transform(data, result);
		return (Document) result.getNode();
	}

	// create pdf
	public static FileOutputStream renderToPDF(Source data, File pdfFile)
			throws Exception {
		FileOutputStream out = new FileOutputStream(pdfFile);
		Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(data, new SAXResult(fop.getDefaultHandler()));
		return out;
	}
}
