/*

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
package com.itextpdf.signatures;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDate;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

/**
 * Represents the signature dictionary.
 *
 * @author Paulo Soares
 */
public class PdfSignature extends PdfObjectWrapper<PdfDictionary> {

    /**
     * Creates new PdfSignature.
     */
    public PdfSignature() {
        super(new PdfDictionary());
        put(PdfName.Type, PdfName.Sig);
    }
    /**
     * Creates new PdfSignature.
     *
     * @param filter PdfName of the signature handler to use when validating this signature
     * @param subFilter PdfName that describes the encoding of the signature
     */
    public PdfSignature(PdfName filter, PdfName subFilter) {
        this();
        put(PdfName.Filter, filter);
        put(PdfName.SubFilter, subFilter);
    }

    /**
     * Sets the /ByteRange.
     *
     * @param range an array of pairs of integers that specifies the byte range used in the digest calculation. A pair consists of the starting byte offset and the length
     */
    public void setByteRange(int[] range) {
        PdfArray array = new PdfArray();

        for (int k = 0; k < range.length; ++k) {
            array.add(new PdfNumber(range[k]));
        }

        put(PdfName.ByteRange, array);
    }

    /**
     * Sets the /Contents value to the specified byte[].
     *
     * @param contents a byte[] representing the digest
     */
    public void setContents(byte[] contents) {
        put(PdfName.Contents, new PdfString(contents).setHexWriting(true));
    }

    /**
     * Sets the /Cert value of this signature.
     *
     * @param cert the byte[] representing the certificate chain
     */
    public void setCert(byte[] cert) {
        put(PdfName.Cert, new PdfString(cert));
    }

    /**
     * Sets the /Name of the person signing the document.
     *
     * @param name name of the person signing the document
     */
    public void setName(String name) {
        put(PdfName.Name, new PdfString(name, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Sets the /M value. Should only be used if the time of signing is not available in the signature.
     *
     * @param date time of signing
     */
    public void setDate(PdfDate date) {
        put(PdfName.M, date.getPdfObject());
    }

    /**
     * Sets the /Location value.
     *
     * @param location physical location of signing
     */
    public void setLocation(String location) {
        put(PdfName.Location, new PdfString(location, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Sets the /Reason value.
     *
     * @param reason reason for signing
     */
    public void setReason(String reason) {
        put(PdfName.Reason, new PdfString(reason, PdfEncodings.UNICODE_BIG));
    }

    /**
     * Sets the signature creator name in the
     * {@link PdfSignatureBuildProperties} dictionary.
     *
     * @param signatureCreator name of the signature creator
     */
    public void setSignatureCreator(String signatureCreator) {
        if (signatureCreator != null) {
            getPdfSignatureBuildProperties().setSignatureCreator(signatureCreator);
        }
    }

    /**
     * Sets the /ContactInfo value.
     *
     * @param contactInfo information to contact the person who signed this document
     */
    public void setContact(String contactInfo) {
        put(PdfName.ContactInfo, new PdfString(contactInfo, PdfEncodings.UNICODE_BIG));
    }

    public PdfSignature put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }

    /**
     * Gets the {@link PdfSignatureBuildProperties} instance if it exists, if
     * not it adds a new one and returns this.
     *
     * @return {@link PdfSignatureBuildProperties}
     */
    private PdfSignatureBuildProperties getPdfSignatureBuildProperties() {
        PdfDictionary buildPropDict = getPdfObject().getAsDictionary(PdfName.Prop_Build);

        if (buildPropDict == null) {
            buildPropDict = new PdfDictionary();
            put(PdfName.Prop_Build, buildPropDict);
        }

        return new PdfSignatureBuildProperties(buildPropDict);
    }
}