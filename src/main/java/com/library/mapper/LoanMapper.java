package com.library.mapper;

import com.library.entity.Loan;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface LoanMapper {

    Loan selectById(@Param("id") Long id);

    Loan selectActiveByCopyId(@Param("copyId") Long copyId);

    List<Loan> selectByPage(@Param("readerId") Long readerId, @Param("status") Integer status,
                            @Param("keyword") String keyword,
                            @Param("offset") int offset, @Param("limit") int limit);

    long count(@Param("readerId") Long readerId, @Param("status") Integer status,
               @Param("keyword") String keyword);

    List<Loan> selectActiveByReaderId(@Param("readerId") Long readerId);

    int countActiveByReaderId(@Param("readerId") Long readerId);

    int countOverdueByReaderId(@Param("readerId") Long readerId);

    int insert(Loan loan);

    int updateReturn(@Param("id") Long id, @Param("returnedAt") Date returnedAt,
                     @Param("overdueDays") int overdueDays, @Param("fine") BigDecimal fine,
                     @Param("status") int status, @Param("remark") String remark);

    int updateRenew(@Param("id") Long id, @Param("dueAt") Date dueAt,
                    @Param("renewCount") int renewCount);

    int updateOverdue(@Param("id") Long id, @Param("overdueDays") int overdueDays,
                      @Param("fine") BigDecimal fine);

    /** 状态为在借且已超过应还日期的记录（逾期扫描用） */
    List<Loan> selectDuePassed(@Param("now") Date now);

    // ---- 统计 ----

    List<Map<String, Object>> selectTopBorrowed(@Param("days") Integer days, @Param("limit") int limit);

    List<Map<String, Object>> selectMonthlyCount(@Param("months") int months);
}
