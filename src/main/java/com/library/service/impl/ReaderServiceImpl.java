package com.library.service.impl;

import com.library.common.BusinessException;
import com.library.common.Constants;
import com.library.common.PageResult;
import com.library.entity.Reader;
import com.library.entity.ReaderType;
import com.library.mapper.LoanMapper;
import com.library.mapper.ReaderMapper;
import com.library.mapper.ReaderTypeMapper;
import com.library.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReaderServiceImpl implements ReaderService {

    @Autowired
    private ReaderMapper readerMapper;

    @Autowired
    private ReaderTypeMapper readerTypeMapper;

    @Autowired
    private LoanMapper loanMapper;

    @Override
    public PageResult<Reader> page(String keyword, Integer status, int pageNum, int pageSize) {
        int size = pageSize <= 0 ? 10 : Math.min(pageSize, 100);
        int offset = Math.max(0, (pageNum - 1) * size);
        String kw = (keyword == null || keyword.trim().isEmpty()) ? null : keyword.trim();
        List<Reader> list = readerMapper.selectByPage(kw, status, offset, size);
        long total = readerMapper.count(kw, status);
        return PageResult.of(pageNum, size, total, list);
    }

    @Override
    public Reader detail(Long id) {
        Reader reader = requireReader(id);
        reader.setActiveLoans(loanMapper.countActiveByReaderId(id));
        return reader;
    }

    @Override
    @Transactional
    public Reader create(Reader reader) {
        if (reader == null || isBlank(reader.getName())) {
            throw new BusinessException("读者姓名不能为空");
        }
        if (reader.getTypeId() == null) {
            throw new BusinessException("请选择读者类型");
        }
        ReaderType type = readerTypeMapper.selectById(reader.getTypeId());
        if (type == null) {
            throw new BusinessException("读者类型不存在");
        }
        reader.setName(reader.getName().trim());
        if (reader.getStatus() == null) {
            reader.setStatus(Constants.ReaderStatus.NORMAL);
        }
        // 先以临时证号插入，再按自增 ID 生成正式证号：R + 6 位序号
        reader.setReaderNo("TMP" + System.nanoTime());
        readerMapper.insert(reader);
        readerMapper.updateReaderNo(reader.getId(), String.format("R%06d", reader.getId()));
        return readerMapper.selectById(reader.getId());
    }

    @Override
    @Transactional
    public Reader update(Reader reader) {
        if (reader == null || reader.getId() == null) {
            throw new BusinessException("缺少读者 ID");
        }
        requireReader(reader.getId());
        if (reader.getTypeId() != null && readerTypeMapper.selectById(reader.getTypeId()) == null) {
            throw new BusinessException("读者类型不存在");
        }
        readerMapper.update(reader);
        return readerMapper.selectById(reader.getId());
    }

    @Override
    @Transactional
    public void changeStatus(Long id, int status) {
        Reader reader = requireReader(id);
        if (status < 0 || status > 3) {
            throw new BusinessException("非法的读者状态：" + status);
        }
        // 注销前校验在借图书（FR-READER-003）
        if (status == Constants.ReaderStatus.CANCELLED) {
            int active = loanMapper.countActiveByReaderId(id);
            if (active > 0) {
                throw new BusinessException("读者 " + reader.getName() + " 还有 " + active
                        + " 册在借图书，不能注销");
            }
        }
        readerMapper.updateStatus(id, status);
    }

    private Reader requireReader(Long id) {
        if (id == null) {
            throw new BusinessException("缺少读者 ID");
        }
        Reader reader = readerMapper.selectById(id);
        if (reader == null) {
            throw new BusinessException("读者不存在");
        }
        return reader;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
