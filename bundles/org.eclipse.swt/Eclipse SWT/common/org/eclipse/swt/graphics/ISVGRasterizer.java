/*******************************************************************************
 * Copyright (c) 2024 Vector Informatik GmbH and others.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors: Vector Informatik GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.graphics;

import java.awt.image.*;
import java.io.*;

/**
 * Defines the interface for an SVG rasterizer, responsible for converting SVG
 * data into rasterized images.
 *
 * @since 3.129
 */
public interface ISVGRasterizer {

	/**
	 * Rasterizes an SVG image from the provided byte array, using the specified
	 * zoom factor.
	 *
	 * @param bytes         the SVG image as a byte array.
	 * @param scalingFactor the scaling ratio e.g. 2.0 for doubled size.
	 * @return a {@link BufferedImage} containing the rasterized image, or
	 *         {@code null} if the input is not a valid SVG file or cannot be
	 *         processed.
	 * @throws IOException if an error occurs while reading the SVG data.
	 */
	public BufferedImage rasterizeSVG(byte[] bytes, float scalingFactor) throws IOException;

}
