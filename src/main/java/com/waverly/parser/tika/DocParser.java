package com.waverly.parser.tika;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.apache.tika.exception.TikaException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DocParser {

    public Map<String, Object> processRecord(String path) {
        Map<String, Object> map = new HashMap<String, Object>();
        String docPath = path;
        try {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            InputStream inputstream = new FileInputStream(new File(docPath));
            ParseContext pcontext = new ParseContext();
            Parser pdfparser = new AutoDetectParser();
            pdfparser.parse(inputstream, handler, metadata, pcontext);
            map.put("text", handler.toString()
                    .replaceAll("[^A-Za-z0-9. ]+", ""));
            map.put("title", metadata.get(TikaCoreProperties.TITLE));
            map.put("pageCount", metadata.get("xmpTPg:NPages"));

        } catch (IOException ex) {
            System.out.println("Caught IOException:" + ex.getMessage());
        } catch (TikaException tx) {
            System.out.println("Caught TikaException: " + tx.getMessage());
        } catch (SAXException sx) {

            System.out.println("Caught SAXException: " + sx.getMessage());

        }

        return map;
    }

    public static void main(String args[]) {

        String in = args[0];
        String out = args[1];
        File dir = new File(in);
        File outDir = new File(out);
        //	if ((!dir.isDirectory() && !dir.isFile()) || (!outDir.isDirectory())) {
        if (dir.isDirectory()) {
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    System.out.println("crawling "+child);
                    DocParser textExtract = new DocParser();
                    String childString = child.toString();
                    String baseFileName = child.getName();
                    String stripChildExt = FilenameUtils
                            .removeExtension(baseFileName);
                    Map<String, Object> extractedMap = textExtract
                            .processRecord(childString);
                    try {
                        PrintWriter writer = new PrintWriter(out + "/"
                                + stripChildExt + "-completed.txt", "UTF-8");
                        writer.println(extractedMap.get("text"));
                        writer.flush();
                        writer.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        if (dir.isFile()) {
            System.out.println("crawling"+out);
            DocParser textExtract = new DocParser();
            Map<String, Object> extractedMap = textExtract
                    .processRecord(in);
            try {
                String fileStripPath = dir.getName();
                String fileStripExtension = FilenameUtils
                        .removeExtension(fileStripPath);

                PrintWriter writer = new PrintWriter(out + "/"
                        + fileStripExtension + "-completed.txt", "UTF-8");
                writer.println(extractedMap.get("text"));
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }

        //} else {

        //System.out.println("you must specify an input directory or file and you must specify an output directory");

        //}

    }
}