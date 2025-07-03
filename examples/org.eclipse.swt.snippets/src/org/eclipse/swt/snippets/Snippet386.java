/*******************************************************************************
 * Copyright (c) 2025 Vector Informatik GmbH and others.
 *
 * This program and the accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Michael Bangas (Vector Informatik GmbH) - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.snippets;

import java.io.*;
import java.util.*;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.internal.*;
import org.eclipse.swt.internal.DPIUtil.*;
import org.eclipse.swt.widgets.*;

public class Snippet386 {

	public static void main(String[] args) {
		drawWithImageFileNameProvider();
		drawWithImageDataProvider();
	}

	private static void drawWithImageFileNameProvider() {
		ImageFileNameProvider provider = createImageFileNameProvider();
		Image image = new Image(Display.getDefault(), provider);
		createShellWithImage(image, "Snippet 386 - ImageFileNameProvider");
	}

	private static void drawWithImageDataProvider() {
		ImageDataProvider provider = createImageDataProvider();
		Image image = new Image(Display.getDefault(), provider);
		createShellWithImage(image, "Snippet 386 - ImageDataProvider");
	}

	private static ImageFileNameProvider createImageFileNameProvider() {
		return new ImageFileNameAtSizeProvider() {
			@Override
			public Optional<String> getImagePath(int targetWidth, int targetHeight) {
				return Optional.of("resources/Snippet386/collapseall.svg");
			}

			@Override
			public String getImagePath(int zoom) {
				return "resources/Snippet386/collapseall.svg";
			}
		};
	}

	private static ImageDataProvider createImageDataProvider() {
		return new ImageDataAtSizeProvider() {
			@SuppressWarnings("restriction")
			@Override
			public Optional<ImageData> getImageData(int targetWidth, int targetHeight) {
				try (InputStream stream = new FileInputStream("resources/Snippet386/collapseall.svg")) {
					return Optional.of(NativeImageLoader.load(stream, new ImageLoader(), targetWidth, targetHeight));
				} catch (IOException e) {
					SWT.error(SWT.ERROR_IO, e);
				}
				return null;
			}

			@SuppressWarnings("restriction")
			@Override
			public ImageData getImageData(int zoom) {
				try (InputStream stream = new FileInputStream("resources/Snippet386/collapseall.svg")) {
					return NativeImageLoader.load(new ElementAtZoom<>(stream, 100), new ImageLoader(), 100).get(0).element();
				} catch (IOException e) {
					SWT.error(SWT.ERROR_IO, e);
				}
				return null;
			}
		};
	}

	private static void createShellWithImage(Image image, String title) {
		Shell shell = new Shell(Display.getDefault(), SWT.SHELL_TRIM | SWT.DOUBLE_BUFFERED);
		shell.setText(title);

		shell.addListener(SWT.Paint, e -> {
			Rectangle rect = image.getBounds();
			e.gc.drawImage(image, 0, 0, rect.width, rect.height, 0, 0, 100, 200);
		});

		shell.setSize(600, 400);
		shell.open();
		while (!shell.isDisposed()) {
			if (!Display.getDefault().readAndDispatch())
				Display.getDefault().sleep();
		}
		Display.getDefault().dispose();
	}
}
