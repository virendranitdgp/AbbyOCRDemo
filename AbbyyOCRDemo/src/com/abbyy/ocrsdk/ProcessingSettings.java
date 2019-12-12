/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.abbyy.ocrsdk;

/**
 *
 * @author virendra
 */
public class ProcessingSettings {

    private String language = "English";
    private OutputFormat outputFormat = OutputFormat.pdfSearchable;

    public String asUrlParams() {
        return String.format("language=%s&exportFormat=%s", language,
                outputFormat);
    }

    public enum OutputFormat {
        txt, rtf, docx, xlsx, pptx, pdfSearchable, pdfTextAndImages, xml
    }

    public void setOutputFormat(OutputFormat format) {
        outputFormat = format;
    }

    public OutputFormat getOutputFormat() {
        return outputFormat;
    }

    public String getOutputFileExt() {
        switch (outputFormat) {
            case txt:
                return ".txt";
            case rtf:
                return ".rtf";
            case docx:
                return ".docx";
            case xlsx:
                return ".xlsx";
            case pptx:
                return ".pptx";
            case pdfSearchable:
            case pdfTextAndImages:
                return ".pdf";
            case xml:
                return ".xml";
        }
        return ".ocr";
    }

    public void setLanguage(String newLanguage) {
        language = newLanguage;
    }

    public String getLanguage() {
        return language;
    }
}
