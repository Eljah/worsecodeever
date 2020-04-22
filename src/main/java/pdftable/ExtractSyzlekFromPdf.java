package pdftable;

import org.apache.pdfbox.pdmodel.PDDocument;
import pdftable.models.ParsedTablePage;

import java.io.File;
import java.io.IOException;

public class ExtractSyzlekFromPdf {
    public static void main(String[] args) throws IOException {
        PDDocument pdfDoc = PDDocument.load(new File("syzlek.pdf"));
        PdfTableReader reader = new PdfTableReader();

// first page in document has index == 1, not 0 !
        ParsedTablePage firstPage = reader.parsePdfTablePage(pdfDoc, 1);
        for ( ParsedTablePage.ParsedTableRow parsedTableRow: firstPage.getRows()){
            System.out.println(parsedTableRow.getCell(0)+" "+parsedTableRow.getCell(1));
        }
    }
}
