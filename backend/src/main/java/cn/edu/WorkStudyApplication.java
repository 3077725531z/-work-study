package cn.edu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.edu.modules.*.mapper")
public class WorkStudyApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkStudyApplication.class, args);
    }
}
