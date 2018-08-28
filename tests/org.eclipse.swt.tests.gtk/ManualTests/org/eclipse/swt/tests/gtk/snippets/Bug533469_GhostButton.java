/*******************************************************************************
 * Copyright (c) 2018 Red Hat and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Red Hat - initial API and implementation
 *******************************************************************************/
package org.eclipse.swt.tests.gtk.snippets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/*
 * On GTK3.18: Empty Shell
 * On GTK3.20+: Button/Text/etc is visible when size is 0x0
 */
public class Bug533469_GhostButton {
	public static void main (String [] args) {
		Display display = new Display ();
		Shell shell = new Shell(display);

		Button button = new Button(shell, SWT.None);
		button.setText("Hi"); // size is not set, it should be 0x0
//		button.setSize(100, 100);

//		Text text = new Text(shell, SWT.None);
//		text.setText("Hello");
//		text.setSize(100, 100);

//		System.out.println(button.getSize());

		shell.setSize(300, 300);
		shell.open ();
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
