/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abbyyocrdemo;

import com.abbyy.ocrsdk.ProcessingSettings;
import java.util.Vector;

/**
 *
 * @author virendra
 */
public class CmdLineOptions {

    public static String extractRecognitionLanguage(Vector<String> args) {
        String lang = extractParameterValue("lang", args);
        if (lang != null) {
            return lang;
        }

        System.out.println(
                "Warning! The document will be processed with English language.\n"
                + "To change this, specify --lang=<languages> option.\n");

        return "English";
    }

    public static ProcessingSettings.OutputFormat extractOutputFormat(Vector<String> args) {
        String fmt = extractParameterValue("format", args);
        if (fmt == null) {
            System.out.println(
                    "Warning! Document will be converted to plain text.\n"
                    + "To change this, specify --format=<format> option.\n");
            fmt = "txt";
        }

        if (fmt.equalsIgnoreCase("txt")) {
            return ProcessingSettings.OutputFormat.txt;
        } else if (fmt.equalsIgnoreCase("rtf")) {
            return ProcessingSettings.OutputFormat.rtf;
        } else if (fmt.equalsIgnoreCase("docx")) {
            return ProcessingSettings.OutputFormat.docx;
        } else if (fmt.equalsIgnoreCase("xlsx")) {
            return ProcessingSettings.OutputFormat.xlsx;
        } else if (fmt.equalsIgnoreCase("pptx")) {
            return ProcessingSettings.OutputFormat.pptx;
        } else if (fmt.equalsIgnoreCase("pdfSearchable")) {
            return ProcessingSettings.OutputFormat.pdfSearchable;
        } else if (fmt.equalsIgnoreCase("pdfTextAndImages")) {
            return ProcessingSettings.OutputFormat.pdfTextAndImages;
        } else if (fmt.equalsIgnoreCase("xml")) {
            return ProcessingSettings.OutputFormat.xml;
        } else {
            throw new IllegalArgumentException("Invalid output format");
        }
    }

    public static String extractReceiptCountry(Vector<String> args) {
        String lang = extractParameterValue("country", args);
        if (lang != null) {
            return lang;
        }

        System.out.println(
                "Warning! The receipt will be treated as receipt from USA.\n"
                + "To change this, specify --country=<countryNames> option.\n");

        return "Usa";
    }

    public static String extractParameterValue(String parameterName,
            Vector<String> args) {
        String prefix = "--" + parameterName + "=";

        for (int i = 0; i < args.size(); i++) {
            String arg = args.elementAt(i);
            if (arg.startsWith(prefix)) {
                String value = arg.substring(prefix.length());
                args.remove(i);
                return value;
            }
        }
        return null;
    }
}
