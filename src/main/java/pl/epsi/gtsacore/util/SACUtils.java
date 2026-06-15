package pl.epsi.gtsacore.util;

import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SACUtils {

    public static InputStream asInputStream(ResourceLocation rl) {
        return SACUtils.class.getResourceAsStream("/assets/" + rl.getNamespace() + "/" + rl.getPath());
    }

    public static String readFile(InputStream in) throws IOException {
        if (in == null) throw new IllegalArgumentException("Input stream is null when retrieving file!");

        final StringBuilder sBuffer = new StringBuilder();
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        final char[] buffer = new char[1024];

        int cnt;
        while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
            sBuffer.append(buffer, 0, cnt);
        }
        br.close();
        in.close();
        return sBuffer.toString();
    }

}