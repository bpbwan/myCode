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
 * The source Renderscript file: /home/bpb/Flyaudio/ac8317/BPBGITHUB/myCode/wallpapers/Basic/src/com/android/wallpaper/grass/grass.rs
 */
package com.android.wallpaper.grass;

import android.renderscript.*;
import android.content.res.Resources;

/**
 * @hide
 */
public class ScriptC_grass extends ScriptC {
    private static final String __rs_resource_name = "grass";
    // Constructor
    public  ScriptC_grass(RenderScript rs) {
        this(rs,
             rs.getApplicationContext().getResources(),
             rs.getApplicationContext().getResources().getIdentifier(
                 __rs_resource_name, "raw",
                 rs.getApplicationContext().getPackageName()));
    }

    public  ScriptC_grass(RenderScript rs, Resources resources, int id) {
        super(rs, resources, id);
    }

    private FieldPacker __rs_fp_ALLOCATION;
    private FieldPacker __rs_fp_F32;
    private FieldPacker __rs_fp_I32;
    private FieldPacker __rs_fp_MESH;
    private FieldPacker __rs_fp_PROGRAM_FRAGMENT;
    private FieldPacker __rs_fp_PROGRAM_STORE;
    private FieldPacker __rs_fp_PROGRAM_VERTEX;
    private final static int mExportVarIdx_gBladesCount = 0;
    private int mExportVar_gBladesCount;
    public synchronized void set_gBladesCount(int v) {
        setVar(mExportVarIdx_gBladesCount, v);
        mExportVar_gBladesCount = v;
    }

    public int get_gBladesCount() {
        return mExportVar_gBladesCount;
    }

    private final static int mExportVarIdx_gIndexCount = 1;
    private int mExportVar_gIndexCount;
    public synchronized void set_gIndexCount(int v) {
        setVar(mExportVarIdx_gIndexCount, v);
        mExportVar_gIndexCount = v;
    }

    public int get_gIndexCount() {
        return mExportVar_gIndexCount;
    }

    private final static int mExportVarIdx_gWidth = 2;
    private int mExportVar_gWidth;
    public synchronized void set_gWidth(int v) {
        setVar(mExportVarIdx_gWidth, v);
        mExportVar_gWidth = v;
    }

    public int get_gWidth() {
        return mExportVar_gWidth;
    }

    private final static int mExportVarIdx_gHeight = 3;
    private int mExportVar_gHeight;
    public synchronized void set_gHeight(int v) {
        setVar(mExportVarIdx_gHeight, v);
        mExportVar_gHeight = v;
    }

    public int get_gHeight() {
        return mExportVar_gHeight;
    }

    private final static int mExportVarIdx_gXOffset = 4;
    private float mExportVar_gXOffset;
    public synchronized void set_gXOffset(float v) {
        setVar(mExportVarIdx_gXOffset, v);
        mExportVar_gXOffset = v;
    }

    public float get_gXOffset() {
        return mExportVar_gXOffset;
    }

    private final static int mExportVarIdx_gDawn = 5;
    private float mExportVar_gDawn;
    public synchronized void set_gDawn(float v) {
        setVar(mExportVarIdx_gDawn, v);
        mExportVar_gDawn = v;
    }

    public float get_gDawn() {
        return mExportVar_gDawn;
    }

    private final static int mExportVarIdx_gMorning = 6;
    private float mExportVar_gMorning;
    public synchronized void set_gMorning(float v) {
        setVar(mExportVarIdx_gMorning, v);
        mExportVar_gMorning = v;
    }

    public float get_gMorning() {
        return mExportVar_gMorning;
    }

    private final static int mExportVarIdx_gAfternoon = 7;
    private float mExportVar_gAfternoon;
    public synchronized void set_gAfternoon(float v) {
        setVar(mExportVarIdx_gAfternoon, v);
        mExportVar_gAfternoon = v;
    }

    public float get_gAfternoon() {
        return mExportVar_gAfternoon;
    }

    private final static int mExportVarIdx_gDusk = 8;
    private float mExportVar_gDusk;
    public synchronized void set_gDusk(float v) {
        setVar(mExportVarIdx_gDusk, v);
        mExportVar_gDusk = v;
    }

    public float get_gDusk() {
        return mExportVar_gDusk;
    }

    private final static int mExportVarIdx_gIsPreview = 9;
    private int mExportVar_gIsPreview;
    public synchronized void set_gIsPreview(int v) {
        setVar(mExportVarIdx_gIsPreview, v);
        mExportVar_gIsPreview = v;
    }

    public int get_gIsPreview() {
        return mExportVar_gIsPreview;
    }

