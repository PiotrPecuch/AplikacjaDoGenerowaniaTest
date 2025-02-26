package com.pracainzynierska.PiotrPecuch.Services;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.*;

@Service
public class CompressionUtil {

    public static byte[] compressToGZIP(byte[] data) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(data);
        } catch (IOException e) {
            throw new IOException("Error during GZIP compression");
        }
        return byteArrayOutputStream.toByteArray();
    }


    public static byte[] decompress(byte[] compressedData) throws IOException {
        if(isGzipCompressed(compressedData)) {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);

            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                byteArrayOutputStream.write(buffer, 0, len);
            }

            gzipInputStream.close();
            return byteArrayOutputStream.toByteArray();
        } else{
            return compressedData;
        }
    }

    private static boolean isGzipCompressed(byte[] data) {
        return data.length > 2 && data[0] == (byte) 0x1F && data[1] == (byte) 0x8B;
    }


}