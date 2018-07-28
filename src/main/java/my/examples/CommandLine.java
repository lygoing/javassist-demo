package my.examples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Mr. Luo
 */
public class CommandLine {

    public String[] execute(Process process) throws InterruptedException {
        final StringBuilder errorContent = new StringBuilder();
        final StringBuilder normalContent = new StringBuilder();

        new Thread(() -> {
            try (InputStream inputStream = process.getErrorStream()) {
                readCommandOutput(inputStream, errorContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        new Thread(() -> {
            try (InputStream inputStream = process.getInputStream()) {
                readCommandOutput(inputStream, normalContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

        process.waitFor();
        return new String[]{errorContent.toString(), normalContent.toString()};
    }

    public StringBuilder readCommandOutput(InputStream inputStream, StringBuilder stringBuilder) {
        try (InputStreamReader isr = new InputStreamReader(inputStream, System.getProperty("sun.jnu.encoding"));
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append('\n');
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuilder;
    }

}
