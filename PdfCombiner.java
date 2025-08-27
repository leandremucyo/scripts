///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 24
//PREVIEW 

//DEPS com.itextpdf:kernel:7.2.5

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

class PdfCombiner {

    void main(String args[]) throws Exception{
        if(args.length != 3){
            throw new UnsupportedOperationException("Requires destFileWithFullPath sources1.pdf soource2.pdf");
        }
        String src1 = args[0];
        String src2 = args[1];
        String dest = args[2];

        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        PdfMerger merger = new PdfMerger(pdf);

        PdfDocument firstSource = new PdfDocument(new PdfReader(src1));
        merger.merge(firstSource, 1, firstSource.getNumberOfPages());
        firstSource.close();


        PdfDocument secondSource = new PdfDocument(new PdfReader(src2));
        merger.merge(secondSource, 1, secondSource.getNumberOfPages());
        
        secondSource.close();
        pdf.close();

    }
}