package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.model.element.PageBreak;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.testutils.CompareTool;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class DefaultLayoutTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/DefaultLayoutTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/DefaultLayoutTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    @Ignore("We should really think if it is worth supporting this smelly case. In case of pre-layout this probably won't work")
    public void multipleAdditionsOfSameModelElementTest() throws FileNotFoundException, PdfException {
        String outFileName = destinationFolder + "multipleAdditionsOfSameModelElementTest1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph("Hello. I am a paragraph. I want you to process me correctly");
        document.add(p).add(p).add(new PageBreak(PageSize.Default)).add(p);

        document.close();
    }

    @Test
    public void rendererTest01() throws IOException, PdfException, DocumentException, InterruptedException {
        String outFileName = destinationFolder + "rendererTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_rendererTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        String str = "Hello. I am a paragraph. I want you to process me correctly";
        document.add(new Paragraph(str)).
                add(new Paragraph(str)).
                add(new PageBreak(PageSize.Default)).
                add(new Paragraph(str));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}