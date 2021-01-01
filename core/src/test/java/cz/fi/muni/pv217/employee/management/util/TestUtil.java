package cz.fi.muni.pv217.employee.management.util;

import cz.fi.muni.pv217.employee.management.entity.Employee;
import cz.fi.muni.pv217.employee.management.entity.Order;
import cz.fi.muni.pv217.employee.management.entity.Workday;
import cz.fi.muni.pv217.employee.management.enums.OrderState;
import io.smallrye.jwt.build.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

public class TestUtil {

    /**
     * Secret for HS256 signing algorithm for JWT, shared between mocked keycloak server and tests
     */
    public static final String HS256_SECRET = "B3KIh5W4Yl/8gN1xnAen0QOOFFLew2z36B1Dlxl2KmI=";

    public static final Employee EMPLOYEE =  new Employee("martin", "Martin", "Hrasko", LocalDate.of(1990, 1,1), "Union", "0900000000", "Brno", LocalDate.of(2020,11,11), BigDecimal.valueOf(5));
    public static final Order ORDER = new Order("Jozef", "Orac", "0910000000", OrderState.INPROGRESS, "Zasad mrkvu");
    public static final Workday WORKDAY1 = new Workday(LocalDate.of(2020,12,28), 8.5, EMPLOYEE, ORDER);
    public static final Workday WORKDAY2 = new Workday(LocalDate.of(2020,12,25), 8.5, EMPLOYEE, ORDER);

    public static Employee createAnotherEmployee() {
        return new Employee("adam", "Adam", "Mrkva", LocalDate.of(1990, 2,2), "Union", "0910000000", "Brno", LocalDate.of(2020,11,11), BigDecimal.valueOf(5));
    }

    public static Order createAnotherOrder() {
        return new Order("Izak", "Cierny", "0920000000", OrderState.INPROGRESS, "Vykop jamu");
    }

    public static Workday createAnotherWorkday() {
        return new Workday(LocalDate.of(2020,12,31), 6D, EMPLOYEE, ORDER);
    }


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
