package com.vke;

import com.vke.annotations.*;
import com.vke.annotations.expected.ExpectedFail;
import com.vke.annotations.lifecycle.AfterAll;
import com.vke.annotations.lifecycle.AfterEach;
import com.vke.annotations.lifecycle.BeforeAll;
import com.vke.annotations.lifecycle.BeforeEach;
import com.vke.annotations.organization.DisplayName;
import com.vke.annotations.organization.Tag;
import com.vke.assertions.AssertionFailedException;
import com.vke.assertions.MultipleAssertionsFailedException;
import com.vke.utils.Colors;
import com.vke.utils.TagFilter;
import com.vke.utils.Utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class TestRunner {

    private static final String GRAY = "\u001B[90m";

    public static void main(String[] args) {
        runAll();
    }

    public static void runAll() {
        long startTime = System.currentTimeMillis();
        List<TestInfo> infos = runTests(collectTestClasses(FileScanner.findAllClasses()));
        long time = System.currentTimeMillis() - startTime;
        printTestResults(infos, time);
    }

    private static Set<ClassDescriptor> collectTestClasses(List<String> classes) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        Set<ClassDescriptor> descriptors = new HashSet<>();

        for (String className : classes) {
            try {
                Class<?> clazz = loader.loadClass(className);

                descriptors.add(buildDescriptor(clazz));
            } catch (ClassNotFoundException | NoSuchMethodException _) {}
        }

        return descriptors;
    }

    private static ClassDescriptor buildDescriptor(Class<?> clazz) throws NoSuchMethodException {
        ClassDescriptor classDescriptor = new ClassDescriptor(clazz.getDeclaredConstructor());

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) classDescriptor.tests().add(method);

            if (method.isAnnotationPresent(BeforeEach.class)) {
                validateLifecycleEachMethod(method);
                classDescriptor.beforeEach().add(method);
            }

            if (method.isAnnotationPresent(AfterEach.class)) {
                validateLifecycleEachMethod(method);
                classDescriptor.afterEach().add(method);
            }

            if (method.isAnnotationPresent(BeforeAll.class)) {
                validateLifecycleMethod(method);
                classDescriptor.beforeAll().add(method);
            }

            if (method.isAnnotationPresent(AfterAll.class)) {
                validateLifecycleMethod(method);
                classDescriptor.beforeAll().add(method);
            }
        }

        return classDescriptor;
    }

    private static List<TestInfo> runTests(Set<ClassDescriptor> descriptors) {
        List<TestInfo> infos = new ArrayList<>();

        for (ClassDescriptor classDescriptor : descriptors) {
            String className = classDescriptor.constructor().getDeclaringClass().getName();

            try {
                Object instance = classDescriptor.constructor().newInstance();

                for (Method method : classDescriptor.beforeAll()) {
                    method.invoke(instance);
                }

                for (Method test : classDescriptor.tests()) {
                    String path = className + "." + test.getName();
                    infos.addAll(runTest(test, classDescriptor.beforeEach(), classDescriptor.afterEach(), path, instance));
                }

                for (Method method : classDescriptor.afterAll()) {
                    method.invoke(instance);
                }
            } catch (InstantiationException | InvocationTargetException e) {
                System.out.println(new Colors().red("Failed to create instance of class: " + className));
                e.printStackTrace();
            } catch (IllegalAccessException _) {} finally {
                System.out.print(Colors.RESET);
            }
        }

        return infos;
    }

    private static List<TestInfo> runTest(Method test, Set<Method> beforeEach, Set<Method> afterEach, String path, Object instance) {
        long now = System.currentTimeMillis();
        List<TestInfo> infos = new ArrayList<>();

        path = test.isAnnotationPresent(DisplayName.class) ? test.getAnnotation(DisplayName.class).value() : path;

        if (shouldSkip(test)) {
            infos.add(new TestInfo(State.SKIP, path, System.currentTimeMillis() - now, null, test.getAnnotation(Disabled.class).value()));
            return infos;
        }
        if (shouldSkipTag(test)) {
            infos.add(new TestInfo(State.SKIP, path, System.currentTimeMillis() - now, null, "Disabled Tag"));
            return infos;
        }

        int repeatAmount = getRepeatAmount(test);

        for (int i = 1; i <= repeatAmount; i++) {
            try {
                for (Method method : beforeEach) {
                    method.invoke(instance);
                }

                test.invoke(instance);

                infos.add(new TestInfo(State.SUCCESS, path, System.currentTimeMillis() - now, null, null, repeatAmount, i));
            } catch (InvocationTargetException | IllegalAccessException e) {
                if (expectsFail(test)) {
                    infos.add(new TestInfo(State.XFAIL, path, System.currentTimeMillis() - now, null, null, test.getAnnotation(ExpectedFail.class).reason(), repeatAmount, i));
                    continue;
                }

                infos.add(new TestInfo(State.FAIL, path, System.currentTimeMillis() - now, e.getCause(), null, repeatAmount, i));
            } finally {
                try {
                    for (Method method : afterEach) {
                        method.invoke(instance);
                    }
                } catch (InvocationTargetException | IllegalAccessException e) {
                    infos.add(new TestInfo(State.FAIL, path, System.currentTimeMillis() - now, e.getCause(), null, repeatAmount, i));
                }
            }
        }
        return infos;
    }

    private static void printTestResults(List<TestInfo> infos, long time) {
        System.out.println("▶ Running tests…\n");

        int state[] = new int[State.values().length];
        final int total = infos.size();

        Colors c = new Colors();

        for (int i = 0; i < infos.size(); i++) {
            TestInfo info = infos.get(i);
            state[info.state().ordinal()]++;

            c.write(formatTest(info, i + 1, infos.size()));
        }

        System.out.println(c);

        System.out.println(buildFinishInfo(state[0], state[1], state[2], state[3], total, time));
    }

    private static String formatTest(TestInfo info, int idx, int total) {
        Colors c = new Colors();

        c.blue("[ %d/%d ] ".formatted(idx, total));

        if (info.repeatAmount() != 1) {
            c.cyan("[ %d/%d ] ".formatted(info.currentRepeat(), info.repeatAmount())).toString();
        }

        switch (info.state()) {
            case SUCCESS -> c.green("✔ PASS  %s (%d ms)%n".formatted(info.path(), info.time()));
            case FAIL -> {
                c.red("✘ FAIL  %s (%d ms)%n".formatted(info.path(), info.time()));
                c.red(formatException(info.e()));
            }
            case SKIP -> c.yellow("⚠ SKIP  %s (Cause: %s) (%d ms)%n".formatted(info.path(), info.skipMessage(), info.time()));
            case XFAIL -> c.yellow("⚠ XFAIL  %s (Reason: %s) (%d ms)%n".formatted(info.path(), info.xFailReason(), info.time()));
        }

        return c.toString();
    }

    private static String buildFinishInfo(int passed, int failed, int skipped, int xfails, int total, long time) {
        Colors output = new Colors().line(GRAY, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        output.reset ("Tests:            %d/%d%n".formatted(passed + failed + skipped + xfails, total));
        output.green ("Passed:           %d%n".formatted(passed));
        output.red   ("Failed:           %d%n".formatted(failed));
        output.yellow("Skipped:          %d%n".formatted(skipped));
        output.yellow("Expected Fails:   %d%n".formatted(xfails));
        output.reset ("Time:             %d ms%n".formatted(time));
        output.line(GRAY, "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        if (failed == 0) {
            output.green("\n✔ ALL TESTS PASSED");
        } else {
            output.red("\n✘ TEST RUN FAILED");
        }

        return output.reset().toString();
    }

    private static String formatException(Throwable e) {
        if (e instanceof MultipleAssertionsFailedException mafe) {
            return formatMultipleAssertionsFailedException(mafe);
        }

        Colors output = new Colors().red("\t\t\t").write(e).write("\n");

        for (StackTraceElement element : e.getStackTrace()) {
            if (Utils.isReflectStackTraceElement(element)) {
                break;
            }

            if (!Config.LARGE_STACK_TRACE.get()) {
                if (element.getClassName().startsWith("com.vke.assertions.Assertions")) continue;
            }

            output.red("\t\t\t\t").write("at ").write(element).write("\n");
        }
        return output.toString();
    }

    private static String formatMultipleAssertionsFailedException(MultipleAssertionsFailedException e) {
        Colors output = new Colors().red("\t\t\t").write(e.getMessage()).write("\n");

        for (Throwable throwable : e.getThrowables()) {
            output.write(formatException(throwable));
        }

        return output.toString();
    }

    private static void validateLifecycleEachMethod(Method m) {
        if (m.getParameterCount() != 0)
            throw new IllegalStateException("@Before/@After methods must have no parameters");

        if (m.getReturnType() != void.class)
            throw new IllegalStateException("@Before/@After methods must return void");
    }

    private static void validateLifecycleMethod(Method m) {
        validateLifecycleEachMethod(m);
        if (!Modifier.isStatic(m.getModifiers())) throw new IllegalStateException("@BeforeAll/@AfterAll methods must be static");
    }

    private static boolean shouldSkip(Method m) {
        return m.isAnnotationPresent(Disabled.class);
    }

    private static boolean shouldSkipTag(Method m) {
        if (m.isAnnotationPresent(Tag.class)) {
            return TagFilter.readIncludedTags().contains(m.getAnnotation(Tag.class).value());
        }
        return false;
    }

    private static int getRepeatAmount(Method m) {
        if (m.isAnnotationPresent(Repeat.class)) {
            return m.getAnnotation(Repeat.class).value();
        }
        return 1;
    }

    private static boolean expectsFail(Method m) {
        return m.isAnnotationPresent(ExpectedFail.class);
    }

    public record TestInfo(State state, String path, long time, Throwable e, String skipMessage, String xFailReason, int repeatAmount, int currentRepeat) {
        public TestInfo(State state, String path, long time, Throwable e) {
            this(state, path, time, e, null, null, 1, 1);
        }
        public TestInfo(State state, String path, long time, Throwable e, String skipMessage) {
            this(state, path, time, e, skipMessage, null, 1, 1);
        }
        public TestInfo(State state, String path, long time, Throwable e, String skipMessage, int repeatAmount, int currentRepeat) {
            this(state, path, time, e, skipMessage, null, repeatAmount, currentRepeat);
        }
    }
    public record ClassDescriptor(
            Constructor<?> constructor,
            Set<Method> tests,
            Set<Method> beforeEach,
            Set<Method> afterEach,
            Set<Method> beforeAll,
            Set<Method> afterAll
    ) {
        public ClassDescriptor(Constructor<?> constructor) {
            this(constructor, new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        }
    }

    public enum State {

        SUCCESS,
        FAIL,
        SKIP,
        XFAIL

    }

}
