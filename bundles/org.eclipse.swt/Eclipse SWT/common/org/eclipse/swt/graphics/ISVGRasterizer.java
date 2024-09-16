package org.eclipse.swt.graphics;

import java.awt.image.*;
import java.io.*;

/**
 * Defines the interface for an SVG rasterizer, responsible for converting
 * SVG data into rasterized images.
 *
 * @since 3.129
 */
public interface ISVGRasterizer {

	/**
     * Rasterizes an SVG image from the provided byte array, using the specified zoom factor.
     *
     * @param bytes the SVG image as a byte array.
     * @param zoom  the zoom factor.
     * @return a {@link BufferedImage} containing the rasterized image, or {@code null} if the input
     *         is not a valid SVG file or cannot be processed.
     * @throws IOException if an error occurs while reading the SVG data.
     */
	public BufferedImage rasterizeSVG(byte[] bytes, int zoom) throws IOException;

	/**
     * Determines whether the given {@link InputStream} contains a SVG file.
     *
     * @param inputStream the input stream to check.
     * @return {@code true} if the input stream contains SVG content; {@code false} otherwise.
     * @throws IOException if an error occurs while reading the stream.
     * @throws IllegalArgumentException if the input stream is {@code null}.
     */
	public boolean isSVGFile(InputStream inputStream) throws IOException;
}
