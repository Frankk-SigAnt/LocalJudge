package xyz.macromogic.file;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class FileCompiler {
    public static Class<?> compile(File srcFile) {
        int compilationReturnVal = jCompiler.run(null, null, null, srcFile.getAbsolutePath());

        if (compilationReturnVal == 0) {
            try {
                URL classURL = srcFile.getParentFile().toURI().toURL();
                URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classURL});
                return Class.forName(getClassName(srcFile), true, classLoader);
            } catch (ClassNotFoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String getClassName(File srcFile) {
        String fName = srcFile.getName();
        return fName.substring(0, fName.indexOf("."));
    }

    private static JavaCompiler jCompiler = ToolProvider.getSystemJavaCompiler();
}
