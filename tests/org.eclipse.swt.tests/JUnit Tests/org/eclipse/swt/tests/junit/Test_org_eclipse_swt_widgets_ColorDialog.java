/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.tests.junit;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Automated Test Suite for class org.eclipse.swt.widgets.ColorDialog
 *
 * @see org.eclipse.swt.widgets.ColorDialog
 */
public class Test_org_eclipse_swt_widgets_ColorDialog extends Test_org_eclipse_swt_widgets_Dialog {

ColorDialog colorDialog;

@Override
@BeforeEach
public void setUp() {
	super.setUp();
	colorDialog = new ColorDialog(shell, SWT.NULL);
	setDialog(colorDialog);
}

@Test
public void test_ConstructorLorg_eclipse_swt_widgets_Shell() {
	new ColorDialog(shell);

	assertThrows(IllegalArgumentException.class,()-> new ColorDialog(null),"No exception thrown for parent == null");
}

@Test
public void test_ConstructorLorg_eclipse_swt_widgets_ShellI() {
	new ColorDialog(shell, SWT.NULL);

	assertThrows(IllegalArgumentException.class,()-> new ColorDialog(null, SWT.NULL),"No exception thrown for parent == null");
}

@Test
public void test_setRGBLorg_eclipse_swt_graphics_RGB() {
	RGB rgb = new RGB(0, 0, 0);

	assertNull(colorDialog.getRGB());

	colorDialog.setRGB(rgb);
	assertTrue(colorDialog.getRGB() == rgb);

	colorDialog.setRGB(null);
	assertNull(colorDialog.getRGB());
}
}
