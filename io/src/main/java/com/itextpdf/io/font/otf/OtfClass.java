package com.itextpdf.io.font.otf;

import com.itextpdf.io.util.IntHashtable;
import com.itextpdf.io.source.RandomAccessFileOrArray;

public class OtfClass {
    //key is glyph, value is class inside all 2
    private IntHashtable mapClass = new IntHashtable();
    
    public OtfClass(RandomAccessFileOrArray rf, int classLocation) throws java.io.IOException {
        rf.seek(classLocation);
        int classFormat = rf.readUnsignedShort();
        if (classFormat == 1) {
            int startGlyph = rf.readUnsignedShort();
            int glyphCount = rf.readUnsignedShort();
            int endGlyph = startGlyph + glyphCount;
            for (int k = startGlyph; k < endGlyph; ++k) {
                int cl = rf.readUnsignedShort();
                mapClass.put(k, cl);
            }
        } else if (classFormat == 2) {
            int classRangeCount = rf.readUnsignedShort();
            for (int k = 0; k < classRangeCount; ++k) {
                int glyphStart = rf.readUnsignedShort();
                int glyphEnd = rf.readUnsignedShort();
                int cl = rf.readUnsignedShort();
                for (; glyphStart <= glyphEnd; ++glyphStart) {
                    mapClass.put(glyphStart, cl);
                }
            }
        } else {
            throw new java.io.IOException("Invalid class format " + classFormat);
        }
    }
    
    public int getOtfClass(int glyph) {
        return mapClass.get(glyph);
    }
    
    public boolean hasClass(int glyph) {
        return mapClass.containsKey(glyph);
    }
    
    public int getOtfClass(int glyph, boolean strict) {
        if (strict) {
            if (mapClass.containsKey(glyph)) {
                return mapClass.get(glyph);
            } else {
                return -1;
            }
        } else {
            return mapClass.get(glyph);
        }
    }
}