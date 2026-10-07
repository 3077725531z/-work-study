package cn.edu.modules.salary.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 工资核算：net = hours * rate - deduct
 * 纯计算，不落库，落库由确认接口完成
 */
@Service
public class SalaryService {

    public Map<String, BigDecimal> calc(BigDecimal hours, BigDecimal rate, BigDecimal deduct) {
        if (deduct == null) {
            deduct = BigDecimal.ZERO;
        }

        BigDecimal gross = hours.multiply(rate);
        BigDecimal net = gross.subtract(deduct);

        return Map.of("gross", gross, "net", net);
    }
}
