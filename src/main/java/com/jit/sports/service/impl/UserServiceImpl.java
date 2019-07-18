package com.jit.sports.service.impl;

import com.jit.sports.Dao.UserDao;
import com.jit.sports.entry.SportInfo;
import com.jit.sports.entry.UserInfo;
import com.jit.sports.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserDao userDao;

    @Override
    public UserInfo login(String userName, String password) {
        return userDao.login(userName, password);
    }

    @Override
    public String existUserName(String userName) {
        return userDao.existUserName(userName);
    }

    @Override
    public void reg(String userName, String password) {
        userDao.reg(userName, password);
    }

    @Override
    public void insertSport(String tag, String userName, String startTime, String overTime, double totalDistance,
                            double totalUp, double totalDown, double averageSpeed, double maxSpeed,
                            double maxElevation, double minElevation) {
        userDao.insertSport(tag, userName, startTime, overTime, totalDistance, totalUp, totalDown, averageSpeed,
                maxSpeed, maxElevation, minElevation);
    }

    @Override
    public SportInfo[] selectSportByName(String userName) {
        return userDao.selectSportByName(userName);
    }
}
