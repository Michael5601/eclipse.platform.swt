package org.eclipse.swt.graphics;

import static java.awt.RenderingHints.*;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

import org.eclipse.swt.internal.*;

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

	static BufferedImage rasterizeSVG(byte[] bytes) throws IOException {
		int zoom = DPIUtil.getNativeDeviceZoom();
		SVGLoader loader = new SVGLoader();
		SVGDocument svgDocument = null;
		if (isSVGFile(bytes)) {
			try (InputStream stream = new ByteArrayInputStream(bytes)) {
				svgDocument = loader.load(stream, null, LoaderContext.createDefault());
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
//		    newWidth = (int) (originalWidth * scalingFactor);
//		    newHeight = (int) (originalHeight * scalingFactor);
				BufferedImage image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = image.createGraphics();
				g.setRenderingHints(RENDERING_HINTS);
				AffineTransform transform = AffineTransform.getScaleInstance(scalingFactor, scalingFactor);
				g.setTransform(transform);
				svgDocument.render(null, g);
				g.dispose();
				return image;
			}
		}
		return null;
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
	        byte[] data = stream.readNBytes(1024); // Read only the first 1024 bytes for efficiency
	        String content = new String(data, StandardCharsets.UTF_8);
	        return content.contains("<svg");
	    }
	}
}
