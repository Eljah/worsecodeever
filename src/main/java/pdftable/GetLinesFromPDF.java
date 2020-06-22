package pdftable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * This is an example on how to extract text line by line from pdf document
 */
public class GetLinesFromPDF extends PDFTextStripper {

    static List<String> lines = new ArrayList<String>();

    public GetLinesFromPDF() throws IOException {
    }

    /**
     * @throws IOException If there is an error parsing the document.
     */
    public static void main(String[] args) throws IOException {
        PDDocument document = null;
        String fileName = "syzlek.pdf";
        try {
            document = PDDocument.load(new File(fileName));
            PDFTextStripper stripper = new GetLinesFromPDF();
            stripper.setSortByPosition(true);
            stripper.setStartPage(0);
            stripper.setEndPage(document.getNumberOfPages());

            Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
            stripper.writeText(document, dummy);


            BufferedWriter writer = new BufferedWriter(new FileWriter("name2.csv", false));

            //Iterator linesIter=lines.
            ListIterator<String> listIterator = lines.listIterator();
            listIterator.next();
            listIterator.next();
            listIterator.next();
            listIterator.next();
            listIterator.next();//ҿ Ҿ
            listIterator.next();
            listIterator.next();
            while (listIterator.hasNext()) {
                writer.write(listIterator.next().
                        replace("\uFEFF", "").
                        replace('Ҽ', 'Ә').replace('ҽ', 'ә').
                        replace('ҿ', 'ө').replace('Ҿ', 'Ө').
                        replace("ый", "ї").replace("Ый", "ї").replace("ЫЙ", "ї").
                        replace("ль", "љ").replace("Ль", "љ").replace("ЛЬ", "љ").
                        replace("дж", "џ").replace("Дж", "џ").replace("ДЖ", "џ").
                        replace("къ", "қ").replace("Къ", "қ").replace("КЪ", "қ").
                        replace("гъ", "ғ").replace("Гъ", "ғ").replace("ГЪ", "ғ").
                        toLowerCase() + "," +
                        listIterator.next().
                                replace("\uFEFF", "").
                                replace('Ҽ', 'Ә').replace('ҽ', 'ә').
                                replace('ҿ', 'ө').replace('Ҿ', 'Ө').
                                replace("ый", "ї").replace("Ый", "ї").replace("ЫЙ", "ї").
                                replace("ль", "љ").replace("Ль", "љ").replace("ЛЬ", "љ").
                                replace("дж", "џ").replace("Дж", "џ").replace("ДЖ", "џ").
                                replace("къ", "қ").replace("Къ", "қ").replace("КЪ", "қ").
                                replace("гъ", "ғ").replace("Гъ", "ғ").replace("ГЪ", "ғ").
                                toLowerCase()
                        + "," + listIterator.next().
                        replace("\uFEFF", "").
                        replace('Ҽ', 'Ә').replace('ҽ', 'ә').
                        replace('ҿ', 'ө').replace('Ҿ', 'Ө').
                        replace("ый", "ї").replace("Ый", "ї").replace("ЫЙ", "ї").
                        replace("ль", "љ").replace("Ль", "љ").replace("ЛЬ", "љ").
                        replace("дж", "џ").replace("Дж", "џ").replace("ДЖ", "џ").
                        replace("къ", "қ").replace("Къ", "қ").replace("КЪ", "қ").
                        replace("гъ", "ғ").replace("Гъ", "ғ").replace("ГЪ", "ғ")
                        .toLowerCase() + "," + listIterator.next().toLowerCase() + "\n");
                //System.out.println(listIterator.next());
            }
            writer.close();
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    /**
     * Override the default functionality of PDFTextStripper.writeString()
     */
    @Override
    protected void writeString(String str, List<TextPosition> textPositions) throws IOException {
        lines.add(str);
        // you may process the line here itself, as and when it is obtained
    }
}