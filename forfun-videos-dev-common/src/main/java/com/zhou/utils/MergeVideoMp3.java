package com.zhou.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MergeVideoMp3 {
    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,String mp3InputPath,
                          double seconds,String videoOutputPath) throws IOException {
        //ffmpeg.exe -i new.mp4 -i eminem.mp3 -t 3 -y new.avi
         List<String> commond = new ArrayList<>();
         commond.add(ffmpegEXE);

        commond.add("-i");
        commond.add(videoInputPath);

        commond.add("-i");
        commond.add(mp3InputPath);

        commond.add("-t");
        commond.add(String.valueOf(seconds));

         commond.add("-y");
         commond.add(videoOutputPath);

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
            MergeVideoMp3 ffmpeg = new MergeVideoMp3("D:\\apps\\ffmpeg\\bin\\ffmpeg.exe");
            ffmpeg.convertor("D:\\apps\\ffmpeg\\bin\\new.mp4","D:\\apps\\ffmpeg\\bin\\eminem.mp3",3,"D:\\apps\\ffmpeg\\bin\\aaaarenew.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
