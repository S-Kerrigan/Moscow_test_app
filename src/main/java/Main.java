import org.json.JSONObject;
import org.json.XML;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class Main {
    public static String path   = "";
    static WriteFile writeFile  = new WriteFile();
    static String tWaiteValue   = "10";

    public static void main(String[] args) {
        Main main   = new Main();

        try {
            path    = main.findDirectory();

            /*This just some exception for showing mechanism of writing to log-file*/
            try {
                writeFile.binary("Test error first", path);
            } catch (Exception ex) {
                writeErrors(ex, path);
            }
            try {
                writeFile.binary("Test error second", path);
            } catch (Exception ex) {
                writeErrors(ex, path);
            }

            try {
                writeFile.binary(1073741824, path);
            } catch (Exception ex) {
                writeErrors(ex, path);
            }

            try {
                main.ping();
            } catch (Exception ex) {
                writeErrors(ex, path);
            }

            try {
                main.xmlToJson(path,"/xml.xml");
            } catch (Exception ex) {
                writeErrors(ex, path);
            }

        } catch (Exception ex) {
            if (ex instanceof NullPointerException) {
                System.out.println("Critical system error: cannot define source-directory");
            } else {
                writeErrors(ex, path);
            }
        }

    }

    /*-------------------------------------------------------------------*/
    /*First task*/
    private String findDirectory() throws Exception {
        String result   = "";

        Path tmpPath    = Paths.get("");
        String target   = tmpPath.toAbsolutePath().toString();

        if (target == null) {
            throw new NullPointerException();
        }

        result          = target;

        return result;
    }
    private static class WriteFile {
        public void exceptions(Object source, String target) throws FileNotFoundException {
            if (source instanceof Exception) {
                FileOutputStream fileOutputStream   = new FileOutputStream(target + "/error.txt", true);
                PrintStream printStream             = new PrintStream(fileOutputStream);
                ((Exception) source).printStackTrace(printStream);
            } else {
                writeErrors(new Exception("Type of file is denied"),target);
            }
        }
        public void binary(Object source, String target) throws IOException {
            if (source instanceof Integer) {
                FileOutputStream fileOutputStream   = new FileOutputStream(target + "/testFile1Gb");
                byte[] buf                          = new byte[(int) source];
                fileOutputStream.write(buf);
                fileOutputStream.close();
            } else {
                writeErrors(new Exception("Type of file is denied"),target);
            }
        }
        public void json(Object source, String target) throws IOException {
            if (source instanceof String) {
                FileOutputStream fileOutputStream   = new FileOutputStream(target + "/xml.json");
                byte[] buf                          = String.valueOf(source).getBytes();
                fileOutputStream.write(buf);
                fileOutputStream.close();
            } else {
                writeErrors(new Exception("Type of file is denied"),target);
            }
        }
    }

    private static void writeErrors(Exception ex, String dir) {
        try {
            ex.printStackTrace();
            writeFile.exceptions(ex, dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*-------------------------------------------------------------------*/
    /*Second task*/
    /*I did't know how doing it, so solution was completed on topics from forums*/
    private void ping() throws ExecutionException, InterruptedException {
        ExecutorService executor    = Executors.newSingleThreadExecutor();
        Future<String> future       = executor.submit(new TimeLimit());

        try {
            System.out.println("Started..");

            String ip           = "infotecs.ru";
            StringBuffer output = new StringBuffer();
            String command      = "ping -n 3 " + ip;

            Process p;
            try {
                p                       = Runtime.getRuntime().exec(command);
                p.waitFor();
                BufferedReader reader   = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line             = "";

                while ((line = reader.readLine())!= null) {
                    output.append(line + "\n");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(output.toString());

            System.out.println(future.get(Long.parseLong(tWaiteValue), TimeUnit.SECONDS));
            System.out.println("Ping has been completed.");
        } catch (TimeoutException e) {
            future.cancel(true);
            System.out.println("Timeout limit has been reached!");
        }

        executor.shutdownNow();
    }
    private class TimeLimit implements Callable<String> {
        @Override
        public String call() throws Exception {
            Thread.sleep(4000);
            return "break";
        }
    }

    /*-------------------------------------------------------------------*/
    /*Third task*/
    private void xmlToJson(String path, String name) throws IOException {

        Reader fileReader           = new FileReader(path+name);
        BufferedReader bufReader    = new BufferedReader(fileReader);
        String xs                   = "";

        StringBuilder sb            = new StringBuilder();
        String line                 = bufReader.readLine();
        while( line != null){
            sb.append(line).append("\n");
            line = bufReader.readLine();
        }

        xs                              = sb.toString();

        JSONObject xmlJSONObj           = XML.toJSONObject(xs);
        String jsonPrettyPrintString    = xmlJSONObj.toString(4);

        if (jsonPrettyPrintString.length() > 0) {
            System.out.println("Convert has been successful.");
        } else {
            System.out.println("Convert has been successful, but file is empty.");
        }

        writeFile.json(jsonPrettyPrintString,path);
    }
}
