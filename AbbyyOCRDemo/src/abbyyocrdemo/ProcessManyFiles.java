/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abbyyocrdemo;

import com.abbyy.ocrsdk.*;
import java.util.*;
import java.io.*;

/**
 *
 * @author virendra
 */
public class ProcessManyFiles {

    private static Client restClient;
    private static String outputDir;

    public static void main(String[] args) {
        if (!checkAppId()) {
            return;
        }
        restClient = new Client();
        // replace with 'https://cloud-eu.ocrsdk.com' to enable secure connection
        // replace with 'https://cloud-westus.ocrsdk.com' if your application is created in US location
        restClient.serverUrl = "https://cloud-westus.ocrsdk.com";
        restClient.applicationId = ClientSettings.APPLICATION_ID;
        restClient.password = ClientSettings.PASSWORD;
        List<String> list = new ArrayList<>();
        list.add("remote");
        //for local data you have to pass data directory 
        list.add("/home/virendra/Downloads/images");
// If you want to scan image from remote the you have to pass image url 
//        list.add("https://github.com/abbyysdk/ocrsdk.com/blob/master/SampleData/Page_08.tif?raw=true");
        list.add("/home/virendra/Downloads/DownloadImagesTxt");
        //for pdf output format you have to pass '--format=pdfTextAndImages'
//        list.add("--format=pdfTextAndImages");
//      By default format is txt 
// for rtf output format  '--format=rtf' 
        Vector<String> argList = new Vector<String>(list);

        // Select processing mode
        String mode = "recognize";
        argList.remove(0);
        try {
            if (mode.equalsIgnoreCase("recognize")) {
                performRecognition(argList);
            } else if (mode.equalsIgnoreCase("remote")) {
                performRemoteFileRecognition(argList);
            } else if (mode.equalsIgnoreCase("receipt")) {
                performReceiptRecognition(argList);
            }
        } catch (Exception e) {
            System.out.println("Exception occured: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean checkAppId() {
        String appId = ClientSettings.APPLICATION_ID;
        String password = ClientSettings.PASSWORD;
        if (appId.isEmpty() || password.isEmpty()) {
            System.out
                    .println("Error: No application id and password are specified.");
            System.out.println("Please specify them in ClientSettings.java.");
            return false;
        }
        return true;
    }

    private static void performRecognition(Vector<String> argList)
            throws Exception {

        ProcessingSettings settings = new ProcessingSettings();
        settings.setLanguage(CmdLineOptions.extractRecognitionLanguage(argList));
        settings.setOutputFormat(CmdLineOptions.extractOutputFormat(argList));

        String sourceDirPath = argList.get(0);
        String targetDirPath = argList.get(1);
        setOutputPath(targetDirPath);

        File sourceDir = new File(sourceDirPath);

        File[] listOfFiles = sourceDir.listFiles();

        Vector<String> filesToProcess = new Vector<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile()) {
                String fullPath = file.getAbsolutePath();
                filesToProcess.add(fullPath);
            }
        }

        Map<String, String> taskIds = submitAllFiles(filesToProcess, settings);

        waitAndDownloadResults(taskIds);
    }

