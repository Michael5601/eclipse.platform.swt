/*******************************************************************************
 * Copyright (c) 2000, 2025 IBM Corporation and others.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

/* Note: This file was auto-generated by org.eclipse.swt.tools.internal.JNIGenerator */
/* DO NOT EDIT - your changes will be lost. */

#include "osversion.h"

#ifndef NO_OSVERSIONINFOEX
void cacheOSVERSIONINFOEXFields(JNIEnv *env, jobject lpObject);
OSVERSIONINFOEX *getOSVERSIONINFOEXFields(JNIEnv *env, jobject lpObject, OSVERSIONINFOEX *lpStruct);
void setOSVERSIONINFOEXFields(JNIEnv *env, jobject lpObject, OSVERSIONINFOEX *lpStruct);
#define OSVERSIONINFOEX_sizeof() sizeof(OSVERSIONINFOEX)
#else
#define cacheOSVERSIONINFOEXFields(a,b)
#define getOSVERSIONINFOEXFields(a,b,c) NULL
#define setOSVERSIONINFOEXFields(a,b,c)
#define OSVERSIONINFOEX_sizeof() 0
#endif

