package org.joq4j.serde;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultJavaSerializationTest {

    static @Data
    class Person implements Serializable {
        private String name = "John Doe";
        private int age = 18;
        private Date birthDate = new Date(933191282821L);
        private Company company = new Company();

        public boolean equals(Object o) {
            if (!(o instanceof Person)) return false;
            Person p = (Person) o;
            return this.name.equals(p.name) && this.age == p.age && this.birthDate.equals(p.birthDate);
        }
    }

    static @Data
    class Company implements Serializable {
        private String name = "Five9";
        private String address = "9 Duy Tan";

        public boolean equals(Object o) {
            if (!(o instanceof Company)) return false;
            Company c = (Company) o;
            return this.name.equals(c.name) && this.address.equals(c.address);
        }
    }

    @Test
    public void write() {
        Person p = new Person();
        p.setName("tjeubaoit");
        p.setAge(29);
        p.getCompany().setName("VCCorp");
        p.getCompany().setAddress("1 Nguyen Huy Tuong");

        JavaSerialization javaSerialization = new DefaultJavaSerialization();
        String b = javaSerialization.serializeAsBase64(p, Object.class);
        System.out.println(b);

        Person p2 = (Person) javaSerialization.deserializeFromBase64(b, Object.class);
        assertEquals(p, p2);
        assertEquals(p.getCompany(), p2.getCompany());
    }

    @Test
    public void read() {
    }
}