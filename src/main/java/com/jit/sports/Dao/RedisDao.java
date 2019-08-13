package com.jit.sports.Dao;

import java.util.List;
import java.util.Map;

/**
 * Created by xushangyu on 2019/8/9.
 */
public interface RedisDao {
    void putHashTable(String tname, Map<String, String> htable);
    List<String> getHashTable(String tname, List<String> hkeylist);
    boolean existTable(String tname, String heky);
    boolean deletekey(String tname);
    Map<String,String>getHashMap(String tname);
}
