package com.jcl.dao;

import com.jcl.pojo.Weblink;
import com.jcl.pojo.WeblinkExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface WeblinkMapper {
    int countByExample(WeblinkExample example);

    int deleteByExample(WeblinkExample example);

    int deleteByPrimaryKey(Integer wid);

    int insert(Weblink record);

    int insertSelective(Weblink record);

    List<Weblink> selectByExample(WeblinkExample example);

    Weblink selectByPrimaryKey(Integer wid);

    int updateByExampleSelective(@Param("record") Weblink record, @Param("example") WeblinkExample example);

    int updateByExample(@Param("record") Weblink record, @Param("example") WeblinkExample example);

    int updateByPrimaryKeySelective(Weblink record);

    int updateByPrimaryKey(Weblink record);
}