    private final static int mExportVarIdx_gPVBackground = 10;
    private ProgramVertex mExportVar_gPVBackground;
    public synchronized void set_gPVBackground(ProgramVertex v) {
        setVar(mExportVarIdx_gPVBackground, v);
        mExportVar_gPVBackground = v;
    }

    public ProgramVertex get_gPVBackground() {
        return mExportVar_gPVBackground;
    }

    private final static int mExportVarIdx_gPFBackground = 11;
    private ProgramFragment mExportVar_gPFBackground;
    public synchronized void set_gPFBackground(ProgramFragment v) {
        setVar(mExportVarIdx_gPFBackground, v);
        mExportVar_gPFBackground = v;
    }

    public ProgramFragment get_gPFBackground() {
        return mExportVar_gPFBackground;
    }

    private final static int mExportVarIdx_gPFGrass = 12;
    private ProgramFragment mExportVar_gPFGrass;
    public synchronized void set_gPFGrass(ProgramFragment v) {
        setVar(mExportVarIdx_gPFGrass, v);
        mExportVar_gPFGrass = v;
    }

    public ProgramFragment get_gPFGrass() {
        return mExportVar_gPFGrass;
    }

    private final static int mExportVarIdx_gPSBackground = 13;
    private ProgramStore mExportVar_gPSBackground;
    public synchronized void set_gPSBackground(ProgramStore v) {
        setVar(mExportVarIdx_gPSBackground, v);
        mExportVar_gPSBackground = v;
    }

    public ProgramStore get_gPSBackground() {
        return mExportVar_gPSBackground;
    }

    private final static int mExportVarIdx_gTNight = 14;
    private Allocation mExportVar_gTNight;
    public synchronized void set_gTNight(Allocation v) {
        setVar(mExportVarIdx_gTNight, v);
        mExportVar_gTNight = v;
    }

    public Allocation get_gTNight() {
        return mExportVar_gTNight;
    }

    private final static int mExportVarIdx_gTSunset = 15;
    private Allocation mExportVar_gTSunset;
    public synchronized void set_gTSunset(Allocation v) {
        setVar(mExportVarIdx_gTSunset, v);
        mExportVar_gTSunset = v;
    }

    public Allocation get_gTSunset() {
        return mExportVar_gTSunset;
    }

    private final static int mExportVarIdx_gTSunrise = 16;
    private Allocation mExportVar_gTSunrise;
    public synchronized void set_gTSunrise(Allocation v) {
        setVar(mExportVarIdx_gTSunrise, v);
        mExportVar_gTSunrise = v;
    }

    public Allocation get_gTSunrise() {
        return mExportVar_gTSunrise;
    }

    private final static int mExportVarIdx_gTSky = 17;
    private Allocation mExportVar_gTSky;
    public synchronized void set_gTSky(Allocation v) {
        setVar(mExportVarIdx_gTSky, v);
        mExportVar_gTSky = v;
    }

    public Allocation get_gTSky() {
        return mExportVar_gTSky;
    }

    private final static int mExportVarIdx_gTAa = 18;
    private Allocation mExportVar_gTAa;
    public synchronized void set_gTAa(Allocation v) {
        setVar(mExportVarIdx_gTAa, v);
        mExportVar_gTAa = v;
    }

    public Allocation get_gTAa() {
        return mExportVar_gTAa;
    }

    private final static int mExportVarIdx_gBladesMesh = 19;
    private Mesh mExportVar_gBladesMesh;
    public synchronized void set_gBladesMesh(Mesh v) {
        setVar(mExportVarIdx_gBladesMesh, v);
        mExportVar_gBladesMesh = v;
    }

    public Mesh get_gBladesMesh() {
        return mExportVar_gBladesMesh;
    }

    private final static int mExportVarIdx_Blades = 20;
    private ScriptField_Blade mExportVar_Blades;
    public void bind_Blades(ScriptField_Blade v) {
        mExportVar_Blades = v;
        if (v == null) bindAllocation(null, mExportVarIdx_Blades);
        else bindAllocation(v.getAllocation(), mExportVarIdx_Blades);
    }

    public ScriptField_Blade get_Blades() {
        return mExportVar_Blades;
    }

    private final static int mExportVarIdx_Verticies = 21;
    private ScriptField_Vertex mExportVar_Verticies;
    public void bind_Verticies(ScriptField_Vertex v) {
        mExportVar_Verticies = v;
        if (v == null) bindAllocation(null, mExportVarIdx_Verticies);
        else bindAllocation(v.getAllocation(), mExportVarIdx_Verticies);
    }

    public ScriptField_Vertex get_Verticies() {
        return mExportVar_Verticies;
    }

    private final static int mExportFuncIdx_updateBlades = 0;
    public void invoke_updateBlades() {
        invoke(mExportFuncIdx_updateBlades);
    }

}

