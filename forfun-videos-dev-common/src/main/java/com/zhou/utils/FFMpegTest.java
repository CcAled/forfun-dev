package com.zhou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FFMpegTest {
    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,String videoOutputPath) throws IOException {

         List<String> commond = new ArrayList<>();
         commond.add(ffmpegEXE);

         commond.add("-i");
         commond.add(videoInputPath);
         commond.add("-y");
         commond.add(videoOutputPath);

         for (String c : commond){
             System.out.print(c + " ");
         }

        ProcessBuilder builder = new ProcessBuilder(commond);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while((line = br.readLine()) != null){
        }

        if (br != null){
            br.close();
        }
        if (inputStreamReader != null){
            inputStreamReader.close();
        }
        if (errorStream != null){
            errorStream.close();
        }
    }

    public static void main(String[] args) {
        try {
            FFMpegTest ffmpeg = new FFMpegTest("D:\\apps\\ffmpeg\\bin\\ffmpeg.exe");
            ffmpeg.convertor("D:\\apps\\ffmpeg\\bin\\new.mp4","D:\\apps\\ffmpeg\\bin\\renew.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
