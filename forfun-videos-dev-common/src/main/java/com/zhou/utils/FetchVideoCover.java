package com.zhou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FetchVideoCover {
    private String ffmpegEXE;

    public FetchVideoCover(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void getCover(String videoInputPath, String coverOutputPath) throws IOException {
        //ffmpeg.exe -ss 00:00:01 -y -i new.mp4 -vframes 1 frame.jpg
         List<String> commond = new ArrayList<>();
         commond.add(ffmpegEXE);

        commond.add("-ss");
        commond.add("00:00:01");

        commond.add("-y");
        commond.add("-i");
        commond.add(videoInputPath);

         commond.add("-vframes");
         commond.add("1");
         commond.add(coverOutputPath);

//         for (String c : commond){
//             System.out.print(c + " ");
//         }

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
            FetchVideoCover videoInfo = new FetchVideoCover("D:\\apps\\ffmpeg\\bin\\ffmpeg.exe");
            videoInfo.getCover("D:\\apps\\ffmpeg\\bin\\new.mp4","D:\\apps\\ffmpeg\\bin\\java-output-cover.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
