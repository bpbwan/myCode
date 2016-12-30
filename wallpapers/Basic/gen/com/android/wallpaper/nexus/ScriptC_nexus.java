/*
 * Copyright (C) 2011-2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * This file is auto-generated. DO NOT MODIFY!
 * The source Renderscript file: /home/bpb/Flyaudio/ac8317/BPBGITHUB/myCode/wallpapers/Basic/src/com/android/wallpaper/nexus/nexus.rs
 */
package com.android.wallpaper.nexus;

import android.renderscript.*;
import android.content.res.Resources;

/**
 * @hide
 */
public class ScriptC_nexus extends ScriptC {
    private static final String __rs_resource_name = "nexus";
    // Constructor
    public  ScriptC_nexus(RenderScript rs) {
        this(rs,
             rs.getApplicationContext().getResources(),
             rs.getApplicationContext().getResources().getIdentifier(
                 __rs_resource_name, "raw",
                 rs.getApplicationContext().getPackageName()));
    }

    public  ScriptC_nexus(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
    }

    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_F32;
    private FieldPacker __rs_fp_I32;
    private FieldPacker __rs_fp_PROGRAM_FRAGMENT;
    private FieldPacker __rs_fp_PROGRAM_STORE;
    private final static int mExportVarIdx_gWorldScaleX = 0;
    private float mExportVar_gWorldScaleX;
    public synchronized void set_gWorldScaleX(float v) {
        setVar(mExportVarIdx_gWorldScaleX, v);
        mExportVar_gWorldScaleX = v;
    }

    public float get_gWorldScaleX() {
        return mExportVar_gWorldScaleX;
    }

    private final static int mExportVarIdx_gWorldScaleY = 1;
    private float mExportVar_gWorldScaleY;
    public synchronized void set_gWorldScaleY(float v) {
        setVar(mExportVarIdx_gWorldScaleY, v);
        mExportVar_gWorldScaleY = v;
    }

    public float get_gWorldScaleY() {
        return mExportVar_gWorldScaleY;
    }

    private final static int mExportVarIdx_gXOffset = 2;
    private float mExportVar_gXOffset;
    public synchronized void set_gXOffset(float v) {
        setVar(mExportVarIdx_gXOffset, v);
        mExportVar_gXOffset = v;
    }

    public float get_gXOffset() {
        return mExportVar_gXOffset;
    }

    private final static int mExportVarIdx_gIsPreview = 3;
    private int mExportVar_gIsPreview;
    public synchronized void set_gIsPreview(int v) {
        setVar(mExportVarIdx_gIsPreview, v);
        mExportVar_gIsPreview = v;
    }

    public int get_gIsPreview() {
        return mExportVar_gIsPreview;
    }

    private final static int mExportVarIdx_gMode = 4;
    private int mExportVar_gMode;
    public synchronized void set_gMode(int v) {
        setVar(mExportVarIdx_gMode, v);
        mExportVar_gMode = v;
    }

    public int get_gMode() {
        return mExportVar_gMode;
    }

    private final static int mExportVarIdx_gPFTexture = 5;
    private ProgramFragment mExportVar_gPFTexture;
    public synchronized void set_gPFTexture(ProgramFragment v) {
        setVar(mExportVarIdx_gPFTexture, v);
        mExportVar_gPFTexture = v;
    }

    public ProgramFragment get_gPFTexture() {
        return mExportVar_gPFTexture;
    }

    private final static int mExportVarIdx_gPSBlend = 6;
    private ProgramStore mExportVar_gPSBlend;
    public synchronized void set_gPSBlend(ProgramStore v) {
        setVar(mExportVarIdx_gPSBlend, v);
        mExportVar_gPSBlend = v;
    }

    public ProgramStore get_gPSBlend() {
        return mExportVar_gPSBlend;
    }

    private final static int mExportVarIdx_gPFTexture565 = 7;
    private ProgramFragment mExportVar_gPFTexture565;
    public synchronized void set_gPFTexture565(ProgramFragment v) {
        setVar(mExportVarIdx_gPFTexture565, v);
        mExportVar_gPFTexture565 = v;
    }

    public ProgramFragment get_gPFTexture565() {
        return mExportVar_gPFTexture565;
    }

    private final static int mExportVarIdx_gTBackground = 8;
    private Allocation mExportVar_gTBackground;
    public synchronized void set_gTBackground(Allocation v) {
        setVar(mExportVarIdx_gTBackground, v);
        mExportVar_gTBackground = v;
    }

    public Allocation get_gTBackground() {
        return mExportVar_gTBackground;
    }

    private final static int mExportVarIdx_gTPulse = 9;
    private Allocation mExportVar_gTPulse;
    public synchronized void set_gTPulse(Allocation v) {
        setVar(mExportVarIdx_gTPulse, v);
        mExportVar_gTPulse = v;
    }

    public Allocation get_gTPulse() {
        return mExportVar_gTPulse;
    }

    private final static int mExportVarIdx_gTGlow = 10;
    private Allocation mExportVar_gTGlow;
    public synchronized void set_gTGlow(Allocation v) {
        setVar(mExportVarIdx_gTGlow, v);
        mExportVar_gTGlow = v;
    }

    public Allocation get_gTGlow() {
        return mExportVar_gTGlow;
    }

    private final static int mExportFuncIdx_initPulses = 0;
    public void invoke_initPulses() {
        invoke(mExportFuncIdx_initPulses);
    }

    private final static int mExportFuncIdx_addTap = 1;
    public void invoke_addTap(int x, int y) {
        FieldPacker addTap_fp = new FieldPacker(8);
        addTap_fp.addI32(x);
        addTap_fp.addI32(y);
        invoke(mExportFuncIdx_addTap, addTap_fp);
    }

}

