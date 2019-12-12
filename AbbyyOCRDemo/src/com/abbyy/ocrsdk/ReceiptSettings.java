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
public class ReceiptSettings {

    public String asUrlParams() {
        // For all possible parameters, see documentation at
        // https://ocrsdk.com/documentation/apireference/processReceipt/
        return String.format("country=%s", receiptCountry);
    }

    public void setReceiptCountry(String newCountry) {
        receiptCountry = newCountry;
    }

    public String getReceiptCountry() {
        return receiptCountry;
    }

    private String receiptCountry = "Usa";
}
