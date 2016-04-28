/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf.canvas.parser.data;

import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;

/**
 * Represents image data from a PDF
 */
public class ImageRenderInfo implements IEventData {
    /** The coordinate transformation matrix that was in effect when the image was rendered */
    private Matrix ctm;
    private PdfImageXObject image;
    /** the color space dictionary from resources which are associated with the image */
    private PdfDictionary colorSpaceDictionary;
    /** defines if the encountered image was inline */
    private boolean isInline;

    /**
     * Create an ImageRenderInfo
     * @param ctm the coordinate transformation matrix at the time the image is rendered
     * @param stream image stream object
     * @param colorSpaceDictionary the color space dictionary from resources which are associated with the image
     * @param isInline defines if the encountered image was inline
     */
    public ImageRenderInfo(Matrix ctm, PdfStream stream, PdfDictionary colorSpaceDictionary, boolean isInline) {
        this.ctm = ctm;
        this.image = new PdfImageXObject(stream);
        this.colorSpaceDictionary = colorSpaceDictionary;
        this.isInline = isInline;
    }

    /**
     * Gets an image wrapped in ImageXObject.
     * You can:
     * <ul>
     *     <li>get image bytes with {@link PdfImageXObject#getImageBytes(boolean)}, these image bytes
     *     represent native image, i.e you can write these bytes to disk and get just an usual image;</li>
     *     <li>obtain PdfStream object which contains image dictionary with {@link PdfImageXObject#getPdfObject()} method;</li>
     *     <li>convert image to {@link java.awt.image.BufferedImage} with {@link PdfImageXObject#getBufferedImage()};</li>
     * </ul>
     */
    public PdfImageXObject getImage() {
        return image;
    }

    /**
     * @return a vector in User space representing the start point of the image
     */
    public Vector getStartPoint(){
        return new Vector(0, 0, 1).cross(ctm);
    }

    /**
     * @return The coordinate transformation matrix which was active when this image was rendered. Coordinates are in User space.
     */
    public Matrix getImageCtm(){
        return ctm;
    }

    /**
     * @return the size of the image, in User space units
     */
    public float getArea(){
        // the image space area is 1, so we multiply that by the determinant of the CTM to get the transformed area
        return ctm.getDeterminant();
    }

    /**
     * @return true if image was inlined in original stream.
     */
    public boolean isInline() {
        return isInline;
    }

    /**
     * @return the color space dictionary from resources which are associated with the image
     */
    public PdfDictionary getColorSpaceDictionary() {
        return colorSpaceDictionary;
    }
}
