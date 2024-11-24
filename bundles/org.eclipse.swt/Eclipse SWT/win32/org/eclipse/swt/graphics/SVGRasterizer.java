package org.eclipse.swt.graphics;

import static java.awt.RenderingHints.*;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.eclipse.swt.internal.*;
import org.w3c.dom.*;

import com.github.weisj.jsvg.*;
import com.github.weisj.jsvg.geometry.size.*;
import com.github.weisj.jsvg.parser.*;

/**
 * @since 3.128
 */
public class SVGRasterizer {

	private final static Map<Object, Object> RENDERING_HINTS = Map.of(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON,
			KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY, KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY,
			KEY_DITHERING, VALUE_DITHER_DISABLE, KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON, KEY_INTERPOLATION,
			VALUE_INTERPOLATION_BICUBIC, KEY_RENDERING, VALUE_RENDER_QUALITY, KEY_STROKE_CONTROL, VALUE_STROKE_PURE,
			KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);

	static BufferedImage rasterizeSVG(byte[] bytes, boolean shouldDisable) throws IOException {
		int zoom = DPIUtil.getNativeDeviceZoom();
		SVGLoader loader = new SVGLoader();
		SVGDocument svgDocument = null;
		if (isSVGFile(bytes)) {
			try (InputStream stream = new ByteArrayInputStream(bytes)) {
				if(shouldDisable) {
					InputStream disabledStream = applyDisabledLook(stream);
					svgDocument = loader.load(disabledStream, null, LoaderContext.createDefault());
				} else {
					svgDocument = loader.load(stream, null, LoaderContext.createDefault());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (svgDocument != null) {
				double scalingFactor = zoom / 100.0;
				FloatSize size = svgDocument.size();
				double originalWidth = size.getWidth();
				double originalHeight = size.getHeight();
				int newWidth;
				int newHeight;
				double scaledWidth = originalWidth * scalingFactor;
				double scaledHeight = originalHeight * scalingFactor;
				if (scaledWidth - Math.floor(scaledWidth) >= 0.5) {
					newWidth = (int) Math.ceil(originalWidth * scalingFactor);
				} else {
					newWidth = (int) Math.floor(originalWidth * scalingFactor);
				}
				if (scaledHeight - Math.floor(scaledHeight) >= 0.5) {
					newHeight = (int) Math.ceil(originalHeight * scalingFactor);
				} else {
					newHeight = (int) Math.floor(originalHeight * scalingFactor);
				}
				BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = image.createGraphics();
				g.setRenderingHints(RENDERING_HINTS);
				g.scale(scalingFactor, scalingFactor);
				svgDocument.render(null, g);
				g.dispose();
//				image = applyDisabledGrayEffect(image);
				return image;
			}
		}
		return null;
	}

//	public static BufferedImage applyDisabledGrayEffect(BufferedImage image) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//        BufferedImage processedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                int argb = image.getRGB(x, y);
//                int alpha = (argb >> 24) & 0xff;
//                int red = (argb >> 16) & 0xff;
//                int green = (argb >> 8) & 0xff;
//                int blue = argb & 0xff;
//                float[] hsb = java.awt.Color.RGBtoHSB(red, green, blue, null);
//                hsb[1] *= 0.0f;  // Lower saturation (0.0f = grayscale, 1.0f = full saturation)
//                hsb[2] = Math.min(hsb[2] * 1.1f, 1.0f);  // Increase brightness (cap at 1.0 for max brightness)
//                int newRgb = java.awt.Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
//                processedImage.setRGB(x, y, (newRgb & 0xFFFFFF) | (alpha << 24));
//            }
//        }
//        return processedImage;
//    }

	private static InputStream applyDisabledLook(InputStream svgInputStream) throws Exception {
		Document svgDocument = parseSVG(svgInputStream);
		addDisabledFilter(svgDocument);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			writeSVG(svgDocument, outputStream);
			return new ByteArrayInputStream(outputStream.toByteArray());
		}
	}

	private static Document parseSVG(InputStream inputStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder.parse(inputStream);
	}

	private static void addDisabledFilter(Document document) {
		Element defs = (Element) document.getElementsByTagName("defs").item(0);
		if (defs == null) {
			defs = document.createElement("defs");
			document.getDocumentElement().appendChild(defs);
		}

		Element filter = document.createElement("filter");
		filter.setAttribute("id", "disabledLook");

		Element colorMatrix = document.createElement("feColorMatrix");
		colorMatrix.setAttribute("type", "saturate");
		colorMatrix.setAttribute("values", "0");
		filter.appendChild(colorMatrix);

		Element componentTransfer = document.createElement("feComponentTransfer");
		for (String channel : new String[] { "R", "G", "B" }) {
			Element func = document.createElement("feFunc" + channel);
			func.setAttribute("type", "linear");
			func.setAttribute("slope", "0.64");
			func.setAttribute("intercept", "0.1");
			componentTransfer.appendChild(func);
		}
		filter.appendChild(componentTransfer);
		defs.appendChild(filter);
		document.getDocumentElement().setAttribute("filter", "url(#disabledLook)");
	}

	private static void writeSVG(Document document, OutputStream outputStream) throws Exception {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.transform(new DOMSource(document), new StreamResult(outputStream));
	}

	public static boolean isSVGFile(byte[] data) throws IOException {
		String content = new String(data, 0, Math.min(data.length, 1024), StandardCharsets.UTF_8);
		return content.contains("<svg");
	}

	public static boolean isSVGFile(URL url) throws IOException {
		if (url == null) {
			throw new IllegalArgumentException("URL cannot be null");
		}
		try (InputStream stream = url.openStream()) {
			byte[] data = stream.readNBytes(1024);
			String content = new String(data, 0, Math.min(data.length, 1024), StandardCharsets.UTF_8);
			return content.contains("<svg");
		}
	}

	public static boolean isSVGFile(InputStream inputStream) throws IOException {
	    if (inputStream == null) {
	        throw new IllegalArgumentException("InputStream cannot be null");
	    }
	    byte[] data = inputStream.readNBytes(1024);
	    String content = new String(data, 0, Math.min(data.length, 1024), StandardCharsets.UTF_8);
	    return content.contains("<svg");
	}
}
