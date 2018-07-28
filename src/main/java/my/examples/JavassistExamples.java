package my.examples;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

/**
 * @author Mr. Luo
 */
public class JavassistExamples {

    private static final Logger LOGGER = LoggerFactory.getLogger(JavassistExamples.class);

    public static void main(String[] args) throws Exception {
        String className = "com.zaxxer.hikari.HikariDataSource";

        CtClass ctClass = ClassPool.getDefault().getCtClass(className);
        CtConstructor ctConstructor = ctClass.makeClassInitializer();
        ctConstructor.setBody("System.out.println(\"Hello Javassist!\");");

        byte[] bytes = ctClass.toBytecode();
        ctClass.detach();

        //Write new class file
        File jarFile = getJarFile(className);
        String newClassPath = jarFile.getParentFile().getAbsolutePath() +
                File.separator + className.replace('.', File.separatorChar) + ".class";
        FileUtils.writeByteArrayToFile(new File(newClassPath), bytes);

        CommandLine commandLine = new CommandLine();

        //Uninstall the application jar package
        URLClassLoader classLoader = (URLClassLoader) JavassistExamples.class.getClassLoader();
        classLoader.close();

        //Write new class file to the jar package
        String[] command = new String[]{"jar", "-uvf", jarFile.getName(), className.replace('.', '/') + ".class"};
        String[] output = commandLine.execute(Runtime.getRuntime().exec(command, null, jarFile.getParentFile()));
        System.err.println(output[0]);
        System.out.println(output[1]);
    }

    public static File getJarFile(String className) throws ClassNotFoundException, URISyntaxException {
        Class clazz = Class.forName(className);
        ProtectionDomain protectionDomain = clazz.getProtectionDomain();
        File jarFile = new File(protectionDomain.getCodeSource().getLocation().toURI());
        LOGGER.debug(jarFile.getParentFile().getAbsolutePath());
        return jarFile;
    }
}
