package org.local.springawslambda;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class HelloFunction {

    @Bean
    public Function<String, String> hello() {
        return input -> {
            long result = 0;
            for (int i = 0; i < 1_000_000_000; i++) {
                result += new Vector(i, i + 1, i + 2).dot(new Vector(i + 3, i + 4, i + 5));
            }
            return String.format("result=%d (input: %s)", result, input);
        };
    }

    private record Vector(long x, long y, long z) {
        long dot(Vector other) {
            return x * other.x + y * other.y + z * other.z;
        }
    }
}
