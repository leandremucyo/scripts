///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 24
//PREVIEW 

//DEPS com.itextpdf:kernel:7.2.5
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.PdfMerger;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MergePdfs {
    private static final int MAX_FILES = 100;
    private static final long MAX_BYTES_PER_FILE = 10L * 1024 * 1024; // 10 MB

    /**
     * CLI:
     *   jbang MergePdfs.java <dest.pdf> <sourceFolder>
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java MergeFolderPdfs <dest.pdf> <sourceFolder>");
            System.exit(1);
        }
        String dest = args[0];
        String srcFolder = args[1];

        try {
            mergeFolder(dest, srcFolder);
            System.out.println("Merged PDFs from folder '" + srcFolder + "' into: " + dest);
        } catch (Exception e) {
            System.err.println("Merge failed: " + e.getMessage());
            System.exit(2);
        }
    }

    /**
     * Merge all PDFs from the given folder into the destination PDF,
     * sorted lexicographically by filename. Enforces: ≤100 files and each ≤10MB.
     */
    public static void mergeFolder(String dest, String sourceFolder) throws IOException {
        Path folder = Paths.get(sourceFolder);
        if (!Files.isDirectory(folder)) {
            throw new IllegalArgumentException("Not a directory: " + folder.toAbsolutePath());
        }

        List<Path> pdfs;
        try (Stream<Path> s = Files.list(folder)) {
            pdfs = s.filter(p -> !Files.isDirectory(p))
                    .filter(p -> {
                        String name = p.getFileName().toString().toLowerCase();
                        return name.endsWith(".pdf");
                    })
                    .sorted(Comparator.comparing(p -> p.getFileName().toString(), String::compareTo))
                    .collect(Collectors.toList());
        }

        if (pdfs.isEmpty()) {
            throw new IllegalArgumentException("No .pdf files found in folder: " + folder.toAbsolutePath());
        }
        if (pdfs.size() > MAX_FILES) {
            throw new IllegalArgumentException("Too many PDF files (" + pdfs.size() + "). Max is " + MAX_FILES + ".");
        }

        for (Path p : pdfs) {
            if (!Files.isReadable(p)) {
                throw new IOException("File not readable: " + p.toAbsolutePath());
            }
            long size = Files.size(p);
            if (size > MAX_BYTES_PER_FILE) {
                throw new IllegalArgumentException("File exceeds 10 MB: " + p.getFileName() + " (" + size + " bytes)");
            }
        }

        Path destPath = Paths.get(dest);
        Path parent = destPath.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        try (PdfDocument destPdf = new PdfDocument(new PdfWriter(destPath.toString()))) {
            
            PdfMerger merger = new PdfMerger(destPdf);
            merger.setCloseSourceDocuments(true);

            for (Path p : pdfs) {
                try (PdfReader reader = new PdfReader(p.toString());
                     PdfDocument src = new PdfDocument(reader)) {
                    int pages = src.getNumberOfPages();
                    if (pages == 0) {
                        System.err.println("Warning: " + p.getFileName() + " has 0 pages; skipping.");
                        continue;
                    }
                    merger.merge(src, 1, pages);
                } catch (Exception e) {
                    throw new IOException("Failed merging file: " + p.getFileName() + " -> " + e.getMessage(), e);
                }
            }
        }
    }
}

