package cz.fi.muni.pv217.employee.management.util;

import cz.fi.muni.pv217.employee.management.entity.Vacation;
import io.smallrye.jwt.build.Jwt;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

public class TestUtil {

    /**
     * Secret for HS256 signing algorithm for JWT, shared between mocked keycloak server and tests
     */
    public static final String HS256_SECRET = "B3KIh5W4Yl/8gN1xnAen0QOOFFLew2z36B1Dlxl2KmI=";

    public static final Vacation USER_VACATION1 = new Vacation(
            1,
            LocalDate.parse("2021-01-01"),
            8,
            true);

    public static final Vacation USER_VACATION2 = new Vacation(
            2,
            LocalDate.parse("2021-01-01"),
            4,
            false);

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
