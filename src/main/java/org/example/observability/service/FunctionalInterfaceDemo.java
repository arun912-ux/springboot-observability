package org.example.observability.service;

@FunctionalInterface
public interface FunctionalInterfaceDemo {

    String functionalInterface();

    String SOME_RANDOM_CONSTANT = "some-random-value";
    public static String SOME_RANDOM_CONSTANT_2 = "some-random-value-2";

    private String someRandomMethod(String input) {
        return "some-random-value";
    }

    public static String someRandomMethod2(String input) {
        return "some-random-value-2";
    }

    default String someRandomMethod3(String input) {
        return "some-random-value-3";
    }

    default String someRandomMethod4(String input) {
        return "some-random-value-4";
    }

}
