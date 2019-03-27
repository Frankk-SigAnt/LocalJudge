import xyz.macromogic.file.FileCompiler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test {
    public static void main(String[] args) {
        File f = new File("bin/A.java");
        System.out.println(f.getName());
        Class<?> c = FileCompiler.compile(f);

        try {
            Method mainMethod = c.getMethod("main", String[].class);
            mainMethod.invoke(null, (Object) new String[] {"123"});

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
/**/
    }
}
