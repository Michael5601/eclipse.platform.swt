package org.eclipse.swt.graphics;

import java.awt.image.*;
import java.io.*;

/**
 * @since 3.128
 */
public interface ISVGRasterizer {
	public BufferedImage rasterizeSVG(byte[] bytes, int zoom) throws IOException;

	public boolean isSVGFile(InputStream inputStream) throws IOException;
}