    private static Map<String, String> submitAllFiles(Vector<String> fileList, ProcessingSettings settings) throws Exception {
        System.out.println(String.format("Uploading %d files..", fileList.size()));

        Map<String, String> taskIds = new HashMap<String, String>();

        for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
            String filePath = fileList.get(fileIndex);

            File file = new File(filePath);
            String fileBase = file.getName();
            if (fileBase.indexOf(".") > 0) {
                fileBase = fileBase.substring(0, fileBase.lastIndexOf("."));
            }

            System.out.println(filePath);
            Task task = restClient.processImage(filePath, settings);
            taskIds.put(task.Id, fileBase + settings.getOutputFileExt());
        }
        return taskIds;
    }

    private static void waitAndDownloadResults(Map<String, String> taskIds) throws Exception {
        System.out.println("Waiting..");

        while (taskIds.size() > 0) {
            Task[] finishedTasks = restClient.listFinishedTasks();

            for (int i = 0; i < finishedTasks.length; i++) {
                Task task = finishedTasks[i];
                if (taskIds.containsKey(task.Id)) {
                    // Download task
                    String fileName = taskIds.remove(task.Id);

                    if (task.Status == Task.TaskStatus.Completed) {
                        String outputPath = outputDir + "/" + fileName;
                        restClient.downloadResult(task, outputPath);
                        System.out.println(String.format("Ready %s, %d remains", fileName, taskIds.size()));
                    } else {
                        System.out.println(String.format("Failed %s, %d remains", fileName, taskIds.size()));
                    }

                } else {
                    System.out.println(String.format("Deleting task %s from server", task.Id));
                }
                restClient.deleteTask(task.Id);
            }
            Thread.sleep(2000);
        }
    }

    private static void performRemoteFileRecognition(Vector<String> argList)
            throws Exception {
        displayBetaWarning();

        ProcessingSettings settings = new ProcessingSettings();
        settings.setLanguage(CmdLineOptions.extractRecognitionLanguage(argList));
        settings.setOutputFormat(CmdLineOptions.extractOutputFormat(argList));

        String remoteFile = argList.get(0);
        String targetDirPath = argList.get(1);
        setOutputPath(targetDirPath);

        Vector<String> urlsToProcess = new Vector<String>();
        if (remoteFile.startsWith("http://") || remoteFile.startsWith("https://")) {
            urlsToProcess.add(remoteFile);
        } else {
            // Get url list from remoteFile
            BufferedReader br = new BufferedReader(new FileReader(remoteFile));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    urlsToProcess.add(line);
                }
            } finally {
                br.close();
            }
        }

        Map<String, String> taskIds = submitRemoteUrls(urlsToProcess, settings);
        waitAndDownloadResults(taskIds);
    }

    private static Map<String, String> submitRemoteUrls(Vector<String> urlList, ProcessingSettings settings) throws Exception {
        System.out.println(String.format("Processing %d urls...", urlList.size()));
        Map<String, String> taskIds = new HashMap<String, String>();

        for (int i = 0; i < urlList.size(); i++) {
            String url = urlList.get(i);

            String fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
            String fileBase = fileName.substring(0, fileName.lastIndexOf('.'));

            System.out.println(url);
            Task task = restClient.processRemoteImage(url, settings);
            taskIds.put(task.Id, fileBase + settings.getOutputFileExt());
        }
        return taskIds;
    }

    private static void displayBetaWarning() {
        System.out.println(
                "*** WARNING! You are using API that is in beta stage. ***\n"
                + "*** It can change any time without notice or even be removed from ABBYY Cloud OCR SDK service. ***\n\n"
        );
    }

    private static void performReceiptRecognition(Vector<String> argList)
            throws Exception {

        ReceiptSettings settings = new ReceiptSettings();
        settings.setReceiptCountry(CmdLineOptions.extractReceiptCountry(argList));

        String sourceDirPath = argList.get(0);
        String targetDirPath = argList.get(1);
        setOutputPath(targetDirPath);

        File sourceDir = new File(sourceDirPath);

        File[] listOfFiles = sourceDir.listFiles();

        Vector<String> filesToProcess = new Vector<String>();

        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile()) {
                String fullPath = file.getAbsolutePath();
                filesToProcess.add(fullPath);
            }
        }

        Map<String, String> taskIds = submitAllReceipts(filesToProcess, settings);

        waitAndDownloadResults(taskIds);
    }

    private static Map<String, String> submitAllReceipts(Vector<String> fileList, ReceiptSettings settings) throws Exception {
        System.out.println(String.format("Uploading %d receipts..", fileList.size()));

        Map<String, String> taskIds = new HashMap<String, String>();

        for (int fileIndex = 0; fileIndex < fileList.size(); fileIndex++) {
            String filePath = fileList.get(fileIndex);

            File file = new File(filePath);
            String fileBase = file.getName();
            if (fileBase.indexOf(".") > 0) {
                fileBase = fileBase.substring(0, fileBase.lastIndexOf("."));
            }

            System.out.println(filePath);
            Task task = restClient.processReceipt(filePath, settings);
            taskIds.put(task.Id, fileBase + ".xml");
        }
        return taskIds;
    }

    private static void setOutputPath(String value) {
        outputDir = value;
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
