package cz.fi.muni.pv217.employee.management.util;

import cz.fi.muni.pv217.employee.management.entity.Salary;
import io.smallrye.jwt.build.Jwt;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestUtil {

    /**
     * Secret for HS256 signing algorithm for JWT, shared between mocked keycloak server and tests
     */
    public static final String HS256_SECRET = "B3KIh5W4Yl/8gN1xnAen0QOOFFLew2z36B1Dlxl2KmI=";

    public static final Salary USER_SALARY1 = new Salary(
            "user",
            LocalDate.now(),
            LocalDate.now(),
            2,
            5,
            100);

    public static final Salary USER_SALARY2 = new Salary(
            "not-existing-user",
            LocalDate.now(),
            LocalDate.now(),
            2,
            5,
            100);

    public static String getAccessToken(String userName, String... roles) {
        return Jwt.preferredUserName(userName)
                .claim("name", userName)
                .claim("sub", userName)
                .claim("email", userName)
                .groups(new HashSet<>(Arrays.asList(roles)))
                .signWithSecret(HS256_SECRET);
    }

    public static String getAccessToken(String userName) {
        return Jwt.preferredUserName(userName)
                .claim("name", userName)
                .claim("sub", userName)
                .claim("email", userName)
                .signWithSecret(HS256_SECRET);
    }
}
