package org.eclipse.swt.svg;

import static java.awt.RenderingHints.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.charset.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.swt.graphics.ISVGRasterizer;
import org.eclipse.swt.graphics.SVGRasterizerRegistry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.github.weisj.jsvg.*;
import com.github.weisj.jsvg.geometry.size.*;
import com.github.weisj.jsvg.parser.*;

/**
 * A rasterizer implementation for converting SVG data into rasterized images.
 * This class implements the {@code ISVGRasterizer} interface.
 * 
 * @since 3.128
 */
public class SVGRasterizer implements ISVGRasterizer {
	
	private SVGLoader svgLoader;

	/**
     * Initializes the SVG rasterizer by registering an instance of this rasterizer 
     * with the {@link SVGRasterizerRegistry}.
     */
	public static void intializeSVGRasterizer() {
		SVGRasterizerRegistry.register(new SVGRasterizer());
	}

	private final static Map<Object, Object> RENDERING_HINTS = Map.of(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON,
			KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY, KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY,
			KEY_DITHERING, VALUE_DITHER_DISABLE, KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON, KEY_INTERPOLATION,
			VALUE_INTERPOLATION_BICUBIC, KEY_RENDERING, VALUE_RENDER_QUALITY, KEY_STROKE_CONTROL, VALUE_STROKE_PURE,
			KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);

	@Override
	public BufferedImage rasterizeSVG(byte[] bytes, int zoom) throws IOException {
		if(svgLoader == null) {
			svgLoader = new SVGLoader();
		}
		if (this.isSVGFile(bytes)) {
			try (InputStream stream = new ByteArrayInputStream(bytes)) {
				return rasterize(stream, zoom);
			}
		}
		return null;
	}

	private BufferedImage rasterize(InputStream stream, int zoom) throws IOException {
		SVGDocument svgDocument = null;
		svgDocument = svgLoader.load(stream, null, LoaderContext.createDefault());
		if (svgDocument != null) {
			double scalingFactor = zoom / 100.0;
			FloatSize size = svgDocument.size();
			double originalWidth = size.getWidth();
			double originalHeight = size.getHeight();
			int scaledWidth = (int) Math.round(originalWidth * scalingFactor);
			int scaledHeight = (int) Math.round(originalHeight * scalingFactor);
			BufferedImage image = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setRenderingHints(RENDERING_HINTS);
			g.scale(scalingFactor, scalingFactor);
			svgDocument.render(null, g);
			g.dispose();
			return image;
		}
		return null;
	}
	
	@Override
	public BufferedImage rasterizeDisabledSVG(byte[] bytes, int zoom) throws IOException {
		if(svgLoader == null) {
			svgLoader = new SVGLoader();
		}
		if (this.isSVGFile(bytes)) {
			try (InputStream stream = new ByteArrayInputStream(bytes)) {
				InputStream disabledStream = applyDisabledLook(stream);
				return rasterize(disabledStream, zoom);
			}
		}
		return null;
	}
	
	@Override
	public BufferedImage rasterizeGraySVG(byte[] bytes, int zoom) throws IOException {
		if(svgLoader == null) {
			svgLoader = new SVGLoader();
		}
		if (this.isSVGFile(bytes)) {
			try (InputStream stream = new ByteArrayInputStream(bytes)) {
				InputStream disabledStream = applyGrayLook(stream);
				return rasterize(disabledStream, zoom);
			}
		}
		return null;
	}
	
	private static InputStream applyDisabledLook(InputStream svgInputStream) throws IOException {
		Document svgDocument = parseSVG(svgInputStream);
		addDisabledFilter(svgDocument);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			writeSVG(svgDocument, outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
	}
	
	private static InputStream applyGrayLook(InputStream svgInputStream) throws IOException {
		Document svgDocument = parseSVG(svgInputStream);
		addGrayFilter(svgDocument);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			writeSVG(svgDocument, outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
	}

	private static Document parseSVG(InputStream inputStream) throws IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			return builder.parse(inputStream);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new IOException(e.getMessage());
		}
	}

	private static void addDisabledFilter(Document document) {
		addFilter(document, 0.64f, 0.4f);
	}
	
	private static void addGrayFilter(Document document) {
		addFilter(document, 0.64f, 0.1f);
	}

	private static void addFilter(Document document, float slope, float intercept) {
		Element defs = (Element) document.getElementsByTagName("defs").item(0);
		if (defs == null) {
			defs = document.createElement("defs");
			document.getDocumentElement().appendChild(defs);
		}

		Element filter = document.createElement("filter");
		filter.setAttribute("id", "customizedLook");

		Element colorMatrix = document.createElement("feColorMatrix");
		colorMatrix.setAttribute("type", "saturate");
		colorMatrix.setAttribute("values", "0");
		filter.appendChild(colorMatrix);

		Element componentTransfer = document.createElement("feComponentTransfer");
		for (String channel : new String[] { "R", "G", "B" }) {
			Element func = document.createElement("feFunc" + channel);
			func.setAttribute("type", "linear");
			func.setAttribute("slope", Float.toString(slope));
			func.setAttribute("intercept", Float.toString(intercept));
			componentTransfer.appendChild(func);
		}
		filter.appendChild(componentTransfer);
		defs.appendChild(filter);
		document.getDocumentElement().setAttribute("filter", "url(#customizedLook)");
	}

	private static void writeSVG(Document document, OutputStream outputStream) throws IOException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(outputStream));
		} catch (TransformerException e) {
			throw new IOException(e.getMessage());
		}
	}

	private boolean isSVGFile(byte[] data) throws IOException {
		String content = new String(data, 0, Math.min(data.length, 512), StandardCharsets.UTF_8);
		return content.contains("<svg");
	}

	@Override
	public boolean isSVGFile(InputStream inputStream) throws IOException {
		if (inputStream == null) {
			throw new IllegalArgumentException("InputStream cannot be null");
		}
		byte[] data = inputStream.readNBytes(512);
		return isSVGFile(data);
	}
}
